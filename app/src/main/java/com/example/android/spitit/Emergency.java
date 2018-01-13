package com.example.android.spitit;

/**
 * Created by aashayjain611 on 03/01/18.
 */

public class Emergency
{
    String people;
    String location,tip,type;

    public String getPeople() {
        return people;
    }

    public Emergency() {
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Emergency(String people, String location, String tip, String type) {

        this.people = people;
        this.location = location;
        this.tip = tip;
        this.type = type;
    }
}
