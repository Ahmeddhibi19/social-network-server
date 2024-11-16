pipeline {
    agent any
    tools {
        jdk 'jdk17'
        maven 'maven'
        git 'Default'
    }

    environment {
        MYSQL_USERNAME = credentials('mysql-username')
        MYSQL_PASSWORD = credentials('mysql-password')
        JWT_SECRET = credentials('jwt-secret')
        EMAIL_USERNAME = credentials('email-username')
        EMAIL_PASSWORD = credentials('email-password')
        ANSIBLE_SERVER_IP = '20.106.202.45'  // IP of the Ansible server
        GITHUB_REPO = 'https://github.com/Ahmeddhibi19/manifestes.git'
    }

    triggers {
        // Trigger pipeline on changes pushed to the master branch
        githubPush()
    }
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'master', credentialsId: 'github-credentials-id', url: 'https://github.com/Ahmeddhibi19/social-network-server.git'
            }
        }

        stage('Start MySQL Container') {
            steps {
                script {
                    sh """
            docker run -d --name mysql-container \\
          -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} \\
          -e MYSQL_DATABASE=social \\
          -p 3306:3306 mysql:8

            """
                }
            }
        }
//        stage ('deb'){
//            steps {
//                script {
//                    // You can use the environment variables directly
//                    echo "MySQL Username: ${MYSQL_USERNAME}"
//                    //echo "MySQL Password: [MASKED]"  // Masking sensitive data in the logs
//                }
//            }
//        }


        stage('Build Spring Boot Application') {
            steps {
                script {
                    sh "docker logs mysql-container"
                    echo "Running Maven command: mvn clean package -Dspring.datasource.username=${env.MYSQL_USERNAME} -Dspring.datasource.password=${env.MYSQL_PASSWORD} -Djwt=${env.JWT_SECRET} -Dspring.mail.username=${env.EMAIL_USERNAME} -Dspring.mail.password=${env.EMAIL_PASSWORD}"
                    sh "mvn clean package -DskipTests"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t social-app:latest ."
                    sh "docker tag social-app:latest ahmeddhibi/social-app:latest"
                }
            }
        }
        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'dockerhub-credentials', url: 'https://index.docker.io/v1/']) {
                        sh "docker push ahmeddhibi/social-app:latest"
                    }
                }
            }
        }

        stage('Clone Manifests Repo on Ansible Server') {
            steps {
                script {
                    echo "Cloning the 'manifests' repo on the Ansible server..."
                    sh """
            ssh azureuser@${ANSIBLE_SERVER_IP} '
                git clone ${GITHUB_REPO} /home/azureuser/manifests || (cd /home/azureuser/manifests && git pull)
            '
            """
                }
            }
        }




    }

    post {
        always {
            // Cleanup Docker container after build
            script {
                sh "docker rm -f mysql-container || true"
            }
        }

        success {
            echo 'Build completed successfully.'
        }

        failure {
            echo 'Build failed.'
        }
    }
}
