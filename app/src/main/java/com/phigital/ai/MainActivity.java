package com.phigital.ai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.phigital.ai.Fragment.HomeFragment;
import com.phigital.ai.Fragment.JobFragment;
import com.phigital.ai.Fragment.PollFragment;
import com.phigital.ai.Fragment.ProfileFragment;
import com.phigital.ai.Fragment.SearchFragment;
import com.phigital.ai.Fragment.SearchMainFragment;
import com.phigital.ai.Fragment.SearchUserFragment;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.Activity.EditProfile;
import com.phigital.ai.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends BaseActivity {
    // Main Activity here we inflate fragments so user can use our app and creating tokens
    ActivityMainBinding binding;
    Fragment selectedFragment = null;
    SharedPref sharedPref;
    String userId,hisphoto,username;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
/*   Not safe to use
      if(id.isEmpty()){

        }else{
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                String token = task.getResult();
                if (token.isEmpty()){

                }else{
                    updateToken(token);
                }
            });
            loadPostInfo(id);
        }

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:{
                        selectedFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    }
                    case R.id.poll:{
                        selectedFragment = new PollFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    }
                    case R.id.search:{
                        selectedFragment = new SearchFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    }
                    case R.id.job:{
                        selectedFragment = new JobFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    }
                    case R.id.profile:{
                        selectedFragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    }
                }
                return false;
            }
        });*/
        binding.profile.setOnClickListener(v -> {
            selectedFragment = new ProfileFragment();
            binding.bottom.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        });

        binding.bottomNavigation.setOnNavigationItemSelectedListener(navigationSelected);

        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (user != null){
                    try {
                        Picasso.get().load(user.getPhoto()).placeholder(R.drawable.placeholder).into(binding.userphoto);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   /* Forcing for auto follow for main account
                   FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Following").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (!snapshot.hasChild("uieAMrxq4oeeI5W4Eu8oUcaLo6V2")){
                                FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Following").child("uieAMrxq4oeeI5W4Eu8oUcaLo6V2").setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });*/
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
//       Making sure user has token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            try{
                String token = task.getResult();
                if (!token.isEmpty()){
                    updateToken(token);
                }
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(mToken);
    }

    // Bottom navigation bar
    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationSelected = item -> {
                switch (item.getItemId()){
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.search:
                       selectedFragment = new SearchUserFragment();
//                        String url ="https://youtube.com/shorts/uJAbB00j-yI?feature=share";
//                        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(intent);
                        break;
                    case R.id.poll:
                        selectedFragment = new PollFragment();
                        break;
                    case R.id.job:
                        selectedFragment = new JobFragment();
                        break;
                }
                if (selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            };

}




