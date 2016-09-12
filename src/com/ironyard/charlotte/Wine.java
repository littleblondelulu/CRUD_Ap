package com.ironyard.charlotte;

import java.util.ArrayList;

/**
 * Created by lindseyringwald on 9/3/16.
 */
public class Wine {
    private String wineName;
    private String rating;
    int id;
    int idNum;

    ArrayList<Wine> wines = new ArrayList<>();


    public Wine(String wineName, String rating) {
        this.wineName = wineName;
        this.rating = rating;
        this.id = idNum++;
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

    public int getIdNum() {
        return idNum;
    }

    public void setIdNum(int idNum) {
        this.idNum = idNum;
    }

}
