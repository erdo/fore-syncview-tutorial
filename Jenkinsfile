//https://www.jenkins.io/doc/book/pipeline/pipeline-best-practices/


//see here for all the available environment variables
// ${YOUR_JENKINS_URL}/pipeline-syntax/globals#env

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
  tools {
    maven 'apache-maven-1.0.1'
  }
  environment {
    example_key = 'example value'
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '7'))
    retry(3)
    timeout(time: 200, unit: 'MINUTES')
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
        timeout(time: 5, unit: 'MINUTES')
      }
    }
    stage("build") {
      steps {
        sh './gradlew assembleDebug'
        //sh "./gradlew clean assemble${flavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
      }
      options {
        timeout(time: 30, unit: 'MINUTES')
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
        }
        stage("unit-test") {
          steps {
            //gradle 'clean test'
            //gradle 'assembleDebug'
            sh "./gradlew test"
            //sh 'make check || true'
            //junit '**/target/*.xml'
          }
        }
        stage("ui-test") {
          steps {
            sh 'mvn compile'
          }
        }
      }
      options {
        timeout(time: 100, unit: 'MINUTES')
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
        timeout(time: 30, unit: 'MINUTES')
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
