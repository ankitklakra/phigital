package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Activity.AdditionalSearch;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Activity.VaultActivity;
import com.phigital.ai.Adapter.AdapterAdminPost;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Adapter.AdapterPost2;
import com.phigital.ai.Adapter.AdapterPostHome;
import com.phigital.ai.Adapter.AdapterStory;
import com.phigital.ai.Adapter.AdapterUserSuggestion;
import com.phigital.ai.Adapter.AdapterUserSuggestion2;
import com.phigital.ai.Adapter.NewAdapter;
import com.phigital.ai.Chat.ChatViewPagerActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Menu.ActivityManageAccount;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelStory;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.NotificationScreen;
import com.phigital.ai.R;
import com.phigital.ai.Upload.AddStoryActivity;
import com.phigital.ai.Upload.ArticleActivity;
import com.phigital.ai.Upload.PollActivity;
import com.phigital.ai.Upload.PostActivity;
import com.phigital.ai.Upload.SponsorshipActivity;
import com.phigital.ai.Utility.MyFollowing;
import com.phigital.ai.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements View.OnClickListener{

    // First Fragment where user can see posts
    FragmentHomeBinding binding;
    FirebaseAuth mAuth;
    AdapterPost adapterPost;
    BottomSheetDialog uploadbottomsheet;

    int ITEM_LOAD_COUNT= 20;
    private int mCurrenPage = 1;

    List<ModelPost> postList = new ArrayList<>();
    List<ModelPost> sponsorshipList = new ArrayList<>();

    List<String> followingList;
    List<String> followingSList;

    private AdapterStory story;
    private List<ModelStory> storyList = new ArrayList<>();;

    private String userId,node;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        checkSFollowing();
        createBottomSheet();
/*  Tried second way of implementing same thing
       getLastKeyFromFirebase();
        adapter=new AdapterPost2(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        binding.postView.setLayoutManager(manager);
        binding.postView.setAdapter(adapter);
        loadPosts();
        */
        loadPosts();
/*        Tried third way of implementing same thing
loadPosts();

        binding.suggestionRv.setHasFixedSize(true);


        loadQuerydata();

        binding.postView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.postView.getLayoutManager();
                int totalitem = linearLayoutManager.getItemCount();
                int lastvisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalitem<lastvisible+3){
                    if (!isLoading){
                        isLoading = true;
                        loadPost();
                    }
                }
            }
        });*/

        // Checking if user reached bottom of scroll view so we can fetch new data to load
        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                loadPosts();
                mCurrenPage++;
                binding.postView.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();
    }

/*  Firebase owns pagination solution but achieve same result with current one
    private void loadQuerydata() {

        Query q = FirebaseDatabase.getInstance().getReference("Posts")
//              .limitToLast(mCurrenPage*TOTAL_ITEMS_TO_LOAD)
                .orderByChild("type").equalTo("text");

        FirebaseRecyclerOptions<ModelPost> options= new FirebaseRecyclerOptions.Builder<ModelPost>()
                .setLifecycleOwner(this)
                .setQuery(q,ModelPost.class)
                .build();

        adapterPostHome = new AdapterPostHome(options);
        adapterPostHome.notifyDataSetChanged();
        binding.postView.setAdapter(adapterPostHome);
    }*/

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.pb.setVisibility(View.VISIBLE);

        binding.storyView.setHasFixedSize(true);
        binding.storyView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        story = new AdapterStory(getActivity(), storyList);
        binding.storyView.setAdapter(story);

        binding.postView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.postView.setLayoutManager(layoutManager);

        // Bottomsheet to post new user data
        binding.add.setOnClickListener(v -> uploadbottomsheet.show());
        // Opening search fragment
        binding.search.setOnClickListener(v -> {
            Fragment fragment = new SearchUserFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        binding.chat.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChatViewPagerActivity.class)));
        binding.notification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationScreen.class)));
        binding.counttv.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationScreen.class)));

        //Notification message
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                      binding.notificationimage.setImageResource(R.drawable.ic_notifications_active);
                      binding.counttv.setText(String.valueOf(snapshot.getChildrenCount()));
                      binding.counttv.setVisibility(View.VISIBLE);
                }else {
                    binding.counttv.setVisibility(View.GONE);
                    binding.notificationimage.setImageResource(R.drawable.icon_notification2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //New message
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Countm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.chatimage.setImageResource(R.drawable.ic_data_usage);
                    binding.countmtv.setText(String.valueOf(snapshot.getChildrenCount()));
                    binding.countmtv.setVisibility(View.VISIBLE);
                }else {
                    binding.countmtv.setVisibility(View.GONE);
                    binding.chatimage.setImageResource(R.drawable.ic_offline_bolt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Bottomsheet to post new user data
    private void createBottomSheet() {
        if (uploadbottomsheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.upload_bottom_sheet, null);
            ConstraintLayout  postCL = view.findViewById(R.id.postCL);
            ConstraintLayout  pollCL = view.findViewById(R.id.pollCL);
            ConstraintLayout  articleCL = view.findViewById(R.id.articleCL);
            ConstraintLayout  sponsorshipCL = view.findViewById(R.id.sponsorshipCL);
            ConstraintLayout  storyCl = view.findViewById(R.id.storyCl);
            ConstraintLayout  productCl = view.findViewById(R.id.productCL);

            storyCl.setOnClickListener(this);
            postCL.setOnClickListener(this);
            pollCL.setOnClickListener(this);
            articleCL.setOnClickListener(this);
            sponsorshipCL.setOnClickListener(this);
            productCl.setOnClickListener(this);
            productCl.setVisibility(View.GONE);
/* Product Activity which is currently paused
            FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            productCl.setVisibility(View.VISIBLE);
                            productCl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent4 = new Intent(getContext(), ProductActivity.class);
                                    startActivity(intent4);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/

            uploadbottomsheet = new BottomSheetDialog(requireContext());
            uploadbottomsheet.setContentView(view);
        }
    }

    // Story fetching and loading
    private void checkSFollowing(){
        followingSList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingSList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        followingSList.add(snapshot.getKey());
                    }
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            readStory();
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

/* following list of user
   private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                followingList.add(userId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                loadPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/

/* Normal way to show sponsorship posts
   private void loadads() {
        DatabaseReference sponsorship = FirebaseDatabase.getInstance().getReference("Sponsorship");
        sponsorship.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                sponsorshipList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPost modelspon = ds.getValue(ModelPost.class);
                    if (Objects.requireNonNull(modelspon).getPrivacy().equals("public")){
                        sponsorshipList.add(modelspon);
                    }
                    if (sponsorshipList != null){
                       loadPost(sponsorshipList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadPost(List<ModelPost> sponsorshipList) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                result.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    for (String id : followingList){
                        if (Objects.requireNonNull(modelPost).getId().equals(id) && (modelPost.getType().equals("text"))){
                            postList.add(modelPost);
                        }
                    }
                }
                if (postList.size() != 0) {
                    merge(postList, sponsorshipList, 3, 1);
                }
                binding.pb.setVisibility(View.GONE);
                adapterPost = new AdapterPost(getActivity(), result);
                binding.postView.setAdapter(adapterPost);
                binding.pb.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
                binding.pbs.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    // Current solution which remove user post after 20 posts and load next 20 post
    private void loadPosts() {
        binding.pbs.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * ITEM_LOAD_COUNT);
        q.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               postList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        postList.add(modelPost);
                    }
                    if (isAdded()){
                        requireActivity().runOnUiThread(() -> {
                            if (TextUtils.isEmpty(node)){
                                adapterPost = new AdapterPost(getActivity(),postList);
                                binding.postView.setAdapter(adapterPost);
                                adapterPost.notifyDataSetChanged();
                                binding.pbs.setVisibility(View.GONE);
                                node = "some" ;
                            }else{
                                List<ModelPost> firstNElementsList = postList.stream().limit(20).collect(Collectors.toList());
                                adapterPost = new AdapterPost(getActivity(),firstNElementsList);
                                binding.postView.setAdapter(adapterPost);
                                adapterPost.notifyDataSetChanged();
                                binding.pbs.setVisibility(View.GONE);
                                binding.postView.setVisibility(View.VISIBLE);
                            }

                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

/*  Redundant code not working
  private void loadPosts2() {
        binding.pbs.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * ITEM_LOAD_COUNT);
        q.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               postList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        postList.add(modelPost);
                    }
                    if (isAdded()){
                        requireActivity().runOnUiThread(() -> {
                            List<ModelPost> firstNElementsList = postList.stream().limit(10).collect(Collectors.toList());
//                            Collections.reverse(postList);
//
//                            // Calculate index of last element
//                            int index = postList.size() - 10;
//
//                            // Delete last element by passing index
//                            postList.remove(index);
//
//                            Collections.reverse(postList);

//                            List<ModelPost> list = postList.subList(10, postList.size());
                            adapterPost = new AdapterPost(getActivity(),firstNElementsList);
                            binding.postView.setAdapter(adapterPost);
                            adapterPost.notifyDataSetChanged();
                            binding.pbs.setVisibility(View.GONE);
//                            if (adapterPost.getItemCount() == 0){
//                                binding.pb.setVisibility(View.GONE);
//                                binding.pbs.setVisibility(View.GONE);
//                                binding.postView.setVisibility(View.GONE);
//                            }else {
//                                binding.pb.setVisibility(View.GONE);
//                                binding.pbs.setVisibility(View.GONE);
//                                binding.postView.setVisibility(View.VISIBLE);
//                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

/* For merging sponsor and normal post
   public void merge(List<ModelPost> l1, List<ModelPost> l2, int r1, int r2) {
        int index1 = 0;
        int index2 = 0;

        while (index1 < l1.size() || index2 < l2.size()) {
            for (int i = 0; i < r1; ++i)
                result.add(l1.get((index1 + i) % l1.size()));
            index1 += r1;
            if (index2 < l2.size()) {
                for (int i = 0; i < r2; ++i)
                    result.add(l2.get((index2 + i) % l2.size()));
                index2 += r2;
            }

        }

    }*/

    // Fetching data for story from server
    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new ModelStory("",0,0,"", userId));
                AsyncTask.execute(() -> {
                    for (String id : followingSList){
                        int countStory = 0;
                        ModelStory modelStory = null;
                        for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                            modelStory = snapshot1.getValue(ModelStory.class);
                            if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                                countStory++;
                            }
                        }
                        if (countStory > 0){
                            storyList.add(modelStory);
                        }
                    }
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            binding.pb.setVisibility(View.GONE);
                            story.notifyDataSetChanged();
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
        }
    }

    @Override
    //Pressed return button - returns to the home page
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    requireActivity().finishAndRemoveTask();
                    return true;
                }
                return false;
            }
        });
    }


}
