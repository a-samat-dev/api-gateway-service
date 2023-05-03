pipeline {
  agent {
    docker {
        image 'asamatdev/mvn-docker-agent'
        args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
    }
  }

  stages {
    stage('Checkout') {
      steps {
        sh 'echo passed'
      }
    }
    stage('Build and Test') {
      steps {
        sh 'ls -ltr'
        sh 'mvn --version'
        sh 'echo JAVA_HOME'
        sh 'mvn clean package'
        sh 'maven build completed'
      }
    }
//     stage('Build and Push Docker Image') {
//       environment {
//         DOCKER_IMAGE = "asamatdev/sh-api-gateway-service:${BUILD_NUMBER}"
//       }
//       steps {
//         script {
//             sh 'docker build --platform linux/amd64 -t ${DOCKER_IMAGE} .'
//             sh 'docker push ${DOCKER_IMAGE}'
//         }
//       }
//     }
//     stage('Update Deployment File') {
//         environment {
//             GIT_REPO_NAME = "api-gateway-service"
//             GIT_USER_NAME = "a-samat-dev"
//         }
//         steps {
//             withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
//                 sh '''
//                     git config user.email "a.samat.dev@gmail.com"
//                     git config user.name "Samat Abibulla"
//                     BUILD_NUMBER=${BUILD_NUMBER}
//                     sed -i "s/replaceImageTag/${BUILD_NUMBER}/g" deploy.yml
//                     git add deploy.yml
//                     git commit -m "Update deployment image to version ${BUILD_NUMBER}"
//                     git push https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} HEAD:dev
//                 '''
//             }
//         }
//     }
  }
}