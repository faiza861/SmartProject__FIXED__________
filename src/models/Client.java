package models;
public class Client extends User { public Client(){} public Client(String id,String name,String email,String password){super(id,name,email,password);} public String getRole(){return "CLIENT";} }
