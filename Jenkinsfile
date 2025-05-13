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
                sh 'echo "模拟 Minikube 启动成功"'
            }
        }
        stage('Set Image') {
            steps {
                sh 'echo "模拟设置镜像: ${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME}"'
            }
        }
        stage('Verify') {
            steps {
                sh 'echo "模拟验证部署状态: 成功"'
                sh 'echo "模拟获取 Pod 列表: hello-node-12345-abcd (Running)"'
            }
        }
    }
    post {
        success {
            sh 'echo "CI/CD 流程完成！"'
        }
    }
}
