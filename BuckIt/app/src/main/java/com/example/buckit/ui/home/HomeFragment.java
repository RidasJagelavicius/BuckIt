package com.example.buckit.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.buckit.MyListsActivity;
import com.example.buckit.R;
import com.example.buckit.SharedCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.example.buckit.SharedCode.dpToPx;

// Because rn if you navigate to a different fragment and back, it's reset
public class HomeFragment extends Fragment implements View.OnClickListener {

    // Private variables
    private LinearLayout bucketContainer;
    private HomeViewModel homeViewModel;
    private Dialog popup;
    private Context thisContext;
    private ImageButton addBucket;
    private JSONObject master = null;
    private ArrayList<Button> buttonBucket  = new ArrayList<Button>();


    public void FirstFragment() {
        // Required empty public constructor
    }

    @Override
    // This and the method above enable creating new elements by replacing "this" with "thisContext"
    public void onAttach(Context context) {
        super.onAttach(context);
        thisContext=context;
    }

    // Either loads saved buckets or sets up a blank view
    private void loadOrBlank() {
        // Get the master directory if it exists, otherwise create one
        boolean masterExists = SharedCode.fileExists(thisContext, "bucket_to_list.json");

        // Read if exists
        if (masterExists) {
            String jsonString = SharedCode.read(thisContext, "bucket_to_list.json");
            try {
                master = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read bucket-to-list mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        } else {
            // Create
            boolean fileCreated = SharedCode.create(thisContext, "bucket_to_list.json", "{}");
            if (!fileCreated) {
                Log.e("JSON", "Failed to create master JSON file");
                return;
            } else {
                String jsonString = SharedCode.read(thisContext, "bucket_to_list.json");
                try {
                    master = new JSONObject(jsonString);
                } catch (Throwable t) {
                    Log.e("JSON", "Failed to parse master JSON file");
                    t.printStackTrace();
                    return;
                }
            }
        }

        // If master already existed, populate the buckets
        if (masterExists) {
            // Iterate through keys and make buckets of their name
            Iterator<String> keys = master.keys();
            if (keys.hasNext()) {
                do {
                    String bucketID = keys.next();
                    try {
                        JSONObject bucket = master.getJSONObject(bucketID);
                        String name = bucket.getString("name");
                        insertBucket(name, false, Integer.parseInt(bucketID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                } while (keys.hasNext());
            }
        } else {
            // Otherwise add the blank view
//            TextView addBucketText = new TextView(thisContext);
//            LinearLayout.LayoutParams theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            theseParams.setMargins(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(30)); // set size in DP
//            addBucketText.setLayoutParams(theseParams);
//            addBucketText.setText("Add your first bucket");
//            addBucketText.setTextColor(ContextCompat.getColor(thisContext, R.color.textPrimary));
//            addBucketText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25); // set size in SP
//
//            ImageButton backgroundAddBuckets = new ImageButton(thisContext);
//            theseParams = new LinearLayout.LayoutParams(dpToPx(200), dpToPx(200));
//            backgroundAddBuckets.setLayoutParams(theseParams);
//            backgroundAddBuckets.setAdjustViewBounds(true);
//            backgroundAddBuckets.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            backgroundAddBuckets.setBackgroundResource(R.drawable.add_in_circle);
//            backgroundAddBuckets.setId(R.id.backgroundAddBuckets);
//            backgroundAddBuckets.setOnClickListener(this);
//
//            bucketContainer.addView(addBucketText);
//            bucketContainer.addView(backgroundAddBuckets);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Delete the master JSON each time the device boots so ID's aren't reused and mappings don't break
        //thisContext.deleteFile("bucket_to_list.json");

        // Initialize things
        popup = new Dialog(thisContext);
        addBucket = (ImageButton) root.findViewById(R.id.addBucket);
        bucketContainer = root.findViewById(R.id.bucketContainer);

        // If data has already been created, retrieve it, otherwise display "Add a bucket"
        loadOrBlank();

        // Make it so that clicking a + will create a new container
        addBucket.setOnClickListener(this);
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
        else if (master != null){
            // See if it's a bucket
            String bucketID = Integer.toString(myid);
            if (master.has(bucketID)) {
                // If it is, start a new Activity and pass the array of list ID's to it
                try {
                    String dictionary = master.getString(bucketID);
//
//                    // Start the activity that shows the lists
                    Intent intent = new Intent(thisContext, MyListsActivity.class);
                    intent.putExtra("dict", dictionary); // pass the dictionary to the list
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    // Open a popup for user to type in name of new bucket, then calls insertBucket()
    // https://www.youtube.com/watch?v=0DH2tZjJtm0
    public void createBucket() {
        // Create the dialog that asks user to name their bucket
        popup.setContentView(R.layout.popup_new_bucket);
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
                    insertBucket(innerText, true, 0);

                    // Close the popup
                    popup.dismiss();
                }
            }
        });
    }

    // Given the name for the bucket, actually inserts the bucket
    // Also removes the background + icon if it's the first bucket
    // TODO: Check if bucket with name already exists?
    public void insertBucket(String name, boolean addToJson, int id) {
        // uppercase name here so later store it as uppercase in JSON
        name = name.toUpperCase();

        // A bucket will just be a styled button or something
        final Button bucket = new Button(thisContext);
        LinearLayout.LayoutParams bucketParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        bucketParams.setMargins(dpToPx(10), 0, dpToPx(10), dpToPx(10));
        bucket.setLayoutParams(bucketParams);
        bucket.setText(name);
        bucket.setTransformationMethod(null); // removes the ALL-caps
        bucket.setBackground(ContextCompat.getDrawable(thisContext, R.drawable.round_normal_outline_button));


        int bucketID;
        if (!addToJson)
                bucketID = id;
        else {
            do {bucketID = View.generateViewId();}
            while (master.has(Integer.toString(bucketID)));
        }
        bucket.setId(bucketID);

        // Remove the + from the bucket container IF it's still there
        if (bucketContainer.findViewById(R.id.backgroundAddBuckets) != null)
            bucketContainer.removeAllViews();

        // Give the button a listener so clicking on it opens its lists
        bucket.setOnClickListener(this);
        bucket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup.setContentView(R.layout.popup_delete);
                popup.show();

                Button cancel = popup.findViewById(R.id.cancel);
                Button delete = popup.findViewById(R.id.delete);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View t) {
                        // delete list from container
                        bucketContainer.removeView(bucket);

                        // delete list from JSON
                        int bucketID  = bucket.getId();
                        master.remove(Integer.toString(bucketID));
                        SharedCode.create(thisContext, "bucket_to_list.json", master.toString());

                        // delete list from internal buttonList arrayList
                        buttonBucket.remove(bucket);

                        popup.dismiss();
                    }
                });
                return true;
            }
        });

        // Insert bucket into bucket container
        bucketContainer.addView(bucket);

        // Create JSON to represent bucket's lists
        if (addToJson)
            createBucketJSON(bucketID, name);

        // Add the newly created bucket "button" to an array so that we can access it for deletion
        buttonBucket.add(bucket);
    }

    // Creates a JSON that looks like this
    /*
        {
            <bucketID 1>: {
                                bucketID : "1",
                                name : "",
                                lists: [<list 1 ID>, <list 2 ID>, <list 3 ID>],
                          },
            <bucketID 2>: {
                                bucketID : "2",
                                lists: [<list 1 ID>, <list 2 ID>, <list 3 ID>],
                          }
        }

        * Appends to directory if already exists
        * By default, array of list ID's is empty

        Ref:
            Store and Retrieve: https://stackoverflow.com/questions/40168601/android-how-to-save-json-data-in-a-file-and-retrieve-it
     */
    public void createBucketJSON(int bucketID, String bucketName) {
        // Now that have the master JSON, insert a record from bucketID to an array
        String toJSON = "{ \"bucketID\" : \"" + bucketID + "\", \"name\" : \"" + bucketName + "\", \"lists\": []}";
        try {
            JSONObject name_and_lists = new JSONObject(toJSON);

            // Insert the empty lists array into the bucket
            master.put(Integer.toString(bucketID), name_and_lists);
            boolean fileCreated = SharedCode.create(thisContext, "bucket_to_list.json", master.toString());
            if (!fileCreated) {
                Log.e("JSON", "Failed to append to master JSON file");
                return;
            }
            Log.v("JSON", "Successfully inserted bucket-to-list mapping");
        } catch (JSONException e) {
            Log.v("JSON", "Failed to create bucket-to-list mapping");
            e.printStackTrace();
        }
    }
}