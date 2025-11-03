# Campus Commute – Sprint 1 System Design Document
**EECS 3311 – Software Design (Fall 2025)**  

**Team Name:** CampusCommute  
**Instructor:** Ilir Dema  
**TA:** [TA Name]  
**Date:** Nov 3rd, 2025  
**Sprint:** Sprint 1  

---

## Team Members

| Name | Student ID | Role |
|------|-------------|------|
| Ibrahim Khalil Chaudhry | 219618354 | Developer |
| Arsh Dhothar | 220614129 | Scrum Master / Developer |
| Zack Nayar | 220373643 | Product Owner / Developer |
| Sahil Panchal | 220568481 | Developer |
| Alp Duras | 220603346 | Developer |

---

## Table of Contents
1. [Introduction](#1-introduction)  
2. [System Overview](#2-system-overview)  
3. [Class Responsibilities and Collaborators (CRC Cards)](#3-class-responsibilities-and-collaborators-crc-cards)  
4. [System Interaction and Environment](#4-system-interaction-and-environment)  
5. [System Architecture Diagram](#5-system-architecture-diagram)  
6. [Error Handling](#6-error-handling)

---

## 1. Introduction

Campus Commute is a collaborative transportation platform designed for university communities to reduce traffic, cut costs, and promote sustainable travel.  
It allows students, staff, and faculty to **offer or request carpool rides** with others traveling along similar routes, as well as view available campus shuttles and parking options.  
The system matches users heading to the same destination or area, enabling efficient ride-sharing within the campus network.  

Sprint 1 focuses on building the core foundation — implementing user registration and authentication, route and ride management, and basic carpool-matching logic.  
This document outlines the system’s design structure, including its classes, architecture, and environment setup, which together form the backbone for later features such as live tracking and payment integration.

---

## 2. System Overview

Campus Commute follows the **Model–View–Controller (MVC)** architecture to ensure modularity, scalability, and ease of future expansion.

- **Model Layer:** Contains data and business logic through classes such as `User`, `Ride`, and `RideManager`. These handle user profiles, route data, and carpool matches.  
- **Controller Layer:** Coordinates data flow and actions between the interface and the model, managing requests such as creating a ride offer, joining a ride, or canceling a trip. The `DatabaseHandler` supports data persistence.  
- **View Layer:** Implemented through `CampusCommuteGUI`, which lets users log in, view available carpool options or shuttles, and confirm ride participation.  

In Sprint 1, users can create accounts, log in, browse or offer rides, and see suggested matches for nearby passengers going in the same direction.  
All information is stored locally through the `DatabaseHandler` for testing.  
The system’s design encourages eco-friendly travel and sets the stage for later enhancements like driver ratings, in-app navigation, and dynamic carpool recommendations.

---

## 3. Class Responsibilities and Collaborators (CRC Cards)

---

### **Class Name:** User  
**Parent Class:** Person  
**Subclasses:** Student, Driver  

**Responsibilities:**  
- Store and manage profile, credentials, and role.  
- Allow passengers to request rides or drivers to accept them.  
- Track past and upcoming trips.  

**Collaborators:**  
- RideManager  
- DatabaseHandler  

---

### **Class Name:** Ride  
**Parent Class:** None  
**Subclasses:** ShuttleRide, CarpoolRide  

**Responsibilities:**  
- Represent an individual ride or shuttle trip.  
- Contain origin, destination, time, capacity, and driver info.  
- Track ride status (booked / in progress / completed).  

**Collaborators:**  
- User  
- RideManager  

---

### **Class Name:** RideManager  
**Parent Class:** None  
**Subclasses:** ShuttleManager, CarpoolManager  

**Responsibilities:**  
- Handle creation, assignment, and cancellation of rides.  
- Match users to available drivers or shuttles.  
- Update ride status and notify users.  

**Collaborators:**  
- User  
- Ride  
- DatabaseHandler  

---

### **Class Name:** DatabaseHandler  
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**  
- Store and retrieve user and ride data.  
- Maintain persistence between sessions.  
- Provide CRUD operations for all modules.  

**Collaborators:**  
- User  
- RideManager  
- Ride  

---

### **Class Name:** CampusCommuteGUI  
**Parent Class:** UIController  
**Subclasses:** None  

**Responsibilities:**  
- Display the interface for login, route viewing, and ride requests.  
- Handle input and display messages.  
- Connect user actions to RideManager.  

**Collaborators:**  
- User  
- RideManager  

---

---

## 4. System Interaction and Environment

The Campus Commute system is a desktop application developed in **Java SE 21** using **Swing** for the user interface.  
It runs on any operating system that supports the Java Virtual Machine (JVM).  
All data is stored locally in text files, so the system does not depend on an external database or internet connection.  

**Assumptions:**  
- The user has Java installed on their computer.  
- The system has permission to read and write local files.  
- Only one user instance runs at a time.

---

## 5. System Architecture Diagram

**Link to Lucidchart Diagram:**  
![Campus Commute Architecture Diagram](doc/sprint1/Architecture%20Diagram.png)


---

## 6. Error Handling

The system handles basic user-input errors (for example, empty fields or invalid data) using **Swing popup alerts**.  
If a required file is missing or unreadable, it automatically creates a new file and continues running instead of crashing.  
When duplicate data is entered, a **warning message** appears to inform the user.  
These error-handling mechanisms ensure smooth user experience and program stability during runtime.

---

