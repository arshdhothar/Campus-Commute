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

Sprint 1 focuses on building the core foundation, implementing user registration and authentication, route and ride management, and basic carpool-matching logic.  
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
**Subclasses:** None  

**Responsibilities:**
- Represent a single ride listing created by a driver.
- Stores ride details such as start location, destination, departure time, available seats, and price.
- Provides methods to return formatted ride details (e.g., via toString() for display in the GUI).
- Validates ride information to ensure numeric and logical correctness (e.g., seat and price validation).


**Collaborators:**
- RideRequest
- CampusCommuteGUI (Driver UI)
- PassengerGUI (Passenger UI)


---

### **Class Name:** RideRequest
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Represents a request made by a passenger seeking a ride.
- Stores user details, desired start location, destination, and time preferences.
- Provides comparison and matching functionality against existing Ride objects to find suitable rides.


**Collaborators:**
- Ride
- PassengerGUI
- CampusCommuteGUI
---

### **Class Name:** Passenger  
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Represent a passenger user
- Search rides by location, time, or destination
- Send ride requests to drivers

**Collaborators:**
- Ride
- RideRequest
- PassengerGUI

---

### **Class Name:** CampusCommuteGUI  
**Parent Class:** JFrame  
**Subclasses:** None  

**Responsibilities:**
- Acts as the main user interface for CampusCommute.
- Collects input for rides and ride requests from the user.
- Displays available rides and handles UI interaction such as adding a new ride or showing error popups.
- Connects user interface actions with model operations (e.g., creating new Ride objects and displaying them in the list).


**Collaborators:**
- Ride
- RideRequest
- PassengerGUI
---
### **Class Name:** PassengerGUI  

**Parent Class:** JFrame  
**Subclasses:** None  

**Responsibilities:**
- Provide Passenger Portal UI for searching rides
- Display ride matches by location/time/destination
- Allow passengers to send ride requests

**Collaborators:**
- Ride
- RideRequest
- Passenger


---
### **Class Name:** CommuteFrame

**Parent Class:** JFrame  
**Subclasses:** None  

**Responsibilities:**
- Display welcome screen with Login / Sign-Up
- Serve as entry point for launching modules

**Collaborators:**
- CampusCommuteGUI
- PassengerGUI


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
![Campus Commute Architecture Diagram](Architecture%20Diagram.png)


---

## 6. Error Handling

The system handles basic user-input errors (for example, empty fields or invalid data) using **Swing popup alerts**.  
If a required file is missing or unreadable, it automatically creates a new file and continues running instead of crashing.  
When duplicate data is entered, a **warning message** appears to inform the user.  
These error-handling mechanisms ensure smooth user experience and program stability during runtime.

---

