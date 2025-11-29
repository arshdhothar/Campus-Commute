package campusCommute;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;
public class CarbonCalculator {
		
	private static String linkTemplate ="https://api-v2.distancematrix.ai/maps/api/distancematrix/json?origins=%s&destinations=%s&key=htIIVze7NXqOdcWajxMiFNkeJWRW5LSansfxEWuB68zpRIcySsC68UL8iR89wznx";
	private static final String appK = "htIIVze7NXqOdcWajxMiFNkeJWRW5LSansfxEWuB68zpRIcySsC68UL8iR89wznx";
	private static final double avgCarbonPerM = 0.00016;
	private static final String testingLink = "https://jsonplaceholder.typicode.com/todos/1";
	/*
	 * https://api-v2.distancematrix.ai/maps/api/distancematrix/
	 * json?origins=119+hillsdale+aveneueast+toronto&destinations=york+university+ontario&key=htIIVze7NXqOdcWajxMiFNkeJWRW5LSansfxEWuB68zpRIcySsC68UL8iR89wznx
	 */
	public static String getCarbon(String start,String destination) {
		//URL BUILDING PART
		if(start.length()==0||destination.length()==0) {
			return "N/A";
		}
		if(!(start.toLowerCase().contains("toronto"))) {
			start = start +" toronto";
		}
		if(!(destination.toLowerCase().contains("toronto"))) {
			destination = destination +" toronto";
		}
		String origin =  URLEncoder.encode(start, StandardCharsets.UTF_8);
		String dest = URLEncoder.encode(destination, StandardCharsets.UTF_8);
		String urlCreated = String.format(linkTemplate, origin, dest);
		
		//URL BUILDING PART DONE
		//HTTPS PART
		try {
			URL url = URI.create(urlCreated).toURL();
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		  JsonObject obj = JsonParser.parseReader(
		            new InputStreamReader(con.getInputStream())
		    ).getAsJsonObject();
		

		    int distanceMeters =
		            obj.getAsJsonArray("rows")
		               .get(0).getAsJsonObject()
		               .getAsJsonArray("elements")
		               .get(0).getAsJsonObject()
		               .getAsJsonObject("distance")
		               .get("value").getAsInt();
		
		    if(distanceMeters >= 200000) {
		    	distanceMeters = 15000 + (int)(Math.random() * (50000 - 15000 + 1));
		    }
		    double emission = distanceMeters * CarbonCalculator.avgCarbonPerM;
		    
		    return ""+emission;
	} catch(Exception e) {
		e.printStackTrace();
		System.out.println("ERROR");
		return "N/A";
		
	}
		
		
		
	}
	public static void main(String[] args) {
		System.out.println(CarbonCalculator.getCarbon("1 Manor Lane","York University"));
	
	}

}

/**
 * try {
		URL url = URI.create("https://jsonplaceholder.typicode.com/todos/1").toURL();
	HttpURLConnection con = (HttpURLConnection) url.openConnection();
	
	JsonObject obj = JsonParser.parseReader(new InputStreamReader(con.getInputStream()))
	                           .getAsJsonObject();
	
	String title = obj.get("title").getAsString();
	
	System.out.println(title);
} catch(IOException e) {
	System.out.println("ERROR");
}*/
