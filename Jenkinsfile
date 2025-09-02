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
    
    stage('Build') {
      steps {
        sh '''
          echo "[INFO] Setting executable permissions..."
          chmod +x ./gradlew
          
          echo "[INFO] Building with Gradle (skipping tests)..."
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
    
    stage('Deploy via ASG Rolling Update') {
      steps {
        withCredentials([
          string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
          string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY')
        ]) {
          sh '''
            echo "[INFO] Configuring AWS CLI..."
            aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
            aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
            aws configure set default.region ap-northeast-2

            aws autoscaling update-auto-scaling-group \
              --auto-scaling-group-name planet \
              --launch-template "LaunchTemplateName=planet-backend,Version=\$Latest"

            echo "[INFO] ✅ Rolling update triggered on ASG: planet"
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
