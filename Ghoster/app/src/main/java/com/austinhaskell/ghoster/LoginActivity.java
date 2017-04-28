package com.austinhaskell.ghoster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity
{

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    TextView errorLabel;

    // ----- Initialization code -----
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        errorLabel = (TextView) findViewById(R.id.error_label);

        // -- Init Firebase objects --
        auth = FirebaseAuth.getInstance();

        // This is called whenever the authorization level is changed
        authListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null)
                {
                    // User is not logged in anymore
                }
                else
                {
                    auth.getCurrentUser().sendEmailVerification();
                    // user is logged in
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        // ---------------------------


        // -- Button Listeners --
        Button signUp = (Button) findViewById(R.id.sign_up_bttn);
        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username_text);
                EditText password = (EditText) findViewById(R.id.password_text);

                if (username.getText() == null ||
                        username.getText().toString().equals("Username") ||
                        username.getText().toString().equals(""))
                {
                    errorLabel.setText("Invalid Username");
                }
                else if (password.getText() == null ||
                        password.getText().toString().equals("Password") ||
                        password.getText().toString().equals(""))
                {
                    errorLabel.setText("Invalid Password");
                }
                else
                {
                    errorLabel.setText("");
                    findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
                    createAccount(username.getText().toString(), password.getText().toString());
                    findViewById(R.id.login_progress).setVisibility(View.INVISIBLE);
                }
            }
        });

        Button loginUser = (Button) findViewById(R.id.login_bttn);
        loginUser.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText username = (EditText) findViewById(R.id.username_text);
                EditText password = (EditText) findViewById(R.id.password_text);

                if (username.getText() == null ||
                        username.getText().toString().equals("Username") ||
                        username.getText().toString().equals(""))
                {
                    errorLabel.setText("Invalid Username");
                }
                else if (password.getText() == null ||
                        password.getText().toString().equals("Password") ||
                        password.getText().toString().equals(""))
                {
                    errorLabel.setText("Invalid Password");
                }
                else
                {
                    errorLabel.setText("");
                    findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
                    login(username.getText().toString(), password.getText().toString());
                    findViewById(R.id.login_progress).setVisibility(View.INVISIBLE);
                }
            }
            });
        // ----------------------

    }
    // ------------------------------

    // ----- Start and Stop messages -----
    @Override
    public void onStart()
    {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }
    // -----------------------------------


    // ----- Create account code -----
    private void createAccount(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this,
                                    task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void login(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }



                        // ...
                    }
                });
    }

    // -------------------------------
}

