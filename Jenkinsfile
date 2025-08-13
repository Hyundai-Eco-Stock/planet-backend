pipeline {
    agent { label 'built-in' }

    tools {
        gradle 'gradle 8.14'
        jdk 'jdk-21'
    }

    environment {
        SERVICE_NAME = 'planet-backend'
        HARBOR_URL = 'https://localhost:8443'
        HARBOR_REPO = 'localhost:8443/planet'
    }

    stages {
        stage('Check workspace') {
            steps {
                sh 'pwd && ls -al'
            }
        }

        stage('Build') {
            steps {
		            sh '''
                ./gradlew build -x test
                '''
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'Harbor_Credentials', usernameVariable: 'HARBOR_USER', passwordVariable: 'HARBOR_PASS')]) {
                    sh '''
                    echo "$HARBOR_PASS" | docker login $HARBOR_URL -u "$HARBOR_USER" --password-stdin

                    docker rmi -f $HARBOR_REPO/$SERVICE_NAME:latest || true
                    docker build -t $HARBOR_REPO/$SERVICE_NAME:latest .
                    docker push $HARBOR_REPO/$SERVICE_NAME:latest
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'Harbor_Credentials', usernameVariable: 'HARBOR_USER', passwordVariable: 'HARBOR_PASS')]) {
                    sh '''
                    echo "$HARBOR_PASS" | docker login $HARBOR_URL -u "$HARBOR_USER" --password-stdin
                    docker compose pull $SERVICE_NAME || docker compose pull
                    docker compose down || true
                    docker compose up -d --no-deps --force-recreate $SERVICE_NAME
                    '''
                }
            }
        }
    }
}