package com.vb.vaultbox.models;

// [START blog_user_class]
public class User {

    public String username;
    public String email;
    public String id;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String id) {
        this.username = username;
        this.email = email;
        this.id=id;
    }

}
// [END blog_user_class]
