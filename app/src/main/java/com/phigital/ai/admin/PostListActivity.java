package com.phigital.ai.admin;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.Adapter.AdapterAdminPost;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RunnableFuture;

public class PostListActivity extends BaseActivity {

    //Post
    AdapterAdminPost adapterPost;
    List<ModelPost> postList;

    RecyclerView trendingRv;
    NestedScrollView cv;

    private static final int TOTAL_ITEMS_TO_LOAD = 15;
    private int mCurrentPage = 1;
    SharedPref sharedPref;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_user_list);
        ImageView imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        EditText editText = findViewById(R.id.editText);
        trendingRv = findViewById(R.id.trendingRv);
        progressBar = findViewById(R.id.pb);
        cv = findViewById(R.id.cv);
        progressBar.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterPost(s.toString());
                }else {
                    getAllTrend();
                }
                progressBar.setVisibility(View.VISIBLE);

            }
        });
        //EdiText
        trendingRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        trendingRv.setLayoutManager(layoutManager);

        cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrentPage++;
                getAllTrend();
            }
        });

        //Post
        postList = new ArrayList<>();
        getAllTrend();
    }
    private void filterPost(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        if (modelPost != null){
                            if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(query.toLowerCase())){
                                postList.add(modelPost);
                            }
                        }
                    }
                });
                runOnUiThread(() -> {
                    adapterPost = new AdapterAdminPost(getApplicationContext(), postList);
                    trendingRv.setAdapter(adapterPost);
                    adapterPost.notifyItemInserted(postList.size()-1);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllTrend() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        postList.add(modelPost);
                    }
                    runOnUiThread(() -> {
                        adapterPost = new AdapterAdminPost(PostListActivity.this, postList);
                        trendingRv.setAdapter(adapterPost);
                        adapterPost.notifyItemInserted(postList.size()-1);
                        progressBar.setVisibility(View.GONE);
                    });
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(databaseError.getMessage())
                        .gravity(0)
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }
}