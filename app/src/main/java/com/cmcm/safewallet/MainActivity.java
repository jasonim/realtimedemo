package com.cmcm.safewallet;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private FirebaseDatabase database;
    private Uri mInvitationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database
        database = FirebaseDatabase.getInstance();

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String uid = user.getUid();
        findViewById(R.id.button_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDeepLink();
            }
        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //
                        Log.e(TAG, "onSuccess" + pendingDynamicLinkData);
                        if (deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            Log.e(TAG, "onSuccess: uid " + referrerUid);
                            DatabaseReference users = database.getReference().child("users");
                            users.removeValue();
                            users.child("id_b").removeValue();

                            User user = new User("b", "id_a", 0, ServerValue.TIMESTAMP);
//                            DatabaseReference userRecord =
//                                    database.getReference()
//                                            .child("users")
//                                            .child("id_b");
//                            userRecord.child("last_signin_at").setValue(ServerValue.TIMESTAMP);
                            users.child("id_b").setValue(user);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//
//        database.getReference("users").removeValue();
//
//        writeNewUser("id_a", "a", null);
//
//        Log.e(TAG, "onCreate: doTransaction");
//
//        writeNewUser("id_b", "b", "id_a");
//        writeNewUser("id_c", "c", "id_b");
//        writeNewUser("id_d", "d", "id_b");

//
//        writeNewPost("id_a", "a", "test", "body");
//
//        onStarClicked(database.getReference("posts").child("-LBV9-oMHwzrYKsmymoD"));

    }

    private void shareDeepLink() {
        String link = "https://example.com/?invitedby=id_a";
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("ud578.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder()
                                .setMinimumVersion(0)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        Log.e(TAG, "onSuccess: " + mInvitationUrl.toString());
                        sendToInvite();

                    }
                });

//        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
//                .createDynamicLink()
//                .setDynamicLinkDomain("s444d.app.goo.gl")
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
//                        .setMinimumVersion(0)
//                        .build())
//                .setLink(Uri.parse(link));
//        DynamicLink dynlink = builder.buildDynamicLink();
//        mInvitationUrl = dynlink.getUri();
//        sendToInvite();
    }


    private void sendToInvite() {
//        String referrerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String subject = String.format("%s wants you to play MyExampleGame!", "a");
        String invitationLink = mInvitationUrl.toString();
        String msg = "Let's play MyExampleGame together! Use my referrer link: "
                + invitationLink;
        String msgHtml = String.format("<p>Let's play MyExampleGame together! Use my "
                + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setData(Uri.parse("share:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        intent.setType("text/plain");
//        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
//        }

    }
    private void writeNewUser(String userId, String name, String parent) {
        User user = new User(name, parent, 0, null);


        DatabaseReference userDatabaseRef = database.getReference("users");
        userDatabaseRef.child(userId).setValue(user);
        updateChild(userDatabaseRef, userId, 0);
    }

    private void updateChild(final DatabaseReference userDatabaseRef, String userId, final int level) {
        Log.e(TAG, "updateChild: " + userId );
        userDatabaseRef.child(userId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User p = mutableData.getValue(User.class);

//                Log.e(TAG, "doTransaction: p is null");
                if (p == null) {
                    return Transaction.success(mutableData);
                }

//                p.score = level;
//                mutableData.setValue(p);
//                Log.e(TAG, "doTransaction: " + p.score + " name: " + p.username + " parent: " + p.parent);
//                if (p.parent == null) {
//                    return Transaction.success(mutableData);
//                }
//
//
//
//                updateChild(userDatabaseRef, p.parent, level + 1);
//                Log.e(TAG, "doTransaction: end " + p.score + " parent: " + p.parent + " name: " + p.username);
//                // Set value and report transaction success

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

    }

    private void update(final DatabaseReference userDatabaseRef, String userId, final int level) {
        if (userDatabaseRef.child(userId).child("parent").getKey() == null) {
            return;
        }

        userDatabaseRef.child(userId).child("score").setValue(level);

//        update(userDatabaseRef, );
    }


    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = database.getReference().child("posts").push().getKey();

        Log.e(TAG, "writeNewPost: " + key);
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        database.getReference().updateChildren(childUpdates);
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public String getUid() {
        return "id_b";
    }

}
