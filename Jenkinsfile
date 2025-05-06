pipeline {
    agent any

    environment {
        // 定义环境变量
        // Jenkins 凭据配置
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials') // 存储在 Jenkins 中的 Docker Hub 凭据 ID
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
                    // 登录 Docker Hub
                    docker.withRegistry('https://registry.hub.docker.com', 'DOCKER_HUB_CREDENTIALS') {
                        // 推送镜像
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        // 可选：标记为 latest
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        // 运行 Docker 容器
        stage('Run Docker Container') {
            steps {
                script {
                    // 如果容器已存在，则停止并删除
                    sh 'docker stop teedy || true'
                    sh 'docker rm teedy || true'

                    // 运行容器
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
                        '--name teedy -d -p 8081:8080'
                    )

                    // 可选：列出所有运行的容器
                    sh 'docker ps --filter "name=teedy"'
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
