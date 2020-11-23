package com.example.buckit.ui.inspirationFeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.buckit.R;

public class InspirationFeedFragment extends Fragment implements View.OnClickListener{

    private InspirationFeedViewModel mViewModel;
    ViewPager viewPager;
    private Button travel_button;
    private Button cook_button;
    private Button kind_button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(InspirationFeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_inspiration_feed, container, false);
        viewPager = (ViewPager) root.findViewById(R.id.viewpager);

        //Creates the slider for the friends
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getContext());
        viewPager.setAdapter(viewPagerAdapter);

        //buttons for the trending categories
        travel_button = (Button) root.findViewById(R.id.travel_button);
        travel_button.setOnClickListener(this);

        cook_button = (Button) root.findViewById(R.id.cooking_button);
        cook_button.setOnClickListener(this);

        kind_button = (Button) root.findViewById(R.id.kindness_button);
        kind_button.setOnClickListener(this);

        return root;
    }

    public void onClick(View v) {
        if(v.getId() == R.id.travel_button){
            Intent intent = new Intent(this.getActivity(), TravelActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setMessage("Unable to load lists. Please try again later.");

            // Set Alert Title
            builder.setTitle("Error!");

            builder.setCancelable(true);

            //Set Ok button to dismiss popup
            builder.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();
        }
    }
}
