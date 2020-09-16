package com.lightidea.taxidriver.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lightidea.taxidriver.MainActivity;
import com.lightidea.taxidriver.R;

public class RegisterActivity extends AppCompatActivity {
    public static final String filename = "TaxiDriverData";
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    public static String Key_for_imageURL = "imageURL";
    public static String Key_for_name = "username";
    public static String Key_for_email = "useremail";
    //Key is for control signIn Data in SplashScreen activity.
    public static String Key = "flag";
    SharedPreferences sp;

    GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        sp = getApplicationContext().getSharedPreferences(filename, Context.MODE_PRIVATE);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Sign In
                signInToGoogle();
            }
        });

        // Configure Google Client
        configureGoogleClient();
    }

    private void configureGoogleClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in: " + currentUser.getEmail());
            showToastMessage("Currently Logged in: " + currentUser.getEmail());
        }
    }

    public void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showToastMessage("Google Sign in Succeeded");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                showToastMessage("Google Sign in Failed " + e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());

                            showToastMessage("Firebase Authentication Succeeded ");
                            saveSigninData(user);
                            launchMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            showToastMessage("Firebase Authentication failed:" + task.getException());
                        }
                    }
                });
    }

    private void showToastMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void saveSigninData(FirebaseUser user) {

        if (user != null) {
            sp = getApplicationContext().getSharedPreferences(filename, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Key_for_imageURL, String.valueOf(user.getPhotoUrl()));
            editor.putString(Key_for_name, user.getDisplayName());
            editor.putString(Key_for_email, user.getEmail());
            editor.putString(Key, "ok");
            editor.commit();

        }

    }
}