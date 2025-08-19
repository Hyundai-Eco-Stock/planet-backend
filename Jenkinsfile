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
  }
  
  stages {
    stage('Setup') {
      steps {
        script {
          // Check if AWS CLI is available
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
    
    stage('Build') {
      steps {
        sh '''
          echo "[INFO] Setting executable permissions..."
          chmod +x ./gradlew
          
          echo "[INFO] Building with Gradle (skipping tests)..."
          # Use Gradle wrapper cache
          export GRADLE_USER_HOME=~/.gradle
          ./gradlew build -x test --no-daemon --build-cache
          
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
            echo "[INFO] Configuring AWS credentials..."
            aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
            aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
            aws configure set default.region "ap-northeast-2"
            
            echo "[INFO] Logging into Amazon ECR..."
            aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet
            
            echo "[INFO] Building Docker image..."
            docker build -t 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest .
            
            echo "[INFO] Pushing to ECR..."
            docker push 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest
            
            echo "[INFO] ✅ Docker image pushed successfully!"
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
            echo "[INFO] Finding EC2 instances in Auto Scaling Group..."
            INSTANCE_IDS=$(aws ec2 describe-instances \
              --filters "Name=tag:aws:autoscaling:groupName,Values=planet" "Name=instance-state-name,Values=running" \
              --query "Reservations[].Instances[].InstanceId" --output text)
            
            echo "[INFO] Deploying to instances: $INSTANCE_IDS"
            
            if [ -z "$INSTANCE_IDS" ]; then
              echo "[ERROR] No running instances found in ASG: planet"
              exit 1
            fi
            
            echo "[INFO] Sending deployment command via SSM..."
            aws ssm send-command \
              --document-name "AWS-RunShellScript" \
              --comment "Deploy Docker container" \
              --instance-ids $INSTANCE_IDS \
              --parameters commands='[
                "aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet",
                "docker pull 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest",
                "docker rm -f planet || true",
                "docker run -d --name planet -p 8080:8080 --restart unless-stopped --env SPRING_PROFILES_ACTIVE=prod --env-file /home/ec2-user/.env 958948421852.dkr.ecr.ap-northeast-2.amazonaws.com/planet:latest",
                "echo Deployment completed on $(hostname)"
              ]' \
              --region ap-northeast-2
            
            echo "[INFO] ✅ Deployment command sent!"
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
      sh '''
        echo "[INFO] Cleaning up Docker images..."
        docker system prune -f || true
      '''
    }
  }
}
