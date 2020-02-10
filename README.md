# mobergliuslefors CI
A simple (as in non-feature-complete), localhosted continuous integration service created for assignment 2 at KTH course DD2480 - spring of 2020. The service is to be used by hooking it up to a Github commit push webhook, and will run the scripts inside the `.dd.yml` config file and report the result back as a commit status update on Github. There are two main parts of the service; a build job written in Java and a frontend in React. The two parts are connected through a database with JSON objects as well as a `Jetty` server. The purpose of the frontend is to give more context to the commit status on Github, where the latter links to a corresponding web page with build logs etc.

## Getting Started
Follow these instructions to set up the service in a development environment. This setup will let you connect to _one_ repository on github.

### Prerequisites
To run the build job, database and server you need the java sdk on your machine. It has been tested on version 8 and 11 but any version from 8 and up should work. Our prefered method of installation is using [SDKMAN](https://sdkman.io/install) with java version __8.0.242.j9-adpt__.

The frontend relies on node which can be installed by for example [nvm](https://github.com/nvm-sh/nvm). This also gives you the package manager `npm` which can be used to build the frontend. The other option is to use [yarn](https://yarnpkg.com/).

### Environment
For the service to authenticate to a Github repository and be able to update commit statuses you need to setup an authentication token on Github and add it to a `./token` file in the root directory of the project. This file will be ignored by git. 

The frontend needs to know where the server is located, which is done by adding a `./frontend/src/env.js` file with the following code
```javascript 
const env = {
  REACT_APP_URL:<url to ci job server>, 
  REACT_APP_PROXY:<url to proxy>
}

export default env;
```

If you don't need the proxy, set it to `""`. It's only there to resolve CORS issues.

### Webhook
To put the system into use, you need to setup a push webhook in your repository on Github which points to the service. See [github](https://developer.github.com/webhooks/) for more information.

### Build config
For your repository to be run by the service you need to add a `.dd.yml` file in the root directory with the following instructions. 

```yml
Build
// your compilation/build command on this line
Test
// your command for running test on this line
```

The service will first run the build command and then the test command.

### Building, testing and running the service
The repository comes with a build tool (`./gradlew`) which takes care of all building, testing and running of the Java parts of the service. `./gradlew` is essentially a wrapper of the [gradle](https://gradle.org/) build system at a fixed version. 

See `./gradlew help` for more information on tasks.

#### Building
To build the java service, you can run either `./gradlew assemble` or `./gradlew build`. The second one also runs the projects test suite.

#### Running test
To run the test suite, either run `./gradlew test` or `./gradlew build`.

#### Running the service
To start up the Java service, run `./gradlew run --args='PORT_NUMBER'`. Where`PORT_NUMBER` is the port on which you're planning to run the frontend locally (this will be 3000 for most people). 

To start up the frontend, go into `/frontend` and run `npm start`. This should start a node server accessible on `localhost:3000`. More instructions for the frontend can be found in `/frontend/README.md`.

### Test that everything works
When all is up and running, try to push a commit into your repository. This should output a log in the Java server as well as showing up as an entry at `http://localhost:PORT_NUMBER/`. If you find the commit on github, it should have a `pending` status, which later should be resolved to either a `success`, `failure` or an `error`. Clicking on "Details" on the commit status should take you to `http://localhost:PORT_NUMBER/JOB_ID` where you see the log of the build from this commit.

### Built With
* [gradle](https://gradle.org/)
* [jetty](https://www.eclipse.org/jetty/)
* [jersey](https://eclipse-ee4j.github.io/jersey/)
* [react](https://reactjs.org/)

### Contributions
#### Adam Björnberg
- Setup project with gradle and added all dependencies.
- Co-authored REST API.
- Co-authored commit status updater class.


#### Robin Bråtfors
- Co-authored BuildJob class
- Co-authored RunBash class

#### Gabriel Gessle
- Implemented storage and storage API for builds
- Created frontend application and integrated with backend API
- Co-authored backend API

#### Kasper Liu
- Co-authored Status-Updater class
- Co-authored RunBash class 
- Contributed to the investigation of webhook

#### Johan Moritz
- Setup Travis for continuous integration
- Implement skeleton for asyncronous build jobs
- Updated the state representation of build jobs
- Wrote webpage for displaying single build jobs
