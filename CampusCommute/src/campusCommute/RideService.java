package campusCommute;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Central ride service — now fully connected to backend REST API.
 */
public class RideService {

    private static final RideService INSTANCE = new RideService();
    public static RideService getInstance() { return INSTANCE; }

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private RideService() {}

    // ---------------------------------------------------------
    // LOAD ALL RIDES
    // ---------------------------------------------------------
    public Ride[] getAllRides() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides"))
                    .GET().build();

            HttpResponse<String> resp =
                    http.send(req, HttpResponse.BodyHandlers.ofString());

            return gson.fromJson(resp.body(), Ride[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Ride[0];
        }
    }

    // ---------------------------------------------------------
    // CREATE A NEW RIDE (driver)
    // ---------------------------------------------------------
    public Ride createRide(Ride ride) {
        try {
            String json = gson.toJson(ride);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides/create"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> resp =
                    http.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                return gson.fromJson(resp.body(), Ride.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------------------------------------------------------
    // SEND RIDE REQUEST (passenger)
    // ---------------------------------------------------------
    public boolean sendRideRequest(Long rideId, RideRequest requestBody) {
        try {
            String json = gson.toJson(requestBody);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides/" + rideId + "/request"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> resp =
                    http.send(req, HttpResponse.BodyHandlers.ofString());

            return resp.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------
    // PASSENGER RIDE HISTORY
    // ---------------------------------------------------------
    public Ride[] getPassengerHistory(long passengerId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:8080/api/history/passenger/" + passengerId
                    ))
                    .GET().build();

            HttpResponse<String> resp =
                    http.send(req, HttpResponse.BodyHandlers.ofString());

            return gson.fromJson(resp.body(), Ride[].class);

        } catch (Exception e) {
            return new Ride[0];
        }
    }

    // ---------------------------------------------------------
    // DRIVER RIDE HISTORY
    // ---------------------------------------------------------
    public Ride[] getDriverHistory(long driverId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:8080/api/history/driver/" + driverId
                    ))
                    .GET().build();

            HttpResponse<String> resp =
                    http.send(req, HttpResponse.BodyHandlers.ofString());

            return gson.fromJson(resp.body(), Ride[].class);

        } catch (Exception e) {
            return new Ride[0];
        }
    }

    // ---------------------------------------------------------
    // RATE A DRIVER (NEW METHOD)
    // ---------------------------------------------------------
    public boolean rateDriver(Long driverId, double rating) {
        Long passengerId = CurrentUser.getId();
        
        if (passengerId <= 0 || driverId == null || driverId <= 0) {
            return false;
        }

        try {
            RatingRequest reqBody = new RatingRequest(driverId, rating, passengerId);
            String json = gson.toJson(reqBody);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rate/driver"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

            return resp.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}