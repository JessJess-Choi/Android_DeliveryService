package com.example.projectdelivery;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String UserId; //Minjh12
    public String UserPassword;
    public String Sex;
    public Long Age;
    public String Location;
    public String Name;
    
    
    public User(String user_id, String password, String age, String sex, String address, String name) {
        this.UserId = user_id;
        this.UserPassword = password;
        this.Sex = sex;
        this.Age = Long.valueOf(age);
        this.Location = address;
        this.Name = name;
        
        
    }
}