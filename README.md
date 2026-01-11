#  Smart Project Management System

## Overview
The **Smart Project Management System** is a JavaFX-based desktop application designed to manage software projects efficiently using **role-based access control**. It supports multiple stakeholders—**Admin, Manager, Member, and Client**—each with defined responsibilities to ensure smooth collaboration.

The system allows project creation, task assignment, progress tracking, and AI-assisted risk suggestions. Data is persisted using JSON files through the GSON library. The project demonstrates strong use of **Object-Oriented Programming (OOP)** principles and layered architecture.

---

##  Key Features
- Role-based dashboards (Admin, Manager, Member, Client)
- Secure login and registration system
- Project creation and deletion
- Task assignment and progress tracking
- AI-based project risk suggestions
- JSON-based persistent storage
- Modular, maintainable, and scalable code structure

---

## Technologies Used
- **Java SE** – Core programming language  
- **JavaFX** – GUI framework  
- **FXML** – UI layout design  
- **CSS** – User interface styling  
- **GSON Library** – JSON serialization/deserialization  
- **JSON Files** – Persistent data storage  

---

##  System Architecture
The application follows an **MVC-inspired layered architecture**:

- **Presentation Layer**: JavaFX UI, FXML files, CSS styles  
- **Controller Layer**: Handles UI events and navigation  
- **Service Layer**: Business logic implementation  
- **Repository Layer**: Manages data access and persistence  
- **Data Layer**: JSON files for storing application data  

---

## Application Workflow
1. Application starts with a **Splash Screen**
2. User is redirected to the **Login Screen**
3. New users can register via the **Registration Screen**
4. After authentication, users are redirected to dashboards based on their role

---

##  User Roles & Functionalities

###  Admin
- Login and authentication
- Create new projects with name, description, and deadline
- Delete projects on client request

###  Manager
- View assigned projects
- Assign tasks to members
- Update project progress
- View AI-based risk suggestions

###  Member
- View assigned projects and tasks
- Update task and project progress

###  Client
- View project list
- Monitor project progress
- View project risk based on progress

---

##  Data Storage Design
The system uses JSON files stored in the `data` package:

- **users.json** – Stores user credentials and roles  
- **projects.json** – Stores project details, tasks, deadlines, and progress  

All file operations are handled using a centralized `JSONFileHandler` utility class.

---

##  UML Design
- `User` is a base class extended by `Admin`, `Manager`, `Member`, and `Client`
- `Project` contains multiple `Task` objects
- Repository classes manage data persistence
- Service classes handle authentication, project management, task management, and AI-based risk analysis

This structure ensures clear separation of concerns and system scalability.

---

##  Non-Functional Requirements
- User-friendly interface
- Role-based access control
- Reliable data persistence
- Modular and maintainable codebase
- Efficient performance for small to medium datasets

---

##  Future Enhancements
- Database integration (MySQL)
- Real-time notifications
- Advanced AI-based risk analysis
- Reporting and analytics module

---

##  Team Members
- **Faiza Qayyum** (SP25-BCS-004)  
- **Syed Shehryar Mutee** (SP25-BCS-050)

---

##  Course Information
**Course:** Programming Fundamentals / OOP with Java  
**Institution:** COMSATS University Islamabadc, Sahiwal Campus.
---
## Creators Note:
We have developed this as our semester project for practical learning of Java OOPS.
