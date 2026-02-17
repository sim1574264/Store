pipeline {
    agent any

    triggers { githubPush() }

    stages {
        stage('Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/sim1574264/Store.git'
            }
        }

        stage('Build + SonarCloud Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat """
                         mvnw.cmd -B -e clean verify ^
  org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar ^
  -Dsonar.projectKey=cargo-tracker ^
  -Dsonar.projectName="Store" ^
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.token=squ_bace32cced22fe7f2dbf8a350d5072f50b3d0764
                    """
                }
            }
        }

        // Optionnel: faire échouer le pipeline si Quality Gate KO
        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success { echo 'Build et analyse SonarCloud OK !' }
        failure { echo 'Échec du build / tests / analyse.' }
    }
}
