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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO: Figure out how to save state between fragments
// Because rn if you navigate to a different fragment and back, it's reset
public class HomeFragment extends Fragment implements View.OnClickListener {

    // Private variables
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

    // Open a popup for user to type in name of new bucket, then calls insertBucket()
    // https://www.youtube.com/watch?v=0DH2tZjJtm0
    public void createBucket() {
        // Create the dialog that asks user to name their bucket
        popup.setContentView(R.layout.new_bucket_popup);
        final EditText editText = (EditText) popup.findViewById(R.id.popupBucketName);
        Button btnCreate = (Button) popup.findViewById(R.id.popupCreateBucket);

        // By default, show the popup
        popup.show();

        // Once name bucket, create a new bucket
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String innerText = editText.getText().toString();

                // Make sure that the name for the bucket is valid
                if (innerText.length() > 0) {
                    // Insert the bucket
                    insertBucket(innerText);

                    // Close the popup
                    popup.dismiss();
                }
            }
        });
    }

    // Given the name for the bucket, actually inserts the bucket
    // Also removes the background + icon if it's the first bucket
    // TODO: Check if bucket with name already exists
    public void insertBucket(String name) {
        // A bucket will just be a styled button or something
        Button bucket = new Button(thisContext);
        LinearLayout.LayoutParams bucketParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        bucketParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
        bucket.setLayoutParams(bucketParams);
        bucket.setText(name);

        int bucketID = View.generateViewId();
        bucket.setId(bucketID);

        // Remove the + from the bucket container IF it's still there
        if (bucketContainer.findViewById(R.id.backgroundAddBuckets) != null)
            bucketContainer.removeAllViews();

        // Insert bucket into bucket container
        bucketContainer.addView(bucket);

        // Create JSON to represent bucket's lists
        createBucketJSON(bucketID);
    }

    // Used to check if master JSON (list of buckets and their inner lists) exists
    public boolean fileExists(Context context, String filename) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + filename;
        File file = new File(path);
        return file.exists();
    }

    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean create(Context context, String fileName, String jsonString){
        String FILENAME = "storage.json";
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    // Creates a JSON that looks like this
    /*
        {
            <bucketID 1>: [<list 1 ID>, <list 2 ID>, <list 3 ID>],
            <bucketID 2>: [<list 1 ID>, <list 2 ID>, <list 3 ID>]
        }

        * Appends to directory if already exists
        * By default, array of list ID's is empty

        Ref:
            Store and Retrieve: https://stackoverflow.com/questions/40168601/android-how-to-save-json-data-in-a-file-and-retrieve-it
     */
    public void createBucketJSON(int bucketID) {
        // Get the master directory if it exists, otherwise create one
        boolean masterExists = fileExists(thisContext, "bucket_to_list.json");
        JSONObject master;
        // Read if exists
        if (masterExists) {
            String jsonString = read(thisContext, "bucket_to_list.json");
            try {
                master = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read bucket-to-list mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        } else {
            // Create
            boolean fileCreated = create(thisContext, "bucket_to_list.json", "{}");
            if (!fileCreated) {
                Log.e("JSON", "Failed to create master JSON file");
                return;
            } else {
                String jsonString = read(thisContext, "bucket_to_list.json");
                try {
                    master = new JSONObject(jsonString);
                } catch (Throwable t) {
                    Log.e("JSON", "Failed to parse master JSON file");
                    t.printStackTrace();
                    return;
                }
            }
        }

        // Now that have the master JSON, insert a record from bucketID to an array
        JSONArray lists = new JSONArray();
        try {
            // Insert the empty lists array into the bucket
            master.put(Integer.toString(bucketID), lists);
            boolean fileCreated = create(thisContext, "bucket_to_list.json", master.toString());
            if (!fileCreated) {
                Log.e("JSON", "Failed to append to master JSON file");
                return;
            }
            Log.v("JSON", "Successfully inserted bucket-to-list mapping");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        backgroundAddBuckets.setOnClickListener(this);
        return root;
    }

    @Override
    // What happens when you click on something within onCreateView
    public void onClick(View v) {
        int myid = v.getId();

        // Check if clicked on a +
        if (myid == R.id.backgroundAddBuckets || myid == R.id.addBucket) {
            createBucket();
        }
    }
}