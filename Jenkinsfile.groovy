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
                    docker run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} \
                    -e MYSQL_USER=${MYSQL_USERNAME} -e MYSQL_PASSWORD=${MYSQL_PASSWORD} \
                    -e MYSQL_DATABASE=social -p 3306:3306 mysql:8
                    """
                }
            }
        }

        stage('Build Spring Boot Application') {
            steps {
                sh 'chmod +x ./mvnw'
                sh """
    ./mvnw clean package -Dspring.datasource.username=${MYSQL_USERNAME} \
    -Dspring.datasource.password=${MYSQL_PASSWORD} \
    -Djwt.secret=${JWT_SECRET} \
    -Dspring.mail.username=${EMAIL_USERNAME} \
    -Dspring.mail.password=${EMAIL_PASSWORD}
"""

                //sh './mvnw clean package -Dspring.datasource.username=**** -Dspring.datasource.password=**** -Djwt.secret=**** -Dspring.mail.username=**** -Dspring.mail.password=****\n'



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
