package models;
public class Member extends User { public Member(){} public Member(String id,String name,String email,String password){super(id,name,email,password);} public String getRole(){return "MEMBER";} }
