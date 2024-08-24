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
        stage('Build with Gradle') {
            steps {
                sh 'gradle build -x test'
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
                        withCredentials([usernamePassword(credentialsId: 'frydae-maven-key', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh 'gradle :fcs-core:publish -PfrydaeRepositoryUsername=$USERNAME -PfrydaeRepositoryPassword=$PASSWORD'
                        }
                    }
                }

                stage('Deploy JDA') {
                    steps {
                        withCredentials([usernamePassword(credentialsId: 'frydae-maven-key', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh 'gradle :fcs-jda:publish -PfrydaeRepositoryUsername=$USERNAME -PfrydaeRepositoryPassword=$PASSWORD'
                        }
                    }
                }

                stage('Deploy Fabric') {
                    steps {
                        withCredentials([usernamePassword(credentialsId: 'frydae-maven-key', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh 'gradle :fcs-fabric:publish -PfrydaeRepositoryUsername=$USERNAME -PfrydaeRepositoryPassword=$PASSWORD'
                        }
                    }
                }
            }
        }
    }
}