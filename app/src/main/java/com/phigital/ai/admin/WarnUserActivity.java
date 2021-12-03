package com.phigital.ai.admin;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterAdminUsers;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarnUserActivity extends AppCompatActivity {

    String hisUid;
    private RecyclerView users_rv;
    private List<ModelUser> userList;
    private AdapterAdminUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who);

        //Back
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        //User
        users_rv = findViewById(R.id.list);
        users_rv.setLayoutManager(new LinearLayoutManager(WarnUserActivity.this));
        userList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("warn").child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    hisUid = ""+ ds.getRef().getKey();
                    getAllUsers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //EdiText
        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filter(editText.getText().toString());
                return true;
            }
            return false;
        });

    }


    private void getAllUsers() {
        FirebaseDatabase.getInstance().getReference("Users").orderByChild("id").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelUser modelUser = ds.getValue(ModelUser.class);
                            userList.add(modelUser);
                        }
                        adapterUsers = new AdapterAdminUsers(WarnUserActivity.this, userList);
                        users_rv.setAdapter(adapterUsers);
                        if (adapterUsers.getItemCount() == 0){
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        }else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filter(String query) {
        FirebaseDatabase.getInstance().getReference("Users").orderByChild("id").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelUser modelUser = ds.getValue(ModelUser.class);
                            if (Objects.requireNonNull(modelUser).getName().toLowerCase().contains(query.toLowerCase()) ||
                                    modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                                userList.add(modelUser);
                            }
                        }
                        adapterUsers = new AdapterAdminUsers(WarnUserActivity.this, userList);
                        users_rv.setAdapter(adapterUsers);
                        adapterUsers.notifyDataSetChanged();
                        if (adapterUsers.getItemCount() == 0){
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        }else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}