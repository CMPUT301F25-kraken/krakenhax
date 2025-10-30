package com.kraken.krakenhax;
import java.io.Serializable;
public class Profile implements Serializable{
    private String username;
    private String password;
    private String email;
    private String type;

    public Profile(String UName,String pwd,String tp, String UEmail){
        this.username = UName;
        this.password = pwd;
        this.type = tp;
        this.email = UEmail;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    public String getType() {
        return type;
    }

    public void setUsername(String newUsern){
        this.username = newUsern;
    }

    public void setPassword(String newPWD){
        this.password = newPWD;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

}
