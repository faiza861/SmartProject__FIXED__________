package services;

import models.*;
import repository.UserRepository;

public class AuthService {

    public static User login(String email, String password) {
        return UserRepository.findByEmailAndPassword(email, password);
    }

    public static boolean register(String name, String email, String password, String role) {
        if (UserRepository.existsByEmail(email)) return false;

        String id = UserRepository.generateId(role);
        User user;

        switch(role) {
            case "MANAGER": user = new Manager(id,name,email,password); break;
            case "MEMBER":  user = new Member(id,name,email,password);  break;
            case "CLIENT":  user = new Client(id,name,email,password);  break;
            case "ADMIN":   user = new Admin(id,name,email,password);   break;
            default:        return false;
        }

        UserRepository.add(user);
        return true;
    }
}