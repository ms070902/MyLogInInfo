package com.example.mylogininfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

public class LogIn extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    SignInButton googleSignIn;
    Button twitterSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN=123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        firebaseAuth = FirebaseAuth.getInstance();
        googleSignIn = findViewById(R.id.googleSignIn);
        twitterSignIn = findViewById(R.id.twitterSignIn);
        requestGoogleSignIn();
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        twitterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuthWithTwitter();
            }
        });

    }

    private void firebaseAuthWithTwitter() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang","eng");
        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // authResult.getCredential().getSecret().
                                    Toast.makeText(LogIn.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LogIn.this, MainActivity.class));
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d("Login failed",e.toString());

                                    Toast.makeText(LogIn.this, "error1 "+e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
        } else {
            firebaseAuth
                    .startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // authResult.getCredential().getSecret().
                                    SharedPreferences.Editor editor = getApplicationContext()
                                            .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                                            .edit();
                                    editor.putString("userName",authResult.getAdditionalUserInfo().getUsername());
                                    editor.putString("userEmail",firebaseAuth.getCurrentUser().getDisplayName());
                                    editor.putString("userProfile",firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                                    editor.putString("userPhone",firebaseAuth.getCurrentUser().getPhoneNumber());
                                    editor.apply();

                                    Toast.makeText(LogIn.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LogIn.this, MainActivity.class));
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d("Login failed",e.toString());
                                    Toast.makeText(LogIn.this, "Something went wrong: "+e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void requestGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("885283574576-i8a7n9i613fqcctms57v2ejhpgu0hk1l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LogIn.this,gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount user = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(user.getIdToken());
                SharedPreferences.Editor editor = getApplicationContext()
                        .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                        .edit();
                editor.putString("userName",user.getDisplayName());
                editor.putString("userEmail",user.getEmail());
                editor.putString("userProfile",user.getPhotoUrl().toString());
                editor.apply();
            } catch (ApiException e){
                e.printStackTrace();
                Log.d("login failed",e.toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LogIn.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LogIn.this, MainActivity.class));
                        }
                        else{
                            Toast.makeText(LogIn.this, "Something went wrong: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}