package com.tannerowens.a407_roommate_app;

import java.util.ArrayList;

/**
 * Created by Tanner on 4/7/2017.
 */

public class House {

    private String name;
    private ArrayList<User> users;
    //private ArrayLise<Chore> houseChores;

    public House(String name, User user){
        this.name = name;
        this.users.add(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}