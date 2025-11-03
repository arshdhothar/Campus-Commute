package campusCommute;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class UserDataManager {

    private static final Path DATA_DIR  = Paths.get("ApplicationData");
    private static final Path DATA_FILE = DATA_DIR.resolve("userData.json");
    private static final String EMPTY_DB = "{\"users\":[]}\n";

    /** Appends a Person to ApplicationData/userData.json (no external libs). */
    public static void addUser(Person p) throws IOException {
        if (p == null) throw new IllegalArgumentException("person cannot be null");
        ensureInitialized();

        String userJson = buildUserJson(p);

        // Read whole file (no handle kept open)
        String content = Files.readString(DATA_FILE, StandardCharsets.UTF_8);

        int usersIdx = content.indexOf("\"users\"");
        if (usersIdx < 0) {
            content = EMPTY_DB;
            Files.writeString(DATA_FILE, content, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            usersIdx = content.indexOf("\"users\"");
        }

        int arrayStart = content.indexOf('[', usersIdx);
        int arrayEnd   = content.indexOf(']', arrayStart);
        if (arrayStart < 0 || arrayEnd < 0) {
            content = EMPTY_DB;
            Files.writeString(DATA_FILE, content, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            arrayStart = content.indexOf('[');
            arrayEnd   = content.indexOf(']');
        }

        boolean isEmpty = content.substring(arrayStart + 1, arrayEnd).trim().isEmpty();

        String head   = content.substring(0, arrayEnd);
        String tail   = content.substring(arrayEnd);
        String insert = (isEmpty ? "" : ",") + "\n" + userJson + "\n";
        String updated = head + insert + tail;

        if (Files.notExists(DATA_DIR)) Files.createDirectories(DATA_DIR);
        Path tmp = Files.createTempFile(DATA_DIR, "userData", ".tmp");
        Files.writeString(tmp, updated, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

        try {
            Files.move(tmp, DATA_FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        }

        // Cleanup stray temp files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DATA_DIR, "userData*.tmp")) {
            for (Path tmpFile : stream) {
                try { Files.deleteIfExists(tmpFile); } catch (IOException ignore) {}
            }
        }
    }

    /** Ensure base file exists and is minimally valid. */
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

    /** Build JSON object string from Person using getters (matches your current fields). */
    private static String buildUserJson(Person p) {
        String email    = safe(p.getEmail());
        String phone    = safe(p.getPhone());
        String fName    = safe(p.getfName());
        String lName    = safe(p.getlName());
        String dob      = safe(p.getDOB());
        String password = safe(p.getPassword());
        String adress   = safe(p.getAdress());   // keeping your current spelling
        String userType = safe(p.getUserType());

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

    private static String safe(String s) { return s == null ? "" : s; }

    /** Minimal JSON escaping for quotes, backslashes, and control chars. */
    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
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
                    if (c < 0x20) out.append(String.format("\\u%04x", (int)c));
                    else out.append(c);
            }
        }
        return out.toString();
    }
    public static boolean validateCredentials(String email, String password) throws IOException {
        ensureInitialized();
        String content = Files.readString(DATA_FILE, StandardCharsets.UTF_8);

        // Find the user object that matches this email
        int objStart = findUserObjectStart(content, email);
        if (objStart < 0) return false;
        int objEnd = findMatchingObjectEnd(content, objStart);
        if (objEnd < 0) return false;

        String userObj = content.substring(objStart, objEnd + 1);

        // Extract stored password
        String stored = extractJsonStringValue(userObj, "\"password\"");
        if (stored == null) return false;

        // Plain-text compare (for now).
        return stored.equals(password);
    }
 // ------------- helpers (package-private/private) -------------

    /** Locate the start index of the user object containing this email, or -1 if not found. */
    private static int findUserObjectStart(String content, String email) {
        String emailKey = "\"email\":\"" + esc(email) + "\"";
        int emailPos = content.indexOf(emailKey);
        if (emailPos < 0) return -1;
        // find the nearest '{' before emailPos (start of this object's JSON)
        return content.lastIndexOf('{', emailPos);
    }

    /** From the given object start index ('{'), find the matching closing '}' considering nested braces. */
    private static int findMatchingObjectEnd(String s, int startBrace) {
        int depth = 0;
        for (int i = startBrace; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
                // skip string literals to avoid counting braces inside strings
                i = skipJsonString(s, i);
                if (i < 0) return -1;
                continue;
            }
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /** Returns index of closing quote for a JSON string starting at opening quote index 'i'. */
    private static int skipJsonString(String s, int i) {
        // i is at the opening quote
        i++;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\') { // skip escaped char
                i += 2;
                continue;
            }
            if (c == '"') return i;
            i++;
        }
        return -1; // malformed
    }

    /** Extracts a JSON string value for a given key inside a JSON object string without full parsing. */
    private static String extractJsonStringValue(String obj, String keyLiteral) {
        int k = obj.indexOf(keyLiteral);
        if (k < 0) return null;
        int colon = obj.indexOf(':', k + keyLiteral.length());
        if (colon < 0) return null;

        // find first quote after colon
        int startQuote = obj.indexOf('"', colon + 1);
        if (startQuote < 0) return null;

        // find matching closing quote respecting escapes
        int endQuote = skipJsonString(obj, startQuote);
        if (endQuote < 0) return null;

        // Unescape minimal JSON escapes (we only ever escaped quotes/backslashes/control chars when writing)
        String raw = obj.substring(startQuote + 1, endQuote);
        return unescapeMinimal(raw);
    }

    /** Reverse of esc() minimal unescape for quotes/backslashes/control chars. */
    private static String unescapeMinimal(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                switch (n) {
                    case '"':  out.append('"');  i++; break;
                    case '\\': out.append('\\'); i++; break;
                    case 'b':  out.append('\b'); i++; break;
                    case 'f':  out.append('\f'); i++; break;
                    case 'n':  out.append('\n'); i++; break;
                    case 'r':  out.append('\r'); i++; break;
                    case 't':  out.append('\t'); i++; break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            String hex = s.substring(i + 2, i + 6);
                            try { out.append((char) Integer.parseInt(hex, 16)); } catch (Exception ignore) {}
                            i += 5;
                            break;
                        }
                        // fallthrough if malformed
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
