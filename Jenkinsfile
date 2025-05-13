pipeline {
    agent any
    environment {
        DEPLOYMENT_NAME = "hello-node"
        CONTAINER_NAME = "docs"
        IMAGE_NAME = "shuoer001/teedy:${env.BUILD_NUMBER}"
    }
    stages {
        stage('Start Minikube') {
            steps {
                bat '''
                @echo off
                minikube status | findstr "Running" > nul
                if errorlevel 1 (
                    echo Starting Minikube...
                    minikube start
                ) else (
                    echo Minikube already running.
                )
                '''
            }
        }
        stage('Set Image') {
            steps {
                bat '''
                echo Setting image for deployment...
                kubectl set image deployment/%DEPLOYMENT_NAME% %CONTAINER_NAME%=%IMAGE_NAME%
                '''
            }
        }
        stage('Verify') {
            steps {
                bat 'kubectl rollout status deployment/%DEPLOYMENT_NAME%'
                bat 'kubectl get pods'
            }
        }
    }
}
