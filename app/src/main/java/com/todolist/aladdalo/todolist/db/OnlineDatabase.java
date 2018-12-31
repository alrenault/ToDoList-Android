package com.todolist.aladdalo.todolist.db;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class OnlineDatabase{
    //id projet firebase : todolist-cf1df
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final Activity act;
    private FirebaseUser user = null;
    private boolean signIn = false;

    public OnlineDatabase(Activity act){
        this.act = act;
    }

    /**
     *
     * */
    private class createUserListener implements OnCompleteListener<AuthResult> {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.v("aaa", "createUserWithEmail:success");
                user = mAuth.getCurrentUser();
                //updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.v("aaa", "createUserWithEmail:failure");
                //Toast.makeText(act, "Authentication failed.",
                //        Toast.LENGTH_SHORT).show();
                //updateUI(null);
                user = null;
            }
        }
    }

    /**
     *
     * */
    private class SignInListener implements OnCompleteListener<AuthResult> {


        @Override
        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.v("aaa", "signInWithEmail:success");
                //FirebaseUser user = mAuth.getCurrentUser();
                signIn = true;
                //updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.v("aaa", "signInWithEmail:failure", task.getException());
                //Toast.makeText(act, "Authentication failed.",
                //        Toast.LENGTH_SHORT).show();
                //updateUI(null);
                signIn = false;
            }
        }
    }

    /**
     *
     * */
    private class ReadListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String value = dataSnapshot.getValue(String.class);
            Log.v("aaa", "Value is: " + value);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.v("aaa", "Failed to read value.", error.toException());
        }
    }

    /**
     *
     * */
    public FirebaseUser createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new createUserListener());
        return user;
    }

    /**
     *
     * */
    public boolean signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new SignInListener());
        return signIn;
    }

    /**
     *
     * */
    public<T> void writeData(T message, String ref){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ref);

        myRef.setValue("Hello, World!");
        Log.v("aaa", "write "+message+" in "+ref);
    }

    /**
     *
     * */
    public<T> T readData(String ref){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ref);

        // Read from the database
        myRef.addValueEventListener(new ReadListener());
        return null;
    }

    public void getInfos(String email, String password) {
        Log.v("aaa", "getInfos");
        Log.v("aaa", "mAuth : "+mAuth);

        createUser(email, password);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (signIn(email, password)) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String mail = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            Log.v("aaa", "infos : name="+name+" mail="+mail+" photoUrl="+photoUrl+" emailVerified="+emailVerified+" uid="+user.getUid());

        }else{
            Log.v("aaa", "noUserData");
        }
        writeData("coucou", "hello");
        readData("hello");
        writeData("coucou2 - lue ?", "hello");
        Log.v("aaa", "startactivity");
    }
}
