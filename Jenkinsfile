//some jenkins pipeline best practices
//https://www.jenkins.io/doc/book/pipeline/pipeline-best-practices/

//see here for all the available environment variables
//[YOUR_JENKINS_URL]/pipeline-syntax/globals#env

//recommended IDE for editing this file is VSCode with Jenkins Pipeline Linter Connector plugin
//https://marketplace.visualstudio.com/items?itemName=janjoerke.jenkins-pipeline-linter-connector
//setup from within VS as follows:
//crubURL = http://<your_jenkins_server:port>/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,%22:%22,//crumb)
//linterURL = http://<your_jenkins_server:port>/pipeline-model-converter/validate
//password = jenkins API token
//user = jenkins user ID (without :token, or :api appended)
// to use -> shift cmd p -> select Validate Jenkins

pipeline {

  //agent { docker { image 'node:6.3' } }
  //agent {
  //  label "android"
  //}
  agent any

  triggers {
    cron('*/5 * * * *')
  }

  environment {
    EXAMPLE_KEY = 'example value'
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '7'))
    timeout(time: 20, unit: 'MINUTES')
  }
  parameters {
    string(name: 'teritory', defaultValue: 'GB', description: 'Teritory')
  }

  stages {
    stage("build") {
      steps {
        echo "Running build ${env.BUILD_ID} on ${env.JENKINS_URL} for territory ${params.teritory}"
        sh "./gradlew clean"
        sh './gradlew --stacktrace assembleDebug'
      }
      options {
        timeout(time: 5, unit: 'MINUTES')
      }
    }
    stage("parallel-checks") {
      failFast false
      parallel {
        stage("linter") {
            when {
              not {
          			branch 'master';
          		}
            }
          steps {
            sh './gradlew :app:lintDebug'
          }
          options {
            timeout(time: 2, unit: 'MINUTES')
          }
        }
        stage("unit-test") {
          steps {
            sh "./gradlew testDebug"
          }
          options {
            timeout(time: 5, unit: 'MINUTES')
          }
        }
        stage("ui-test") {
          steps {
            sh 'mvn compile'
          }
          options {
            timeout(time: 10, unit: 'MINUTES')
            retry(3)
          }
        }
      }
      options {
        timeout(time: 15, unit: 'MINUTES')
      }
    }
  }

  post {
      success {
          echo 'it worked'
      }
      failure {
          echo 'it didn work'
        //  mail to: "XXXXX@email.com", subject:"FAIL: ${env.BUILD_ID}", body: "din work"
      }
  }

}
