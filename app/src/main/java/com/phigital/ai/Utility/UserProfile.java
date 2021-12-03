package com.phigital.ai.Utility;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.android.volley.RequestQueue;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Adapter.AdapterProfileFragmentImages;
import com.phigital.ai.Adapter.AdapterUserSuggestion;
import com.phigital.ai.Adapter.ViewPagerAdapter;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Notifications.NotificationScreen;
import com.phigital.ai.Upload.ArticleActivity;
import com.phigital.ai.Auth.RegisterActivity;
import com.phigital.ai.Fragment.ProfileFragment;
import com.phigital.ai.Menu.ActivityManageAccount;
import com.phigital.ai.Activity.VaultActivity;
import com.phigital.ai.Model.ModelArticle;
import com.phigital.ai.Model.ModelPoll;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Upload.PollActivity;
import com.phigital.ai.Upload.PostActivity;
import com.phigital.ai.R;
import com.phigital.ai.Activity.AdditionalSearch;
import com.phigital.ai.Chat.ChatActivity;
import com.phigital.ai.SharedPref;
import com.phigital.ai.UserTabFragments.FeelFragment2;
import com.phigital.ai.UserTabFragments.PostFragment2;
import com.phigital.ai.UserTabFragments.RejoyFragment2;
import com.phigital.ai.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfile extends BaseActivity implements  View.OnClickListener{

    ActivityProfileBinding binding;
    ViewPagerAdapter adapter;
    AdapterPost adapterPost2;
    List<ModelPost> rejoyList;
    AdapterPost adapterPost3;
    FirebaseDatabase firebaseDatabase;
    String uid;
    String hisUid;
    List<ModelPost> feelList;
    SharedPref sharedPref;
    private RequestQueue requestQueue;
    private boolean notify = false;

    private int[] imagelist = {R.drawable.ic_grid, R.drawable.ic_create_black,R.drawable.icon_loop_black};

    List<String> idList;
    BottomSheetDialog bottomSheetDialog,addbottomSheetDialog,notificationbottomsheetDialog,uploadbottomsheet,settingbottomsheet,reportbottomsheet;
    List<ModelPost> postList;
    AdapterProfileFragmentImages adapterPost;
    AdapterUserSuggestion adapterSuggestion;
    List<ModelUser> userList;
    ImageView suggestionview,suggestionview2,verified;
    int num = 3;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    RadioButton radioBtn1,radioBtn2,radioBtn3;

    TextView followtv,followingtv;
    Context context =UserProfile.this;
    //xml
    ImageView imageView3,imageView4,imageView5,notification;
    CircleImageView circularImageView;
    ProgressBar pb;
    RecyclerView recyclerView,suggestionrv;
    ImageView chat, back,upload;
    SeekBar seekBar,seekBar2,seekBar3,seekBar4;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    DatabaseReference mDatabase;
    //Theme
    ImageButton notificationall;
    TextView editprofile;
    CoordinatorLayout containers;
    ConstraintLayout followers,following,friends,photos,videos,shoots,articles,feels,polls,logout,like,comment,share,story,poll,rejoy,
            post,header,noneCL,personalizedCL,allNotificationCL,postCL,articleCL,pollCL,feelCL,termsandpolicy,vault,manageaccount,
            privacy,help,myactivity,reportCL;

    RelativeLayout bio_layout,location_layout,work_layout,joined_layout,dob_layout,link_layout,imbtn,suggestion_layout,suggestion_rv;
    TextView  bio, link, location,work,joined,dob;
    TextView mUsername, mName,additional;
    TextView noFollowers, noFollowing,noShare,noComments,noLikes,noPolls,noArticle,noRejoy,noFeels,noStory,noShoot,noVideos,
            noPhotos,noFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
//        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        hisUid = getIntent().getStringExtra("hisUid");
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(hisUid);
        ((AppCompatActivity) UserProfile.this).setSupportActionBar(toolbar);

//        Intent intenthisuid = new Intent("hisUid");
//        intenthisuid.putExtra("item", hisUid);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intenthisuid);

        seekBar =findViewById(R.id.seekbar);
        suggestionrv =findViewById(R.id.suggestionrv);
        suggestion_layout =findViewById(R.id.suggestion_layout);
        suggestionview =findViewById(R.id.suggestionview);
        suggestionview2 =findViewById(R.id.suggestionview2);

        toolbar =findViewById(R.id.toolbar);
        imbtn =findViewById(R.id.imbtn);
        tabLayout =findViewById(R.id.tab_layout);
        viewPager =findViewById(R.id.view_pager);
        editprofile = findViewById(R.id.edit_profile);
        additional = findViewById(R.id.additional);
        notificationall =findViewById(R.id.notificationall);

        back = findViewById(R.id.i);
        followtv = findViewById(R.id.followtv);
        followingtv = findViewById(R.id.followingtv);
        notification = findViewById(R.id.notification);
        imbtn =findViewById(R.id.imbtn);
        chat = findViewById(R.id.chat);
        mUsername = findViewById(R.id.textView10);
        mName = findViewById(R.id.textView9);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setVisibility(View.GONE);
        imageView4 =findViewById(R.id.imageView4);
        imageView5 =findViewById(R.id.imageView5);
        imageView4.setVisibility(View.GONE);
        imageView5.setVisibility(View.GONE);
        circularImageView = findViewById(R.id.circularImageView);
        pb =findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        containers = findViewById(R.id.container);
        header = findViewById(R.id.header);
        suggestion_rv = findViewById(R.id.suggestion_rv);

        verified =findViewById(R.id.verified);
        //view
        bio = findViewById(R.id.bio);
        link = findViewById(R.id.link);
        location = findViewById(R.id.location);
        joined = findViewById(R.id.joined);
        dob = findViewById(R.id.dob);

        work_layout = findViewById(R.id.work_layout);
        dob_layout = findViewById(R.id.dob_layout);
        location_layout = findViewById(R.id.location_layout);
        joined_layout = findViewById(R.id.joined_layout);
        bio_layout = findViewById(R.id.bio_layout);
        link_layout = findViewById(R.id.link_layout);

        recyclerView = findViewById(R.id.postView);

//        post = findViewById(R.id.post);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> onBackPressed());

        NestedScrollView cv = findViewById(R.id.cv);
        cv.fullScroll(ScrollView.FOCUS_UP);
        cv.smoothScrollTo(0,0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0){
                    progress = 1;
                }else if(progress == 1){
                    progress = 2;
                }else if(progress == 2){
                    progress = 3;
                } else if (progress == 3){
                    progress = 4;
                }else if (progress == 4) {
                    progress = 5;
                }else if (progress == 5) {
                    progress = 6;
                }
                loadPost(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        suggestionrv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        userList = new ArrayList<>();
        suggestionrv.smoothScrollToPosition(0);

        suggestion_layout.setOnClickListener(v -> {
            if (suggestion_rv.getVisibility() == View.VISIBLE){
                suggestion_rv.setVisibility(View.GONE);
                suggestionview2.setVisibility(View.GONE);
                suggestionview.setVisibility(View.VISIBLE);
            }else{
                suggestion_rv.setVisibility(View.VISIBLE);
                suggestionview2.setVisibility(View.VISIBLE);
                suggestionview.setVisibility(View.GONE);
                loaduser();
            }
        });

        imageView4.setOnClickListener(v -> settingbottomsheet.show());

//        everyonest = getString(R.string.every).trim();
//        personalst =  getString(R.string.personal).trim();
//        nonest = getString(R.string.none).trim();

        circularImageView.setOnLongClickListener(v -> {
            Intent intent = new Intent(context, AboutUser.class);
            intent.putExtra("hisUid",hisUid);
            startActivity(intent);
            return false;
        });

        editprofile.setVisibility(View.GONE);

        idList = new ArrayList<>();

//        notification.setOnClickListener(v2 -> startActivity(new Intent(UserProfile.this, NotificationScreen.class)));
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                    startActivity(intent);

                } catch ( ActivityNotFoundException e ) {
                    //e.printStackTrace();

                    //Open the generic Apps page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);

                }
            }
        });
//        notificationall.setOnClickListener(v -> notificationbottomsheetDialog.show());

//        upload.setOnClickListener(v -> uploadbottomsheet.show());

        feelList= new ArrayList<>();
        notification =findViewById(R.id.notification);
        notificationall =findViewById(R.id.notificationall);
//        notification.setOnClickListener(v -> {
//            NotificationBottomSheet bottom = new NotificationBottomSheet();
//            bottom.show(getSupportFragmentManager(),"notiicationbottomsheet");
//        });
//        notificationall.setOnClickListener(v -> {
//            NotificationBottomSheet bottom2 = new NotificationBottomSheet();
//            bottom2.show(getSupportFragmentManager(),"notiicationbottomsheet2");
//        });
        mUsername.setOnClickListener(v -> {
            reportbottomsheet.show();
        });

        chat.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfile.this, ChatActivity.class);
            intent.putExtra("hisUid",hisUid);
            startActivity(intent);
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                mName.setText(name);
                String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                mUsername.setText(username);
                String photo = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                try{
                    Picasso.get().load(photo).placeholder(R.drawable.placeholder).resize(256, 256).centerCrop().into(circularImageView);
                }catch(Exception e){
                    Picasso.get().load(R.drawable.placeholder).into(circularImageView);
                }

                String mVerified = dataSnapshot.child("verified").getValue().toString();

                if (mVerified.isEmpty()){
                    verified.setVisibility(View.GONE);
                }else {
                    verified.setVisibility(View.VISIBLE);
                }

//                if(data.equals("all")) {
//                    radioBtn1.setChecked(true);
//                    radioBtn2.setChecked(false);
//                    radioBtn3.setChecked(false);
//                }
//                if(data.equals("off")) {
//                    radioBtn1.setChecked(false);
//                    radioBtn2.setChecked(false);
//                    radioBtn3.setChecked(true);
//                }
//                if(data.equals("personal")) {
//                    radioBtn1.setChecked(false);
//                    radioBtn2.setChecked(true);
//                    radioBtn3.setChecked(false);
//                }

                String dbBirthdate = Objects.requireNonNull(dataSnapshot.child("birthdate").getValue()).toString();
                dob.setText(dbBirthdate);
                if (dataSnapshot.hasChild("city")){
                    String dbLocation = dataSnapshot.child("city").getValue().toString();
                        location.setText(dbLocation);
                    if(dbLocation.length() > 0) { location_layout.setVisibility(View.VISIBLE); }
                    else { location_layout.setVisibility(View.GONE); }
                    String url ="http://maps.google.co.in/maps?q=" + dbLocation;
                    location.setOnClickListener(v -> {
                        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    });
                }
                String dbjoined = Objects.requireNonNull(dataSnapshot.child("joined").getValue()).toString();
                joined.setText(dbjoined);
                String dbBio = Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString();
                bio.setText(dbBio);
                String dbLink = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();
                link.setText(dbLink);

                //bio
                if(dbBio.length() > 0) { bio_layout.setVisibility(View.VISIBLE); }
                else { bio_layout.setVisibility(View.GONE); }
                //link
                if(dbLink.length() > 0) { link_layout.setVisibility(View.VISIBLE); }
                else { link_layout.setVisibility(View.GONE); }
                //location


                link.setOnClickListener(v -> {
                    Intent intent = new Intent (Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY,dbLink);
                    context.startActivity(intent);
                });
                //dob
                if(dbBirthdate.length() > 0) { dob_layout.setVisibility(View.VISIBLE); }
                else { dob_layout.setVisibility(View.GONE); }
                //joined
                if(dbjoined.length() > 0) { joined_layout.setVisibility(View.VISIBLE); }
                else { joined_layout.setVisibility(View.GONE); }

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                long m = Long.parseLong(dbjoined);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(m);

                String t =(formatter.format(calendar.getTime()));
                joined.setText(t);

                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);

            }
        });

        additional.setOnClickListener(v -> addbottomSheetDialog.show());

        followtv.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("Following").child(hisUid).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUid)
                    .child("Followers").child(firebaseUser.getUid()).setValue(true);
            followtv.setVisibility(View.GONE);
            followingtv.setVisibility(View.VISIBLE);
            notify = true;

            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(hisUid);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    if (notify){
//                        sendNotification(hisUid, Objects.requireNonNull(user).getName(), "Started following you");
                    }
                    notify = false;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        followingtv.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("Following").child(hisUid).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUid)
                    .child("Followers").child(firebaseUser.getUid()).removeValue();
            followingtv.setVisibility(View.GONE);
            followtv.setVisibility(View.VISIBLE);
        });

        isFollowing();

        postList= new ArrayList<>();
        rejoyList= new ArrayList<>();
        addFragment();

        createBottomSheetDialog();
    }

    private void createBottomSheetDialog() {

        if (addbottomSheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.additional_bottom_sheet, null);
            followers = view.findViewById(R.id.followers);
            following = view.findViewById(R.id.following);
            friends = view.findViewById(R.id.friends);
            photos = view.findViewById(R.id.photo);
            videos = view.findViewById(R.id.videos);
            shoots = view.findViewById(R.id.shoot);
            articles = view.findViewById(R.id.article);
            feels = view.findViewById(R.id.feels);
            rejoy = view.findViewById(R.id.rejoy);
            share = view.findViewById(R.id.share);
            like = view.findViewById(R.id.like);
            comment = view.findViewById(R.id.comment);
            story = view.findViewById(R.id.story);
            poll = view.findViewById(R.id.poll);
            noFollowers = view.findViewById(R.id.noFollowers);
            noFollowing = view.findViewById(R.id.noFollowing);
            noShare = view.findViewById(R.id.noShare);
            noComments = view.findViewById(R.id.noComments);
            noLikes = view.findViewById(R.id.noLikes);
            noPolls = view.findViewById(R.id.noPolls);
            noArticle = view.findViewById(R.id.noArticle);
            noRejoy = view.findViewById(R.id.noRejoy);
            noFeels = view.findViewById(R.id.noFeels);
            noStory = view.findViewById(R.id.noStory);
            noShoot = view.findViewById(R.id.noShoot);
            noVideos = view.findViewById(R.id.noVideos);
            noPhotos = view.findViewById(R.id.noPhotos);
            noFriends = view.findViewById(R.id.noFriends);

            getallnum();

            followers.setOnClickListener(this);
            following.setOnClickListener(this);
            friends.setOnClickListener(this);
            photos.setOnClickListener(this);
            videos.setOnClickListener(this);
            shoots.setOnClickListener(this);
            articles.setOnClickListener(this);
            feels.setOnClickListener(this);
            poll.setOnClickListener(this);
            rejoy.setOnClickListener(this);
            share.setOnClickListener(this);
            like.setOnClickListener(this);
            comment.setOnClickListener(this);
            story.setOnClickListener(this);
            addbottomSheetDialog = new BottomSheetDialog(context);
            addbottomSheetDialog.setContentView(view);
        }

        if (settingbottomsheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.settings_bottom_sheet, null);
            termsandpolicy = view.findViewById(R.id.termsandpolicy);
            vault = view.findViewById(R.id.vault);
            manageaccount = view.findViewById(R.id.manageaccount);
            privacy = view.findViewById(R.id.privacy);
            help = view.findViewById(R.id.help);
            myactivity = view.findViewById(R.id.myactivity);


            termsandpolicy.setOnClickListener(this);
            vault.setOnClickListener(this);
            manageaccount.setOnClickListener(this);
            privacy.setOnClickListener(this);
            help.setOnClickListener(this);
            myactivity.setOnClickListener(this);

            settingbottomsheet = new BottomSheetDialog(context);
            settingbottomsheet.setContentView(view);
        }

//        if (notificationbottomsheetDialog == null) {
//            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.notification_bottom_sheet, null);
//            allNotificationCL = view.findViewById(R.id.allNotificationCL);
//            personalizedCL = view.findViewById(R.id.personalizedCL);
//            noneCL = view.findViewById(R.id.noneCL);
//            radioBtn1 = view.findViewById(R.id.radioBtn1);
//            radioBtn2 = view.findViewById(R.id.radioBtn2);
//            radioBtn3 = view.findViewById(R.id.radioBtn3);
//
//            allNotificationCL.setOnClickListener(this);
//            personalizedCL.setOnClickListener(this);
//            noneCL.setOnClickListener(this);
//            radioBtn1.setOnClickListener(this);
//            radioBtn2.setOnClickListener(this);
//            radioBtn3.setOnClickListener(this);
//
//            notificationbottomsheetDialog = new BottomSheetDialog(context);
//            notificationbottomsheetDialog.setContentView(view);
//        }

        if (uploadbottomsheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.upload_bottom_sheet, null);
            postCL = view.findViewById(R.id.postCL);
            pollCL = view.findViewById(R.id.pollCL);
            articleCL = view.findViewById(R.id.articleCL);
//            feelCL = view.findViewById(R.id.feelCL);

            postCL.setOnClickListener(this);
            pollCL.setOnClickListener(this);
            articleCL.setOnClickListener(this);
//            feelCL.setOnClickListener(this);

            uploadbottomsheet = new BottomSheetDialog(context);
            uploadbottomsheet.setContentView(view);
        }

        if (reportbottomsheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.report_bottom_sheet, null);

            reportCL = view.findViewById(R.id.reportCL);

            reportCL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child("userReport").child(hisUid).setValue(true);
                    Toast.makeText(context, "User Report", Toast.LENGTH_SHORT).show();
                }
            });

            reportbottomsheet = new BottomSheetDialog(context);
            reportbottomsheet.setContentView(view);
        }

    }

    private void getallnum() {
        getPhotos();
        getFeels();
        getRejoys();
        getFollowers();
        getFollowing();
        getArticle();
        getPolls();
        getFollowing();
    }

    private void addFragment() {
        viewPager =findViewById(R.id.view_pager);
        setupViewpager(viewPager);
        tabLayout =findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcon();
    }

    private void setupViewpager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new PostFragment2(),"");
        adapter.addFrag(new FeelFragment2(),"");
        adapter.addFrag(new RejoyFragment2(),"");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcon() {
        viewPager =findViewById(R.id.view_pager);
        setupViewpager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00ACED"));
        tabLayout.setTabIconTint(ColorStateList.valueOf(Color.parseColor("#757575")));
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(imagelist[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(imagelist[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(imagelist[2]);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
                int position =tab.getPosition();
                if (position == 0){
                    seekBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> loadPost(String.valueOf(num)),100);
                }
                if (position == 1){
                    seekBar.setVisibility(View.GONE);
                    new Handler().postDelayed(this::loadPost2,100);
                }
                if (position == 2){
                    seekBar.setVisibility(View.GONE);
                    new Handler().postDelayed(this::loadPost3,100);
                }

            }

            private void loadPost(String num) {
                recyclerView = findViewById(R.id.postView);
                int nu =Integer.parseInt(String.valueOf(num));
                recyclerView.setLayoutManager(new GridLayoutManager(context,nu));
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                Query query = ref.orderByChild("id").equalTo(hisUid);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelPost modelPost = ds.getValue(ModelPost.class);
                            if(modelPost.getType().equals("meme") || modelPost.getType().equals("image")) {
                                if (modelPost.getReId().equals("")) {
                                    postList.add(modelPost);
                                }
                            }
                            Collections.reverse(postList);
                            adapterPost = new AdapterProfileFragmentImages(context, postList);
                            recyclerView.setAdapter(adapterPost);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void loadPost2() {
                recyclerView = findViewById(R.id.feelView);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                Query query = ref.orderByChild("id").equalTo(hisUid);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        feelList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelPost modelPost = ds.getValue(ModelPost.class);
                            if (modelPost.getType().equals("text")&&modelPost.getReId().equals("")){
                                feelList.add(modelPost);
                            }
                            adapterPost2 = new AdapterPost(context, feelList);
                            recyclerView.setAdapter(adapterPost2);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void loadPost3() {
                recyclerView = findViewById(R.id.rejoyView);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        rejoyList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelPost modelPost = ds.getValue(ModelPost.class);
                            if (modelPost.getId().equals(hisUid)&&!modelPost.getReId().isEmpty()){
                                rejoyList.add(modelPost);
                            }
                            adapterPost3 = new AdapterPost(context, rejoyList);
                            recyclerView.setAdapter(adapterPost3);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        new Handler().postDelayed(() -> loadPost(String.valueOf(num)),100);
    }

    private void getPhotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);
                    assert post != null;
                    if(post.getType().contains("meme") || post.getType().contains("image")) {
                        if (post.getReId().equals("")) {
                            if (Objects.requireNonNull(post).getId().equals(hisUid)) {
                                i++;
                            }
                        }
                    }
                }
                noPhotos.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFeels() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);
                    assert post != null;
                    if(post.getType().contains("text")) {
                        if (post.getReId().equals("")) {
                            if (Objects.requireNonNull(post).getId().equals(hisUid)) {
                                i++;
                            }
                        }
                    }
                }
                noFeels.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRejoys() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);
                    assert post != null;
                    if (post.getReTweet().equals(hisUid)&&post.getId().equals(hisUid)){
                        if (Objects.requireNonNull(post).getId().equals(hisUid)) {
                            i++;
                        }
                    }
                }
                noRejoy.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getArticle() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Article");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPoll post = snapshot.getValue(ModelPoll.class);
                    if (Objects.requireNonNull(post).getId().equals(hisUid)) {
                        i++;
                    }
                }
                noArticle.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPolls() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Poll");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelArticle post = snapshot.getValue(ModelArticle.class);
                    if (Objects.requireNonNull(post).getId().equals(hisUid)) {
                        i++;
                    }
                }
                noPolls.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisUid).child("Following");
        reference1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noFollowing.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisUid).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noFollowers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout:
                bottomSheetDialog.cancel();
                mAuth.signOut();
                checkUserStatus();
                break;
            case R.id.followers:
                addbottomSheetDialog.cancel();
                Intent intent = new Intent(context, MyFollowing.class);
                intent.putExtra("title", "followers");
                intent.putExtra("id", hisUid);
                startActivity(intent);
                break;
            case R.id.following:
                addbottomSheetDialog.cancel();
                Intent intent2 = new Intent(context, MyFollowing.class);
                intent2.putExtra("title", "following");
                intent2.putExtra("id", hisUid);
                startActivity(intent2);
                break;
            case R.id.poll:
                addbottomSheetDialog.cancel();
                Intent intent3 = new Intent(context, AdditionalSearch.class);
                intent3.putExtra("title", "polls");
                intent3.putExtra("id",hisUid);
                startActivity(intent3);
                break;
            case R.id.feels:
                addbottomSheetDialog.cancel();
                Intent intent4 = new Intent(context, AdditionalSearch.class);
                intent4.putExtra("title", "feels");
                intent4.putExtra("id",hisUid);
                startActivity(intent4);
                break;
            case R.id.article:
                addbottomSheetDialog.cancel();
                Intent intent5 = new Intent(context, AdditionalSearch.class);
                intent5.putExtra("title", "articles");
                intent5.putExtra("id",hisUid);
                startActivity(intent5);
                break;
            case R.id.shoot:
                addbottomSheetDialog.cancel();

                break;
            case R.id.videos:
                addbottomSheetDialog.cancel();
                Intent intent7 = new Intent(context, AdditionalSearch.class);
                intent7.putExtra("title", "videos");
                intent7.putExtra("id",hisUid);
                startActivity(intent7);
                break;
            case R.id.photo:
                addbottomSheetDialog.cancel();
                Intent intent8 = new Intent(context, AdditionalSearch.class);
                intent8.putExtra("title", "photos");
                intent8.putExtra("id",hisUid);
                startActivity(intent8);
                break;
            case R.id.like:
                addbottomSheetDialog.cancel();

                break;
            case R.id.comment:
                addbottomSheetDialog.cancel();

                break;
            case R.id.share:
                addbottomSheetDialog.cancel();

                break;
            case R.id.story:
                addbottomSheetDialog.cancel();

                break;
            case R.id.rejoy:
                addbottomSheetDialog.cancel();
                Intent intent9 = new Intent(context, AdditionalSearch.class);
                intent9.putExtra("title", "rejoys");
                intent9.putExtra("id",hisUid);
                startActivity(intent9);
                break;
            case R.id.postCL:
                uploadbottomsheet.cancel();
                Intent intentpost = new Intent(context, PostActivity.class);
                startActivity(intentpost);
                break;
            case R.id.pollCL:
                uploadbottomsheet.cancel();
                Intent intentpoll = new Intent(context, PollActivity.class);
                startActivity(intentpoll);
                break;
            case R.id.articleCL:
                uploadbottomsheet.cancel();
                Intent intentarticle = new Intent(context, ArticleActivity.class);
                startActivity(intentarticle);
                break;
//            case R.id.feelCL:
//                uploadbottomsheet.cancel();
//                Intent intentfeel = new Intent(context, Feel.class);
//                startActivity(intentfeel);
//                break;
            case R.id.myactivity:
                settingbottomsheet.cancel();
                break;
            case R.id.vault:
                Intent intentsaved = new Intent(context, VaultActivity.class);
                startActivity(intentsaved);
                settingbottomsheet.cancel();
                break;
            case R.id.manageaccount:
                Intent intentmanage = new Intent(context, ActivityManageAccount.class);
                startActivity(intentmanage);
                settingbottomsheet.cancel();
                break;
            case R.id.privacy:
                settingbottomsheet.cancel();
                break;
            case R.id.help:
                settingbottomsheet.cancel();
                break;
            case R.id.termsandpolicy:
                settingbottomsheet.cancel();
                break;
        }
    }

    private void setNotificationOff() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("notify", "off");
                mDatabase.updateChildren(hashMap);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setNotificationPersonal() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("notify", "personal");
                mDatabase.updateChildren(hashMap);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setNotificationAll() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("notify", "all");
                mDatabase.updateChildren(hashMap);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loaduser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query q = databaseReference.limitToLast(100);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!userId.equals(Objects.requireNonNull(modelUser).getId())){
                        userList.add(modelUser);
                    }
                    adapterSuggestion = new AdapterUserSuggestion(context, userList);
                    suggestionrv.setAdapter(adapterSuggestion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPost(String num) {
        recyclerView = findViewById(R.id.postView);
        int nu =Integer.parseInt(String.valueOf(num));
        recyclerView.setLayoutManager(new GridLayoutManager(context,nu));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(Objects.requireNonNull(modelPost).getType().equals("meme") || modelPost.getType().equals("image")) {
                        if (modelPost.getReId().equals("")) {
                            postList.add(modelPost);
                        }
                    }
                    Collections.reverse(postList);
                    adapterPost = new AdapterProfileFragmentImages(context, postList);
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hisUid.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.container,profileFragment).commit();
        }
    }

    private void isFollowing() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(hisUid).exists()){
                    followtv.setVisibility(View.GONE);
                    followingtv.setVisibility(View.VISIBLE);
                }else {
                    followtv.setVisibility(View.VISIBLE);
                    followingtv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
