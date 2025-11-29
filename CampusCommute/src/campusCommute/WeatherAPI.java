package campusCommute;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class WeatherAPI {

   
   
    private static final String TEMPLATE =
            "https://api.weatherapi.com/v1/current.json?key=fe4915c7db5d4f659b532606252911&q=%s&aqi=yes";

    public static WeatherInfo getWeather(String cityName) {
        try {
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            String urlStr = String.format(TEMPLATE, encodedCity);

            URL url = URI.create(urlStr).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (InputStreamReader reader = new InputStreamReader(con.getInputStream())) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                JsonObject location = root.getAsJsonObject("location");
                JsonObject current  = root.getAsJsonObject("current");
                JsonObject cond     = current.getAsJsonObject("condition");
                JsonObject aq       = current.has("air_quality") && !current.get("air_quality").isJsonNull()
                                        ? current.getAsJsonObject("air_quality") : null;

                WeatherInfo info = new WeatherInfo();

                // City + time
                info.setCity(location.get("name").getAsString());                // City
                info.setProvince(location.get("region").getAsString());
                info.setTimeDate(location.get("localtime").getAsString());       // e.g. "2025-11-28 22:31"

                // Core weather (strings to match your class)
                info.setTemp(current.get("temp_c").getAsString());               // °C
                info.setIsDay(current.get("is_day").getAsString());              // "0" or "1"
                info.setCondition(cond.get("text").getAsString());               // e.g. "Clear"
                info.setWindSpeed(current.get("wind_kph").getAsString());        // km/h
                info.setHumidity(current.get("humidity").getAsString());         // %
                info.setFeelsLike(current.get("feelslike_c").getAsString());     // °C
                info.setUv(current.get("uv").getAsString());                     // index value

                // EPA index (1..6). Present when aqi=yes.
                String epa = (aq != null && aq.has("us-epa-index"))
                        ? aq.get("us-epa-index").getAsString()
                        : "N/A";
                info.setEpaIndex(epa);

                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
    }
  
}