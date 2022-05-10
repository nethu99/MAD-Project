package com.example.usermanagement.Admin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import usermanagement.R;

import com.example.usermanagement.Login_front.SignIn;
import com.example.usermanagement.Views.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewUsers extends AppCompatActivity implements UserAdapter.UserAdapterListener{
    private RecyclerView mRecyclerView;
    private UserAdapter memberAdapter;
    private FirebaseFirestore firestore;
    private List<Member> memberList;
    private String key = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        mRecyclerView = findViewById(R.id.user_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        showMembers();
    }

    public void showMembers()
    {
        memberList.clear();
        firestore.collection("Member")
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful())
                   {
                       for(QueryDocumentSnapshot document: task.getResult())
                       {
                           if(document.exists())
                           {
                               Member member = document.toObject(Member.class);
                                key = document.getId();
                               memberList.add(member);
                           }
                       }
                       memberAdapter = new UserAdapter(ViewUsers.this,memberList, ViewUsers.this);
                       mRecyclerView.setAdapter(memberAdapter);
                   }
                });
    }
    @Override
    public void userClick(View v, int position) {
        Intent i = new Intent(ViewUsers.this, SignIn.class);
        i.putExtra("Action","showuser");
        i.putExtra("UserId",memberList.get(position).getUid());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin__new,menu);
        final View addPhone = menu.findItem(R.id.add_user).getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_user:
                Intent intent = new Intent(this, SignIn.class);
                intent.putExtra("Action","register");
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}