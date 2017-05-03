package com.austinhaskell.ghoster;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignupActivity extends AppCompatActivity {


    // ----- Authentication objects for firebase -----
    private FirebaseAuth authorization = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    // -----------------------------------------------


    // ----- UI Elements -----
    private Button submit;
    private Button cancel;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    // -----------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initilizae UI elements
        submit          = (Button)   findViewById(R.id.submit_bttn);
        name            = (EditText) findViewById(R.id.name_text);
        email           = (EditText) findViewById(R.id.email_text);
        password        = (EditText) findViewById(R.id.password_text);
        confirmPassword = (EditText) findViewById(R.id.password_confirm_text);
        cancel          = (Button)   findViewById(R.id.cancel_bttn);
        // -----

        // Create Button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setClickable(false);
                changeUIVisable(View.INVISIBLE);
                findViewById(R.id.signup_progress).setVisibility(View.VISIBLE);

                // Check fields
                if (allFieldsFull() && passwordsMatch())
                {
                    createAccount(email.getText().toString(), password.getText().toString());
                }
                else if (!passwordsMatch())
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }

                submit.setClickable(true);
                changeUIVisable(View.VISIBLE);
                findViewById(R.id.signup_progress).setVisibility(View.INVISIBLE);
            }
        });

        // Cancel Button
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SignupActivity.this.finish();
            }
        });
    }

    // --- Create Account ---
    private void createAccount(String email, String password)
    {
        authorization.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (!task.isSuccessful())
                        {
                            try
                            {
                                throw task.getException();
                            }
                            catch(FirebaseAuthWeakPasswordException e)
                            {
                                Toast.makeText(getApplicationContext(), "Weak Password - Must be atleast 6 characters", Toast.LENGTH_SHORT).show();
                            }
                            catch(FirebaseAuthInvalidCredentialsException e)
                            {
                                Toast.makeText(getApplicationContext(), "Invalid Credentials - Is email valid?", Toast.LENGTH_SHORT).show();
                            }
                            catch(FirebaseAuthUserCollisionException e)
                            {
                                Toast.makeText(getApplicationContext(), "A user with that email already exists", Toast.LENGTH_SHORT).show();
                            }
                            catch(Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "Something went horribly wrong, please try again later", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            // TODO: Enable email
                            //authorization.getCurrentUser().sendEmailVerification();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
    // ----------------------


    // ----- UI Manipulation -----
    private void changeUIVisable(int state)
    {
        submit.setVisibility(state);
        email.setVisibility(state);
        password.setVisibility(state);
        confirmPassword.setVisibility(state);
        name.setVisibility(state);
    }
    // ---------------------------


    // ----- Field Checks -----
    private boolean allFieldsFull()
    {
           return (!name.getText().toString().equals("") &&
                   !email.getText().toString().equals("") &&
                   !password.getText().toString().equals("") &&
                   !confirmPassword.getText().toString().equals(""));
    }

    private boolean passwordsMatch()
    {
        String passOne = password.getText().toString();
        String passTwo = confirmPassword.getText().toString();

        return passOne.equals(passTwo);
    }
    // ------------------------

}
