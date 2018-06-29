buildCount = env.DEFAULT_HISTORY_COUNT ?: "5"

pipeline {
    agent {
        node {
            label 'mac && java'
        }
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: buildCount))
        disableConcurrentBuilds()
        ansiColor('css')
    }

    stages {
        stage('Select') {
            parallel {
                stage('Checkout') {
                    when {
                        expression {
                            notIntegration()
                        }
                    }

                    steps {
                        checkout scm
                        seedReset()
                    }
                }

                stage('Integrate') {
                    when {
                        expression {
                            isIntegration()
                        }
                    }

                    steps {
                        echo "Execute integration"
                        stopUnless(isStartedBy("upstream"))
                    }
                }
            }
        }

        stage("Coverage, Analyze and Test") {
            when {
                expression {
                    notIntegration() && notRelease()
                }
            }

            steps {
                seedGrow("test")
                resetBuildState()
                echo "Build for test and analyze"
                sh """echo "Execute test"
                        ./gradlew cleanTest test --fail-fast jacocoTestReport
                        ./gradlew worksGatherReport"""
            }
        }

        stage("Publish CAT") {
            when {
                expression {
                    notIntegration() && notRelease()
                }
            }

            steps {
                echo "Publishing test and analyze result"

                jacoco execPattern: 'build/reports/jacoco/exec/root/*.exec', classPattern: 'plugin/build/classes/kotlin/main', sourcePattern: ''
                junit allowEmptyResults: true, testResults: 'build/reports/junit/xml/**/*.xml', healthScaleFactor: 100.0
//                checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'build/reports/checkstyle/**/*.xml', unHealthy: ''
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'build/reports/jacoco/html/root', reportFiles: 'index.html', reportName: 'Coverage Report', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'build/reports/junit/html/works-swift/default', reportFiles: 'index.html', reportName: 'Test Report', reportTitles: ''])

                codeCoverage()
            }
        }

//        stage("Build") {
//            when {
//                expression {
//                    notIntegration() && notFeatureBranch()
//                }
//            }
//
//            parallel {
//                stage("Snapshot") {
//                    when {
//                        expression {
//                            notRelease()
//                        }
//                    }
//
//                    steps {
//                        updateVersion()
//                        sh './gradlew clean worksGeneratePublication'
//                    }
//                }
//
//                stage("Release") {
//                    when {
//                        expression {
//                            isRelease()
//                        }
//                    }
//
//                    steps {
//                        sh """./gradlew clean test worksGeneratePublication"""
//                    }
//                }
//            }
//        }
//
//        stage("Compare") {
//            when {
//                expression {
//                    notIntegration() && notFeatureBranch()
//                }
//            }
//
//
//            parallel {
//                stage("Snapshot") {
//                    when {
//                        expression {
//                            notRelease()
//                        }
//                    }
//
//                    steps {
//                        echo "Compare snapshot"
//                        compareArtifact("snapshot", "integrate/snapshot", false)
//                    }
//                }
//
//                stage("Release") {
//                    when {
//                        expression {
//                            isRelease()
//                        }
//                    }
//
//                    steps {
//                        echo "Compare release"
//                        compareArtifact("release", "integrate/release", true)
//                    }
//                }
//            }
//        }
//
//        stage("Publish") {
//            when {
//                expression {
//                    doPublish()
//                }
//            }
//
//            parallel {
//                stage("Snapshot") {
//                    when {
//                        expression {
//                            notIntegration() && notRelease()
//                        }
//                    }
//
//                    steps {
//                        echo "Publishing snapshot"
//                        publish("snapshot")
//                    }
//                }
//
//                stage("Release") {
//                    when {
//                        expression {
//                            notIntegration() && isRelease()
//                        }
//                    }
//
//                    steps {
//                        echo "Publishing release"
//                        publish("release")
//                    }
//                }
//            }
//        }
    }

    post {
//        success {
//            notifyDownstream()
//        }
        failure {
            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'plugin/build/reports/tests/test', reportFiles: 'index.html', reportName: 'Failure Report', reportTitles: ''])
        }

        changed {
            slackSend """
                Job: ${GIT_URL} 
                Branch: ${env.GIT_BRANCH} 
                Build ${env.BUILD_DISPLAY_NAME} (<${env.BUILD_URL}|Open>)
                ---
                Running on: ${NODE_NAME}
                Build state: ${currentBuild.result == null ? "Success" : currentBuild.result}

                Commit#: ${GIT_COMMIT} by ${GIT_AUTHOR_NAME}
            """.stripIndent()
        }
    }
}

def resetBuildState() {
    currentBuild.result = "SUCCESS"
}

def updateVersion() {
    bintrayDownloadMatches repository: "mobilesolutionworks/snapshot",
            packageInfo: readYaml(file: 'plugin/module.yaml'),
            credential: "mobilesolutionworks.jfrog.org"

    def properties = readYaml(file: 'plugin/module.yaml')
    def incremented = versionIncrementQualifier()
    if (incremented != null) {
        properties.version = incremented
    } else {
        properties.version = properties.version + "-BUILD-1"
    }

    sh "rm plugin/module.yaml"
    writeYaml file: 'plugin/module.yaml', data: properties
}


def compareArtifact(String repo, String job, boolean download) {
    if (download) {
        bintrayDownloadMatches repository: "mobilesolutionworks/${repo}",
                packageInfo: readYaml(file: 'plugin/module.yaml'),
                credential: "mobilesolutionworks.jfrog.org"
    }

    def same = bintrayCompare repository: "mobilesolutionworks/${repo}",
            packageInfo: readYaml(file: 'plugin/module.yaml'),
            credential: "mobilesolutionworks.jfrog.org",
            path: "plugin/build/libs"

    if (fileExists(".jenkins/notify")) {
        sh "rm .jenkins/notify"
    }

    if (same) {
        echo "Artifact output is identical, no integration needed"
    } else {
        writeFile file: ".jenkins/notify", text: job
    }
}

def doPublish() {
    return fileExists(".jenkins/notify")
}

def notifyDownstream() {
//    if (fileExists(".notify")) {
//
//        def job = readFile file: ".notify"
//        def encodedJob = java.net.URLEncoder.encode(job, "UTF-8")
//
//        build job: "github/yunarta/works-controller-android/${encodedJob}", propagate: false, wait: false
//    }
}

def publish(String repo) {
    def who = env.JENKINS_WHO ?: "anon"
    if (who == "works") {
        bintrayPublish([
                credential: "mobilesolutionworks.jfrog.org",
                pkg       : readProperties(file: 'plugin/module.yaml'),
                repo      : "mobilesolutionworks/${repo}",
                src       : "plugin/build/libs"
        ])
    }
}

def codeCoverage() {
    withCredentials([[$class: 'StringBinding', credentialsId: "codecov-token", variable: "CODECOV_TOKEN"]]) {
        sh "curl -s https://codecov.io/bash | bash -s - -f build/reports/jacoco/xml/root/coverage.xml"
    }
}
