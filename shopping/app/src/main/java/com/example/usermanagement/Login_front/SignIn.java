package com.example.usermanagement.Login_front;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usermanagement.Admin.ViewUsers;
import com.example.usermanagement.Home.MainActivity;
import com.example.usermanagement.Views.User;
import com.example.usermanagement.Views.Member;
import usermanagement.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity {
    DatabaseReference reff;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[0-9])" +
                    "(?=.*[@#$%^&+=])" +
                    "(?=\\S+$)" +
                    ".{8,15}" +
                    "$");
    private static final Pattern EMAIL=Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    private EditText fullname;
    private EditText email;
    private EditText mobile;
    private EditText location;
    private EditText pswd;
    private EditText conpswd;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private Button actionButton;
    private static  final int REQUEST_LOCATION=1;
    Member member;
    User user;
    private String userType ="User";
    private TextView username,useremail,userphone,userLocation,title;
    String action = "";
    String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        fullname = findViewById(R.id.fullName);
        email = findViewById(R.id.userEmailId);
        mobile = findViewById(R.id.mobileNumber);
        location = findViewById(R.id.location);
        pswd = findViewById(R.id.password);
        conpswd = findViewById(R.id.confirmPassword);
        scrollView = findViewById(R.id.edit_details);
        linearLayout = findViewById(R.id.show_individual_users);
        username = findViewById(R.id.show_name);
        useremail = findViewById(R.id.show_email);
        userphone = findViewById(R.id.show_phoneno);
        userLocation = findViewById(R.id.show_Location);
        actionButton = findViewById(R.id.signUpBtn);
        title = findViewById(R.id.title);
        firestore = FirebaseFirestore.getInstance();
        action = getIntent().getStringExtra("Action");
        if(action.contains("update"))
        {
            linearLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            userId = getIntent().getStringExtra("UserId");
            actionButton.setText("Update");
            title.setText("Edit User");
            updateUser(userId);
        }
        else if(action.contains("showuser"))
        {
            userId = getIntent().getStringExtra("UserId");
            linearLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            showUser(userId);
        }
        else if(action.contains("register"))
        {
            linearLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            title.setText("Create new user");
            actionButton.setText("register");
        }
        member = new Member();
        user = new User();
        reff = FirebaseDatabase.getInstance().getReference().child("Member");

        firebaseAuth = FirebaseAuth.getInstance();
    }
    private void updateUser(String uid)
    {
        firestore.collection("Member")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Member member2 = new Member();
                        member2 = task.getResult().toObject(Member.class);
                        fullname.setText(member2.getName());
                        email.setText(member2.getEmail());
                        mobile.setText(String.valueOf(member2.getMobile()));
                        conpswd.setText(member2.getPassword());
                        pswd.setText(member2.getPassword());
                        location.setText(member2.getLocation());
                    }
                });
    }
    private void showUser(String uid)
    {
        firestore.collection("Member")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Member member1 = new Member();
                        member1 = task.getResult().toObject(Member.class);
                        username.setText(member1.getName());
                        useremail.setText(member1.getEmail());
                        userphone.setText(String.valueOf(member1.getMobile()));
                        userLocation.setText(member1.getLocation());
                    }
                });
    }
    private void addUser(Member member,String uid)
    {
        firestore.collection("Member")
                .document(uid).set(member);
    }
    private void addUserType(String userId)
    {
        Map<String ,String> userRole = new HashMap<>();
        userRole.put(userId,userType);
        firestore.collection("UserRole").
                document(userId).set(userRole);
    }
    private boolean validefullname()
    {
        String full=fullname.getText().toString().trim();

        if(full.isEmpty())
        {
            fullname.setError("Fullname can't be empty");
            return false;
        }
        return  true;
    }
    private boolean validelocation()
    {
        String place = location.getText().toString().trim();
        if(place.isEmpty())
        {
            location.setError("location can't be empty");
            return false;
        }
        return  true;
    }
    private boolean validemobile()
    {
        String mob=mobile.getText().toString().trim();
        if(mob.isEmpty())
        {
            mobile.setError("mobile number can't be empty");
            return false;
        }
        return  true;
    }
    private boolean validecon()
    {
        String con = conpswd.getText().toString().trim();
        String ps = pswd.getText().toString().trim();
        if(con.isEmpty())
        {
            conpswd.setError("Field can't be empty");
            return false;
        }
        else if(!con.equals(ps))
        {
            conpswd.setError("Password didn't match");
            return  false;
        }
        return  true;
    }

    private boolean valideEmail() {
        String emailinput = email.getText().toString().trim();
        if (emailinput.isEmpty()) {
            email.setError("Email can't be empty");
            return false;
        } else if (!EMAIL.matcher(emailinput).matches()) {
            email.setError("Please enter a valid email address");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validePassword() {
        String passinput = pswd.getText().toString().trim();
        if (passinput.isEmpty()) {
            pswd.setError("password can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passinput).matches()) {
            pswd.setError("Password too weak");
            return false;
        } else {
            pswd.setError(null);
            return true;
        }
    }

    public void delete(View v)
    {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SignIn.this);
            alertDialog.setTitle("Conformation");
            alertDialog.setMessage("Do you want to delete member data?");
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                if(userId.contains(FirebaseAuth.getInstance().getUid())) {
                    FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .delete()
                            .addOnSuccessListener(task ->{
                                firestore
                                        .collection("Member")
                                        .document(userId)
                                        .delete()
                                        .addOnSuccessListener(
                                                unused ->{
                                                    Toast.makeText(SignIn.this,"Member delete successfully",Toast.LENGTH_SHORT)
                                                            .show();
                                                    Intent i = new Intent(SignIn.this,MainActivity.class);
                                                    startActivity(i);
                                                            }
                                                        );
                            });
                }
                else {
                    firestore
                            .collection("Member")
                            .document(userId)
                            .get()
                            .addOnCompleteListener(task -> {
                              if(task.isSuccessful()) {
                                  Member mem = new Member();
                                  mem = task.getResult().toObject(Member.class);
                                  Objects.requireNonNull(FirebaseAuth
                                          .getInstance()
                                          .signInWithEmailAndPassword(
                                                  mem.getEmail(),
                                                  mem.getPassword())
                                          .addOnCompleteListener(task1 -> {
                                              if (task1.isSuccessful()) {
                                                  task1.getResult()
                                                          .getUser()
                                                          .delete()
                                                          .addOnCompleteListener(
                                                                  task2 ->
                                                                  {
                                                                      if(task2.isSuccessful())
                                                                      {
                                                                          firestore
                                                                                  .collection("Member")
                                                                                  .document(userId)
                                                                                  .delete()
                                                                                  .addOnSuccessListener(
                                                                                          unused ->
                                                                                          {
                                                                                              Toast.makeText(SignIn.this,"Member delete successfully",Toast.LENGTH_SHORT)
                                                                                                      .show();
                                                                                              Intent i2 = new Intent(SignIn.this,MainActivity.class);
                                                                                              startActivity(i2);
                                                                                          });

                                                                      }
                                                                  }
                                                          );

                                              }
                                          }));
                                }
                            });
                }



            });
            alertDialog.setNegativeButton("no" ,((dialog, which) -> {
            }));
            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
    }

    public void edit(View v)
    {
        Intent i = new Intent(SignIn.this,SignIn.class);
        i.putExtra("Action","update");
        i.putExtra("UserId",userId);
        startActivity(i);
    }
    public void signin(View v) {
        if(userType.contains("User") && action.contains("register"))
        {
            member.setName(fullname.getText().toString().trim());
            member.setEmail(email.getText().toString().trim());
            try {
                Long phn=Long.parseLong(mobile.getText().toString().trim());
                member.setMobile(phn);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            member.setLocation(location.getText().toString().trim());
            member.setPassword(pswd.getText().toString().trim());

            String input = "Email: " + email.getText().toString();
            input += "\n";
            firebaseAuth
                    .createUserWithEmailAndPassword(
                            email.getText().toString().trim(),
                            pswd.getText().toString().trim())
                    .addOnCompleteListener(
                            task -> {
                                if(task.isSuccessful())
                                {
                                    String uid = task.getResult().getUser().getUid();
                                    if(userType.contains("User"))
                                    {
                                        member.setUid(uid);
                                        reff.child(uid).setValue(member);
                                        addUser(member,uid);
                                    }
                                    addUserType(uid);
                                    Toast.makeText(SignIn.this, "Data input & user created successfully", Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(SignIn.this, MainActivity.class);
                                    startActivity(i);
                                }
                                else if(!task.isSuccessful())
                                {
                                    Toast.makeText(SignIn.this,"Email id already exists",Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
        }
        else if(action.contains("update"))
        {
            member.setName(fullname.getText().toString().trim());
            member.setEmail(email.getText().toString().trim());
            try {
                Long phn=Long.parseLong(mobile.getText().toString().trim());
                member.setMobile(phn);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            member.setLocation(location.getText().toString().trim());
            member.setPassword(pswd.getText().toString().trim());
            member.setUid(userId);
            firestore.collection("Member")
                    .document(userId)
                    .set(member)
                    .addOnSuccessListener(unused -> {
                        if(userId.contains(FirebaseAuth.getInstance().getUid()))
                        {
                            FirebaseAuth
                                    .getInstance()
                                    .getCurrentUser()
                                    .updateEmail(member.getEmail())
                                    .addOnSuccessListener(unused12 -> FirebaseAuth
                                            .getInstance()
                                            .getCurrentUser()
                                            .updatePassword(member.getPassword())
                                            .addOnSuccessListener(unused1 -> {
                                                Toast.makeText(this,"user updated successfully",Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(this,SignIn.class);
                                                i.putExtra("Action","showuser");
                                                i.putExtra("UserId",userId);
                                                startActivity(i);
                                            }));
                        }
                       else
                       {
                           firestore
                                   .collection("Member")
                                   .document(userId)
                                   .get()
                                   .addOnCompleteListener(task -> {
                                       if(task.isSuccessful()) {
                                           Member mem = new Member();
                                           mem = task.getResult().toObject(Member.class);
                                           Objects.requireNonNull(FirebaseAuth
                                                   .getInstance()
                                                   .signInWithEmailAndPassword(
                                                           mem.getEmail(),
                                                           mem.getPassword())
                                                   .addOnCompleteListener(task1 -> {
                                                       if (task1.isSuccessful()) {
                                                           task1.getResult().getUser().updateEmail(member.getEmail())
                                                                   .addOnSuccessListener(
                                                                           task2 ->{
                                                                               task1.getResult().getUser()
                                                                                       .updatePassword(member.getPassword())
                                                                                       .addOnSuccessListener(
                                                                                               task3 ->{
                                                                                                   Toast.makeText(this,"user updated successfully",Toast.LENGTH_LONG).show();
                                                                                                   Intent i = new Intent(this, ViewUsers.class);
                                                                                                   startActivity(i);
                                                                                               }
                                                                                       );
                                                                           }
                                                                   );
                                                       }
                                                   }));
                                           Toast.makeText(this,"user updated successfully",Toast.LENGTH_LONG).show();
                                           Intent i = new Intent(this, ViewUsers.class);
                                           startActivity(i);
                                       }
                                   });

                       }
                    });
        }

        if (!valideEmail() | !validePassword() | !validecon() |!validefullname() |!validelocation() |!validemobile()) {
            return;
        }

    }

}