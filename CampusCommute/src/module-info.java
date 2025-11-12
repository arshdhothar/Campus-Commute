module CampusCommute {
    requires java.desktop; 
    requires java.net.http;
    
    // This line lets YOU use GSON
    requires com.google.gson; 
    
    // ADD THIS LINE:
    // This lets GSON use YOUR code (via reflection)
    opens campusCommute to com.google.gson;
}