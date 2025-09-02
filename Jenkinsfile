pipeline {
  agent {
    docker {
      image 'cimg/openjdk:21.0'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
    }
  }
  
  options { 
    timestamps()
    timeout(time: 20, unit: 'MINUTES')
  }
  
  environment {
    AWS_REGION = 'ap-northeast-2'
    ASG_NAME = 'planet'
    IMAGE_TAG = 'latest'
    CONTAINER_NAME = 'planet'
    ECR_REPO = '958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet'
    LT_NAME        = 'planet-backend'
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
          // 브랜치 체크 (deploy 브랜치 아니면 STOP)
          if (env.GIT_BRANCH != 'origin/deploy') {
            error("[SKIP] Not deploy branch → stopping pipeline.")
          }

          // PR 빌드 체크 (CHANGE_ID != null이면 PR 빌드, merge는 null)
          if (env.CHANGE_ID != null) {
            error("[SKIP] This is a PR build (not merged) → stopping pipeline.")
          }

          echo "[INFO] ✅ Valid deploy pipeline (deploy branch push or PR merge). Continuing..."
        }
      }
    }
    
    stage('Setup') {
      steps {
        script {
          def awsInstalled = sh(
            script: 'which aws || echo "notfound"',
            returnStdout: true
          ).trim()
          
          if (awsInstalled.contains('notfound')) {
            echo "[INFO] Installing required packages..."
            sh '''
              apt-get update -qq
              apt-get install -y curl unzip python3 python3-pip awscli
            '''
          } else {
            echo "[INFO] AWS CLI already available, skipping package installation..."
          }
        }
        
        sh '''
          echo "[INFO] Tool versions:"
          java -version
          which docker && docker --version || echo "Docker not available"
          aws --version
        '''
      }
    }
    
//     stage('Build') {
//       steps {
//         sh '''
//           echo "[INFO] Setting executable permissions..."
//           chmod +x ./gradlew
//
//           echo "[INFO] Building with Gradle (skipping tests)..."
//           export GRADLE_USER_HOME=~/.gradle
//           ./gradlew build -x test --no-daemon --build-cache
//
//           echo "[INFO] Build artifacts:"
//           ls -la build/libs/
//         '''
//       }
//     }
//
//     stage('Docker Build & Push') {
//       steps {
//         withCredentials([
//           string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
//           string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY')
//         ]) {
//           sh '''
//             echo "[INFO] Configuring AWS credentials..."
//             aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
//             aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
//             aws configure set default.region "ap-northeast-2"
//
//             echo "[INFO] Logging into Amazon ECR..."
//             aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet
//
//             echo "[INFO] Building Docker image..."
//             docker build -t 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest .
//
//             echo "[INFO] Pushing to ECR..."
//             docker push 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest
//
//             echo "[INFO] ✅ Docker image pushed successfully!"
//           '''
//         }
//       }
//     }

    stage('Deploy to Idle Stack') {
      steps {
        withCredentials([
          string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
          string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY')
        ]) {
          sh '''
            set -e
            aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
            aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
            aws configure set default.region ap-northeast-2

            echo "[INFO] Creating new Launch Template version..."
            CURRENT_VERSION=$(aws ec2 describe-launch-template-versions \
              --launch-template-name $LT_NAME \
              --versions '$Latest' \
              --query 'LaunchTemplateVersions[0].VersionNumber' \
              --output text)

            NEW_VERSION=$(aws ec2 create-launch-template-version \
              --launch-template-id $LT_ID \
              --source-version $CURRENT_VERSION \
              --version-description "CI/CD deploy $(date +%Y%m%d%H%M%S)" \
              --launch-template-data '{}' \
              --query 'LaunchTemplateVersion.VersionNumber' \
              --output text)

            echo "[INFO] ✅ Created Launch Template version: $NEW_VERSION"

            echo "[INFO] Detecting active TargetGroup..."
            ACTIVE_TG=$(aws elbv2 describe-listeners \
              --listener-arn $LISTENER_ARN \
              --query "Listeners[0].DefaultActions[0].TargetGroupArn" \
              --output text)

            if [ "$ACTIVE_TG" = "$BLUE_TG" ]; then
              IDLE_STACK=planet-green-asg
              IDLE_TG=$GREEN_TG
              IDLE_COLOR=green
            else
              IDLE_STACK=planet-blue-asg
              IDLE_TG=$BLUE_TG
              IDLE_COLOR=blue
            fi

            echo "[INFO] Deploying to $IDLE_STACK ($IDLE_COLOR)..."
            aws cloudformation deploy \
              --stack-name $IDLE_STACK \
              --template-url https://s3.ap-northeast-2.amazonaws.com/planet-cf-templates/blue-green.yml \
              --capabilities CAPABILITY_NAMED_IAM \
              --parameter-overrides \
                VpcId=$VPC_ID \
                Subnets="$SUBNETS" \
                LaunchTemplateId=$LT_ID \
                LaunchTemplateVersion=$NEW_VERSION \
                TargetGroupArn=$IDLE_TG \
                DeploymentColor=$IDLE_COLOR

            echo "$IDLE_TG" > /tmp/idle_tg.txt
            echo "$LISTENER_ARN" > /tmp/listener_arn.txt
          '''
        }
      }
    }


    stage('Wait for Idle Stack Health') {
      steps {
        sh '''
          IDLE_TG=$(cat /tmp/idle_tg.txt)
          for i in {1..60}; do
            HEALTH=$(aws elbv2 describe-target-health --target-group-arn $IDLE_TG \
              --query 'TargetHealthDescriptions[*].TargetHealth.State' \
              --output text | grep -v draining | uniq)
            echo "Current health: $HEALTH"
            if [ "$HEALTH" = "healthy" ]; then
              echo "[INFO] ✅ New stack is healthy!"
              exit 0
            fi
            sleep 10
          done
          echo "[ERROR] ❌ New stack did not become healthy in time"
          exit 1
        '''
      }
    }

    stage('Switch Traffic') {
      steps {
        sh '''
          IDLE_TG=$(cat /tmp/idle_tg.txt)
          LISTENER_ARN=$(cat /tmp/listener_arn.txt)

          echo "[INFO] Switching traffic to $IDLE_TG..."
          aws elbv2 modify-listener \
            --listener-arn $LISTENER_ARN \
            --default-actions Type=forward,TargetGroupArn=$IDLE_TG
          echo "[INFO] ✅ Traffic switched!"
        '''
      }
    }
  }

//     stage('Deploy via ASG Rolling Update') {
//       steps {
//         withCredentials([
//           string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
//           string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY')
//         ]) {
//           sh '''
//             echo "[INFO] Configuring AWS CLI..."
//             aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
//             aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
//             aws configure set default.region ap-northeast-2
//
//             echo "[INFO] Getting current latest Launch Template version..."
//             CURRENT_VERSION=$(aws ec2 describe-launch-template-versions \
//               --launch-template-name planet-backend \
//               --versions $Latest \
//               --query 'LaunchTemplateVersions[0].VersionNumber' \
//               --output text)
//
//             echo "[INFO] Creating new Launch Template version from v$CURRENT_VERSION ..."
//             NEW_VERSION=$(aws ec2 create-launch-template-version \
//               --launch-template-name planet-backend \
//               --source-version $CURRENT_VERSION \
//               --launch-template-data '{}' \
//               --query 'LaunchTemplateVersion.VersionNumber' \
//               --output text)
//
//             echo "[INFO] New Launch Template version created: v$NEW_VERSION"
//
//             echo "[INFO] Updating Auto Scaling Group to use new version..."
//             aws autoscaling update-auto-scaling-group \
//               --auto-scaling-group-name planet \
//               --launch-template "LaunchTemplateName=planet-backend,Version=$NEW_VERSION"
//
//             echo "[INFO] Starting ASG Instance Refresh..."
//             REFRESH_ID=$(aws autoscaling start-instance-refresh \
//               --auto-scaling-group-name planet \
//               --preferences MinHealthyPercentage=50,InstanceWarmup=10 \
//               --query 'InstanceRefreshId' --output text)
//
//             echo "[INFO] Instance refresh started: $REFRESH_ID"
//
//             # Polling until refresh completes
//             while true; do
//               STATUS=$(aws autoscaling describe-instance-refreshes \
//                 --auto-scaling-group-name planet \
//                 --instance-refresh-ids $REFRESH_ID \
//                 --query 'InstanceRefreshes[0].Status' \
//                 --output text)
//
//               echo "[INFO] Current refresh status: $STATUS"
//
//               if [ "$STATUS" = "Successful" ]; then
//                 echo "[INFO] 🎉 Instance refresh completed successfully!"
//                 break
//               elif [ "$STATUS" = "Failed" ] || [ "$STATUS" = "Cancelled" ]; then
//                 echo "[ERROR] ❌ Instance refresh ended with status: $STATUS"
//                 exit 1
//               fi
//
//               sleep 30
//             done
//           '''
//         }
//       }
//     }
  post {
    success {
      echo "🎉 Backend deployment completed successfully!"
    }
    failure {
      echo "❌ Backend deployment failed!"
    }
    cleanup {
      sh '''
        echo "[INFO] Cleaning up Docker images..."
        docker system prune -f || true
      '''
    }
  }
}
