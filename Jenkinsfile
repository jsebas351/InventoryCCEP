pipeline {
    agent any
    
    tools {
        jdk 'JDK17'
        maven 'Maven3'
        nodejs 'NodeJS18'
    }
    
    environment {
        DOCKER_IMAGE_BACKEND = 'inventoryccep-backend'
        DOCKER_IMAGE_FRONTEND = 'inventoryccep-frontend'
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }
        
        stage('Validate Project Structure') {
            steps {
                echo 'Validating project structure...'
                script {
                    if (!fileExists('backendCCEP/pom.xml')) {
                        error('Backend pom.xml not found')
                    }
                    if (!fileExists('frontend-ccep/package.json')) {
                        error('Frontend package.json not found')
                    }
                }
            }
        }
        

        stage('Run Backend') {
            steps {
                echo 'Starting Spring Boot backend...'
                dir('backendCCEP') {
                    script {
                        if (isUnix()) {
                            sh "docker build -t ${env.DOCKER_IMAGE_BACKEND} ."
                        } else {
                            bat "docker build -t ${env.DOCKER_IMAGE_BACKEND} ."
                        }
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo 'Starting React frontend...'
                dir('frontend-ccep') {
                    script {
                        if (isUnix()) {
                            sh 'npm install'
                            sh 'npm run build'
                        } else {
                            bat 'npm install'
                            bat 'npm run build'
                        }
                    }
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                echo 'Running security scans...'
                // Add security scanning tools here if needed
                script {
                    // Example: OWASP Dependency Check
                    dir('backendCCEP') {
                        if (isUnix()) {
                            sh './mvnw org.owasp:dependency-check-maven:check || true'
                        } else {
                            bat 'mvnw.cmd org.owasp:dependency-check-maven:check || exit 0'
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Development') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Deploying to development environment...'
                script {
                    if (isUnix()) {
                        sh 'docker-compose -f docker-compose.dev.yml down || true'
                        sh 'docker-compose -f docker-compose.dev.yml up -d'
                    } else {
                        bat 'docker-compose -f docker-compose.dev.yml down || exit 0'
                        bat 'docker-compose -f docker-compose.dev.yml up -d'
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'staging'
            }
            steps {
                echo 'Deploying to staging environment...'
                script {
                    if (isUnix()) {
                        sh 'docker-compose -f docker-compose.staging.yml down || true'
                        sh 'docker-compose -f docker-compose.staging.yml up -d'
                    } else {
                        bat 'docker-compose -f docker-compose.staging.yml down || exit 0'
                        bat 'docker-compose -f docker-compose.staging.yml up -d'
                    }
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                echo 'Deploying to production environment...'
                input message: 'Deploy to production?', ok: 'Deploy',
                      submitterParameter: 'DEPLOYER'
                script {
                    if (isUnix()) {
                        sh 'docker-compose -f docker-compose.prod.yml down || true'
                        sh 'docker-compose -f docker-compose.prod.yml up -d'
                    } else {
                        bat 'docker-compose -f docker-compose.prod.yml down || exit 0'
                        bat 'docker-compose -f docker-compose.prod.yml up -d'
                    }
                }
            }
        }
        
    }
    
    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
            
        }
        failure {
            echo 'Pipeline failed!'
            
        }
        unstable {
            echo 'Pipeline is unstable!'
        }
    }
}