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

## **Installation for Your Software/System**

To run our project, you need:  
- **Java JDK 21**  
- **Java Swing** (included in Java 21)  
- **Eclipse IDE for Java Developers**  
- **GitHub**

### **1. Import Using Git Integration In eclipse**
1. In eclipse, Go To File -> Import
2. Git -> Project From Git(with smart import)
3. The url field should be the link to the repository, user is your github username and the password is a classic token generated in github(not your actual password), press next
If you have trouble creating a token follow : https://stackoverflow.com/questions/29807277/cloning-the-git-repository-on-eclipse-transport-error
4. Select the branches you want to download then press next
5. Choose the initial branch as main and press next
6. In this screen, it is very important **uncheck Search for nested projects** this is very important and check detect and configure project natures and click finish
7. Now you have acess to our application

### **2. Build and Run the Project**
1. Right-click the project name in **Package Explorer** → **Build Project** to compile all files.  
2. Open the **main class** that starts the project.  
3. Click **Run As → Java Application**.  
4. The **Java Swing GUI** will appear — this is the user interface for the project.  
5. To stop the program, simply close the GUI window.

### **3. Running from a ZIP File**
1. Zip the project.  
2. Open Eclipse → **Import Existing Projects into Workspace**.  
3. Select the downloaded project and import it.  
4. Run the GUI files to recreate the Sprint 1 demo video.

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
