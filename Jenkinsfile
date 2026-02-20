pipeline {
    agent any

    triggers { githubPush() }

    environment {
        // --- SonarQube ---
        SONAR_INSTANCE = 'SonarQube'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'cargo-tracker'
        SONAR_PROJECT_NAME = 'Store'
        // ⚠️ Ne mets PAS le token en clair. Mets-le dans Jenkins Credentials.
        SONAR_TOKEN_CRED = 'sonar-token' // type: Secret text

        // --- Docker / Registry ---
        REGISTRY = 'registry.intra.local'           // <-- change si DockerHub: 'docker.io'
        IMAGE_REPO = "${REGISTRY}/estore/store"     // <-- repo image
        DOCKER_CRED = 'docker-registry-cred'        // type: Username/Password

        // --- Kubernetes ---
        KUBECONFIG_CRED = 'kubeconfig-jenkins'      // type: Secret file
        K8S_NAMESPACE = 'estore'
        K8S_DEPLOYMENT = 'estore'
        K8S_CONTAINER = 'estore'                    // nom du container dans le deployment

        // Tag image
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/sim1574264/Store.git'
            }
        }

        stage('Build + SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: "${SONAR_TOKEN_CRED}", variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv("${SONAR_INSTANCE}") {
                        bat """
                            mvnw.cmd -B -e clean verify ^
                              org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar ^
                              -Dsonar.projectKey=%SONAR_PROJECT_KEY% ^
                              -Dsonar.projectName="%SONAR_PROJECT_NAME%" ^
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
                              -Dsonar.host.url=%SONAR_HOST_URL% ^
                              -Dsonar.token=%SONAR_TOKEN%
                        """
                    }
                }
            }
        }

        stage('Package (WAR)') {
            steps {
                bat """mvnw.cmd -B -DskipTests package"""
                bat """dir target\\*.war"""
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    env.IMAGE = "${env.IMAGE_REPO}:${env.IMAGE_TAG}"
                }
                bat """
                    docker build -t %IMAGE% .
                    docker images %IMAGE%
                """
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CRED}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        echo %DOCKER_PASS% | docker login %REGISTRY% -u %DOCKER_USER% --password-stdin
                        docker push %IMAGE%
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: "${KUBECONFIG_CRED}", variable: 'KUBECONFIG')]) {
                    bat """
                        kubectl -n %K8S_NAMESPACE% set image deployment/%K8S_DEPLOYMENT% %K8S_CONTAINER%=%IMAGE%
                        kubectl -n %K8S_NAMESPACE% rollout status deployment/%K8S_DEPLOYMENT%
                        kubectl -n %K8S_NAMESPACE% get pods
                    """
                }
            }
        }
    }

    post {
        success { echo "✅ Pipeline OK: image=%IMAGE% déployée sur K8s." }
        failure { echo "❌ Échec: vérifie les logs Jenkins (Sonar/Docker/K8s)." }
        always {
            // Nettoyage optionnel pour éviter d’accumuler des images sur l’agent
            bat """
                docker logout %REGISTRY%
            """
        }
    }
}

