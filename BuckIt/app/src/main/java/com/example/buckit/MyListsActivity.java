package com.example.buckit;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.buckit.SharedCode.dpToPx;

public class MyListsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bucket;
    private LinearLayout listContainer;
    private ImageButton newListButton;
    private ImageButton deleteListButton;
    private Dialog popup;
    private Dialog deletePopup;
    private JSONObject master = null;
    private JSONObject dict;
    private JSONObject listMaster = null;
    private ArrayList<Button> buttonList  = new ArrayList<>();
    private String bucketID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylists);

        bucket = (TextView) findViewById(R.id.bucketName);
        popup = new Dialog(this);
        deletePopup = new Dialog(this);

        // Get name and lists passed from bucket
        try {
            // Have bucket name at top
            String dictionary = getIntent().getStringExtra("dict");
            dict = new JSONObject(dictionary);
            String name = dict.getString("name");
            bucketID = dict.getString("bucketID");
            bucket.setText(name);

            // Also grab lists
            // lists = new JSONArray(dict.getString("lists"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Initialize other components
        listContainer = (LinearLayout) findViewById(R.id.listContainer);
        newListButton = (ImageButton) findViewById(R.id.addList);
        deleteListButton = (ImageButton) findViewById(R.id.deleteList);

        newListButton.setOnClickListener(this);
        deleteListButton.setOnClickListener(this);
        newListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createList();
            }
        });
        deleteListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteList();
            }
        });

        // Load in the saved lists or say "Create your first list"
        loadOrBlank();
    }

    private ArrayList<Integer> goalCrossOutIds = new ArrayList<>(); // array of GOAL cross out button IDs
    private ArrayList<Integer> goalIds = new ArrayList<>(); // array of GOAL edittext IDs

    private void addGoal(Button currList) {
        //
//        Button list = new Button(this);
//        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
//        listParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
//        list.setLayoutParams(listParams);
//        list.setText(name);
//        list.setTransformationMethod(null); // removes the ALL-caps
//        list.setLongClickable(true);
//        list.setClickable(true);

        // each new element (a newGoal) consists of a difficult indicator (button) & a goal (text)
        LinearLayout newGoal = new LinearLayout(this);
//        LinearLayout.LayoutParams newGoal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        newGoal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        newGoal.setOrientation(LinearLayout.HORIZONTAL);

        // create difficulty icon button, goal text, and cross out button
        Button diffIndicator = new Button(this);
        diffIndicator.setBackground(ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_blank));
//        diffIndicator.setGravity(Gravity.START);
//        diffIndicator.setText(" ");
        diffIndicator.setLayoutParams(new ViewGroup.LayoutParams(130,ViewGroup.LayoutParams.WRAP_CONTENT));

        EditText goal = new EditText(this);
        goal.setHint(R.string.new_goal);
        goal.setMaxLines(2);
        goal.generateViewId();
        goalIds.add(goal.getId());

        Button goal_crossOut = new Button(this);
        goal_crossOut.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        goal_crossOut.setText("x");
        goal_crossOut.setGravity(Gravity.CENTER);
        goal_crossOut.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
        goal_crossOut.generateViewId();
        goalCrossOutIds.add(goal_crossOut.getId());

        newGoal.addView(diffIndicator);
        newGoal.addView(goal);
        newGoal.addView(goal_crossOut);

        // Insert goal into bucket container
        if(newGoal.getParent() != null) {
            ((ViewGroup)newGoal.getParent()).removeView(newGoal); // fix for some weird error
        }

        int idx = listContainer.indexOfChild(currList);
        listContainer.addView(newGoal,idx + 1);
    }

    private void addSubgoal() {
        EditText subGoal = new EditText(this);
        subGoal.setHint(R.string.new_subgoal);
        subGoal.setMaxLines(2);

        Button subgoal_crossOut = new Button(this);
        subgoal_crossOut.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        subgoal_crossOut.setText("x");
        subgoal_crossOut.setGravity(Gravity.RIGHT);
        subgoal_crossOut.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }


    // Load in the saved lists or say "Create your first list"
    private void loadOrBlank() {
        String jsonString = SharedCode.read(this, "bucket_to_list.json");
        try {
            master = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Get the master directory if it exists, otherwise create one
        boolean masterExists = SharedCode.fileExists(this, "lists.json");
        // Read if exists
        if (masterExists) {
            jsonString = SharedCode.read(this, "lists.json");
            try {
                listMaster = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read list mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        } else {
            // Create
            boolean fileCreated = SharedCode.create(this, "lists.json", "{}");
            if (!fileCreated) {
                Log.e("JSON", "Failed to create master JSON file");
                return;
            } else {
                jsonString = SharedCode.read(this, "lists.json");
                try {
                    listMaster = new JSONObject(jsonString);
                } catch (Throwable t) {
                    Log.e("JSON", "Failed to parse master JSON file");
                    t.printStackTrace();
                    return;
                }
            }
        }

        // If master already existed, populate the buckets
        if (masterExists) {
            // Iterate through keys and make lists of their name
            try {
                JSONObject thisbucket = master.getJSONObject(bucketID);
                JSONArray listIDs = thisbucket.getJSONArray("lists");
                for (int i = 0; i < listIDs.length(); i++) {
                    String listID = listIDs.getString(i);
                    JSONObject list = listMaster.getJSONObject(listID);
                    String name = list.getString("name");
                    insertList(name, false, Integer.parseInt(listID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            };
        } else {
            // Otherwise add the blank view
            TextView addListText = new TextView(this);
            LinearLayout.LayoutParams theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            theseParams.setMargins(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(30)); // set size in DP
            addListText.setLayoutParams(theseParams);
            addListText.setGravity(Gravity.CENTER);
            addListText.setText("Create your first list by pressing the \"+\" button below");
            addListText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
            addListText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25); // set size in SP
            addListText.setId(R.id.backgroundAddList);

            // Add it to the container
            listContainer.addView(addListText);
        }
    }

    // global var for tracking if short click was activated. if not, execute long click
    // cannot be defined inside onClick bc we are trying to use it in a function (.setOnClick) inside a function
    @Override
    public void onClick(View v) {
        int myid = v.getId();

        if (listMaster != null) {
            String listID = Integer.toString(myid);
            if (listMaster.has(listID)) {
                // show the add goal "dropdown"
                for (int i = 0; i < buttonList.size(); i++) {
                    Button currList = buttonList.get(i);
                    if (currList.getId() == myid)
                        addGoal(currList);

                }
                // If it is, start a new Activity
//                try {
//                    String dictionary = listMaster.getString(listID);
//
//                    // Start the activity that shows the lists
//                    Intent intent = new Intent(this, ListActivity.class);
//                    intent.putExtra("dict", dictionary); // pass the dictionary to the list
//                    startActivity(intent);

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    return;
//                }
            }
        }
    }

    // Open a popup for user to type in name of new list, then calls insertList()
    // https://www.youtube.com/watch?v=0DH2tZjJtm0
    public void createList() {
        assert(popup != null);
        // Create the dialog that asks user to name their bucket
        popup.setContentView(R.layout.new_list_popup);
        final EditText editText = (EditText) popup.findViewById(R.id.popupListName);
        Button btnCreate = (Button) popup.findViewById(R.id.popupCreateList);

        // By default, show the popup
        popup.show();

        // Once name list, create a new list
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String innerText = editText.getText().toString();

                // Make sure that the name for the list is valid
                if (innerText.length() > 0) {
                    // Insert the list
                    insertList(innerText, true, 0);

                    // Close the popup
                    popup.dismiss();
                }
            }
        });
    }

    // Open a popup for user to type in name of list to delete
    public void deleteList() {
        assert(deletePopup != null);
        // Create the dialog that asks user to name their bucket
        deletePopup.setContentView(R.layout.delete_list_popup);
        final EditText editText = (EditText) deletePopup.findViewById(R.id.popupListToDelete);
        Button btnDelete = (Button) deletePopup.findViewById(R.id.popupDeleteList);

        // By default, show the popup
        deletePopup.show();
        Log.v("popup","delete popup should be showing");

        // compare user input string with text on the current buckets (buttons)
        // delete bucket if text matches
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toDelete = editText.getText().toString().toUpperCase();

                // Make sure that the name for the list is valid
                if (toDelete.length() > 0) {
                    for (int i = 0; i < buttonList.size(); i++) {
                        Button currList = buttonList.get(i);
                        if (currList.getText().equals(toDelete)) {
                            // delete list from container
                            listContainer.removeView(currList);

                            // delete list from JSON
                            int listID  = currList.getId();
                            listMaster.remove(Integer.toString(listID));

                            // delete list from internal buttonList arrayList
                            buttonList.remove(currList);
                            break;

                        }
                    }

                    // Close the popup
                    deletePopup.dismiss();
                }
            }
        });
        Log.v("popup","delete popup - activated onlick");
    }

    // Given the name for the list, actually inserts the list
    // Also removes the background "Create list" if it's the first list
    // TODO: Check if list with name already exists
    public void insertList(String name, boolean addToJson, int id) {
        // uppercase name here so later store it as uppercase in JSON
        name = name.toUpperCase();

        // A list will just be a styled button or something
        Button list = new Button(this);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        listParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
        list.setLayoutParams(listParams);
        list.setText(name);
        list.setTransformationMethod(null); // removes the ALL-caps
        list.setLongClickable(true);
        list.setClickable(true);

        int listID;
        if (!addToJson)
            listID = id;
        else {
            do {
                listID = View.generateViewId();
            }
            while (listMaster.has(Integer.toString(listID)));
        }
        list.setId(listID);

        // Remove the "Create new list" from the list container IF it's still there
        if (listContainer.findViewById(R.id.backgroundAddList) != null)
            listContainer.removeAllViews();

        // Give the button a listener so clicking on it opens its lists
        list.setOnClickListener(this);

        // Insert bucket into bucket container
        if(list.getParent() != null) {
            ((ViewGroup)list.getParent()).removeView(list); // fix for some weird error
        }
        listContainer.addView(list);

        // Create JSON to represent bucket's lists
        if (addToJson)
            createListJSON(listID, name);

        // Add the newly created list "button" to an array so that we can access it for deletion
        buttonList.add(list);
    }

    // Creates a JSON that looks like this
    /*
        {
            <listID 1>: {
                                listID : "",
                                name : "",
                                privacy : "",
                                collaborators : "",
                                photos : [],
                                items : []
                          },
            <listID 2>: {
                              ...
                          }
        }

        * Appends to directory if already exists
        * By default, array of list ID's is empty

        Ref:
            Store and Retrieve: https://stackoverflow.com/questions/40168601/android-how-to-save-json-data-in-a-file-and-retrieve-it
     */
    public void createListJSON(int listID, String listName) {
        // Now that have the master JSON, insert a record from listID to an array
        String toJSON = "{ \"listID\" : \"" + listID + "\", \"name\" : \"" + listName + "\", \"privacy\" : \"private\", \"collaborators\" : \"\", \"photos\" : [], \"goals\": []}";

        try {
            JSONObject listStuff = new JSONObject(toJSON);

            // Insert the empty lists array into the bucket
            listMaster.put(Integer.toString(listID), listStuff);
            boolean fileCreated = SharedCode.create(this, "lists.json", listMaster.toString());
            if (!fileCreated) {
                Log.e("JSON", "Failed to append to master JSON file");
                return;
            }
            Log.v("JSON", "Successfully inserted list mapping");
        } catch (JSONException e) {
            Log.v("JSON", "Failed to create list mapping");
            e.printStackTrace();
        }

        // Then insert the list ID into the bucket_to_lists JSON
        if (master == null) {
            String jsonString = SharedCode.read(this, "bucket_to_list.json");
            try {
                master = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read bucket-to-list mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        }

        try {
            // Replace instance in JSON with updated list
            String bucketID = dict.getString("bucketID");
            JSONObject thisbucket = master.getJSONObject(bucketID);
            JSONArray bucketLists = thisbucket.getJSONArray("lists");

            // Don't touch the order of these
            thisbucket.remove("lists");
            bucketLists.remove(listID);
            bucketLists.put(Integer.toString(listID));
            thisbucket.put("lists", bucketLists);
            master.remove(bucketID);
            master.put(bucketID, thisbucket);

            // Rewrite the JSON
            SharedCode.create(this, "bucket_to_list.json", master.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
