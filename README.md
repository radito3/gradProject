# Cloud Foundry Application Package Manager
[![Build Status](https://travis-ci.org/radito3/gradProject.svg?branch=v2)](https://travis-ci.org/radito3/gradProject)
[![HitCount](http://hits.dwyl.io/radito3/gradProject.svg)](http://hits.dwyl.io/radito3/gradProject)

## Setup
1. Install the Cloud Foundry command line interface
 - [Installing cf cli](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)

2. Sign into a Pivotal account
 - Create or have a Pivotal account
 - Use the command line interface to log in and designate an org and space (if not specified)
 ```
 cf login
 ```

3. Run the scripts to install the Application Package Manager
 - Make sure to make the files in the scripts/ folder executable
 - If they aren't by default, type
```
chmod 755 ./scripts/installApp
chmod 755 ./scripts/uploadPlugin
```
 - After that, type
```
./scripts/installApp
./scripts/uploadPlugin
```
 - The command prompt will ask you whether you want to install the plugin.
   Type `y`.
 - You are ready to use the package manager!
 
## Usage

Install a package
```
cf install <package>
```

Update a package
```
cf update <package>
```

Delete a package
```
cf remove <package>
```

List installed apps
```
cf list-installed
```

List repository apps
```
cf list-repo
```

## Contribute 
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/radito3/gradProject/issues)

## Author
 * Rangel Ivanov - rangel.ivanov33@gmail.com
