pipeline {
  agent {
    docker {
      image 'cimg/openjdk:21.0'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
    }
  }
  
  options { 
    timestamps()
    timeout(time: 15, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: '10'))
    skipDefaultCheckout()
  }
  
  environment {
    AWS_REGION = 'ap-northeast-2'
    ASG_NAME = 'planet'
    IMAGE_TAG = 'latest'
    CONTAINER_NAME = 'planet'
    ECR_REPO = '958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet'
    GRADLE_USER_HOME = '/tmp/.gradle'
    GRADLE_OPTS = '-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true'
  }
  
  stages {
    stage('Checkout & Setup') {
      parallel {
        stage('Checkout') {
          steps {
            checkout scm
          }
        }
        stage('Setup Tools') {
          steps {
            script {
              def awsInstalled = sh(
                script: 'command -v aws >/dev/null 2>&1 && echo "found" || echo "notfound"',
                returnStdout: true
              ).trim()
              
              if (awsInstalled == 'notfound') {
                echo "[INFO] Installing AWS CLI..."
                sh '''
                  apt-get update -qq
                  apt-get install -y --no-install-recommends awscli
                '''
              } else {
                echo "[INFO] AWS CLI already available"
              }
            }
          }
        }
      }
    }
    
    stage('Build') {
      steps {
        sh '''
          echo "[INFO] Setting up Gradle..."
          chmod +x ./gradlew
          
          echo "[INFO] Building with Gradle..."
          ./gradlew build -x test --no-daemon --build-cache --parallel --info
          
          echo "[INFO] Build artifacts:"
          ls -la build/libs/
        '''
      }
    }
    
    stage('Docker Build & Push') {
      steps {
        withCredentials([
          string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
          string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY')
        ]) {
          sh '''
            echo "[INFO] Configuring AWS & ECR login..."
            aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
            aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
            aws configure set default.region "ap-northeast-2"
            
            # ECR login
            aws ecr get-login-password --region ap-northeast-2 | \
              docker login --username AWS --password-stdin 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com
            
            echo "[INFO] Building & pushing Docker image..."
            docker build -t "$ECR_REPO:$IMAGE_TAG" . --no-cache=true
            docker push "$ECR_REPO:$IMAGE_TAG"
            
            echo "[INFO] ✅ Image pushed successfully!"
          '''
        }
      }
    }
    
    stage('Deploy to EC2') {
      when { 
        expression { return env.GIT_BRANCH == 'origin/deploy' }
      }
      steps {
        withCredentials([
          string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
          string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY')
        ]) {
          sh '''
            echo "[INFO] Finding EC2 instances..."
            INSTANCE_IDS=$(aws ec2 describe-instances \
              --filters "Name=tag:aws:autoscaling:groupName,Values=planet" "Name=instance-state-name,Values=running" \
              --query "Reservations[].Instances[].InstanceId" --output text --region ap-northeast-2)
            
            if [ -z "$INSTANCE_IDS" ]; then
              echo "[ERROR] No running instances found"
              exit 1
            fi
            
            echo "[INFO] Deploying to: $INSTANCE_IDS"
            
            # Send deployment command
            aws ssm send-command \
              --document-name "AWS-RunShellScript" \
              --comment "Deploy planet container" \
              --instance-ids $INSTANCE_IDS \
              --parameters commands='[
                "aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com",
                "docker pull 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest",
                "docker rm -f planet || true",
                "docker run -d --name planet -p 8080:8080 --restart unless-stopped --env-file /home/ec2-user/.env 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest",
                "docker ps --filter name=planet"
              ]' \
              --region ap-northeast-2 \
              --output text
            
            echo "[INFO] ✅ Deployment initiated!"
          '''
        }
      }
    }
  }
  
  post {
    success {
      echo "🎉 Pipeline completed successfully!"
    }
    failure {
      echo "❌ Pipeline failed!"
    }
    cleanup {
      sh '''
        echo "[INFO] Cleanup..."
        docker system prune -f --volumes || true
      '''
    }
  }
}
