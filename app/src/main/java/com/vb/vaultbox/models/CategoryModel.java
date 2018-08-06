package com.vb.vaultbox.models;

/**
 * Created by Pradeep on 6/22/2018.
 */

public class CategoryModel {
    String catID;
    String catName;
    String catIcon;
    String catImg;
    String superSubCat;

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatIcon() {
        return catIcon;
    }

    public void setCatIcon(String catIcon) {
        this.catIcon = catIcon;
    }

    public String getCatImg() {
        return catImg;
    }

    public void setCatImg(String catImg) {
        this.catImg = catImg;
    }

    public String getSuperSubCat() {
        return superSubCat;
    }

    public void setSuperSubCat(String superSubCat) {
        this.superSubCat = superSubCat;
    }
}
