package com.example.usermanagement.Home;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.Toast;

import com.example.usermanagement.Admin.ViewUsers;
import com.example.usermanagement.Login_front.ForgetPassword;
import com.example.usermanagement.Login_front.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import usermanagement.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String CHANNEL_ID = "100 " ;
    EditText email,pass;
    Button login,consumerLogin;
    DatabaseReference myRef,userRef;
    FirebaseAuth firebaseAuth;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    String userEmail,userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.editEmail);
        pass = (EditText) findViewById(R.id.editPassword);
        login = (Button) findViewById(R.id.userlogin);
        consumerLogin = (Button) findViewById(R.id.consumerlogin);
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("user");
        saveLoginCheckBox = (CheckBox)findViewById(R.id.rempasswordcheckbox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            email.setText(loginPreferences.getString("username", ""));
            pass.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        login.setOnClickListener(this);
    }
    private boolean valideemail()
    {
        String user = email.getText().toString().trim();
        if(user.isEmpty())
        {
            email.setError("Email can't be empty");
            return false;
        }
        return  true;

    }
    private boolean validepass()
    {
        String passwd=pass.getText().toString().trim();

        if(passwd.isEmpty())
        {
            pass.setError("password can't be empty");
            return false;
        }
        return  true;
    }
    public  void signup(View v)
    {
        Intent i = new Intent(this, SignIn.class);
        i.putExtra("Action","register");
        startActivity(i);
    }
    public  void forgetpassword(View v)
    {
        Intent i=new Intent(this, ForgetPassword.class);
        startActivity(i);
    }
    public void setValidLogin()
    {
        myRef=FirebaseDatabase.getInstance().getReference().child("Member");
        if(!validepass() | !valideemail()){
            return;
        }
        String userEmail = email.getText().toString().trim();
        String userPassword = pass.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(
                userEmail, userPassword)
                .addOnCompleteListener(
                        task -> {
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (pass.length() < 8) {
                                    Toast.makeText(getApplicationContext(),"Password must be more than 8 digit",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),"Enter the correct email and password",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(task.isSuccessful()) {
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseFirestore.getInstance()
                                        .collection("UserRole")
                                        .document(uid)
                                        .get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful())
                                    {
                                        String role = task1.getResult().getData().get(uid).toString();
                                       if(role.contains("admin") )
                                       {
                                           Intent i= new Intent(MainActivity.this, ViewUsers.class);
                                           startActivity(i);
                                       }
                                       else if (role.contains("User"))
                                       {
                                           Intent i= new Intent(MainActivity.this, SignIn.class);
                                           i.putExtra("Action","showuser");
                                           System.out.println(uid);
                                           i.putExtra("UserId",uid);
                                           startActivity(i);
                                       }

                                    }});
                            }
                            else {
                                System.out.println(email.getText().toString()+pass.getText().toString());
                                Toast.makeText(getApplicationContext(),"Enter the correct email and password",Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        userEmail = email.getText().toString();
        userPassword = pass.getText().toString();
        if (saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", userEmail);
            loginPrefsEditor.putString("password", userPassword);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
        setValidLogin();
    }
}