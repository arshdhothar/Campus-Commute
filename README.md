![Logo](https://i.imgur.com/7YiWyhJ.png)

Discord Link (for Sprint 2 and Sprint 3 standups - Visit the sprint2 and sprint3 standup channels) : https://discord.gg/vv76pXYrdj

Project Demo Video - Sprint3: https://www.youtube.com/watch?v=ovT_k52Pb-U

Project Demo Video - Sprint2: https://youtu.be/nOJvk0LMDG8

Project Demo Video - Sprint1: https://www.youtube.com/watch?v=ElhrqJMuVyE&feature=youtu.be

# **CampusCommute**

## **Motivation and Project Description**

**CampusCommute** is a university-exclusive ride-sharing platform designed to make traveling to and from campus for students and staff **safer** and **cheaper**.  

Many students and staff rely on public transportation or expensive alternatives such as Uber or Lyft. **CampusCommute** provides a trusted, community-based solution where only **verified university members** can post or join rides.  

### **Problems We Solve**
- **Safety:** Only verified university members can join, ensuring a secure and reliable source of riders and drivers.  
- **Affordability:** Students and staff often face high commuting costs such as public transit, gas, or parking. **CampusCommute** makes commuting more affordable.  
- **Sustainability:** Encourages carpooling to reduce traffic congestion and carbon emissions.  

### **Features**
- Offer or request rides within the campus community  
- Communicate securely through in-app messaging  
- Track trips, receive notifications, and view driver ratings  
- Promote sustainability through shared rides  

**Goal:** Build trust and convenience within the campus community through a simple and reliable ride-sharing experience.  

---

## Architecture & Stack

Our system follows the **Three-Tiered Architecture** pattern, ensuring a clear separation between the client, server, and data layers.

| Layer | Technology (Used) |
| :--- | :--- |
| **Frontend (View)** | Java Swing (`javax.swing`) |
| **Backend (Controller)** | Java Spring Boot (REST API) |
| **Database (Model)** | MySQL Community Server |
| **Communication** | HTTP / JSON (via GSON library) |
| **Version Control & DevOps** | GitHub, Trello, Discord, GitHub Desktop |

## **Installation for Your Software/System**

To run our project, you need:  
- **Java JDK 21**  
- **Java Swing** (included in Java 21)  
- **Eclipse IDE for Java Developers**  
- **GitHub**
- **SQL Workbench and Community Server**
- **Java Spring**

### **1. Import Using Git Integration In eclipse**
Video available for instructions : https://www.youtube.com/watch?v=u9FjJkZeWgg
1. In eclipse, Go To File -> Import
2. Git -> Project From Git(with smart import)
3. The url field should be the link to the repository, user is your github username and the password is a classic token generated in github(not your actual password), press next
If you have trouble creating a token follow : https://stackoverflow.com/questions/29807277/cloning-the-git-repository-on-eclipse-transport-error
4. Select the main branch then press next
5. Choose the initial branch as main and press next
6. In this screen, it is very important **uncheck Search for nested projects** this is very important and check detect and configure project natures and click finish
7. Open SQL workbench and create a database named "campus_commute" using command **create database campus_commute;**
8. Now edit the application.properties file in project-aszia\server\server\src\main\resources, edit the field that requires the password to your sql workbench password
9. To run the sql server, go to to the project-aszia\server\server directory and open **CMD** in that directory
10. In this CMD window run the command ".\mvnw spring-boot:run" , after this the sql server should be running
11. You can now freely use our application with a special database for your system.

### **2. Import Using Git Desktop**
1. Download our main branch on to your computer
2. Go to eclipse -> File -> Import -> Existing Project in to workspace
3. Choose the downloaded folder (**make sure search for nested projects is unchecked**), click finish
4. Open SQL workbench and create a database named "campus_commute" using command **create database campus_commute;**
5. Now edit the application.properties file in project-aszia\server\server\src\main\resources, edit the field that requires the password to your sql workbench password
6. To run the sql server, go to to the project-aszia\server\server directory and open **CMD** in that directory
7. In this CMD window run the command ".\mvnw spring-boot:run" , after this the sql server should be running
8. You can now freely use our application with a special database for your system.
---

## **Contribution**

Our group follows a simple and organized process to manage contributions.  

We use a **Git Flow** workflow where:  
- The **main** branch contains the working, tested code.  
- The **develop** branch is where new features are added.  
- Each new feature or fix is developed in its own branch and merged into **develop** once complete.

### **Branch Naming Conventions**
We named our Branches so that each user story gets its own branch, for example User Story 1 would have a branch name US1/Ride Posting. 
At the end of our project we will have about 20 branches each representing a user story. 

### **Issue Tracking**
We use **GitHub Issues** to track bugs, tasks, and new features.  
Each issue includes:
- A short, clear title  
- A detailed description  
- Labels for easy organization  

### **Pull Requests**
All changes are submitted through **Pull Requests (PRs)**.  
Before merging:
- Teammates review PRs to ensure clarity and testing.  
- Each PR includes a short summary of what was changed or added.  

This ensures the codebase remains organized, stable, and easy to maintain.
