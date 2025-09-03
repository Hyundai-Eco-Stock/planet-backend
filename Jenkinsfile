pipeline {
  agent {
    docker {
      image 'cimg/openjdk:21.0'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
    }
  }

  tools {
    gradle 'gradle-8.14'
  }

  options {
    timestamps()
    timeout(time: 20, unit: 'MINUTES')
  }

  environment {
    AWS_REGION     = 'ap-northeast-2'
    ASG_NAME       = 'planet'
    IMAGE_TAG      = 'latest'
    CONTAINER_NAME = 'planet'
    ECR_REPO       = '958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet'
    LT_ID          = 'lt-0247d3f1f9751c069'
    VPC_ID         = 'vpc-0078d01ffe1b985f2'
    SUBNETS        = 'subnet-01277620756a7119c,subnet-03112ab72dbbebdc2,subnet-04d05f52b13a598b6,subnet-0d9c1cab2bf65b242'
    LISTENER_ARN   = 'arn:aws:elasticloadbalancing:ap-northeast-2:958948421852:listener/app/planet-lb/e80a8f6a74350f0e/81806b45e3367515'
    BLUE_TG        = 'arn:aws:elasticloadbalancing:ap-northeast-2:958948421852:targetgroup/planet-back/d17dc02beb3cf8f3'
    GREEN_TG       = 'arn:aws:elasticloadbalancing:ap-northeast-2:958948421852:targetgroup/planet-second/31753c9206519568'
  }

  stages {
    stage('Check Conditions') {
      steps {
        script {
          if (env.GIT_BRANCH != 'origin/deploy') {
            error("[SKIP] Not deploy branch → stopping pipeline.")
          }
          if (env.CHANGE_ID != null) {
            error("[SKIP] This is a PR build → stopping pipeline.")
          }
          echo "[INFO] ✅ Valid deploy pipeline"
        }
      }
    }

    stage('Setup') {
      steps {
        sh '''
          echo "[INFO] Tool versions:"
          java -version
          gradle -v
          git --version || echo "git not found"
        '''
      }
    }

    stage('Build') {
      steps {
        sh '''
          echo "[INFO] Building with Jenkins Gradle Tool (skipping tests)..."
          export GRADLE_USER_HOME=$WORKSPACE/.gradle
          gradle build -x test --no-daemon --build-cache

          echo "[INFO] Build artifacts:"
          ls -la build/libs/
        '''
      }
    }

    stage('Docker Build & Push') {
      steps {
        withAWS(region: "${AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            echo "[INFO] Login to ECR..."
            aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REPO

            echo "[INFO] Build & push Docker image..."
            docker build -t $ECR_REPO:$IMAGE_TAG .
            docker push $ECR_REPO:$IMAGE_TAG
          '''
        }
      }
    }

    stage('Deploy to Idle Stack') {
      steps {
        withAWS(region: "${AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            echo "[INFO] Creating new Launch Template version..."
            CURRENT_VERSION=$(aws ec2 describe-launch-template-versions \
              --launch-template-name planet-backend \
              --query 'LaunchTemplateVersions|sort_by(@,&VersionNumber)[-1].VersionNumber' \
              --output text)

            NEW_VERSION=$(aws ec2 create-launch-template-version \
              --launch-template-id $LT_ID \
              --source-version $CURRENT_VERSION \
              --version-description "CI/CD deploy $(date +%Y%m%d%H%M%S)" \
              --launch-template-data '{}' \
              --query 'LaunchTemplateVersion.VersionNumber' \
              --output text)

            echo "[INFO] Created Launch Template version: $NEW_VERSION"

            echo "[INFO] Detecting active TargetGroup..."
            ACTIVE_TG=$(aws elbv2 describe-listeners \
              --listener-arns $LISTENER_ARN \
              --query 'Listeners[0].DefaultActions[0].ForwardConfig.TargetGroups[?Weight==`1`].TargetGroupArn' \
              --output text)

            echo "[DEBUG] Active TG: $ACTIVE_TG"

            if [ "$ACTIVE_TG" = "$GREEN_TG" ]; then
              IDLE_STACK=planet-blue-asg
              IDLE_TG=$BLUE_TG
              IDLE_COLOR=blue
            else
              IDLE_STACK=planet-green-asg
              IDLE_TG=$GREEN_TG
              IDLE_COLOR=green
            fi

            echo "[INFO] Deploying to $IDLE_STACK ($IDLE_COLOR)"

            printf '[
              {"ParameterKey":"VpcId","ParameterValue":"%s"},
              {"ParameterKey":"Subnets","ParameterValue":"%s"},
              {"ParameterKey":"LaunchTemplateId","ParameterValue":"%s"},
              {"ParameterKey":"LaunchTemplateVersion","ParameterValue":"%s"},
              {"ParameterKey":"TargetGroupArn","ParameterValue":"%s"},
              {"ParameterKey":"DeploymentColor","ParameterValue":"%s"}
            ]' "$VPC_ID" "$SUBNETS" "$LT_ID" "$NEW_VERSION" "$IDLE_TG" "$IDLE_COLOR" > /tmp/cf-params.json

            echo "[INFO] Creating/updating CloudFormation stack..."
            if aws cloudformation describe-stacks --stack-name $IDLE_STACK >/dev/null 2>&1; then
              aws cloudformation update-stack \
                --stack-name $IDLE_STACK \
                --template-url https://s3.ap-northeast-2.amazonaws.com/planet-cf-templates/blue-green.yml \
                --capabilities CAPABILITY_NAMED_IAM \
                --parameters file:///tmp/cf-params.json
              aws cloudformation wait stack-update-complete --stack-name $IDLE_STACK
            else
              aws cloudformation create-stack \
                --stack-name $IDLE_STACK \
                --template-url https://s3.ap-northeast-2.amazonaws.com/planet-cf-templates/blue-green.yml \
                --capabilities CAPABILITY_NAMED_IAM \
                --parameters file:///tmp/cf-params.json
              aws cloudformation wait stack-create-complete --stack-name $IDLE_STACK
            fi

            echo "$IDLE_TG" > $WORKSPACE/idle_tg.txt
            echo "$ACTIVE_TG" > $WORKSPACE/active_tg.txt
            echo "$LISTENER_ARN" > $WORKSPACE/listener_arn.txt
          '''
        }
      }
    }

    stage('Wait for Idle Stack Health') {
      steps {
        withAWS(region: "${AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            IDLE_TG=$(cat $WORKSPACE/idle_tg.txt)
            for i in {1..60}; do
              HEALTH=$(aws elbv2 describe-target-health \
                --target-group-arn $IDLE_TG \
                --query 'TargetHealthDescriptions[*].TargetHealth.State' \
                --output text | grep -v draining | uniq | awk '{print $1}')

              echo "Current health: $HEALTH"
              if [ "$HEALTH" = "healthy" ]; then
                echo "[INFO] New stack is healthy!"
                exit 0
              fi
              sleep 10
            done
            echo "[ERROR] New stack did not become healthy in time"
            exit 1
          '''
        }
      }
    }

    stage('Switch Traffic') {
      steps {
        withAWS(region: "${AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            IDLE_TG=$(cat $WORKSPACE/idle_tg.txt)
            ACTIVE_TG=$(cat $WORKSPACE/active_tg.txt)
            LISTENER_ARN=$(cat $WORKSPACE/listener_arn.txt)

            echo "[INFO] Switching traffic from $ACTIVE_TG to $IDLE_TG..."
            aws elbv2 modify-listener \
              --listener-arn $LISTENER_ARN \
              --default-actions '[{
                "Type": "forward",
                "ForwardConfig": {
                  "TargetGroups": [
                    {"TargetGroupArn": "'$IDLE_TG'", "Weight": 1},
                    {"TargetGroupArn": "'$ACTIVE_TG'", "Weight": 0}
                  ]
                }
              }]'
          '''
        }
      }
    }
  }

  post {
    success {
      echo "🎉 Backend deployment completed successfully!"
    }
    failure {
      echo "❌ Backend deployment failed!"
    }
    cleanup {
      sh 'docker system prune -f || true'
    }
  }
}
