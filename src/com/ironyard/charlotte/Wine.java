package com.ironyard.charlotte;

import java.util.ArrayList;

/**
 * Created by lindseyringwald on 9/3/16.
 */
public class Wine {
    private String nameWine;
    private String rating;
    int id;
    int idNum;

    ArrayList<Wine> wines = new ArrayList<>();


    public Wine(String nameWine, String rating) {
        this.nameWine = nameWine;
        this.rating = rating;
        this.id = idNum++;
    }

    public String getNameWine() {
        return nameWine;
    }

    public void setNameWine(String nameWine) {
        this.nameWine = nameWine;
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
