package repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.*;
import utils.JSONFileHandler;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

public class UserRepository {

    private static final Path PATH = Path.of("data/users.json");
    private static final List<User> users = new ArrayList<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads all users from JSON file. Seeds default users if file is empty or missing.
     */
    public static void loadAll() {
        try {
            String json = JSONFileHandler.read(PATH.toString());

            if (json == null || json.trim().isEmpty()) {
                JSONFileHandler.write(PATH.toString(), "[]");
                seed();
                saveAll();
                return;
            }

            Type listType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> arr = gson.fromJson(json, listType);

            users.clear();
            for (JsonObject jo : arr) {
                String role = jo.get("role").getAsString();
                switch (role) {
                    case "ADMIN": users.add(gson.fromJson(jo, Admin.class)); break;
                    case "MANAGER": users.add(gson.fromJson(jo, Manager.class)); break;
                    case "MEMBER": users.add(gson.fromJson(jo, Member.class)); break;
                    case "CLIENT": users.add(gson.fromJson(jo, Client.class)); break;
                    default: users.add(gson.fromJson(jo, Member.class)); break;
                }
            }

            if (users.isEmpty()) {
                seed();
                saveAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            seed();
            saveAll();
        }
    }

    /** Seeds default users for the application. */
    private static void seed() {
        users.clear();
        users.add(new Admin("adm-1","Sana Admin","admin@smartpm.com","admin123"));
        users.add(new Manager("mgr-1","Ayesha Manager","mgr@smartpm.com","mgr123"));
        users.add(new Member("mem-1","Ali Dev","ali@smartpm.com","mem123"));
        users.add(new Client("cli-1","Acme Corp","client@acme.com","client1"));
    }

    /** Save all users to JSON */
    public static void saveAll() {
        try {
            java.nio.file.Files.createDirectories(PATH.getParent());
            List<JsonObject> arr = new ArrayList<>();
            for (User u : users) {
                JsonObject jo = (JsonObject) gson.toJsonTree(u);
                jo.addProperty("role", u.getRole());
                arr.add(jo);
            }
            String out = gson.toJson(arr);
            JSONFileHandler.write(PATH.toString(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Finds a user by email and password. Returns null if not found. */
    public static User findByEmailAndPassword(String email, String password) {
        return users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
    }

    /** Checks if a user exists by email. */
    public static boolean existsByEmail(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    /** Adds a new user and immediately saves all users. */
    public static void add(User u) {
        if (u != null) {
            users.add(u);
            saveAll();
        }
    }

    /** Returns a read-only list of all users. */
    public static List<User> getAll() {
        return Collections.unmodifiableList(users);
    }

    /** Generates a unique user ID based on role. */
    public static String generateId(String role) {
        if (role == null || role.length() < 3) role = "usr";
        return role.substring(0, 3).toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
    /**
     * Returns all users with role MEMBER.
     */
    public static List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Member) members.add((Member) u);
        }
        return members;
    }

    /**
     * Get a member by their ID.
     */
    public static Member getMemberById(String id) {
        for (User u : users) {
            if (u.getId().equals(id) && u instanceof Member) {
                return (Member) u;
            }
        }
        return null;
    }
}