// SPDX-License-Identifier: MIT
// Copyright (C) 2021 iris-GmbH infrared & intelligent sensors

def call() {

    stage('Run QEMU Tests') {
        matrix {
            axes {
                axis {
                    name 'MULTI_CONF'
                    values 'qemux86-64-r1', 'qemux86-64-r2'
                }
            }
            stages {
                stage('Run QEMU Tests') {
                    steps {
                        awsCodeBuild buildSpecFile: 'buildspecs/qemu_tests.yml',
                            projectName: 'iris-devops-kas-large-amd-qemu-codebuild',
                            credentialsType: 'keys',
                            region: 'eu-central-1',
                            sourceControlType: 'project',
                            sourceTypeOverride: 'S3',
                            sourceLocationOverride: "${S3_BUCKET}/${JOB_NAME}/${GIT_COMMIT}/iris-kas-sources.zip",
                            secondarySourcesOverride: """[
                                {
                                    "type": "S3",
                                    "location": "${S3_TEMP_BUCKET}/${JOB_NAME}/${GIT_COMMIT}/${MULTI_CONF}-deploy.zip",
                                    "sourceIdentifier": "deploy"
                                }
                            ]""",
                            artifactTypeOverride: 'S3',
                            artifactLocationOverride: "${S3_TEMP_BUCKET}",
                            artifactPathOverride: "${JOB_NAME}/${GIT_COMMIT}",
                            artifactNamespaceOverride: 'NONE',
                            artifactNameOverride: "${MULTI_CONF}-reports.zip",
                            artifactPackagingOverride: 'ZIP',
                            downloadArtifacts: 'true',
                            envVariables: """[
                                { MULTI_CONF, $MULTI_CONF },
                                { GIT_TAG, $GIT_TAG }
                            ]"""
                    }
                    post {
                        always {
                            sh 'find .'
                            // add test reports to pipeline run
                            unzip zipFile: "${JOB_NAME}/${GIT_COMMIT}/${MULTI_CONF}-reports.zip", dir: "${MULTI_CONF}-reports"
                            junit "${MULTI_CONF}-reports/**/*.xml"
                        }
                    }
                }
            }
        }
    }

}
