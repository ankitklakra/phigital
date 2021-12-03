package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Adapter.AdapterGroups;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Adapter.AdapterSearchUsers;
import com.phigital.ai.Adapter.AdapterUserSuggestion2;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelGroups;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.databinding.ActivitySearchmainBinding;
import com.phigital.ai.databinding.ActivitySearchuserBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchUserFragment extends Fragment {

    ActivitySearchuserBinding binding;
    //User
    AdapterUserSuggestion2 adapterSuggestion;
    List<ModelUser> modeluserList;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrenPage1 = 1;
    DatabaseReference userRef;
    String userId;
    @SuppressLint("SetTextI18n")

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivitySearchuserBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        //Back
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), MainActivity.class);
                startActivity(intent1);
            }
        });

        binding.editText.setShowSoftInputOnFocus(false);
        binding.editText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        binding.searchuser.setLayoutManager(layoutManager1);
        //User
        modeluserList = new ArrayList<>();
        getAllUsers();
        binding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage1++;
                getAllUsers();
            }
        });
    }

    private void getAllUsers() {
        Query q = userRef.limitToFirst(mCurrenPage1 * TOTAL_ITEMS_TO_LOAD);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modeluserList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!userId.equals(Objects.requireNonNull(modelUser).getId())) {
                        modeluserList.add(modelUser);
                    }
                    adapterSuggestion = new AdapterUserSuggestion2(getActivity(), modeluserList);
                    binding.searchuser.setAdapter(adapterSuggestion);
                    adapterSuggestion.notifyItemInserted(modeluserList.size()-1);
                    if (adapterSuggestion.getItemCount() == 0){
                        binding.progressBar.setVisibility(View.GONE);
                        binding.searchuser.setVisibility(View.GONE);
                    }else{
                        binding.progressBar.setVisibility(View.GONE);
                        binding.searchuser.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    //Pressed return button - returns to the results menu
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
