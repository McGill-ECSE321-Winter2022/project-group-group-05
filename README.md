# ECSE 321 Grocery Store Project - Winter 2022 - Group 05

[![heroku-deploy-status](https://img.shields.io/badge/Deploy%20to%20Heroku-enabled-blueviolet?style=for-the-badge&logo=heroku)](https://grocery-frontend-g05-mcgill.herokuapp.com/)
[![backend: spring boot](https://img.shields.io/badge/backend-spring%20boot-green?style=for-the-badge&logo=springboot)](https://spring.io/)
[![frontend: vue.js](https://img.shields.io/badge/frontend-Vue.js-42b883?style=for-the-badge&logo=vue.js)](https://v2.vuejs.org/)
[![code style: prettier](https://img.shields.io/badge/code_style-prettier-ff69b4.svg?style=for-the-badge&logo=prettier)](https://github.com/prettier/prettier)  

Welcome to the Group 05 Grocery Store Project Repository!  

## Project Overview

The goal of this project is to create a website and an app to facilitate the grocery shopping process for our customers.

Have a look at our [wiki](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki) for more info!

### Backend

Heroku Backend URL: <https://grocery-backend-g05-mcgill.herokuapp.com/>  
Click [here](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki/RESTful-Service-Documentation) for the documentation of our REST API. All endpoints are appended after the Heroku Backend URL above.

For more detail please see the main branch.

### Frontend

Heroku Frontend URL: <https://grocery-frontend-g05-mcgill.herokuapp.com/>

For more detail please see the main branch.

## Team Members

Below can be found the time spent in hours by each team member on each sprint. Please refer to the specific sprint for details on the work done.

| Name  | Role | Sprint 1 | Sprint 2 | Sprint 3 | Sprint 4 |
| ----- | ----- | :-----: | :-----: | :-----: | :-----: |
| Peini Cheng  | Software Architect | 14 | 13.75 | 24.5 | &mdash; |
| Sibo Huang | Software Developer | 12 | 16.75 | 24.5 | &mdash; |
| Annie Kang | Business Analyst | 12 | 15.75 | 24.5 | &mdash; |
| Yida Pan | Software Developer | 12 | 17.75 | 16.5 | &mdash; |
| Jimmy Sheng | Team Lead | 24 | 27.75 | 48.5 | &mdash; |
| Harrison Wang | Documentation Lead | 21.5 | 22.75 | 44.5 | &mdash; |

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
* **Jimmy**: RESTful services, controller, unit testing, and documentation for Purchase. Set up Github packages and dependencies. Setup CI for deploying to heroku. Service documentation formatting. Fixing typos and code review. Write the integrationTest Gradle Task.
* **Harrison**: RESTful services, controller, unit testing, and documentation for Employee. Set up skeleton for Project Wiki. Code review. Wrote software quality assurance plan. Sprint 2 section of README.md.

## [Sprint 3](https://github.com/McGill-ECSE321-Winter2022/project-group-group-05/wiki/Sprint-3-Project-Report)

### Objectives to complete:
Started on Feb 28, 2022  
* Set up Github CI for Frontend deployment
* Block Diagram of Architecture Model
* Functional Web-Frontend
* Integration of Web-Frontend with Backend Services
* Project Report

### Tasks completed by each member:
* **Peini**: ViewPurchaseHistory, ManageProfile, ManageCustomers, updated UML class diagram
* **Sibo**: StaffDashboard, ManageEmployees, ManageStaffProfile
* **Annie**: Designed architecture model, recorded meeting minutes, ViewCompletedOrders, ManagePaidOrders
* **Yida**: Attempted implementation of ManageCategories, attended meetings.
* **Jimmy**: Set up frontend repository and CI heroku deployment, set up code style using Prettier, vue state management, common AXIOS object, Welcome, LoginForm, CreateCustomerForm, PointOfSale, ManageCategories, ManageHolidays, Navbar, fixed StaffDashboard and ManageStaffProfile, fixed styles, bug fixes, code review.
* **Harrison**: Set up skeleton for project wiki, Sprint 3 project report, added Spring 3 to readme.md, edited architecture model component descriptions, updated project wiki documentation on backend services, ViewSchedules, ManageSchedules, ManageItems, ManageCart, ManageOpeningHours, made style of StaffDashboard pages more uniform, bug fixes, code review.
