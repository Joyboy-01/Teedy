pipeline {
    agent any

    environment {
        // 定义环境变量
        DOCKER_IMAGE = 'shuoer001/teedy' // 你的 Docker Hub 用户名和仓库名称
        DOCKER_TAG = "${env.BUILD_NUMBER}" // 使用构建编号作为标签
    }

    stages {
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }
        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd'
            }
        }
        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        stage('Javadoc') {
            steps {
                sh 'mvn javadoc:javadoc'
            }
        }
        stage('Site') {
            steps {
                sh 'mvn site'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        // 构建 Docker 镜像
        stage('Build Docker Image') {
            steps {
                script {
                    // 假设 Dockerfile 位于根目录
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }

        // 将 Docker 镜像上传到 Docker Hub
        stage('Upload Docker Image') {
            steps {
                script {
                    // 使用正确的凭证ID直接登录Docker Hub
                    docker.withRegistry('https://registry.hub.docker.com', 'b1f03bf0-7493-4a49-b5bb-fa7bfea95b96') {
                        // 推送镜像
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        // 可选：标记为 latest
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        // 运行 Docker 容器
        stage('Run Docker Containers') {
            steps {
                script {
                    // 运行三个容器，分别映射到端口 8082、8083 和 8084
                    [8082, 8083, 8084].each { port ->
                        def containerName = "teedy-${port}"
                        sh "docker stop ${containerName} || true"
                        sh "docker rm ${containerName} || true"
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
                            "--name ${containerName} -d -p ${port}:8080"
                        )
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
