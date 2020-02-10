# DD2480-group-18-CI (Ci test)
A simple continuos integration solution, created for assignment 2 at KTH course DD2480. Spring of 2020.

## Scripts
### Build
`./gradlew build` or `./gradlew assemble`

### Run 
`./gradlew run`

### Test
`./gradlew test`


## Frontend
See `/frontend/README.md` for information about running the frontend. 
Build list url can be found at `http://localhost:3000/` (given that the frontend is running).

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
