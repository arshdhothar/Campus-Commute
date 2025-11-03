package campusCommute;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
/**
 * This class handles appending a person object to the json file called userData.json at the parent folder of the project, it also has a method to check if a given string is in the file
 * heavy method implementation inspiration from : https://github.com/google/gson, implemented to this class to avoid implementing outside jars
 */
public class UserDataManager {
	//the directory of the json file
    private static final Path DATA_DIR  = Paths.get("ApplicationData");
    private static final Path DATA_FILE = DATA_DIR.resolve("userData.json");
    //starting string, must be like this
    private static final String EMPTY_DB = "{\"users\":[]}\n";

    /**
     * this method adds a given person to the json file,
     * @param p person to append 
     * @throws IOException 
     */
    public static void addUser(Person p) throws IOException {
    	//null check 
        if (p == null) throw new IllegalArgumentException("person cannot be null");
        //calls method to check if the json file is valid to append to, or exists
        ensureInitialized();
        
        
        //calls helper method to build a string that will be appended to the json file 
        String userJson = buildUserJson(p);

        //read content of the json file
        String content = Files.readString(DATA_FILE, StandardCharsets.UTF_8);
        
        //find the index of the user field
        int usersIdx = content.indexOf("\"users\"");
        //if this field does not exist, creates the string using the standard formatting string
        if (usersIdx < 0) {
            content = EMPTY_DB;
            //writes the formatted string in to the json file
            Files.writeString(DATA_FILE, content, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            //searchs for index of users again
            usersIdx = content.indexOf("\"users\"");
        }
        //each []block stores 1 users, finds the first and last instance of these brackets to figure out which part of the string we need to iterate
        int arrayStart = content.indexOf('[', usersIdx);
        int arrayEnd   = content.indexOf(']', arrayStart);
        //if they dont exist, adds them to the file for safekeeping
        if (arrayStart < 0 || arrayEnd < 0) {
            content = EMPTY_DB;
            Files.writeString(DATA_FILE, content, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            arrayStart = content.indexOf('[');
            arrayEnd   = content.indexOf(']');
        }
        //checks if there is data in between [] blocks,
        boolean isEmpty = content.substring(arrayStart + 1, arrayEnd).trim().isEmpty();
        //head of the string is stored so is the tail
        String head   = content.substring(0, arrayEnd);
        String tail   = content.substring(arrayEnd);
        //the data we need to store, if there is no starting data we start with nothing, if there is we add a comma to seperate persons
        String insert = (isEmpty ? "" : ",") + "\n" + userJson + "\n";
        //final string we want to add
        String updated = head + insert + tail;
        //safe keeping check to see if the file exists
        if (Files.notExists(DATA_DIR)) Files.createDirectories(DATA_DIR);
        //tmp file for data writing
        Path tmp = Files.createTempFile(DATA_DIR, "userData", ".tmp");
        //writes the new updated string to a tmp file instead of the acutal file
        Files.writeString(tmp, updated, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        //tries to replace the json file with the tmp file we created
        try {
            Files.move(tmp, DATA_FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        }

        //cleans up the tmp files we created
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DATA_DIR, "userData*.tmp")) {
            for (Path tmpFile : stream) {
                try { Files.deleteIfExists(tmpFile); } catch (IOException ignore) {}
            }
        }
    }

    /**
     * method to check if the json file content is correctly initliazed or exists
     * @throws IOException
     */
    private static void ensureInitialized() throws IOException {
    	
        if (Files.notExists(DATA_DIR)) Files.createDirectories(DATA_DIR);
        if (Files.notExists(DATA_FILE) || Files.size(DATA_FILE) == 0) {
            Files.writeString(DATA_FILE, EMPTY_DB, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return;
        }
        String s = Files.readString(DATA_FILE, StandardCharsets.UTF_8);
        if (!s.contains("\"users\"") || s.indexOf('[') < 0 || s.indexOf(']') < 0) {
            Files.writeString(DATA_FILE, EMPTY_DB, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
    }


/**
 * helper method to build the json file string which we write on to the file
 * @param p person we are appending
 * @return the final formatted string
 */
    private static String buildUserJson(Person p) {
    	//gets all the data from person class
        String email    = safe(p.getEmail());
        String phone    = safe(p.getPhone());
        String fName    = safe(p.getfName());
        String lName    = safe(p.getlName());
        String dob      = safe(p.getDOB());
        String password = safe(p.getPassword());
        String adress   = safe(p.getAdress());   
        String userType = safe(p.getUserType());
        //creates a chain string builder, the {} brackets are there to ensure each person is seperated
        return new StringBuilder()
            .append("{")
            .append("\"email\":\"").append(esc(email)).append("\",")
            .append("\"phone\":\"").append(esc(phone)).append("\",")
            .append("\"fName\":\"").append(esc(fName)).append("\",")
            .append("\"lName\":\"").append(esc(lName)).append("\",")
            .append("\"DOB\":\"").append(esc(dob)).append("\",")
            .append("\"password\":\"").append(esc(password)).append("\",")
            .append("\"adress\":\"").append(esc(adress)).append("\",")
            .append("\"userType\":\"").append(esc(userType)).append("\"")
            .append("}")
            .toString();
    }
/**
 * helper method to check if a given string is null, if its null returns a non null string
 * @param s string we want to check
 * @return non null string if null, same string otherwise
 */
    private static String safe(String s) { return s == null ? "" : s; }

 /**
  * helper method that ensures a given string we want to append to the json file is in valid form, makes sures the input is in valid json format
  * @param s string we want to check
  * @return
  */
    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        //checks if there are any special characters in the string, each special character is handled one by one
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b");  break;
                case '\f': out.append("\\f");  break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                default:
                	//if the ASCii value is less than 32(non printale char), convertes the character to a unicode escape sequence
                    if (c < 0x20) out.append(String.format("\\u%04x", (int)c));
                    else out.append(c);
            }
        }
        return out.toString();
    }
    /**
     * this method is used to check if a given email and password combination is in the json file
     * @param email
     * @param password
     * @return true if in the json false otherwise
     * @throws IOException
     */
    public static boolean validateCredentials(String email, String password) throws IOException {
    	//checks if json exists or is in valid format
        ensureInitialized();
        //reads full content
        String content = Files.readString(DATA_FILE, StandardCharsets.UTF_8);

        //calls helper to find the index of where the user is
        int objStart = findUserObjectStart(content, email);
        //if the email does not exist the user does not exists
        if (objStart < 0) return false;
        //finds the end of the person in the json file using helper method 
        int objEnd = findMatchingObjectEnd(content, objStart);
        if (objEnd < 0) return false;
        
        //seperates the string so we are left with the user data only
        String userObj = content.substring(objStart, objEnd + 1);

        //extracts the stored password
        String stored = extractJsonStringValue(userObj, "\"password\"");
        if (stored == null) return false;

        //returns if the given password matches the password in the file
        return stored.equals(password);
    }
 // ------------- helpers for validatecredentials -------------

    /**
     * helper method to locate the person with the given email, 
     * @param content content of file
     * @param email email we want to find 
     * @return -1 if it doesnt exists, index otherwise
     */
    private static int findUserObjectStart(String content, String email) {
    	//creates the string were looking for, uses esc method to ensure its safe string
        String emailKey = "\"email\":\"" + esc(email) + "\"";
        //finds index of the string
        int emailPos = content.indexOf(emailKey);
        if (emailPos < 0) return -1;
        // find the nearest { behind the email position, so the beginning of the user
        return content.lastIndexOf('{', emailPos);
    }

  
    /**
     * findsthe closing } of s a starting {,
     * @param s the string were searching
     * @param startBrace the starting brace index
     * @return index of closing bracket, -1 if nto found
     */
    private static int findMatchingObjectEnd(String s, int startBrace) {
        int depth = 0;
        //itereate the string
        for (int i = startBrace; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
              //skip literals
            	//returns index of closing quote for a string starting at opening quote index 'i'
                i = skipJsonString(s, i);
                if (i < 0) return -1;
                continue;
            }
            //if opening bracket then increase the depth
            if (c == '{') depth++;
            //if closing brakcet decrease the depth
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * this method helps us skip escpaed quotes so the iteration logic doesnt break
     * @param s string
     * @param i the index 
     * @return
     */
    private static int skipJsonString(String s, int i) {
        //index is the opening quote
        i++;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\') {//skips the escape char
                i += 2;
                continue;
            }
            if (c == '"') return i;
            i++;
        }
        return -1; //if we return -1, there is something wrong
    }

   /**
    * this method extracts a specific fields value from the json file
    * @param obj the person we want to extract from
    * @param keyLiteral the key we want to extract the data of
    * @return
    */
    private static String extractJsonStringValue(String obj, String keyLiteral) {
    	//finds the index of the key
        int k = obj.indexOf(keyLiteral);
        //if it doesnt exist return null string
        if (k < 0) return null;
        //finds the colon after the key
        int colon = obj.indexOf(':', k + keyLiteral.length());
        //if colon doesnt exist problem
        if (colon < 0) return null;

        //finds the first quote after the colon
        int startQuote = obj.indexOf('"', colon + 1);
        if (startQuote < 0) return null;

        //finds the closing quote after the colon
        int endQuote = skipJsonString(obj, startQuote);
        if (endQuote < 0) return null;

        //cleans up the unescapes of the json text value,undoing special characters
        String raw = obj.substring(startQuote + 1, endQuote);
        return unescapeMinimal(raw);
    }

    /**
     * this method reverses what the esc() method does, 
     * @param s string we are unescaping
     * @return
     */
    private static String unescapeMinimal(String s) {
    	//start building the string
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            //check for special chars,
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                //if any of these special chars are found, unescapes them to make them usable in java
                switch (n) {
                    case '"':  out.append('"');  i++; break;
                    case '\\': out.append('\\'); i++; break;
                    case 'b':  out.append('\b'); i++; break;
                    case 'f':  out.append('\f'); i++; break;
                    case 'n':  out.append('\n'); i++; break;
                    case 'r':  out.append('\r'); i++; break;
                    case 't':  out.append('\t'); i++; break;
                    case 'u':
                    	//ensures the string has enough space for 6 chars
                        if (i + 5 < s.length()) {
                        	//rxtracts the 4 hex digits
                            String hex = s.substring(i + 2, i + 6);
                            //converts the hex digits in to ac char
                            try { out.append((char) Integer.parseInt(hex, 16)); } catch (Exception ignore) {}
                            i += 5;
                            break;
                        }
                        //
                    default:
                        out.append(n); i++;
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
  
}
