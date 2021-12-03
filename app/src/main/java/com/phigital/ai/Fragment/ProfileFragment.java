package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.phigital.ai.Activity.VaultActivity;
import com.phigital.ai.Notifications.NotificationScreen;
import com.phigital.ai.OnSwipeTouchListener;
import com.phigital.ai.Utility.AboutMe;
import com.phigital.ai.Auth.RegisterActivity;
import com.phigital.ai.Chat.ChatViewPagerActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Menu.ActivityManageAccount;
import com.phigital.ai.R;
import com.phigital.ai.Shop.ProductActivity;
import com.phigital.ai.Adapter.AdapterProfileFragmentImages;
import com.phigital.ai.Adapter.AdapterUserSuggestion;
import com.phigital.ai.Adapter.ViewPagerAdapter;
import com.phigital.ai.Upload.AddStoryActivity;
import com.phigital.ai.Upload.ArticleActivity;
import com.phigital.ai.Model.ModelArticle;
import com.phigital.ai.Model.ModelPoll;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Upload.PollActivity;
import com.phigital.ai.Upload.PostActivity;
import com.phigital.ai.Upload.SponsorshipActivity;

import com.phigital.ai.Activity.AdditionalSearch;
import com.phigital.ai.Activity.EditProfile;
import com.phigital.ai.TabFragments.FeelFragment;
import com.phigital.ai.TabFragments.PostFragment;
import com.phigital.ai.TabFragments.RejoyFragment;
import com.phigital.ai.Utility.MyFollowing;
import com.phigital.ai.admin.AdminActivity;

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
import com.phigital.ai.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

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


public class ProfileFragment extends Fragment implements View.OnClickListener{
    private int[] imagelist = {R.drawable.ic_grid, R.drawable.ic_create_black,R.drawable.icon_loop_black};
    FragmentProfileBinding binding;
    List<String> idList;
    BottomSheetDialog bottomSheetDialog,addbottomSheetDialog,notificationbottomsheetDialog,uploadbottomsheet,settingbottomsheet;
    List<ModelPost> postList;
    AdapterProfileFragmentImages adapterPost;
    AdapterUserSuggestion adapterSuggestion;
    List<ModelUser> userList;
    String userId;
    ImageView suggestionview,suggestionview2,verified;
    int num = 3;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    RadioButton radioBtn1,radioBtn2,radioBtn3;

    Context context;
    //xml
    ImageView imageView3,imageView4;
    CircleImageView circularImageView;
    ProgressBar pb;
    RecyclerView recyclerView,suggestionrv;
    ImageView chat, back,upload,notification;
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
            post,header,postCL,articleCL,pollCL,sponsorshipCL,termsandpolicy,vault,manageaccount,
            privacy,help,myactivity,storyCl,productCl,invite;
    ConstraintLayout onNoti,offNoti;
    RelativeLayout bio_layout,location_layout,work_layout,joined_layout,dob_layout,link_layout,imbtn,suggestion_layout,suggestion_rv,civ;
    TextView  bio, link, location,work,joined,dob;
    TextView mUsername, mName,additional;
    TextView noFollowers, noFollowing,noShare,noComments,noLikes,noPolls,noArticle,noRejoy,noFeels,noStory,noShoot,noVideos,
            noPhotos,noFriends;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        //view
        seekBar =view.findViewById(R.id.seekbar);
        suggestionrv =view.findViewById(R.id.suggestionrv);
        suggestion_layout =view.findViewById(R.id.suggestion_layout);
        suggestionview =view.findViewById(R.id.suggestionview);
        suggestionview2 =view.findViewById(R.id.suggestionview2);

        toolbar =view.findViewById(R.id.toolbar);
        tabLayout =view.findViewById(R.id.tab_layout);
        viewPager =view.findViewById(R.id.view_pager);
        editprofile = view.findViewById(R.id.edit_profile);
        additional = view.findViewById(R.id.additional);
        notificationall = view.findViewById(R.id.notificationall);

        upload =view.findViewById(R.id.imageView5);

        verified =view.findViewById(R.id.verified);

        back = view.findViewById(R.id.i);
        notification = view.findViewById(R.id.notification);
        imbtn = view.findViewById(R.id.imbtn);
        chat = view.findViewById(R.id.chat);
        mUsername = view.findViewById(R.id.textView10);
        mName = view.findViewById(R.id.textView9);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView3.setVisibility(View.GONE);
        imageView4 = view.findViewById(R.id.imageView4);
        circularImageView = view.findViewById(R.id.circularImageView);
        pb = view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        containers = view.findViewById(R.id.container);
        header = view.findViewById(R.id.header);
        suggestion_rv = view.findViewById(R.id.suggestion_rv);
        //view
        bio = view.findViewById(R.id.bio);
        link = view.findViewById(R.id.link);
        location = view.findViewById(R.id.location);
        work = view.findViewById(R.id.work);
        joined = view.findViewById(R.id.joined);
        dob = view.findViewById(R.id.dob);
        civ = view.findViewById(R.id.civ);

        work_layout = view.findViewById(R.id.work_layout);
        dob_layout = view.findViewById(R.id.dob_layout);
        location_layout = view.findViewById(R.id.location_layout);
        joined_layout = view.findViewById(R.id.joined_layout);
        bio_layout = view.findViewById(R.id.bio_layout);
        link_layout = view.findViewById(R.id.link_layout);

        post = view.findViewById(R.id.post);
        recyclerView = view.findViewById(R.id.postView);
        imbtn = view.findViewById(R.id.imbtn);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });



        NestedScrollView cv = view.findViewById(R.id.cv);
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
                String num = String.valueOf(progress);
                loadPost(view,num);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        suggestionrv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
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

        civ.setOnLongClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutMe.class);
            startActivity(intent);
            return false;
        });

//        civ.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userId);
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                            ModelStory modelStory = snapshot1.getValue(ModelStory.class);
//                            long timecurrent = System.currentTimeMillis();
//                            if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
//                                Intent intent = new Intent(getActivity(), StoryViewActivity.class);
//                                intent.putExtra("userid", userId);
//                                startActivity(intent);
//                            }else{
//                                Intent intent2 = new Intent(getActivity(), AboutMe.class);
//                                startActivity(intent2);
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });

        editprofile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        idList = new ArrayList<>();

//        notification.setOnClickListener(v2 -> startActivity(new Intent(getActivity(), NotificationScreen.class)));

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                    startActivity(intent);

                } catch ( ActivityNotFoundException e ) {
                    //e.printStackTrace();

                    //Open the generic Apps page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);

                }
            }
        });

        upload.setOnClickListener(v -> uploadbottomsheet.show());

//        chat.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), ChatViewPagerActivity.class);
//            startActivity(intent);
//        });

        mUsername.setOnClickListener(v -> bottomSheetDialog.show());

        additional.setOnClickListener(v -> addbottomSheetDialog.show());

        //display
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("id")){
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
                        requireActivity().startActivity(intent);
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);

            }
        });

        postList= new ArrayList<>();
        addFragment(view);
        createBottomSheetDialog();
    }

    private void createBottomSheetDialog() {

        if (bottomSheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.logout_bottom_sheet, null);
            logout = view.findViewById(R.id.logout);
            logout.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(view);
        }

        if (addbottomSheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.additional_bottom_sheet, null);
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
            addbottomSheetDialog = new BottomSheetDialog(requireContext());
            addbottomSheetDialog.setContentView(view);
        }

        if (settingbottomsheet == null) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(getContext()).inflate(R.layout.settings_bottom_sheet, null);
            termsandpolicy = view.findViewById(R.id.termsandpolicy);
            vault = view.findViewById(R.id.vault);
            manageaccount = view.findViewById(R.id.manageaccount);
            privacy = view.findViewById(R.id.privacy);
            help = view.findViewById(R.id.help);
            myactivity = view.findViewById(R.id.myactivity);
            invite = view.findViewById(R.id.invite);

            termsandpolicy.setOnClickListener(this);
            vault.setOnClickListener(this);
            manageaccount.setOnClickListener(this);
            privacy.setOnClickListener(this);
            help.setOnClickListener(this);
            myactivity.setOnClickListener(this);
            invite.setOnClickListener(this);

            FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            myactivity.setVisibility(View.VISIBLE);
                            myactivity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent4 = new Intent(getContext(), AdminActivity.class);
                                    startActivity(intent4);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            settingbottomsheet = new BottomSheetDialog(requireContext());
            settingbottomsheet.setContentView(view);
        }

        if (notificationbottomsheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.notification_bottom_sheet, null);
            onNoti = view.findViewById(R.id.onNoti);
            offNoti = view.findViewById(R.id.offNoti);
            radioBtn1 = view.findViewById(R.id.radioBtn1);
            radioBtn2 = view.findViewById(R.id.radioBtn2);

            onNoti.setOnClickListener(this);
            offNoti.setOnClickListener(this);
            radioBtn1.setOnClickListener(this);
            radioBtn2.setOnClickListener(this);

            notificationbottomsheetDialog = new BottomSheetDialog(requireContext());
            notificationbottomsheetDialog.setContentView(view);
        }

        if (uploadbottomsheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.upload_bottom_sheet, null);
            postCL = view.findViewById(R.id.postCL);
            pollCL = view.findViewById(R.id.pollCL);
            articleCL = view.findViewById(R.id.articleCL);
            sponsorshipCL = view.findViewById(R.id.sponsorshipCL);
            storyCl = view.findViewById(R.id.storyCl);
            productCl = view.findViewById(R.id.productCL);

            storyCl.setOnClickListener(this);
            postCL.setOnClickListener(this);
            pollCL.setOnClickListener(this);
            articleCL.setOnClickListener(this);
            sponsorshipCL.setOnClickListener(this);
            productCl.setOnClickListener(this);
            productCl.setVisibility(View.GONE);
//            FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()){
//                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                            productCl.setVisibility(View.VISIBLE);
//                            productCl.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent4 = new Intent(getContext(), ProductActivity.class);
//                                    startActivity(intent4);
//                                }
//                            });
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

            uploadbottomsheet = new BottomSheetDialog(requireContext());
            uploadbottomsheet.setContentView(view);
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

    private void addFragment(View view) {
        viewPager =view.findViewById(R.id.view_pager);
        setupViewpager(viewPager);
        tabLayout =view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcon();
    }

    private void setupViewpager(ViewPager viewPager) {
              ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
              adapter.addFrag(new PostFragment(),"");
              adapter.addFrag(new FeelFragment(),"");
              adapter.addFrag(new RejoyFragment(),"");
              viewPager.setAdapter(adapter);
    }

    private void setupTabIcon() {
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

                }
                if (position == 1){
                    seekBar.setVisibility(View.GONE);

                }
                if (position == 2){
                    seekBar.setVisibility(View.GONE);

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
                            if (Objects.requireNonNull(post).getId().equals(userId)) {
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
                            if (Objects.requireNonNull(post).getId().equals(userId)) {
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
                    if (post.getId().equals(userId)&&!post.getReId().isEmpty()){
                        i++;
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
                    if (Objects.requireNonNull(post).getId().equals(userId)) {
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
                        if (Objects.requireNonNull(post).getId().equals(userId)) {
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
                .child("Follow").child(userId).child("Following");
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
                .child("Follow").child(userId).child("Followers");
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
                Intent intent = new Intent(getContext(), MyFollowing.class);
                intent.putExtra("title", "followers");
                intent.putExtra("id", userId);
                startActivity(intent);
                break;
            case R.id.following:
                addbottomSheetDialog.cancel();
                Intent intent2 = new Intent(getContext(), MyFollowing.class);
                intent2.putExtra("title", "following");
                intent2.putExtra("id", userId);
                startActivity(intent2);
                break;
            case R.id.poll:
                addbottomSheetDialog.cancel();
                Intent intent3 = new Intent(getContext(), AdditionalSearch.class);
                intent3.putExtra("title", "polls");
                intent3.putExtra("id", userId);
                startActivity(intent3);
                break;
            case R.id.feels:
                addbottomSheetDialog.cancel();
                Intent intent4 = new Intent(getContext(), AdditionalSearch.class);
                intent4.putExtra("title", "feels");
                intent4.putExtra("id", userId);
                startActivity(intent4);
                break;
            case R.id.article:
                addbottomSheetDialog.cancel();
                Intent intent5 = new Intent(getContext(), AdditionalSearch.class);
                intent5.putExtra("title", "articles");
                intent5.putExtra("id", userId);
                startActivity(intent5);
                break;
            case R.id.shoot:
                addbottomSheetDialog.cancel();

                break;
            case R.id.videos:
                addbottomSheetDialog.cancel();
                Intent intent7 = new Intent(getContext(), AdditionalSearch.class);
                intent7.putExtra("title", "videos");
                intent7.putExtra("id", userId);
                startActivity(intent7);
                break;
            case R.id.photo:
                addbottomSheetDialog.cancel();
                Intent intent8 = new Intent(getContext(), AdditionalSearch.class);
                intent8.putExtra("title", "photos");
                intent8.putExtra("id", userId);
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
                Intent intent9 = new Intent(getContext(), AdditionalSearch.class);
                intent9.putExtra("title", "rejoys");
                intent9.putExtra("id", userId);
                startActivity(intent9);
                break;
            case R.id.postCL:
                uploadbottomsheet.cancel();
                Intent intentpost = new Intent(getContext(), PostActivity.class);
                startActivity(intentpost);
                break;
            case R.id.pollCL:
                uploadbottomsheet.cancel();
                Intent intentpoll = new Intent(getContext(), PollActivity.class);
                startActivity(intentpoll);
                break;
            case R.id.storyCl:
                uploadbottomsheet.cancel();
                Intent intentstory = new Intent(getContext(), AddStoryActivity.class);
                startActivity(intentstory);
                break;
            case R.id.articleCL:
                uploadbottomsheet.cancel();
                Intent intentarticle = new Intent(getContext(), ArticleActivity.class);
                startActivity(intentarticle);
                break;
            case R.id.sponsorshipCL:
                uploadbottomsheet.cancel();
                Intent intentfeel = new Intent(getContext(), SponsorshipActivity.class);
                startActivity(intentfeel);
                break;
            case R.id.myactivity:
                settingbottomsheet.cancel();
                break;
            case R.id.vault:
                Intent intentsaved = new Intent(getContext(), VaultActivity.class);
                startActivity(intentsaved);
                settingbottomsheet.cancel();
                break;
            case R.id.manageaccount:
                Intent intentmanage = new Intent(getContext(), ActivityManageAccount.class);
                startActivity(intentmanage);
                settingbottomsheet.cancel();
                break;
            case R.id.privacy:
                settingbottomsheet.cancel();
                String url = "https://sites.google.com/view/phigital/privacy-policy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.help:
                settingbottomsheet.cancel();
                String yt = "https://www.youtube.com/channel/UCP7S25lgHYznXgzX_83TTBQ";
                Intent ytin = new Intent(Intent.ACTION_VIEW);
                ytin.setData(Uri.parse(yt));
                startActivity(ytin);
                break;
            case R.id.termsandpolicy:
                settingbottomsheet.cancel();
                String url2 = "https://sites.google.com/view/phigital/terms-and-condition";
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url2));
                startActivity(i2);
                break;
            case R.id.invite:
                settingbottomsheet.cancel();
                String url3 = "https://play.google.com/store/apps/details?id=com.phigital.ai";
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse(url3));
                startActivity(i3);
                break;
        }
    }

    private void loadPost(View view , String num) {
            recyclerView = view.findViewById(R.id.postView);
        int nu =Integer.parseInt(num);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),nu));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(userId);
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
                    adapterPost = new AdapterProfileFragmentImages(getActivity(), postList);
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loaduser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
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
                    adapterSuggestion = new AdapterUserSuggestion(getActivity(), userList);
                    suggestionrv.setAdapter(adapterSuggestion);
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
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    } }

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