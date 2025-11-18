package campusCommute;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmergencyAlertRequest {

    public static boolean send(String userName, String userPhone, String rideId,
                               double lat, double lng) {
        try {
            URL url = new URL("http://localhost:8080/api/emergency-alert");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            // Using a JSON string builder or library (like GSON) is safer,
            // but for a simple object, this works.
            String json = "{"
                    + "\"userName\":\"" + userName + "\","
                    + "\"userPhone\":\"" + userPhone + "\","
                    + "\"rideId\":\"" + rideId + "\","
                    + "\"lat\":" + lat + ","
                    + "\"lng\":" + lng
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            // 200 (OK) or 202 (Accepted) are both good responses
            return code == 200 || code == 202;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}