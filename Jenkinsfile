pipeline {
  agent {
    docker {
      image 'openjdk:21-jdk-slim'
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
  }
  
  stages {
    stage('Setup') {
      steps {
        sh '''
          echo "[INFO] Installing required packages..."
          apt-get update -qq
          apt-get install -y curl unzip python3 python3-pip docker.io awscli
          
          echo "[INFO] Tool versions:"
          java -version
          docker --version
          aws --version
        '''
      }
    }
    
    stage('Cache & Build') {
      steps {
        sh '''
          echo "[INFO] Setting executable permissions..."
          chmod +x ./gradlew
          
          echo "[INFO] Building with Gradle (skipping tests)..."
          ./gradlew build -x test --no-daemon
          
          echo "[INFO] Build artifacts:"
          ls -la build/libs/
        '''
      }
    }
    
    stage('Docker Build & Push') {
      steps {
        withCredentials([
          string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY_ID'),
          string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY'),
          string(credentialsId: 'DOCKER_USERNAME', variable: 'ECR_REPO')
        ]) {
          sh '''
            echo "[INFO] Configuring AWS credentials..."
            aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
            aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
            aws configure set default.region "$AWS_REGION"
            
            echo "[INFO] Logging into Amazon ECR..."
            aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REPO
            
            echo "[INFO] Building Docker image..."
            docker build -t $ECR_REPO:$GIT_COMMIT .
            
            echo "[INFO] Tagging as latest..."
            docker tag $ECR_REPO:$GIT_COMMIT $ECR_REPO:$IMAGE_TAG
            
            echo "[INFO] Pushing to ECR..."
            docker push $ECR_REPO:$GIT_COMMIT
            docker push $ECR_REPO:$IMAGE_TAG
            
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
          string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_ACCESS_KEY'),
          string(credentialsId: 'DOCKER_USERNAME', variable: 'ECR_REPO')
        ]) {
          sh '''
            echo "[INFO] Finding EC2 instances in Auto Scaling Group..."
            INSTANCE_IDS=$(aws ec2 describe-instances \
              --filters "Name=tag:aws:autoscaling:groupName,Values=$ASG_NAME" "Name=instance-state-name,Values=running" \
              --query "Reservations[].Instances[].InstanceId" --output text)
            
            echo "[INFO] Deploying to instances: $INSTANCE_IDS"
            
            if [ -z "$INSTANCE_IDS" ]; then
              echo "[ERROR] No running instances found in ASG: $ASG_NAME"
              exit 1
            fi
            
            echo "[INFO] Sending deployment command via SSM..."
            COMMAND_ID=$(aws ssm send-command \
              --document-name "AWS-RunShellScript" \
              --comment "Deploy Docker container on ASG instances" \
              --instance-ids $INSTANCE_IDS \
              --parameters commands='[
                "echo \"[INFO] Logging into ECR...\"",
                "aws ecr get-login-password --region '"$AWS_REGION"' | docker login --username AWS --password-stdin '"$ECR_REPO"'",
                "echo \"[INFO] Pulling latest image...\"",
                "docker pull '"$ECR_REPO:$IMAGE_TAG"'",
                "echo \"[INFO] Stopping existing container...\"", 
                "docker rm -f '"$CONTAINER_NAME"' || true",
                "echo \"[INFO] Starting new container...\"",
                "docker run -d \\",
                "  --name '"$CONTAINER_NAME"' \\",
                "  -p 8080:8080 \\", 
                "  --restart unless-stopped \\",
                "  -v /etc/localtime:/etc/localtime:ro \\",
                "  -e TZ=Asia/Seoul \\",
                "  --env-file /home/ec2-user/.env \\",
                "  '"$ECR_REPO:$IMAGE_TAG"'",
                "echo \"[INFO] Container status:\"",
                "docker ps | grep '"$CONTAINER_NAME"' || echo \"Container not running\"",
                "echo \"[INFO] ✅ Deployment completed on $(hostname)\""
              ]' \
              --region $AWS_REGION \
              --query "Command.CommandId" --output text)
            
            echo "[INFO] Command ID: $COMMAND_ID"
            echo "[INFO] Waiting for deployment to complete..."
            
            # Wait for command completion and check all instances
            TIMEOUT=300  # 5 minutes timeout
            INTERVAL=10
            ELAPSED=0
            
            while [ $ELAPSED -lt $TIMEOUT ]; do
              ALL_SUCCESS=true
              ALL_COMPLETE=true
              
              for INSTANCE_ID in $INSTANCE_IDS; do
                STATUS=$(aws ssm get-command-invocation \
                  --command-id $COMMAND_ID \
                  --instance-id $INSTANCE_ID \
                  --query "Status" --output text 2>/dev/null || echo "InProgress")
                
                echo "[INFO] Instance $INSTANCE_ID status: $STATUS"
                
                case $STATUS in
                  "Success")
                    # Instance completed successfully
                    ;;
                  "Failed"|"Cancelled"|"TimedOut")
                    echo "[ERROR] Deployment failed on instance $INSTANCE_ID with status: $STATUS"
                    # Get error details
                    ERROR_OUTPUT=$(aws ssm get-command-invocation \
                      --command-id $COMMAND_ID \
                      --instance-id $INSTANCE_ID \
                      --query "StandardErrorContent" --output text 2>/dev/null || echo "No error details available")
                    echo "[ERROR] Error details: $ERROR_OUTPUT"
                    exit 1
                    ;;
                  *)
                    # Still in progress
                    ALL_COMPLETE=false
                    ;;
                esac
              done
              
              if [ "$ALL_COMPLETE" = "true" ]; then
                echo "[INFO] ✅ Deployment completed successfully on all instances!"
                
                # Verify containers are running
                echo "[INFO] Verifying container status on all instances..."
                aws ssm send-command \
                  --document-name "AWS-RunShellScript" \
                  --comment "Verify container status" \
                  --instance-ids $INSTANCE_IDS \
                  --parameters commands='[
                    "echo \"[INFO] Container status on $(hostname):\"",
                    "docker ps --filter name='"$CONTAINER_NAME"' --format \"table {{.Names}}\\t{{.Status}}\\t{{.Ports}}\" || echo \"No containers found\"",
                    "docker logs --tail 10 '"$CONTAINER_NAME"' 2>/dev/null || echo \"No logs available\""
                  ]' \
                  --region $AWS_REGION >/dev/null
                
                break
              fi
              
              sleep $INTERVAL
              ELAPSED=$((ELAPSED + INTERVAL))
            done
            
            if [ $ELAPSED -ge $TIMEOUT ]; then
              echo "[ERROR] Deployment timed out after $TIMEOUT seconds"
              exit 1
            fi
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
      script {
        // Get deployment logs on failure
        sh '''
          if [ ! -z "$COMMAND_ID" ] && [ ! -z "$INSTANCE_IDS" ]; then
            echo "[INFO] Fetching deployment logs for troubleshooting..."
            for INSTANCE_ID in $INSTANCE_IDS; do
              echo "[INFO] Logs for instance $INSTANCE_ID:"
              aws ssm get-command-invocation \
                --command-id $COMMAND_ID \
                --instance-id $INSTANCE_ID \
                --query "StandardOutputContent" --output text 2>/dev/null || echo "No output available"
            done
          fi
        '''
      }
    }
    cleanup {
      sh '''
        echo "[INFO] Cleaning up Docker images..."
        docker system prune -f || true
      '''
    }
  }
}
