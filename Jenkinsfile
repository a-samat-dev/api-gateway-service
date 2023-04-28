pipeline {
  agent {
    docker {
      image 'abhishekf5/maven-abhishek-docker-agent:v1'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock' // mount Docker socket to access the host's Docker daemon
    }
  }
  stages {
    stage('Checkout') {
      steps {
        sh 'echo passed'
        //git branch: 'main', url: 'https://github.com/iam-veeramalla/Jenkins-Zero-To-Hero.git'
      }
    }
    stage('Build and Test') {
      steps {
        sh 'ls -ltr'
        sh 'mvn clean package'
      }
    }
    stage('Build and Push Docker Image') {
      environment {
        DOCKER_IMAGE = "asamatdev/sh-api-gateway-service:${BUILD_NUMBER}"
      }
      steps {
        script {
            sh 'docker build --platform linux/amd64 -t ${DOCKER_IMAGE} .'
            sh 'docker push ${DOCKER_IMAGE}'
        }
      }
    }
    stage('Update Deployment File') {
        environment {
            GIT_REPO_NAME = "api-gateway-service"
            GIT_USER_NAME = "a-samat-dev"
        }
        steps {
            withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
                sh '''
                    git config user.email "a.samat.dev@gmail.com"
                    git config user.name "Samat Abibulla"
                    BUILD_NUMBER=${BUILD_NUMBER}
                    sed -i "s/replaceImageTag/${BUILD_NUMBER}/g" deploy.yml
                    git add deploy.yml
                    git commit -m "Update deployment image to version ${BUILD_NUMBER}"
                    git push https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} HEAD:dev
                '''
            }
        }
    }
  }
}