package com.example.buckit.ui.home;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.buckit.R;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private LinearLayout bucketContainer;

    private HomeViewModel homeViewModel;
    private Dialog popup;
    private Context thisContext;
    private ImageButton addBucket;

    public void FirstFragment() {
        // Required empty public constructor
    }

    @Override
    // This and the method above enable creating new elements by replacing "this" with "thisContext"
    public void onAttach(Context context) {
        super.onAttach(context);
        thisContext=context;
    }

    // This method is called by setMargins to convert px to dp
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // https://www.youtube.com/watch?v=0DH2tZjJtm0
    public void createBucket() {
        // Create the dialog that asks user to name their bucket
        popup.setContentView(R.layout.new_bucket_popup);
        Button btnCreate = (Button) popup.findViewById(R.id.popupCreateBucket);

        // By default, show the popup
        popup.show();

        // Make it so that clicking the Create button dismisses the popup
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize things
        popup = new Dialog(thisContext);
        addBucket = (ImageButton) root.findViewById(R.id.addBucket);
        bucketContainer = root.findViewById(R.id.bucketContainer);

        // Since the program begins with no buckets,
        // make the background that + icon and text to add the button
        TextView addBucketText = new TextView(thisContext);
        LinearLayout.LayoutParams theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        theseParams.setMargins(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(30)); // set size in DP
        addBucketText.setLayoutParams(theseParams);
        addBucketText.setText("Add your first bucket");
        addBucketText.setTextColor(Color.parseColor("#000000"));
        addBucketText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25); // set size in SP

        ImageButton backgroundAddBuckets = new ImageButton(thisContext);
        theseParams = new LinearLayout.LayoutParams(dpToPx(200), dpToPx(200));
        backgroundAddBuckets.setLayoutParams(theseParams);
        backgroundAddBuckets.setAdjustViewBounds(true);
        backgroundAddBuckets.setScaleType(ImageView.ScaleType.FIT_CENTER);
        backgroundAddBuckets.setBackgroundResource(R.drawable.add_in_circle);
        backgroundAddBuckets.setId(R.id.backgroundAddBuckets);

        bucketContainer.addView(addBucketText);
        bucketContainer.addView(backgroundAddBuckets);

        // Make it so that clicking a + will create a new container
        addBucket.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        int myid = v.getId();

        // Check if clicked on a +
        if (myid == R.id.backgroundAddBuckets || myid == R.id.addBucket) {
            createBucket();
        }
    }
}