// SPDX-License-Identifier: MIT
// Copyright (C) 2021 iris-GmbH infrared & intelligent sensors

def call() {

        matrix {
            axes {
                axis {
                    name 'MULTI_CONF'
                    values 'sc573-gen6', 'imx8mp-evk', 'qemux86-64-r1', 'qemux86-64-r2'
                }
                axis {
                    name 'IMAGES'
                    values 'irma6-deploy irma6-maintenance irma6-dev', 'irma6-test'
                }
                axis {
                    name 'SDK_IMAGE'
                    values 'irma6-maintenance', 'irma6-test'
                }
            }
            excludes {
                exclude {
                    axis {
                        name 'MULTI_CONF'
                        values 'sc573-gen6', 'imx8mp-evk'
                    }
                    axis {
                        name 'IMAGES'
                        values 'irma6-test'
                    }
                    axis {
                        name 'SDK_IMAGE'
                        values 'irma6-maintenance', 'irma6-test'
                    }
                }
                exclude {
                    axis {
                        name 'MULTI_CONF'
                        values 'sc573-gen6', 'imx8mp-evk'
                    }
                    axis {
                        name 'IMAGES'
                        values 'irma6-deploy irma6-maintenance irma6-dev'
                    }
                    axis {
                        name 'SDK_IMAGE'
                        values 'irma6-test'
                    }
                }
                exclude {
                    axis {
                        name 'MULTI_CONF'
                        values 'qemux86-64-r1', 'qemux86-64-r2'
                    }
                    axis {
                        name 'IMAGES'
                        values 'irma6-deploy irma6-maintenance irma6-dev', 'irma6-test'
                    }
                    axis {
                        name 'SDK_IMAGE'
                        values 'irma6-maintenance'
                    }
                }
                exclude {
                    axis {
                        name 'MULTI_CONF'
                        values 'qemux86-64-r1', 'qemux86-64-r2'
                    }
                    axis {
                        name 'IMAGES'
                        values 'irma6-deploy irma6-maintenance irma6-dev'
                    }
                    axis {
                        name 'SDK_IMAGE'
                        values 'irma6-test'
                    }
                }
            }
            stages {
                stage('Develop: Build Firmware Artifacts') {
                    steps {
                        awsCodeBuild buildSpecFile: 'buildspecs/build_firmware_images_develop.yml',
                            projectName: 'iris-devops-kas-large-amd-codebuild',
                            credentialsType: 'keys',
                            downloadArtifacts: 'false',
                            region: 'eu-central-1',
                            sourceControlType: 'project',
                            sourceTypeOverride: 'S3',
                            sourceLocationOverride: "${S3_BUCKET}/${JOB_NAME}/${GIT_COMMIT}/iris-kas-sources.zip",
                            artifactTypeOverride: 'S3',
                            artifactLocationOverride: "${S3_TEMP_BUCKET}",
                            artifactPathOverride: "${JOB_NAME}/${GIT_COMMIT}",
                            artifactNamespaceOverride: 'NONE',
                            artifactNameOverride: "${MULTI_CONF}-deploy.zip",
                            artifactPackagingOverride: 'ZIP',
                            secondaryArtifactsOverride: """[
                                {
                                    "artifactIdentifier": "sources",
                                    "type": "S3",
                                    "location": "${S3_TEMP_BUCKET}",
                                    "path": "${JOB_NAME}/${GIT_COMMIT}",
                                    "namespaceType": "NONE",
                                    "name": "${MULTI_CONF}-sources.zip",
                                    "overrideArtifactName": "true",
                                    "packaging": "ZIP"
                                }
                            ]""",
                            envVariables: """[
                                { MULTI_CONF, $MULTI_CONF },
                                { IMAGES, $IMAGES },
                                { JOB_NAME, $JOB_NAME },
                                { GIT_BRANCH, $REAL_GIT_BRANCH }
                            ]"""
                    }
                }
            }
        }
    }

}
