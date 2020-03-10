<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
-->
# custom-tomcat9-maven-plugin
A Custom Maven plugin for Tomcat 9

Using and updating existing Code/Modules from the existing [Tomcat Maven Plugin](https://github.com/apache/tomcat-maven-plugin) 

Goal is just to have a working Tomcat Maven Plugin that runs using Tomcat 9 version artifacts and is accessible from maven central.

Placing under my namespace so no one thinks it's an official Apache release, though package/class names will remain the same.

Changes are marked as required by License and broader changes explanation in the Changes.md

# Original Readme (Note that much of this is now invalid by my changes)

Build Apache Tomcat Maven Plugin
--------------------------------
To build this project you must Apache Maven at least 2.2.1 .
mvn clean install will install the mojos without running integration tests.
As there are some hardcoded integration tests with http port 1973, ajp 2001 and 2008, you could have some port allocation issues (if you don't know why those values ask olamy :-) )
mvn clean install -Prun-its will run integration tests too: to override the default used http port you can use -Dits.http.port= -Dits.ajp.port=

Snapshots deployment
---------------------
To deploy a snaphot version to https://repository.apache.org/content/repositories/snapshots/, you must run : mvn clean deploy .
Note you need some configuration in ~/.m2/settings.xml:
    <server>
      <id>apache.snapshots.https</id>
      <username>your asf id</username>
      <password>your asf paswword</password>
    </server

NOTE: a Jenkins job deploys SNAPSHOT automatically https://builds.apache.org/job/TomcatMavenPlugin-mvn3.x/.
So no real need to deploy manually, just commit and Jenkins will do the job for you.

Site deployment
-----------------

Checkstyle: this project uses the Apache Maven checkstyle configuration for ide codestyle files see http://maven.apache.org/developers/committer-environment.html .

Site: to test site generation, just run: mvn site. If you want more reporting (javadoc, pmd, checkstyle, jxr, changelog from jira entries), use: mvn site -Preporting.

To deploy site, use: mvn clean site-deploy scm-publish:publish-scm -Dusername=$svnuid -Dpassword=$svnpwd -Preporting . The site will be deployed to http://tomcat.apache.org/maven-plugin-trunk($svnuid is your asf id, $svnpwd is your asf password)

When releasing deploy with -Psite-release

Releasing
----------
For release your ~/.m2/settings.xml must contains :

    <server>
      <id>apache.releases.https</id>
      <username>asf id</username>
      <password>asf password</password>
    </server>

And run: mvn release:prepare release:perform -Dusername= -Dpassword=   (username/password are your Apache svn authz)

Test staged Tomcat artifacts
----------------------------
To test staging artifacts for a vote process.
* activate a profile: tc-staging
* pass staging repository as parameter: -DtcStagedReleaseUrl=
* pass tomcat version as parameter: -Dtomcat7Version=

Sample for tomcat8 artifacts: mvn clean install -Prun-its -Ptc-staging -DtcStagedReleaseUrl=stagingrepositoryurl -Dtomcat8Version=8.x

Sample for tomcat7 artifacts: mvn clean install -Prun-its -Ptc-staging -DtcStagedReleaseUrl=stagingrepositoryurl -Dtomcat7Version=7.x