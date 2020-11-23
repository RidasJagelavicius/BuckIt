package com.example.buckit.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.buckit.MyListsActivity;
import com.example.buckit.PostActivity;
import com.example.buckit.R;

public class SearchFragment extends Fragment {

    private SearchViewModel mViewModel;
    protected Context thisContext;

    public void FirstFragment() {
        // Required empty public constructor
    }

    @Override
    // This and the method above enable creating new elements by replacing "this" with "thisContext"
    public void onAttach(Context context) {
        super.onAttach(context);
        thisContext=context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.activity_addusers1, container, false);

        Intent intent = new Intent(thisContext, PostActivity.class);
        startActivity(intent);
        return root;
    }
}