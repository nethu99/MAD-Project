package com.example.usermanagement.Views;

public class Member {
    private String Name;
    private String Email;
    private long Mobile;
    private  String Location;
    private String password;
    private String uid;

    public Member(String name, String email, long mobile, String location, String password, String uid) {
        Name = name;
        Email = email;
        Mobile = mobile;
        Location = location;
        this.password = password;
        this.uid = uid;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public long getMobile() {
        return Mobile;
    }

    public void setMobile(long mobile) {
        Mobile = mobile;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }


    public Member()
    {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
