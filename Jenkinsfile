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

//  environment {
//    IMAGE = 'registry.gitlab.com/XXXXX/bible-server'
//    DOCKER_REGISTRY_CREDENTIALS = credentials('DOCKER_REGISTRY_CREDENTIALS')
//  }

  triggers {
    cron('H */4 * * 1-5')
  }
  //tools {
   // maven 'apache-maven-1.0.1'
  //}
  environment {
    example_key = 'example value'
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '7'))
    retry(3)
    timeout(time: 20, unit: 'MINUTES')
  }
  parameters {
    string(name: 'teritory', defaultValue: 'GB', description: 'Teritory')
  }

  stages {
    stage("checkout") {
      steps {
        echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
        echo "Hello World ${params.teritory}"
        //sh 'mvn compile'
      }
      options {
        timeout(time: 2, unit: 'MINUTES')
      }
    }
    stage("build") {
      steps {
        sh './gradlew assembleDebug'
        //sh "./gradlew clean assemble${flavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
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
            sh './gradlew :app:lint'
          }
          options {
            timeout(time: 5, unit: 'MINUTES')
          }
        }
        stage("unit-test") {
          steps {
            //gradle 'clean test'
            //gradle 'assembleDebug'
            sh "./gradlew test"
            //sh 'make check || true'
            //junit '**/target/*.xml'
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
          }
        }
      }
      options {
        timeout(time: 15, unit: 'MINUTES')
      }
    }
    stage('deployment') {
      when {
        branch 'master'
      }
      steps {
        echo 'deployment'
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
        //  mail to: "XXXXX@gmail.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: "Noooo!"
      }
  }

}
