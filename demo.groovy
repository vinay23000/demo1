#!/usr/bin/env groovy
import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.net.URL
try {

node{
 stage('Checkout') {


 git 'https://github.com/vinay23000/appli.git'

 }
 stage('Build') {
 dir('') {
 sh 'mvn -B -V -U -e clean package'
 }
 }
 stage ('Email') {
 emailext attachLog: true, body: 'The status of the build can be obtained
from the build log attached', subject: 'The build update is ', to: 'some
email id'
}
stage('Deployment') {
 // Deployment
 script {
 echo "deployment"
 sh 'cp
/var/lib/jenkins/workspace/package_1/target/addressbook.war
/opt/tomcat/webapps/'
 }
 }
 stage('publish html report') {
 echo "publishing the html report"
 publishHTML([allowMissing: false, alwaysLinkToLastBuild:
false, keepAll: false, reportDir: '', reportFiles: 'index.html', reportName:
'HTML Report', reportTitles: ''])
 }
 stage('clean up') {
 echo "cleaning up the workspace"
 cleanWs()
 }
}// node
} // try end
finally {


(currentBuild.result != "ABORTED") && node("master") {
 // Send e-mail notifications for failed or unstable builds.
 // currentBuild.result must be non-null for this step to work.
 step([$class: 'Mailer',
 notifyEveryUnstableBuild: true,
 recipients: 'some email id',
 sendToIndividuals: true])
}
}

