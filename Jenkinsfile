pipeline {
    agent any

    tools {
        jdk 'JDK 21 Temurin'
        gradle 'Gradle 8.10'
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Build Projects') {
            steps {
                sh 'gradle :fcs-core:build -x test'
            }

            stage('Build JDA and Fabric') {
                parallel {
                    stage('Build JDA') {
                        steps {
                            sh 'gradle :fcs-jda:build -x test'
                        }
                    }

                    stage('Build Fabric') {
                        steps {
                            sh 'gradle :fcs-fabric:build -x test'
                        }
                    }
                }
            }
        }

        stage('Test with Gradle') {
            steps {
                sh 'gradle test'
            }
        }

        stage('Deploy to Maven') {
            when {
                branch 'main'
            }

            parallel {
                stage('Deploy Core') {
                    steps {
                        script {
                            publishStage('Core')
                        }
                    }
                }

                stage('Deploy JDA') {
                    steps {
                        script {
                            publishStage('JDA')
                        }
                    }
                }

                stage('Deploy Fabric') {
                    steps {
                        script {
                            publishStage('Fabric')
                        }
                    }
                }
            }
        }
    }
}

def publishStage(projectName) {
    withCredentials([usernamePassword(credentialsId: 'frydae-maven-key', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh "gradle :fcs-${projectName.toLowerCase()}:publish -PfrydaeRepositoryUsername=$USERNAME -PfrydaeRepositoryPassword=$PASSWORD"
    }
}