package com.example.buckit;

import android.app.Dialog;
import android.content.Intent;
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
import java.util.Iterator;

import static com.example.buckit.SharedCode.dpToPx;

public class GoalActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bucket;
    private LinearLayout goalContainer;
    private ImageButton newGoalButton;
    private ImageButton deleteGoalButton;
    private Dialog popup;
    private Dialog deletePopup;
    private JSONObject master = null;
    private JSONObject dict;
    private JSONObject goalMaster = null;
    private ArrayList<Button> buttonGoal  = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        bucket = (TextView) findViewById(R.id.bucketName);
        popup = new Dialog(this);
        deletePopup = new Dialog(this);

        // Get name and goals passed from bucket
        try {
            // Have bucket name at top
            String dictionary = getIntent().getStringExtra("dict");
            dict = new JSONObject(dictionary);
            String name = dict.getString("name");
            bucket.setText(name);

            // Also grab goals
            // goals = new JSONArray(dict.getString("goals"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Initialize other components
        goalContainer = (LinearLayout) findViewById(R.id.goalContainer);
        newGoalButton = (ImageButton) findViewById(R.id.addGoal);
        deleteGoalButton = (ImageButton) findViewById(R.id.deleteGoal);

        newGoalButton.setOnClickListener(this);
        deleteGoalButton.setOnClickListener(this);

        // Load in the saved goals or say "Create your first goal"
        loadOrBlank();
    }

    // Load in the saved goals or say "Create your first goal"
    private void loadOrBlank() {
        // Get the master directory if it exists, otherwise create one
        boolean masterExists = SharedCode.fileExists(this, "goals.json");
        // Read if exists
        if (masterExists) {
            String jsonString = SharedCode.read(this, "goals.json");
            try {
                goalMaster = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read goal mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        } else {
            // Create
            boolean fileCreated = SharedCode.create(this, "goals.json", "{}");
            if (!fileCreated) {
                Log.e("JSON", "Failed to create master JSON file");
                return;
            } else {
                String jsonString = SharedCode.read(this, "goals.json");
                try {
                    goalMaster = new JSONObject(jsonString);
                } catch (Throwable t) {
                    Log.e("JSON", "Failed to parse master JSON file");
                    t.printStackTrace();
                    return;
                }
            }
        }

        // If master already existed, populate the buckets
        if (masterExists) {
            // Iterate through keys and make goals of their name
            Iterator<String> keys = goalMaster.keys();
            if (keys.hasNext()) {
                do {
                    String goalID = keys.next();
                    try {
                        JSONObject goal = goalMaster.getJSONObject(goalID);
                        String name = goal.getString("name");
                        insertGoal(name, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                } while(keys.hasNext());
            }
        } else {
            // Otherwise add the blank view
            TextView addGoalText = new TextView(this);
            LinearLayout.LayoutParams theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            theseParams.setMargins(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(30)); // set size in DP
            addGoalText.setLayoutParams(theseParams);
            addGoalText.setGravity(Gravity.CENTER);
            addGoalText.setText("Create your first goal by pressing the \"+\" button below");
            addGoalText.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
            addGoalText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25); // set size in SP
            addGoalText.setId(R.id.backgroundAddList);

            // Add it to the container
            goalContainer.addView(addGoalText);
        }
    }

    // global var for tracking if short click was activated. if not, execute long click
    // cannot be defined inside onClick bc we are trying to use it in a function (.setOnClick) inside a function
    boolean shortClickPressed = false;
    @Override
    public void onClick(View v) {
        int myid = v.getId();

        if (myid == R.id.deleteGoal){
            deleteGoal();
        } else if (myid == R.id.addGoal) {
            // See if need to create a new goal
            createGoal();
        }
        if (goalMaster != null) {
            String goalID = Integer.toString(myid);
            if (goalMaster.has(goalID)) {
                // If it is, start a new Activity
                try {
                    String dictionary = goalMaster.getString(goalID);

                    // Start the activity that shows the goals
                    Intent intent = new Intent(this, GoalActivity.class);
//                    Intent intent = new Intent(this, GoalActivity.class);
                    intent.putExtra("dict", dictionary); // pass the dictionary to the goal
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void onShortClick(View v) {
        // See if it's a goal
        int myid = v.getId();
        String goalID = Integer.toString(myid);
        if (goalMaster.has(goalID)) {
            // If it is, start a new Activity
            try {
                String dictionary = goalMaster.getString(goalID);

                // Start the activity that shows the goals
                Intent intent = new Intent(this, GoalActivity.class);
                intent.putExtra("dict", dictionary); // pass the dictionary to the goal
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }


    // Open a popup for user to type in name of new goal, then calls insertGoal()
    // https://www.youtube.com/watch?v=0DH2tZjJtm0
    public void createGoal() {
        assert(popup != null);
        // Create the dialog that asks user to name their bucket
        popup.setContentView(R.layout.new_goal_popup);
        final EditText editText = (EditText) popup.findViewById(R.id.popupGoalName);
        Button btnCreate = (Button) popup.findViewById(R.id.popupCreateGoal);

        // By default, show the popup
        popup.show();

        // Once name goal, create a new goal
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String innerText = editText.getText().toString();

                // Make sure that the name for the goal is valid
                if (innerText.length() > 0) {
                    // Insert the goal
                    insertGoal(innerText, true);

                    // Close the popup
                    popup.dismiss();
                }
            }
        });
    }

    // Open a popup for user to type in name of goal to delete
    public void deleteGoal() {
        assert(deletePopup != null);
        // Create the dialog that asks user to name their bucket
        deletePopup.setContentView(R.layout.delete_goal_popup);
        final EditText editText = (EditText) deletePopup.findViewById(R.id.popupGoalToDelete);
        Button btnDelete = (Button) deletePopup.findViewById(R.id.popupDeleteGoal);

        // By default, show the popup
        deletePopup.show();
        Log.v("popup","delete popup should be showing");

        // compare user input string with text on the current buckets (buttons)
        // delete bucket if text matches
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toDelete = editText.getText().toString().toUpperCase();

                // Make sure that the name for the goal is valid
                if (toDelete.length() > 0) {
                    for (int i = 0; i < buttonGoal.size(); i++) {
                        Button currGoal = buttonGoal.get(i);
                        if (currGoal.getText().equals(toDelete)) {
                            // delete goal from container
                            goalContainer.removeView(currGoal);

                            // delete goal from JSON
                            int goalID  = currGoal.getId();
                            goalMaster.remove(Integer.toString(goalID));

                            // delete goal from internal buttonGoal arrayGoal
                            buttonGoal.remove(currGoal);
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

    // Given the name for the goal, actually inserts the goal
    // Also removes the background "Create goal" if it's the first goal
    // TODO: Check if goal with name already exists
    public void insertGoal(String name, boolean addToJson) {
        // uppercase name here so later store it as uppercase in JSON
        name = name.toUpperCase();

        // A goal will just be a styled button or something
        Button goal = new Button(this);
        LinearLayout.LayoutParams goalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        goalParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
        goal.setLayoutParams(goalParams);
        goal.setText(name);
        goal.setTransformationMethod(null); // removes the ALL-caps
        goal.setLongClickable(true);
        goal.setClickable(true);

        int goalID = View.generateViewId();
        goal.setId(goalID);

        // Remove the "Create new goal" from the goal container IF it's still there
        if (goalContainer.findViewById(R.id.backgroundAddList) != null)
            goalContainer.removeAllViews();

        // Give the button a listener so clicking on it opens its goals
        goal.setOnClickListener(this);

        // Insert bucket into bucket container
        if(goal.getParent() != null) {
            ((ViewGroup)goal.getParent()).removeView(goal); // fix for some weird error
        }
        goalContainer.addView(goal);

        // Create JSON to represent bucket's goals
        if (addToJson)
            createGoalJSON(goalID, name);

        // Add the newly created goal "button" to an array so that we can access it for deletion
        buttonGoal.add(goal);
    }

    // Creates a JSON that looks like this
    /*
        {
            <goalID 1>: {
                                goalID : "",
                                name : "",
                                privacy : "",
                                collaborators : "",
                                photos : [],
                                items : []
                          },
            <goalID 2>: {
                              ...
                          }
        }

        * Appends to directory if already exists
        * By default, array of goal ID's is empty

        Ref:
            Store and Retrieve: https://stackoverflow.com/questions/40168601/android-how-to-save-json-data-in-a-file-and-retrieve-it
     */
    public void createGoalJSON(int goalID, String goalName) {
        // Now that have the master JSON, insert a record from goalID to an array
        String toJSON = "{ \"goalID\" : \"" + goalID + "\", \"name\" : \"" + goalName + "\", \"goals\": []}";
//        String toJSON = "{ \"goalID\" : \"" + goalID + "\", \"name\" : \"" + goalName + "\", \"privacy\" : \"private\", \"collaborators\" : \"\", \"photos\" : [], \"items\": []}";

        try {
            JSONObject goalStuff = new JSONObject(toJSON);

            // Insert the empty goals array into the bucket
            goalMaster.put(Integer.toString(goalID), goalStuff);
            boolean fileCreated = SharedCode.create(this, "goals.json", goalMaster.toString());
            if (!fileCreated) {
                Log.e("JSON", "Failed to append to master JSON file");
                return;
            }
            Log.v("JSON", "Successfully inserted goal mapping");
        } catch (JSONException e) {
            Log.v("JSON", "Failed to create goal mapping");
            e.printStackTrace();
        }

        // Then insert the goal ID into the bucket_to_goals JSON
        if (master == null) {
            String jsonString = SharedCode.read(this, "bucket_to_goal.json");
            try {
                master = new JSONObject(jsonString);
                Log.v("JSON", "Successfully read bucket-to-goal mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse master JSON file");
                return;
            }
        }

        try {
            // Replace instance in JSON with updated goal
            String bucketID = dict.getString("bucketID");
            JSONObject thisbucket = master.getJSONObject(bucketID);
            JSONArray bucketGoals = thisbucket.getJSONArray("goals");

//            thisbucket.remove("goals");
//            bucketGoals.remove(goalID);
            bucketGoals.put(Integer.toString(goalID));
            thisbucket.put("goals", bucketGoals);

//            master.remove(bucketID);
            master.put(bucketID, thisbucket);

            // Rewrite the JSON
            SharedCode.create(this, "bucket_to_goal.json", master.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
