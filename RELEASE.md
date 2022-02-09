# Absa Inception Framework Release Guide

## Getting started

### Setup a JIRA account to access the Sonatype OSSRH (OSS Repository Hosting) for Maven Central

Complete the following steps to setup a JIRA account to access the Sonatype OSSRH (OSS Repository Hosting) for Maven Central.

1. Create a JIRA account to access the Sonatype OSSRH (OSS Repository Hosting) for Maven Central using this [link](https://issues.sonatype.org/secure/Signup!default.jspa).
2. Request access to the *africa.absa* group by adding a comment to the existing Sonatype OSSRH (OSS Repository Hosting) JIRA [ticket](https://issues.sonatype.org/browse/OSSRH-72738) for the group or raise a new JIRA ticket requesting access.

   **NOTE:*** This must be done by someone who already has access to the *africa.absa* group.

### Setup a Node Package Manager (NPM) account

Complete the following steps to setup a Node Package Manager account.

1. Sign up for a Node Package Manager (NPM) account using this [link](https://www.npmjs.com/signup).
2. Add the Node Package Manager (NPM) account to the **absaoss** organization under the **developers** team.


## Releasing on MacOS

### Prepare for a release on MacOS

Complete the following steps to setup your environment to release on MacOS.

1. Install the Xcode Command Line Tools by executing the following command in a Terminal
   window.
   ```
   xcode-select --install
   ```
2. Install Homebrew by executing the following command in a Terminal window.
   ```
   /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
   ```
3. Install GPG by executing the following commands in a Terminal window.
   ```
   brew install gpg
   ```
4. Ensure the GPG_TTY environment variable is set by adding it to the *~/.zshrc* file.
   ```
   export GPG_TTY=$(tty)
   ```
5. Complete the following steps to setup a new personal PGP key if one does not exist:
   1. Generate the 3072 bit RSA and RSA PGP key, which does not expire, by executing the following command in a Terminal window.
      ```
      gpg --full-gen-key
   
      ```
   2. Retrieve the details of the key by executing the following command in a Terminal window.
      ```
      gpg --list-secret-keys --keyid-format=long
      ```
   3. Push the key to the PGP servers by executing the following commands in a Terminal window.
      ```
      gpg --send-key KEYID
      gpg --keyserver pgp.mit.edu  --send-key KEYID
      gpg --keyserver keys.openpgp.org  --send-key KEYID
   
      e.g.
   
      gpg --send-key 48598D3BF971B3558DF0D099552E409B73B14E75
      gpg --keyserver pgp.mit.edu  --send-key 48598D3BF971B3558DF0D099552E409B73B14E75
      gpg --keyserver keys.openpgp.org  --send-key 48598D3BF971B3558DF0D099552E409B73B14E75
      ```
   4. Export the public and private GPP key if required by executing the following commands in a Terminal window.
      ```
      gpg --output KEY_NAME-public.pgp --armor --export REPLACE_WITH_EMAIL_ADDRESS_FOR_KEY
      gpg --output KEY_NAME-private.pgp --armor --export-secret-key REPLACE_WITH_EMAIL_ADDRESS_FOR_KEY
      ```
   5. Set the GPG key for Git globally if required or locally for a particular project by executing the following commands in a Terminal window.
      ```
      git config --global user.signingkey KEY_ID
  
      OR
   
      git config --local user.signingkey KEY_ID
      ``` 
6. Complete the following steps to import an existing personal PGP key if one already exists:
   1. Import the PGP keys by executing the following commands in a Terminal window.
      ```
      gpg --import YOUR_PGP_PUBLIC_KEY.gpg
      gpg --import YOUR_PGP_PRIVATE_KEY.gpg
      ```
   2. Confirm the PGP key has been imported successfully by executing the following command in a Terminal window.
      ```
      gpg --list-secret-keys --keyid-format=long
      ```
   3. Set the GPG key for Git globally if required or locally for a particular project by executing the following commands in a Terminal window.
      ```
      git config --global user.signingkey KEY_ID
  
      OR
   
      git config --local user.signingkey KEY_ID
      ```
7. Add the required configuration to the *~/.m2/settings.xml* file.
   1. Add the Jira credentials for the Sonatype OSSRH (OSS Repository Hosting) for Maven Central.
      ```
      <settings>
        <servers>
          <server>
            <id>ossrh</id>
            <username>REPLACE_WITH_OSSRH_USERNAME</username>
            <password>REPLACE_WITH_OSSRH_PASSWORD</password>
          </server>
        </servers>
      </settings>
      ```
   2. Add the Sonatype OSSRH (OSS Repository Hosting) profile.
      ```
      <settings>
        <profiles>
          <profile>
            <id>ossrh</id>
            <activation>
              <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
              <gpg.passphrase>REPLACE_WITH_PGP_PASSPHRASE</gpg.passphrase>
            </properties>
          </profile>
        </profiles>
      </settings>
      ```
   
### Create a release on MacOS

Complete the following steps to create a release of the Absa Inception Framework on MacOS.

1. If you want to use a different e-mail address and PGP key when creating a release of the Absa Inception Framework complete the following steps:
   1. Set the local e-mail to use for the Absa Inception Framework Git repository by executing the following commands in a Terminal window under the *inception* project cloned from Git.
      ```
      git config --local user.email 'REPLACE_WITH_YOUR_EMAIL_ADDRESS'
      
      git config --local --get user.email
      ```
   2. Set the local signing key to use for the Absa Inception Framework Git repository by executing the following commands in a Terminal window under the *inception* project cloned from Git.
      ```
      git config --local user.signingkey KEY_ID
      
      git config --local --get user.signingkey
      ```
2. If changes have been made to the Angular frontend components for the Absa Inception Framework, publish a new release of the **ngx-inception** library to NPM. 
   1. Edit the *inception/src/inception-angular/package.json* file and update the release version using the semantic versioning format X.Y.Z where X = major version number, Y = minor version number and Z = patch version number, e.g. 1.2.6.
   2. Edit the *inception/src/inception-angular/projects/ngx-inception/ng-package.json* and update the version number to match the *package.json* file.
   3. Build the library by executing the following commands in a Terminal window under the *inception/src/inception-angular* directory.
      ```
      rm -rf node_modules
      rm -rf dist
      npm i
      ng build
      ```
   4. Login to NPM and publish the **ngx-inception** library to NPM by executing the following commands in a Terminal window under the *inception/src/inception-angular/dist/@absaoss/ngx-inception* directory.
      ```
      npm login
      
      npm publish
      ```
    5. Commit the changes made while creating the new release of the **ngx-inception** library to the *inception* project.
3. Prepare the release by executing the following command in a Terminal window under the *inception/src* directory.

   **NOTE:** 
   Enter the release version using the semantic versioning format X.Y.Z where X = major version number, Y = minor version number and Z = patch version number, e.g. 1.2.6.
   
   Use a SCM release tag based on the release version prefixed with a 'v', e.g. v1.2.6.
   
   Increment the major, minor, or patch version number to create the new development version with the SNAPSHOT suffix, e.g. 1.2.7-SNAPSHOT
   ```
   mvn release:prepare
   ```
4. Perform the release by executing the following command in a Terminal window under the *inception/src* directory.
   ```
   mvn release:perform
   ```
5. Perform the release on Maven Central.
   1. Log into **https://s01.oss.sonatype.org/** with your Sonatype OSSRH (OSS Repository Hosting) for Maven Central username and password.
   2. Click on the **Staging Repositories** link in the left-hand menu.
   3. Select the automatically generated staging repository, e.g. africaabsa-1003.
   4. Click **Close** and provide a description of *Release vX.Y.Z* using the appropriate version, e.g. Release v1.2.6.
   5. Wait for the close to complete.
   6. Select the automatically generated staging repository, e.g. africaabsa-1003.
   7. Click **Release** and provide a description of *Release vX.Y.Z* using the appropriate version, e.g. Release v1.2.6.
6. Push the tags to the remote repository by executing the following command in a Terminal window under the *inception* directory.
   ```
   git push --tags
   ```
7. Push the changes for the release to the remote repository by executing the following command in a Terminal window under the *inception* directory.
   ```
   git push origin main
   ```


## Releasing on Windows

### Prepare for a release on Windows

Complete the following steps to setup your environment to release on Windows.

1. Download and install Gpg4win from [gnupg.org](https://gnupg.org), including the Kleopatra keymanager for OpenPGP and GpgEX shell extension.
2. Complete the following steps to setup a new personal PGP key if one does not exist:
   1. Generate the 3072 bit RSA and RSA PGP, which does not expire key, by executing the following command in a command prompt.
      ```
      gpg --full-gen-key
   
      ```
   2. Retrieve the details of the key by executing the following command in a command prompt.
      ```
      gpg --list-secret-keys --keyid-format=long
      ```
   3. Push the key to the PGP servers by executing the following commands in a command prompt.
      ```
      gpg --send-key KEYID
      gpg --keyserver pgp.mit.edu  --send-key KEYID
      gpg --keyserver keys.openpgp.org  --send-key KEYID
   
      e.g.
   
      gpg --send-key 48598D3BF971B3558DF0D099552E409B73B14E75
      gpg --keyserver pgp.mit.edu  --send-key 48598D3BF971B3558DF0D099552E409B73B14E75
      gpg --keyserver keys.openpgp.org  --send-key 48598D3BF971B3558DF0D099552E409B73B14E75
      ```
   4. Export the public and private GPP key if required by executing the following commands in a command prompt.
      ```
      gpg --output KEY_NAME-public.pgp --armor --export REPLACE_WITH_EMAIL_ADDRESS_FOR_KEY
      gpg --output KEY_NAME-private.pgp --armor --export-secret-key REPLACE_WITH_EMAIL_ADDRESS_FOR_KEY
      ```
   5. Set the GPG key for Git globally if required or locally for a particular project by executing the following commands in a command prompt.
      ```
      git config --global user.signingkey KEY_ID
  
      OR
   
      git config --local user.signingkey KEY_ID
      ``` 
6. Complete the following steps to import an existing personal PGP key if one already exists:
   1. Import the PGP keys by executing the following commands in a command prompt.
      ```
      gpg --import YOUR_PGP_PUBLIC_KEY.gpg
      gpg --import YOUR_PGP_PRIVATE_KEY.gpg
      ```
   2. Confirm the PGP key has been imported successfully by executing the following command in a command prompt.
      ```
      gpg --list-secret-keys --keyid-format=long
      ```
   3. Set the GPG key for Git globally if required or locally for a particular project by executing the following commands in a command prompt.
      ```
      git config --global user.signingkey KEY_ID
  
      OR
   
      git config --local user.signingkey KEY_ID
      ```
7. Add the required configuration to the *YOUR_HOME_DIRECTORY/.m2/settings.xml* file.
   1. Add the Jira credentials for the Sonatype OSSRH (OSS Repository Hosting) for Maven Central.
      ```
      <settings>
        <servers>
          <server>
            <id>ossrh</id>
            <username>REPLACE_WITH_OSSRH_USERNAME</username>
            <password>REPLACE_WITH_OSSRH_PASSWORD</password>
          </server>
        </servers>
      </settings>
      ```
   2. Add the Sonatype OSSRH (OSS Repository Hosting) profile.
      ```
      <settings>
        <profiles>
          <profile>
            <id>ossrh</id>
            <activation>
              <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
              <gpg.passphrase>REPLACE_WITH_PGP_PASSPHRASE</gpg.passphrase>
            </properties>
          </profile>
        </profiles>
      </settings>
      ```
   
### Create a release on Windows

Complete the following steps to create a release of the Absa Inception Framework on Windows.

1. If you want to use a different e-mail address and PGP key when creating a release of the Absa Inception Framework complete the following steps:
   1. Set the local e-mail to use for the Absa Inception Framework Git repository by executing the following commands in a command prompt under the *inception* project cloned from Git.
      ```
      git config --local user.email 'REPLACE_WITH_YOUR_EMAIL_ADDRESS'
      
      git config --local --get user.email
      ```
   2. Set the local signing key to use for the Absa Inception Framework Git repository by executing the following command in a command prompt under the *inception* project cloned from Git.
      ```
      git config --local user.signingkey KEY_ID
      
      git config --local --get user.signingkey
      ```
2. If changes have been made to the Angular frontend components for the Absa Inception Framework, publish a new release of the **ngx-inception** library to NPM. 
   1. Edit the *inception/src/inception-angular/package.json* file and update the release version using the semantic versioning format X.Y.Z where X = major version number, Y = minor version number and Z = patch version number, e.g. 1.2.6.
   2. Edit the *inception/src/inception-angular/projects/ngx-inception/ng-package.json* and update the version number to match the *package.json* file.
   3. Build the library by executing the following commands in a command prompt under the *inception/src/inception-angular* directory.
      ```
      del /s /q node_modules
      del /s /q dist
      npm i
      ng build
      ```
   4. Login to NPM and publish the **ngx-inception** library to NPM by executing the following commands in a command prompt under the *inception/src/inception-angular/dist/@absaoss/ngx-inception* directory.
      ```
      npm login
      
      npm publish
      ```
   5. Commit the changes made while creating the new release of the **ngx-inception** library to the *inception* project.   
3. Prepare the release by executing the following command in a command prompt under the *inception/src* directory.

   **NOTE:** 
   Enter the release version using the semantic versioning format X.Y.Z where X = major version number, Y = minor version number and Z = patch version number, e.g. 1.2.6.
   
   Use a SCM release tag based on the release version prefixed with a 'v', e.g. v1.2.6.
   
   Increment the major, minor, or patch version number to create the new development version with the SNAPSHOT suffix, e.g. 1.2.7-SNAPSHOT
   ```
   mvn release:prepare
   ```
4. Perform the release by executing the following command in a command prompt under the *inception/src* directory.
   ```
   mvn release:perform
   ```
5. Perform the release on Maven Central.
   1. Log into **https://s01.oss.sonatype.org/** with your Sonatype OSSRH (OSS Repository Hosting) for Maven Central username and password.
   2. Click on the **Staging Repositories** link in the left-hand menu.
   3. Select the automatically generated staging repository, e.g. africaabsa-1003.
   4. Click **Close** and provide a description of *Release vX.Y.Z* using the appropriate version, e.g. Release v1.2.6.
   5. Wait for the close to complete.
   6. Select the automatically generated staging repository, e.g. africaabsa-1003.
   7. Click **Release** and provide a description of *Release vX.Y.Z* using the appropriate version, e.g. Release v1.2.6.
6. Push the tags to the remote repository by executing the following command in a command prompt under the *inception* directory.
   ```
   git push --tags
   ```
7. Push the changes for the release to the remote repository by executing the following command in a command prompt under the *inception* directory.
   ```
   git push origin main
   ```
