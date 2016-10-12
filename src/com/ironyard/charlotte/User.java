package com.ironyard.charlotte;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindseyringwald on 9/3/16.
 */
public class User {
    int id;
    String name;
    String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    List<Wine> wines = new ArrayList<>();

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }


}
