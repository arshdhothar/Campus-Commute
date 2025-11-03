package campusCommute;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RideManager {
    private static final String FILE_NAME = "rides.txt";

    // Save all rides to a text file
    public static void saveRides(List<Ride> rides) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Ride r : rides) {
                writer.write(r.getStartLocation() + "," + r.getDestination() + "," + 
                             r.getDepartureTime() + "," + r.getAvailableSeats() + "," + 
                             r.getPrice());
                writer.newLine();
            }
            System.out.println("✅ Rides saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load rides from file
    public static List<Ride> loadRides() {
        List<Ride> rides = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return rides;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    Ride r = new Ride(data[0], data[1], data[2],
                                      Integer.parseInt(data[3]),
                                      Double.parseDouble(data[4]));
                    rides.add(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rides;
    }
}
