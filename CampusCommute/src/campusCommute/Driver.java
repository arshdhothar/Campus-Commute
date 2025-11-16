package campusCommute;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Frontend Driver model — backend-aware and safe.
 * Publishes rides to /api/rides/create.
 */
public class Driver {

    private final String email;
    private final String displayName;
    private final List<Ride> myRides = new ArrayList<>();

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public Driver(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public List<Ride> getMyRides() { return myRides; }

    /**
     * Publish a ride to the backend server.
     * Returns the saved ride (with ID assigned by backend).
     */
    public Ride publishRide(String start, String dest, String departureTime, int seats, double price) {

        Ride ride = new Ride(start, dest, departureTime, seats, price);

        // ⭐ IMPORTANT: use logged-in user ID
        ride.setDriverId(CurrentUser.getId());
        ride.setStatus("PENDING");
        ride.setDate(java.time.LocalDate.now().toString());

        try {
            String json = gson.toJson(ride);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides/create"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Backend usually returns 201 for new creations
            if (response.statusCode() == 200 || response.statusCode() == 201) {

                Ride savedRide = null;

                // backend might send JSON or might send empty body
                if (response.body() != null && !response.body().isEmpty()) {
                    savedRide = gson.fromJson(response.body(), Ride.class);
                } else {
                    // fallback: use local object but still treat as success
                    savedRide = ride;
                }

                myRides.add(savedRide);
                return savedRide;
            }

            System.out.println("❌ Server returned error " + response.statusCode());

        } catch (Exception e) {
            System.out.println("❌ Network error while creating ride:");
            e.printStackTrace();
        }

        return null; // failed
    }
}
