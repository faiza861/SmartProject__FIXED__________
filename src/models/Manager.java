package models;

public class Manager extends User {

    public Manager() {}

    public Manager(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "MANAGER";
    }

}