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

        stage('Commit Tag') {
            when {
                branch 'main'
                expression {
                    checkVersionChange('jda') || checkVersionChange('fabric')
                }
            }

            steps {
                script {
                    commitTag('jda')
                    commitTag('fabric')
                }
            }
        }
    }
}

def checkVersionChange(projectName) {
    return sh(script: "git diff HEAD~1 HEAD -- gradle.properties | grep \"${projectName}_version\"", returnStatus: true) == 0
}

def commitTag(projectName) {
    def version = readFile('gradle.properties').readLines().find { it.startsWith("${projectName}_version") }.split('=')[1].trim()
    def tagName = "${projectName}-${version}"

    // Delete the tag if it exists locally and remotely
    sh "git tag -d ${tagName} || true"
    sh "git push origin --delete ${tagName} || true"

    // Create and push the new tag
    sh "git tag ${tagName}"
    sh "git push origin ${tagName}"
}