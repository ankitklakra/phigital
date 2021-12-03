package com.phigital.ai.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.phigital.ai.Upload.PostActivity;
import com.phigital.ai.Upload.ArticleActivity;
import com.phigital.ai.R;


public class UploadFragment extends Fragment {

    private CardView Post,Feel,Article,Shoot;
    Context Activity;


    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        Post =v.findViewById(R.id.Post);
        Post.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), PostActivity.class)));

        Feel =v.findViewById(R.id.Feel);
//        Feel.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), Feel.class)));

        Article =v.findViewById(R.id.Article);
        Article.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), ArticleActivity.class)));

        Shoot =v.findViewById(R.id.Shoot);
//        Shoot.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), RecorderActivity.class)));

        return v;
    }


}