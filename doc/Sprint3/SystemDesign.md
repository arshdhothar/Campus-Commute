# Campus Commute – Sprint 2 System Design Document
**EECS 3311 – Software Design (Fall 2025)**  

**Team Name:** CampusCommute  
**Instructor:** Ilir Dema  
**TA:** [TA Name]  
**Date:** Nov 11th, 2025  
**Sprint:** Sprint 2  

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

### **Class Name:** EmergencyContanctInfoFrame
**Parent Class:** JFrame
**Subclasses:** None

**Responsibilities:**
- Provides the UI for adding or updating a user's emergency contact.
- Verifies a user is logged in by checking `CurrentUser` before displaying the window.
- Gathers user input from `txtEmergencyName` and `txtEmergencyPhone`.
- Handles the `btnSaveContact` click event and calls the `handleUpdateContact` method.
- Displays success ("Updated Info") or error pop-up messages (`JOptionPane`).

**Collaborators:**
- `CurrentUser`
- `EmergencyContactRequest`
- `Gson`
- `HttpClient`, `HttpRequest`, `HttpResponse`
- `JFrame`
- `JOptionPane`

---

### **Class Name:** EmergencyContactRequest
**Parent Class:** Object
**Subclasses:** None

**Responsibilities:**
- Acts as a data-transfer object (DTO) to hold the information for an emergency contact update.
- Stores the user's `email`, the contact's `contactName`, and the contact's `contactPhone`.
- Is created by `EmergencyContanctInfoFrame` to be serialized into JSON.

**Collaborators:**
- `EmergencyContanctInfoFrame`
- `Gson`
- `AuthController` (on the server side)

---

### **Class Name:** Ride  
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Represent a single ride listing created by a driver.
- Stores ride details such as start location, destination, departure time, available seats, and price.
- Provides methods to return formatted ride details (e.g., via toString() for display in the GUI).
- Validates ride information to ensure numeric and logical correctness (e.g., seat and price validation).
- Maintain list of RideRequest objects.
- Store driver rating data for display in search results.

**Collaborators:**
- RideRequest
- CampusCommuteGUI (Driver UI)
- PassengerGUI (Passenger UI)
- RideRequest
- RideController   (last 3 are backend files) 
- RideRepository


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
- Allow passengers to rate drivers upon completion via the History tab.

**Collaborators:**
- Ride
- RideRequest
- Passenger
- RideService


---
### **Class Name:** SignupFrame

**Parent Class:** None 
**Subclasses:** None  

**Responsibilities:**
- Provide input spaces for signup details
- Ensure correct formatting of given input (correct date,phone number etc.)
- Leads user to login frame after successful sign up
- Informing users of what they are doing wrong


**Collaborators:**
- Person
-UserDataManager
  LoginFrame



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
### **Class Name:** UserDataManager

**Parent Class:** None 
**Subclasses:** None  

**Responsibilities:**
- Store given user data at a json file stored in the project
-Ensure the correct formatting of json form in the file
-Provide content check processes to external classes, to check for user data in the json file
-Ensure safekeeping of string formatting using escaping and unescaping 


**Collaborators:**
- Person
-SignupFrame
-Login Frame
-RideRequest



---

### **Class Name:** LoginFrame

**Parent Class:** None 
**Subclasses:** None  

**Responsibilities:**
- Provide input spaces for login details
- Ensure correct formatting of given input 
- Allows users to to reach to signup frame if they need to signup 
- Informing users of what they are doing wrong, or if their credentials are wrong


**Collaborators:**
- Person
-UserDataManager
- SignupFrame




---

### **Class Name:** UserProfile  
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Store user profile information such as bio and photo path.
- Provide methods to update and retrieve profile data.
- Persist profile data locally (using file serialization).


**Collaborators:**
- ProfileCustomizationGUI (for display and editing)
- User (to associate profile with a user account)

---

### **Class Name:** ProfileCustomizationGUI  
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Provide a GUI for users to edit their profile.
- Allow users to update bio and upload a profile photo.
- Display profile data and photo dynamically in the GUI.
- Save the profile to disk after edits.


**Collaborators:**
- User (determines which profile is being edited)
- UserProfile (stores and provides the profile data)
- ViewProfileGUI
Backend:
- AuthController.java
- Person.java
- PersonRepository.java


---

### **Class Name:** DriverGUI
**Parent Class:** JFrame
**Subclasses:** None

**Responsibilities:**
- Load the logged-in driver's rides from backend.
- Display ride requests for each ride.
- Allow driver to accept a request.
- Show completed ride history.

**Collaborators:**
- Ride
- RideRequest
- CurrentUser
- Backend API 
---

### **Class Name:** NotificationCenter
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Manage and send real-time notifications to users.
- Notify drivers and passengers about ride requests
- Support UI popups using Swing.

**Collaborators:**
- RideService: triggers notifications based on actions.
- Driver: receives or sends notifications.
- Passenger:receives or sends notifications.
- JFrame (UI): displays popups through Swing.

---
### **Class Name:** RideService
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Manage all rides, requests, and approvals in the system.
- Handle ride creation, searching.
- Update ride statuses.
- Notify users through NotificationCenter for ride events.
- Save and load ride data using RideDataManager
- Handle driver rating submissions.

**Collaborators:**
- Driver: publishes rides.
- Passenger: requests rides.
- Ride:stores ride details.
- NotificationCenter: sends alerts.
- RideDataManager
- RatingController
---
### **Class Name:** RideDataManager
**Parent Class:** None  
**Subclasses:** None  

**Responsibilities:**
- Read and write ride data to ridesData.json.
- Convert ride objects to JSON format and back.
- Maintain storage of all ride information.
- Load saved rides when the application starts.

**Collaborators:**
- Ride: represents stored data.
- RideService: requests and updates data.

---

### **Class Name:** NotificationSettingsFrame
**Parent Class:** JFrame
**Subclasses:** None  

**Responsibilities:**
- Show a window where users can choose which notifications they want to receive.
- Provide checkboxes for ride confirmations, ride cancellations, and ride reminders.
- Let users press a “Save Preferences” button to confirm their choices.

**Collaborators:**
- NotificationCenter
- Person 

 ---

 ### **Class Name:** CurrentUser
**Parent Class:** None
**Subclasses:** None

**Responsibilities:**
- Store the logged-in Person object globally.
- Provide global access to the user's ID and name.

**Collaborators:**
- PassengerGUI
- DriverGUI
- LoginFrame
- ProfileCustomizationGUI
  
---

## **Class Name:** EmergencyButtonPanel
**Parent Class:** `JPanel`  
**Subclasses:** None  

### **Responsibilities**
- Display the Emergency button in Passenger and Driver GUIs.  
- Hold user name, phone number, and ride ID for the alert.  
- Send an emergency alert request to the backend API.  
- Show confirmation or error messages to the user.  

### **Collaborators**
- `EmergencyAlertRequest`  
- `EmergencyContactsStore`  
- `PassengerGUI`  
- `CampusCommuteGUI`  
- Backend Emergency Controller  

---

## **Class Name:** EmergencyAlertRequest
**Parent Class:** None  
**Subclasses:** None  

### **Responsibilities**
- Store emergency alert fields (`rideId`, `userName`, `userPhone`).  
- Convert data into JSON for backend transmission.  
- Act as a data-transfer object between UI and backend.  

### **Collaborators**
- `EmergencyButtonPanel`  
- Backend Emergency Controller  

---

## **Class Name:** EmergencyContactsDialog
**Parent Class:** `JDialog`  
**Subclasses:** None  

### **Responsibilities**
- Provide UI for adding, removing, and viewing emergency contacts.  
- Display contacts in a list with Add/Remove buttons.  
- Save updated contacts when the user clicks **Save**.  

### **Collaborators**
- `EmergencyContactsStore`  
- `PassengerGUI`  
- `CampusCommuteGUI`  

---

## **Class Name:** EmergencyContactsStore
**Parent Class:** None  
**Subclasses:** None  

### **Responsibilities**
- Load saved emergency contacts from a local JSON file.  
- Save updated contacts persistently for future sessions.  
- Provide access to the user's emergency contact list.  

### **Collaborators**
- `EmergencyContactsDialog`  
- `EmergencyButtonPanel`  

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

### **Class Name:** Person
**Parent Class:** None
**Subclasses:** None

**Responsibilities:**
- Store user profile information (name, email, password, userType).
- Store optional fields (bio, emergency contact, photo path).
- Store rating metrics (average rating, total rating count).

**Collaborators:**
- CurrentUser
- LoginFrame
- ProfileCustomizationGUI
- RatingController

 ---

 ### **Class Name:** ViewProfileGUI
**Parent Class:** JFrame
**Subclasses:** None

**Responsibilities:**
- Display the current user's profile information (name, email, phone, user type, bio) in a read-only format
- Provide a button to navigate to "Edit Profile" screen that sends you to ProfileCustomizationGUI
- Provide a button to either "Offer a Ride" or "Find a Ride" that sends you to the respective GUI.
- Display the driver's calculated average rating and total review count.

**Collaborators:**
- CurrentUser
- Person
- ProfileCustomizationGUI
- PassengerGUI
- CampusCommuteGUI

 ---

 ## **Class Name:** ChatGUI
**Parent Class:** JFrame 
**Subclasses:** None  

### **Responsibilities**
- Display the chat history window for a specific ride ID.  
- Poll the backend server every 2 seconds to fetch new messages.  
- Capture user input and send new messages to the backend via HTTP POST.  

### **Collaborators**
- CurrentUser  
- ChatController (via HTTP)  
- Message 
- `HttpClient/Gson

---

## **Class Name:** Message
**Parent Class:** None  
**Subclasses:** None  

### **Responsibilities**
- Data model representing a single chat message.  
- Store fields for sender ID, ride ID, content, and timestamp.  
- Map JSON data to Java objects for transfer between frontend and backend.  

### **Collaborators**
- None

---

## **Class Name:** RatingController
**Parent Class:** None  
**Subclasses:** None  

### **Responsibilities**
- Receive rating submissions from the frontend via REST API.
- Validate rating values (ensure 1-5 stars and no duplicates).
- Persist individual Rating records to the database.
- Recalculate and update the Person (Driver) average rating and count. 

### **Collaborators**
- Rating
- RatingRepository
- PersonRepository

---

## **Class Name:** Rating
**Parent Class:** None  
**Subclasses:** None  

### **Responsibilities**
- Represent a single rating event.
- Store the score (1-5), the rater's ID, and the rated driver's ID.
- Store optional comments/reviews. 

### **Collaborators**
- RatingController
- RatingRepository

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

