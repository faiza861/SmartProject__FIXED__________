package models;
public class Admin extends User { 
    public Admin(){

    } 
    public Admin(String id,String name,String email,String password){
        
        super(id,name,email,password);

    } 
        public String getRole(){

            return "ADMIN";
            
        } 
    }
