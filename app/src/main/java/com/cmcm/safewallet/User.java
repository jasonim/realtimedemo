package com.cmcm.safewallet;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String username;
    public String referredBy;
    public int score;
    public Map<String, String> last_signin_at;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String referredBy, int score, Map<String, String> last_signin_at) {
        this.username = username;
        this.referredBy = referredBy;
        this.score = score;
        this.last_signin_at = last_signin_at;
    }

}
