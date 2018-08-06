package com.vb.vaultbox.models;

/**
 * Created by Pradeep on 6/22/2018.
 */

public class FollowModel {
    String image;
    String id;
    String FirstNAme;
    String LastName;
    String Email;
    String address;
//    {
//        "FollowingID":"1", "FirstNAme":"yuvi gaur", "LastName":"", "Email":
//        "yagnihotri68@gmail.com", "Image":"730449270play.png"
//    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstNAme() {
        return FirstNAme;
    }

    public void setFirstNAme(String firstNAme) {
        FirstNAme = firstNAme;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
