pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub...'
                git branch: 'master', 
                    url: 'https://github.com/DEVENDRA9724/medicure-healthcare.git'
                echo '✅ Code checked out successfully!'
            }
        }
        
        stage('Build & Test') {
            steps {
                echo 'Running Maven tests...'
                sh 'mvn clean test'
                echo '✅ All tests passed!'
            }
            post {
                failure {
                    echo '❌ Tests failed!'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn package -DskipTests'
                echo '✅ JAR file created!'
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t medicure-app:latest .'
                echo '✅ Docker image built!'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying container...'
                sh 'docker stop medicure-container || true'
                sh 'docker rm medicure-container || true'
                sh 'docker run -d -p 8080:8080 --name medicure-container medicure-app:latest'
                echo '✅ Application deployed successfully!'
            }
        }
    }
    
    post {
        success {
            echo '🎉 Pipeline SUCCESS! Application is running!'
        }
        failure {
            echo '💥 Pipeline FAILED! Check the logs above.'
        }
    }
}
