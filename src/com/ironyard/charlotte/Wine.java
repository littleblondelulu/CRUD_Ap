package com.ironyard.charlotte;

import java.util.ArrayList;

/**
 * Created by lindseyringwald on 9/3/16.
 */
public class Wine {
    private String wineName;
    private String rating;
    int id;
    User user;

    public Wine(String wineName, String rating) {
//        this.userId = userId;
        this.wineName = wineName;
        this.rating = rating;
    }


    public Wine(String wineName, String rating, int id) {
        this.wineName = wineName;
        this.rating = rating;
        this.id = id;
    }

    public String getWineName() {
        return wineName;
    }

    public void setWineName(String wineName) {
        this.wineName = wineName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
