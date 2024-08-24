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
        stage('Build') {
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

        stage('Commit Tag') {
            when {
                branch 'main'
                expression {
                    checkJdaVersionChange()
                }
            }

            steps {
                script {
                    commitTag()
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

def checkJdaVersionChange() {
    return sh(script: 'git diff HEAD~1 HEAD -- gradle.properties | grep "jda_version"', returnStatus: true) == 0
}

def commitTag() {
    def version = readFile('gradle.properties').readLines().find { it.startsWith('jda_version') }.split('=')[1].trim()
    def tagName = "jda-${version}"
    sh "git tag ${tagName}"
    sh "git push origin ${tagName}"
}