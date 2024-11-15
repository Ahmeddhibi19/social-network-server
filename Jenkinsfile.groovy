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
                    docker run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=${env.MYSQL_PASSWORD} \
                    -e MYSQL_USER=${env.MYSQL_USERNAME} -e MYSQL_PASSWORD=${env.MYSQL_PASSWORD} \
                    -e MYSQL_DATABASE=social -p 3306:3306 mysql:8
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
                    echo "Running Maven command: mvn clean package -Dspring.datasource.username=${env.MYSQL_USERNAME} -Dspring.datasource.password=${env.MYSQL_PASSWORD} -Djwt.secret=${env.JWT_SECRET} -Dspring.mail.username=${env.EMAIL_USERNAME} -Dspring.mail.password=${env.EMAIL_PASSWORD}"
                }
                sh 'chmod +x ./mvnw'
                sh "./mvn clean package -Dspring.datasource.username=${env.MYSQL_USERNAME} -Dspring.datasource.password=${env.MYSQL_PASSWORD} -Djwt.secret=${env.JWT_SECRET} -Dspring.mail.username=${env.EMAIL_USERNAME} -Dspring.mail.password=${env.EMAIL_PASSWORD}"
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
