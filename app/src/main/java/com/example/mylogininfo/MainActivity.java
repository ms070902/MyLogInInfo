package com.example.mylogininfo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ImageView profile;
    TextView username;
    TextView email;
    Button btnLogout;
    String userName, userEmail, userProfile;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this,LogIn.class));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        Intent i = getIntent();
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        btnLogout = findViewById(R.id.btn_logout);

        SharedPreferences preferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        userEmail = preferences.getString("userEmail","");
        userName = preferences.getString("userName","");
        userProfile = preferences.getString("userProfile","drawable/ic_launcher_background.xml");

        username.setText(userName);
        email.setText(userEmail);

        Picasso.get().load(userProfile).into(profile);

        Log.d("email",userEmail);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Log Out?")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getApplicationContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,LogIn.class));
                                SharedPreferences.Editor editor = getApplicationContext()
                                        .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                                        .edit();
                                editor.clear();
                                editor.apply();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }
}