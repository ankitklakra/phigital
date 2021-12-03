package com.phigital.ai.Job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterArticle;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelArticle;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.SinglesearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArticleSearch extends BaseActivity {
    DatabaseReference postnumref;
    SinglesearchBinding binding;
    String title;
    ArticleSearch context;
    //People
    AdapterArticle adapterPost;
    List<ModelArticle> postList;
    String userId;
    SharedPref sharedPref;
//    private static final int TOTAL_ITEMS_TO_LOAD = 10;
//    private int mCurrenPage = 1;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.singlesearch);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        binding.pg.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        postList = new ArrayList<>();
        postnumref = FirebaseDatabase.getInstance().getReference().child("Article");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.users.setLayoutManager(layoutManager);

        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                switch (title){
                    case "Business":
                        Business();
                        binding.textView11.setText("Business");
                        break;
                    case "Education":
                        Education();
                        binding.textView11.setText("Education");
                        break;
                    case "Entertainment":
                        Entertainment();
                        binding.textView11.setText("Entertainment");
                        break;
                    case "Food":
                        Food();
                        binding.textView11.setText("Food");
                        break;
                    case "Fashion":
                        Fashion();
                        binding.textView11.setText("Fashion");
                        break;
                    case "History":
                        History();
                        binding.textView11.setText("History");
                        break;
                    case "Health":
                        Health();
                        binding.textView11.setText("Health");
                        break;
                    case "Literature":
                        Literature();
                        binding.textView11.setText("Literature");
                        break;
                    case "Media":
                        Media();
                        binding.textView11.setText("Media");
                        break;
                    case "Politics":
                        Politics();
                        binding.textView11.setText("Politics");
                        break;
                    case "Science":
                        Science();
                        binding.textView11.setText("Science");
                        break;
                    case "Sports":
                        Sports();
                        binding.textView11.setText("Sports");
                        break;
                    case "Technology":
                        Technology();
                        binding.textView11.setText("Technology");
                        break;
                    case "Others":
                        Others();
                        binding.textView11.setText("Others");
                        break;
                }
            }
        });

        binding.imageView3.setOnClickListener(v -> onBackPressed());

        switch (title){
            case "Business":
                Business();
                binding.textView11.setText("Business");
                break;
            case "Education":
                Education();
                binding.textView11.setText("Education");
                break;
            case "Entertainment":
                Entertainment();
                binding.textView11.setText("Entertainment");
                break;
            case "Food":
                Food();
                binding.textView11.setText("Food");
                break;
            case "Fashion":
                Fashion();
                binding.textView11.setText("Fashion");
                break;
            case "History":
                History();
                binding.textView11.setText("History");
                break;
            case "Health":
                Health();
                binding.textView11.setText("Health");
                break;
            case "Literature":
                Literature();
                binding.textView11.setText("Literature");
                break;
            case "Media":
                Media();
                binding.textView11.setText("Media");
                break;
            case "Politics":
                Politics();
                binding.textView11.setText("Politics");
                break;
            case "Science":
                Science();
                binding.textView11.setText("Science");
                break;
            case "Sports":
                Sports();
                binding.textView11.setText("Sports");
                break;
            case "Technology":
                Technology();
                binding.textView11.setText("Technology");
                break;
            case "Others":
                Others();
                binding.textView11.setText("Others");
                break;
        }

    }

    private void Fashion() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Fashion")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Others() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Others")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Technology() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Technology")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Sports() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Sports")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Science() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Science")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Politics() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Politics")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Media() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Media")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Literature() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Literature")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void History() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("History")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Health() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Health")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Food() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Food")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Entertainment() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Entertainment")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Education() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Education")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Business() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);

                    if(Objects.requireNonNull(modelArticle).getCategory().contains("Business")) {
                        postList.add(modelArticle);
                    }
                    adapterPost = new AdapterArticle(context, postList);
                    binding.users.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}