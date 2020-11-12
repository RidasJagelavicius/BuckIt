package com.example.buckit.ui.inspirationFeed;

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

import com.example.buckit.R;

public class InspirationFeedFragment extends Fragment {

    private InspirationFeedViewModel ifViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ifViewModel =
                ViewModelProviders.of(this).get(InspirationFeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_inspiration_feed, container, false);
        final TextView textView = root.findViewById(R.id.text_inspiration_feed);
        ifViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}