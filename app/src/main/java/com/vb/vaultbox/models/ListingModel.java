package com.vb.vaultbox.models;

import java.util.ArrayList;

/**
 * Created by Pradeep on 7/15/2018.
 */

public class ListingModel {
//    {"ListingID":"18","Title":"Property","Description":"Property","Image":[{"ListImage0":"https:\/\/sparsematrixtechnology.com\/vaultbox\/\/images\/listingImg\/17422592691.jpg"}
//,"Category_ID":"3","Category_Name":"Real Estate","Sub_Category_ID":"1","Sub_Category_Name":"property",
// "Super_Sub_Category_ID":"1","Super_Sub_Category_Name":null,
// "Price":"123456","Auction":"0","Seller_ID":"1","City":"","Add_Date":"2018-06-05 00:00:00","Total_Likes":null}

//{"ListingID":"55","Title":"gh","Description":"tfr",
// "ListImage":"http:\/\/media.sparsematrixtechnology.com\/images\/listing\/18487932161.jpg",
// "Price":"657","Auction":"1","SellerUsername":"pathak",
// "SellerImage":"http:\/\/media.sparsematrixtechnology.com\/images\/user\/Image-1526201472193.webp",
// "City":"Aligarh","Add_Date":"2018-06-28 03:26:40",
// "Total_Likes":"1"},
    String ListingID;
    String Title;
    String Description;
    String ListImage;
    ArrayList<String> Image;
    String Category_ID;
    String Category_Name;
    String Sub_Category_ID;
    String Sub_Category_Name;
    String Super_Sub_Category_ID;
    String Super_Sub_Category_Name;
    String Price;
    String Auction;
    String Seller_ID;
    String SellerUsername;
    String SellerImage;
    String City;
    String Add_Date;
    String Total_Likes;

    public String getListingID() {
        return ListingID;
    }

    public void setListingID(String listingID) {
        ListingID = listingID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public ArrayList<String> getImage() {
        return Image;
    }

    public void setImage(ArrayList<String> image) {
        Image = image;
    }

    public String getCategory_ID() {
        return Category_ID;
    }

    public void setCategory_ID(String category_ID) {
        Category_ID = category_ID;
    }

    public String getCategory_Name() {
        return Category_Name;
    }

    public void setCategory_Name(String category_Name) {
        Category_Name = category_Name;
    }

    public String getSub_Category_ID() {
        return Sub_Category_ID;
    }

    public void setSub_Category_ID(String sub_Category_ID) {
        Sub_Category_ID = sub_Category_ID;
    }

    public String getSub_Category_Name() {
        return Sub_Category_Name;
    }

    public void setSub_Category_Name(String sub_Category_Name) {
        Sub_Category_Name = sub_Category_Name;
    }

    public String getSuper_Sub_Category_ID() {
        return Super_Sub_Category_ID;
    }

    public void setSuper_Sub_Category_ID(String super_Sub_Category_ID) {
        Super_Sub_Category_ID = super_Sub_Category_ID;
    }

    public String getSuper_Sub_Category_Name() {
        return Super_Sub_Category_Name;
    }

    public void setSuper_Sub_Category_Name(String super_Sub_Category_Name) {
        Super_Sub_Category_Name = super_Sub_Category_Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getAuction() {
        return Auction;
    }

    public void setAuction(String auction) {
        Auction = auction;
    }

    public String getSeller_ID() {
        return Seller_ID;
    }

    public void setSeller_ID(String seller_ID) {
        Seller_ID = seller_ID;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAdd_Date() {
        return Add_Date;
    }

    public void setAdd_Date(String add_Date) {
        Add_Date = add_Date;
    }

    public String getTotal_Likes() {
        return Total_Likes;
    }

    public void setTotal_Likes(String total_Likes) {
        Total_Likes = total_Likes;
    }

    public String getListImage() {
        return ListImage;
    }

    public void setListImage(String listImage) {
        ListImage = listImage;
    }

    public String getSellerUsername() {
        return SellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        SellerUsername = sellerUsername;
    }

    public String getSellerImage() {
        return SellerImage;
    }

    public void setSellerImage(String sellerImage) {
        SellerImage = sellerImage;
    }
}
