# ECSE 321 Grocery Store Project - Winter 2022 - Group 05

![heroku-deploy-status](https://img.shields.io/badge/Deploy%20to%20Heroku-enabled-blueviolet?style=for-the-badge&logo=heroku)  

Welcome to the Group 05 Grocery Store Project Repository!  

## Project Overview 

The goal of this project is to create a website and an app to facilitate the grocery shopping process for our customers.

Have a look at our [wiki](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki) for more info!

### Backend
Heroku Backend URL: https://grocery-backend-g05-mcgill.herokuapp.com/  
Click [here](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki/RESTful-Service-Documentation) for the documentation of our REST API. All endpoints are appended after the Heroku Backend URL above.

To run the backend locally, make sure that port 8080 is available. Navigate to project root, then issue the command `./gradlew bootRun` to run the project as a Spring Boot application. The backend will be available at the URL `localhost:8080`. To stop the gradle daemon, issue the command `./gradle --stop`.

Additionally, integration tests of the REST API can be run with the command `./gradlew integrationTest`. Note that the Heroku database is easily overwhelmed when multiple people are working on the project and may cause all tests to fail.

### Frontend
_coming soon_

## Team Members

Below can be found the time spent in hours by each team member on each sprint. Please refer to the specific sprint for details on the work done.

| Name  | Role | Sprint 1 | Sprint 2 | Sprint 3 | Sprint 4 |
| ----- | ----- | :-----: | :-----: | :-----: | :-----: |
| Peini Cheng  | Software Architect | 14 | 13.75 | &mdash; | &mdash; |
| Sibo Huang | Software Developer | 12 | 16.75 | &mdash; | &mdash; |
| Annie Kang | Business Analyst | 12 | 15.75 | &mdash; | &mdash; |
| Yida Pan | Software Developer | 12 | 17.75 | &mdash; | &mdash; |
| Jimmy Sheng | Team Lead | 24 | 23.75 | &mdash; | &mdash; |
| Harrison Wang | Documentation Lead | 21.5 | 22.75 | &mdash; | &mdash; |

## [Sprint 1](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki/Sprint-1-Project-Report)

### Objectives to complete:
Started on Feb 8, 2022
* Set up Github repo
* Set up project drive and timesheet
* Requirements Model
* Domain Model
* Persistence Layer
* Testing of Persistence Layer
* Build System and Continuous Integration
* Project Report

### Tasks completed by each member:
* **Peini**: Designed UML Class Diagram, JPA + CRUD and tests for Customer, Owner, and OpeningHours classes, One detailed use case, 3 System Requirements
* **Sibo**: Wrote README.md, JPA + CRUD and tests for Holiday, One detailed use case, 2 System Requirements
* **Annie**: Recorded meeting minutes, Designed Use Case Diagram, One detailed use case, 2 System Requirements
* **Yida**: JPA + CRUD and tests for Item, ItemCategory classes, One detailed use case, 2 System Requirements
* **Jimmy**: Set up Github repository, CI Actions, template class files, and Heroku backend database, Reviewed pull requests, Revised Domain Model, Revised Use Case Diagram
JPA + CRUD and tests for Purchase, SpecificItem classes, Fixed JPA and tests for Item, ItemCategory classes, One detailed use case, 3 System Requirements
* **Harrison**: Set up project Google Drive and project Timesheet, Revised Domain Model, Revised Use Case Diagram, Helped review pull requests, Helped review detailed use cases, JPA + CRUD and tests for Employee, EmployeeSchedule, and Shift classes, One detailed use case, 3 System Requirements, Outlined Project Wiki and wrote Class Diagram Rationale, Domain Model Constraints, and Sprint 1 Project Report

## [Sprint 2](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki/Sprint-2-Project-Report)

### Objectives to complete:
Started on Feb 28, 2022  
* Set up Github CI for deploying to Heroku
* RESTful service methods for Use-cases
* Unit testing of RESTful service methods using Mockito
* REST API Controllers
* Integration Testing of REST API Controllers
* Software Quality Assurance
* Project Report

### Tasks completed by each member:
* **Peini**: RESTful services, controller, unit testing, and documentation for Customer and ItemCategory.
* **Sibo**: RESTful services, controller, unit testing, and documentation for Owner, OpeningHours, and Holiday.
* **Annie**: RESTful services, controller, unit testing, and documentation for Item. Recorded meeting minutes.
* **Yida**: RESTful services, controller, unit testing, and documentation for Shift.
* **Jimmy**: RESTful services, controller, unit testing, and documentation for Purchase. Set up Github packages and dependencies. Setup CI for deploying to heroku. Service documentation formatting. Fixing typos and code review.
* **Harrison**: RESTful services, controller, unit testing, and documentation for Employee. Set up skeleton for Project Wiki. Code review. Wrote software quality assurance plan. Sprint 2 section of README.md.
