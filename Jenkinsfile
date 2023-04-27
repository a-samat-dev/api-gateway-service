pipeline {
    agent any

    environment {
        SERVICE_NAME = "api-gateway-service"
        REPOSITORY_TAG = "${DOCKERHUB_USERNAME}/sh-api-gateway-service:${BUILD_ID}"
    }

    stages {
        stage('Preparation') {
            steps {
                cleanWs()
                git credentials: 'GitHub', url: "https://github.com/${ORGANIZATION_NAME}/${SERVICE_NAME}"
            }
        }

        stage('Build') {
            steps {
                sh '''mvn clean package'''
            }
        }

        stage('Build and Push Image') {
            steps {
                sh 'docker image build --platform linux/amd64 -t ${REPOSITORY_TAG} .'
            }
        }

        stage('Deploy to Cluster') {
            steps {
                sh 'envsubst < ${WORKSPACE}/deploy.yaml | kubectl apply -f -'
            }
        }
    }
}