pipeline {
  agent {
    docker {
      image 'parkjunhyeon/planet-custom:latest'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
    }
  }

  options {
    timestamps()
    timeout(time: 20, unit: 'MINUTES')
  }

  environment {
    AWS_REGION     = 'ap-northeast-2'
    AWS_DEFAULT_REGION = 'ap-northeast-2'
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

          echo "[INFO] Environment variables:"
          echo "AWS_DEFAULT_REGION: ${AWS_DEFAULT_REGION}"
          echo "ECR_REPO: ${ECR_REPO}"
          echo "IMAGE_TAG: ${IMAGE_TAG}"
        '''
      }
    }

      stage('Docker Debug') {
        steps {
          sh '''
            echo "[DEBUG] Current container info:"
            whoami
            pwd

            echo "[DEBUG] Docker check:"
            which docker || echo "Docker not in PATH"
            ls -la /usr/bin/docker* || echo "No docker in /usr/bin"

            echo "[DEBUG] Docker socket check:"
            ls -la /var/run/docker.sock || echo "No docker socket"

            echo "[DEBUG] PATH:"
            echo $PATH
          '''
        }
      }

    stage('Build') {
      steps {
        sh '''
          echo "[INFO] Building with Gradle (skipping tests)..."
          gradle build -x test --no-daemon --build-cache

          echo "[INFO] Build artifacts:"
          ls -la build/libs/
        '''
      }
    }

    stage('Docker Build & Push') {
      steps {
        withAWS(region: "${env.AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            echo "[DEBUG] AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION}"
            echo "[DEBUG] ECR_REPO=${ECR_REPO}"
            echo "[DEBUG] IMAGE_TAG=${IMAGE_TAG}"

            echo "[INFO] Login to ECR..."
            aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}

            echo "[INFO] Build & push Docker image..."
            docker build -t ${ECR_REPO}:${IMAGE_TAG} .
            docker push ${ECR_REPO}:${IMAGE_TAG}
          '''
        }
      }
    }

    stage('Deploy to Idle Stack') {
      steps {
        withAWS(region: "${env.AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            echo "[INFO] Creating new Launch Template version..."
            CURRENT_VERSION=$(aws ec2 describe-launch-template-versions \
              --launch-template-name planet-backend \
              --query 'LaunchTemplateVersions|sort_by(@,&VersionNumber)[-1].VersionNumber' \
              --output text)

            NEW_VERSION=$(aws ec2 create-launch-template-version \
              --launch-template-id ${LT_ID} \
              --source-version $CURRENT_VERSION \
              --version-description "CI/CD deploy $(date +%Y%m%d%H%M%S)" \
              --launch-template-data '{}' \
              --query 'LaunchTemplateVersion.VersionNumber' \
              --output text)

            echo "[INFO] Created Launch Template version: $NEW_VERSION"

            echo "[INFO] Detecting active TargetGroup..."
            ACTIVE_TG=$(aws elbv2 describe-listeners \
              --listener-arns ${LISTENER_ARN} \
              --query 'Listeners[0].DefaultActions[0].ForwardConfig.TargetGroups[?Weight==`1`].TargetGroupArn' \
              --output text)

            echo "[DEBUG] Active TG: $ACTIVE_TG"

            if [ "$ACTIVE_TG" = "${GREEN_TG}" ]; then
              IDLE_STACK=planet-blue-asg
              IDLE_TG=${BLUE_TG}
              IDLE_COLOR=blue
            else
              IDLE_STACK=planet-green-asg
              IDLE_TG=${GREEN_TG}
              IDLE_COLOR=green
            fi

            echo "[INFO] Deploying to $IDLE_STACK ($IDLE_COLOR)"

            cat > /tmp/cf-params.json << EOF
[
  {"ParameterKey":"VpcId","ParameterValue":"${VPC_ID}"},
  {"ParameterKey":"Subnets","ParameterValue":"${SUBNETS}"},
  {"ParameterKey":"LaunchTemplateId","ParameterValue":"${LT_ID}"},
  {"ParameterKey":"LaunchTemplateVersion","ParameterValue":"$NEW_VERSION"},
  {"ParameterKey":"TargetGroupArn","ParameterValue":"$IDLE_TG"},
  {"ParameterKey":"DeploymentColor","ParameterValue":"$IDLE_COLOR"}
]
EOF

            echo "[INFO] CloudFormation parameters:"
            cat /tmp/cf-params.json

            echo "[INFO] Creating/updating CloudFormation stack..."
            if aws cloudformation describe-stacks --stack-name $IDLE_STACK >/dev/null 2>&1; then
              echo "[INFO] Updating existing stack: $IDLE_STACK"
              aws cloudformation update-stack \
                --stack-name $IDLE_STACK \
                --template-url https://s3.ap-northeast-2.amazonaws.com/planet-cf-templates/blue-green.yml \
                --capabilities CAPABILITY_NAMED_IAM \
                --parameters file:///tmp/cf-params.json
              aws cloudformation wait stack-update-complete --stack-name $IDLE_STACK
            else
              echo "[INFO] Creating new stack: $IDLE_STACK"
              aws cloudformation create-stack \
                --stack-name $IDLE_STACK \
                --template-url https://s3.ap-northeast-2.amazonaws.com/planet-cf-templates/blue-green.yml \
                --capabilities CAPABILITY_NAMED_IAM \
                --parameters file:///tmp/cf-params.json
              aws cloudformation wait stack-create-complete --stack-name $IDLE_STACK
            fi

            # 파일에 저장하여 다음 스테이지에서 사용
            echo "$IDLE_TG" > ${WORKSPACE}/idle_tg.txt
            echo "$ACTIVE_TG" > ${WORKSPACE}/active_tg.txt
            echo "${LISTENER_ARN}" > ${WORKSPACE}/listener_arn.txt
          '''
        }
      }
    }

    stage('Wait for Idle Stack Health') {
      steps {
        withAWS(region: "${env.AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            IDLE_TG=$(cat ${WORKSPACE}/idle_tg.txt)
            echo "[INFO] Waiting for health check on: $IDLE_TG"

            UNHEALTHY_COUNT=0

            for i in $(seq 1 30); do   # 최대 5분 대기 (30회 × 10초)
              RAW_HEALTH=$(aws elbv2 describe-target-health \
                --target-group-arn "$IDLE_TG" \
                --query "TargetHealthDescriptions[*].TargetHealth.State" \
                --output text)

              echo "[DEBUG] Attempt $i: $RAW_HEALTH"

              if echo "$RAW_HEALTH" | grep -q "healthy"; then
                echo "[INFO] ✅ Target became healthy"
                exit 0
              fi

              if echo "$RAW_HEALTH" | grep -q "unhealthy"; then
                UNHEALTHY_COUNT=$((UNHEALTHY_COUNT+1))
                echo "[WARN] Target is unhealthy ($UNHEALTHY_COUNT times)"
                # 최소 12번(=2분) 동안은 계속 대기
                if [ $UNHEALTHY_COUNT -ge 12 ]; then
                  echo "[ERROR] ❌ Target stayed unhealthy for over 2 minutes"
                  aws elbv2 describe-target-health --target-group-arn "$IDLE_TG"
                  exit 1
                fi
              fi

              echo "[INFO] Waiting 10s before next check..."
              sleep 10
            done

            echo "[ERROR] ❌ Target did not become healthy within 5 minutes"
            aws elbv2 describe-target-health --target-group-arn "$IDLE_TG"
            exit 1
          '''
        }
      }
    }

    stage('Switch Traffic') {
      steps {
        withAWS(region: "${env.AWS_REGION}", credentials: 'aws-credentials-id') {
          sh '''
            IDLE_TG=$(cat ${WORKSPACE}/idle_tg.txt)
            ACTIVE_TG=$(cat ${WORKSPACE}/active_tg.txt)
            LISTENER_ARN=$(cat ${WORKSPACE}/listener_arn.txt)

            echo "[INFO] Switching traffic..."
            echo "[INFO] From: $ACTIVE_TG (active → weight 0)"
            echo "[INFO] To:   $IDLE_TG (idle → weight 1)"

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

            echo "[INFO] ✅ Traffic switched successfully!"
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
      sh '''
        echo "[DEBUG] Workspace contents:"
        ls -la ${WORKSPACE}/ || true
        echo "[DEBUG] Temp files:"
        ls -la /tmp/cf-* || true
      '''
    }
    cleanup {
      sh '''
        echo "[INFO] Cleaning up..."
        docker system prune -f || true
        rm -f ${WORKSPACE}/idle_tg.txt ${WORKSPACE}/active_tg.txt ${WORKSPACE}/listener_arn.txt || true
        rm -f /tmp/cf-params.json || true
      '''
    }
  }
}