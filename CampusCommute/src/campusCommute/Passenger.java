package campusCommute;

import java.util.List;

/**
 * Passenger model for frontend — fully backend-connected.
 * Uses RideService REST calls.
 */
public class Passenger {

    private String name;

    public Passenger(String name) {
        this.name = name;
    }

    public String getName() { 
        return name; 
    }

    // ------------------------------------------------------
    // SEARCH RIDES (backend)
    // ------------------------------------------------------
    public List<Ride> searchRides(String query) {
        Ride[] all = RideService.getInstance().getAllRides();

        // local filtering only, because backend does not filter
        return java.util.Arrays.stream(all)
                .filter(r ->
                        r.getStartLocation().toLowerCase().contains(query.toLowerCase()) ||
                        r.getDestination().toLowerCase().contains(query.toLowerCase()) ||
                        r.getDepartureTime().toLowerCase().contains(query.toLowerCase())
                )
                .toList();
    }

    // ------------------------------------------------------
    // SEND RIDE REQUEST (backend)
    // ------------------------------------------------------
    public boolean sendRideRequest(Ride ride) {
        long passengerId = CurrentUser.loggedInUser.getID();
        String passengerName = CurrentUser.getName();

        RideRequest reqBody = new RideRequest(passengerId, passengerName);
        return RideService.getInstance().sendRideRequest(ride.getId(), reqBody);
    }

    // ------------------------------------------------------
    // CANCEL REQUEST → Not implemented yet in backend
    // ------------------------------------------------------
    public boolean cancelRideRequest(Ride ride) {
        // You can implement backend cancellation later
        System.out.println("[INFO] Cancel request not implemented.");
        return false;
    }
}
