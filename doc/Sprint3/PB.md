# 20 User Stories: (Sorted By Order of Completion)
### [Trello link](https://trello.com/invite/b/68cde0f68b3f22f748c81e76/ATTI2b6976d1181f719eed88d3385e4290c2793E2F0B/my-trello-board)

## In Progress 🔄️
### User Story 4: Ratings and Feedback System

As a passenger or driver, I want to rate and review my ride experience after each trip,
so that future users can make informed choices and maintain a culture of safety and respect.

Acceptance Criteria:

* After each trip, both driver and passenger are prompted to rate (1–5 stars).
* Users can leave optional written feedback.
* Average ratings are displayed on user profiles.
* Admins can review and act on low rated or reported rides.

### User Story 5: In-App Chat & Notifications

As a driver I want to send short messages about the ride so passengers will be prepared for pickup. As a passenger I want to receive notifications and updates on my ride so I can stay informed of any changes.

Acceptance Criteria:

* An in-app chat is available between matched drivers and passengers.
* Users receive notifications for new messages, ride requests, and confirmations.
* Drivers can notify all passengers of delays or route changes.

### User Story 14 - Ride Cancellation Policy

As a user, I want a clear ride cancellation feature so that I can manage changes responsibly.

Acceptance Criteria:

* Riders can cancel up to X hours before ride time.
* Cancellation notifies the driver immediately.
* Repeated last-minute cancellations trigger a warning.

### User Story 16 - Lost & Found Reporting

As a rider or driver, I want to report lost items after a trip so that they can be returned to the rightful owner.

Acceptance Criteria:

* Post-ride “Report Lost Item” option available.
* Reports include ride details, contact method, and item description.
* Notifications sent to the other participant (driver/rider).

___

## Unassigned 📥

### User Story 6: Payment
As a passenger, I want to see my payment status for a ride and mark it as paid,
so that I can confirm my part of the split fare.

Acceptance Criteria:
Each ride shows a split fare amount for the passenger.
A passenger can mark their payment as “Paid”.
The backend stores the updated payment status.
The driver can view which passengers have paid.

### User Story 11 - Carpool Scheduling (Recurring Rides)

As a driver,

I want to schedule recurring rides (daily or weekly) so that I don’t need to create a new post every day.

Acceptance Criteria:

* Driver can select “repeat daily” or “repeat weekly.”
* System auto-generates rides on selected days.
* Riders can view and join recurring rides as usual.


### User Story 18 - Reward & Points System

As a frequent driver or rider,

I want to earn points for every ride I complete so that I feel rewarded for using the app regularly.

Acceptance Criteria:

* Each completed ride adds points to a user’s account (e.g., 10 pts per ride).
* Points total is visible on the user profile.
* Points can be used for app perks (e.g., priority booking, badges).
* Points reset or carry over according to policy defined by the team.



___

## Completed ✅

### User Story 1: Ride Posting (Driver)

As a verified student driver, I want to offer available seats in my car to other verified users with similar routes so that I can reduce my commuting costs and contribute to sustainability.

Acceptance Criteria:

* The driver can set the route, number of seats, and departure time.
* The app suggests matches based on location, schedule, and direction.
* Riders can request to join carpools, and the driver can approve or decline.
* Ride confirmation notifications are sent to both parties.

### User Story 2:  Ride Request by Passenger 

As a commuter, I want to request a seat in a posted ride that matches my route or schedule and confirm with the driver, so that I can conveniently travel to campus in an affordable way. 

Acceptance Criteria: 

* Passengers can search rides by location, time, or destination.
* Users can send a ride request and receive confirmation/decline notification.
* Once accepted, both driver and passengers see trip details in their “My Rides” section.
* Passengers can cancel requests before pickup.

### User Story 20 - User Signup/Login

As a user, I want to sign up or login to the application

Acceptance Criteria:

* A login page, which has a link to the sign up page
* Ensuring the user enters information in a valid format and is not breaking any rules
### User Story 3: User Session Persistence (Sahil)

As a user, I want the application to remember my identity after I log in, so that I can update my profile, post rides, or add emergency contacts without having to re-enter my email for every action.

Acceptance Criteria:

* A `CurrentUser` class exists to store the logged-in `Person` object globally.
* Upon a successful 200 OK login response, the system parses the JSON user data and saves it to the session.
* Features like "Update Emergency Contact" can automatically retrieve the logged-in user's email from the session.
* The application prevents access to secured pages if the `CurrentUser` session is null (not logged in).

### User Story 7: Emergency Assistance Button (Ibrahim)

As a passenger or driver, I want to have access to an emergency button during rides so that I can quickly request for help if I feel unsafe or have an issue.

Acceptance Criteria: 

* The ride screen includes an emergency button visible at all times.
* Pressing the button instantly shares the ride’s live location with emergency contacts.
* The user can customize their emergency contacts.

### User Story 8: Ride History (Zack)

As a passenger or driver, I want to view past rides so that I can keep track of my travel history as well as expenses. Moreover I can track my commuting habits and manage my schedule efficiently.

Acceptance Criteria:

* The ride history tab displays previous rides with the date available.
* Each ride includes an option to view or download a receipt.
* Only completed rides appear in the history tab.

### User Story 9: Profile Customization (Arsh)

As a user, I want to be able to add a picture and a short bio to my profile so that other people can recognize me and feel more comfortable sharing rides.

Acceptance Criteria:

* I can upload a profile photo and write a short description about myself.
* My profile can show small badges like “Verified Driver” or “Top Passenger.”
* I can change or hide my info whenever I want.
* The app should flag any inappropriate photos or text.

### User Story 10 - View Profile (Arsh)

As a user, I want to see all my personal details on a dedicated profile screen, so that I can confirm my information is correct.

Acceptance Critiera:
* The user is able to access a read-only profile screen
* This screen displays the user’s full name, email, phone number, user type, and bio
* This screen displays the user’s profile photo.
* The screen has an “Edit Profile” button that, when clicked, opens the ProfileCustomizationGUI.

### User Story 15 - Notification Preferences (Zack)

As a user, I want to customize what notifications I receive so that I’m only alerted about important events.

Acceptance Criteria:

*Users can toggle on/off ride confirmations, cancellations, and reminders.
*Notification settings are saved in the user profile.
*System respects user preferences when sending alerts.

### User Story 17 - Emergency Contact Information (Sahil)

As a user, I want to save an emergency contact so that the system can notify someone if there’s an emergency during my ride.

Acceptance Criteria:

* Users can add/edit one emergency contact in their profile.
* Contact info is securely stored.
* Can be viewed by admin or safety system during emergency events.

### User Story 21 - DriverGUI (Zack)

As a driver,  I want a screen where I can see all my rides and the requests for each ride, so that I can quickly review and accept passengers without confusion.

Acceptance Criteria:

*DriverGUI shows a list of rides created by the logged-in driver.
*Selecting a ride displays its passenger requests.
*An “Accept Request” button is available and triggers the backend accept API.

### User Story 13 - Campus Map Integration

As a rider, I want to see pickup and drop-off locations and get directions for them using Google Maps.

Acceptance Criteria:

* Map view available for all ride posts.
* Directions to the destination and starting locations.
* Integration with Google Maps API or equivalent.

  ### User Story 12 - Carbon Footprint Tracker (Alp)

As a student,

I want to see how much CO₂ I’ve saved by carpooling so that I can feel motivated to ride sustainably.

Acceptance Criteria:

* The system calculates CO₂ saved per ride.
* Displays total CO₂ on the ride cards

### User Story 19 -Weather Viewing (Alp)

As a user,

I want to receive alerts about bad weather conditions that may affect my ride so that I can plan ahead or cancel safely.

Acceptance Criteria:

As a user,

I want to view the weather as a passenger. Both future and current forecast

Acceptance Criteria:

* An view of the current view in a given city
* A way to access to weather data for the upcoming days
