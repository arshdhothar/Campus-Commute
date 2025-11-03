package campusCommute;

import java.util.ArrayList;
import java.util.List;

public class Passenger {
    private String name;

    public Passenger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Search rides by start location, destination, or time
    public List<Ride> searchRides(List<Ride> rides, String query) {
        List<Ride> results = new ArrayList<>();
        query = query.toLowerCase();

        for (Ride r : rides) {
            if (r.getStartLocation().toLowerCase().contains(query) ||
                r.getDestination().toLowerCase().contains(query) ||
                r.getDepartureTime().toLowerCase().contains(query)) {
                results.add(r);
            }
        }
        return results;
    }
}
