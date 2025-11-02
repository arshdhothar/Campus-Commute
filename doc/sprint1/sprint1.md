# Sprint 1 Planning Meeting  
Project: Campus Commute  
Date: Nov 2nd 2025 
Sprint 1  

## Sprint Goal:   
Develop the foundation of Campus Commute by implementing:
- User registration and authentication  
- Ride creation/browsing  
- Basic carpool-matching logic

---

## Participants & Team Capacity  
Name | Role | Capacity (hrs)
------|------|---------------
Zack Nayar | Product Owner | 12  
Arsh Dhothar | Scrum Master | 14  
Sahil Panchal | Developer | 15  
[Name 4] | Developer | 12  
Alp Duras | Developer | 14  

The user stories we selected are User Story 1 : Ride Posting (Driver) where drivers can set route, number of seats, and departure time.
User Story 2: Ride Request by Passenger where Passengers can search rides by location, time, or destination. Lastly User Story 20: User Signup/Login where there is a signup/login page for the student. 

---

## Spikes (Research Tasks)
- Spike 1:  Find a simple way to save user login info 
- Spike 2: Learn the best way to organize Swing code using MVC.  
- Spike 3 : Try a simple match method that compares routes and times.  

---

## Task Breakdown
**Account Creation and Login (Alp)**
- Make a `User` class (name, email, password)  
- Add signup and login panels in Swing  
- Save user info to a local file  
- Add error messages for invalid input  

**Ride Request (Zack and Ibrahim)**
- Make a `Ride` class (driver, route, time, seats)  
- Create a Swing form to add a ride  
- Show a ride list table in the GUI  
- Save and load rides from a file  

**Carpool Matching(Sahil and Arsh)**
- Write a method to check rides with similar start and end points  
- Show suggested matches in the UI  

---
**Decisions**
We will use Java Swing for the interface. The user and ride data will be stored in files for now and the MVC structure will be used to separate logic and GUI. 
