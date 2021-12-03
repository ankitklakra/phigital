package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.Query;
import com.phigital.ai.Job.ApplyActivity;
import com.phigital.ai.Job.ArticlervAdapter;
import com.phigital.ai.Job.JobFragAdapter;
import com.phigital.ai.Job.JobFragAdapter2;
import com.phigital.ai.Job.JobSearch;
import com.phigital.ai.Job.MultipleSearch;
import com.phigital.ai.Job.MultipleSearch2;
import com.phigital.ai.Adapter.AdapterArticle;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelArticle;
import com.phigital.ai.News.NewsActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.databinding.FragmentJobBinding;

import java.util.ArrayList;
import java.util.List;


public class JobFragment extends Fragment {

    private final ArrayList<String> mtext = new ArrayList<>();
    private final ArrayList<String> mnum = new ArrayList<>();
    private final ArrayList<String> mtext2 = new ArrayList<>();
    private final ArrayList<String> mnum2 = new ArrayList<>();
    private final ArrayList<String> mtext3 = new ArrayList<>();

    List<ModelArticle> articleList;
    AdapterArticle adapterArticle;

    String a,b,c,d,e,f;
    String a2,b2,c2,d2,e2,f2;
    FragmentJobBinding binding;
    String id;
    long initial;
    boolean vs = true;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrenPage = 1;

  public JobFragment() {
        // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentJobBinding.inflate(getLayoutInflater());
      article();
      LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
      layoutManager.setStackFromEnd(true);
      layoutManager.setReverseLayout(true);
      binding.articleView.setLayoutManager(layoutManager);

      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Article");
      reference.addValueEventListener(new ValueEventListener() {
          @SuppressLint("SetTextI18n")
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              int i = 0;
              for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                  i++;
              }
              initial = i;
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

      binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
              if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                  if (vs) {
                      mCurrenPage++;
                      article();
                  }
              }
          });
      return binding.getRoot();
  }

   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.applynow.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ApplyActivity.class);
            startActivity(intent);
        });
        binding.jobalert.setOnClickListener(v -> {
           Intent intent = new Intent(getActivity(), NewsActivity.class);
           startActivity(intent);
        });
        binding.searchBar.setOnClickListener(v -> {
           Intent intent = new Intent(getActivity(), JobSearch.class);
            intent.putExtra("hisUid", id);
           startActivity(intent);
        });
        binding.jobtv.setOnClickListener(v -> {
           Intent intent = new Intent(getActivity(), MultipleSearch.class);
           startActivity(intent);
       });
        binding.aaptv.setOnClickListener(v -> {
           Intent intent = new Intent(getActivity(), MultipleSearch2.class);
           startActivity(intent);
       });

        getRC();
        getDC();
        loadmore();

        articleList = new ArrayList<>();

    }
    private void getDC() {
        mtext2.add("Private");
        mnum2.add(a2);
        mtext2.add("Skilled");
        mnum2.add(b2);
        mtext2.add("Assistant");
        mnum2.add(c2);
        mtext2.add("Freelancing");
        mnum2.add(d2);
        mtext2.add("Others");
        mnum2.add(f2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        binding.appView.setLayoutManager(layoutManager2);
        JobFragAdapter2 adapter2 = new JobFragAdapter2(mtext2,mnum2,getActivity());
        binding.appView.setAdapter(adapter2);
    }

    private void getRC() {
        mtext.add("Private");
        mnum.add(a);
        mtext.add("Skilled");
        mnum.add(b);
        mtext.add("Assistant");
        mnum.add(c);
        mtext.add("Freelancing");
        mnum.add(d);
        mtext.add("Others");
        mnum.add(f);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        binding.jobView.setLayoutManager(layoutManager);
        JobFragAdapter adapter = new JobFragAdapter(mtext,mnum,getActivity());
        binding.jobView.setAdapter(adapter);
    }

    private void loadmore() {
      mtext3.add("Business");
      mtext3.add("Education");
      mtext3.add("Entertainment");
      mtext3.add("Food");
      mtext3.add("History");
      mtext3.add("Health");
      mtext3.add("Fashion");
      mtext3.add("Literature");
      mtext3.add("Media");
      mtext3.add("Politics");
      mtext3.add("Science");
      mtext3.add("Sports");
      mtext3.add("Technology");
      mtext3.add("Others");
      LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
      binding.articlervView.setLayoutManager(layoutManager3);
      ArticlervAdapter adapter3 = new ArticlervAdapter(mtext3,getActivity());
      binding.articlervView.setAdapter(adapter3);
    }


    private void article() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Article");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelArticle modelArticle = ds.getValue(ModelArticle.class);
                    articleList.add(modelArticle);
                }
                adapterArticle = new AdapterArticle(getActivity(), articleList);
                binding.articleView.setAdapter(adapterArticle);
                adapterArticle.notifyDataSetChanged();

                if (adapterArticle.getItemCount() == 0){
                    binding.articleView.setVisibility(View.GONE);
                }else {
                    binding.articleView.setVisibility(View.VISIBLE);
                    if(adapterArticle.getItemCount() == initial){
                        vs = false;
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

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });
    }
}