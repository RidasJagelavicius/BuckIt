package com.example.buckit;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static com.example.buckit.SharedCode.dpToPx;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView list;
    private Button addPhoto;
    private String listID;
    private JSONObject listMaster = null;
    private TableLayout gallery;
    private ImageButton changePrivacy;
    private LinearLayout goalContainer;
    private ImageButton newGoalButton;
    private ImageButton deleteGoalButton;
    private Dialog popup;
    private Dialog deletePopup;
    private ArrayList<Button> buttonGoal  = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        list = (TextView) findViewById(R.id.listName);
        addPhoto = (Button) findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(this);
        gallery = (TableLayout) findViewById(R.id.mylistgallery);
        changePrivacy = (ImageButton) findViewById(R.id.changePrivacy);
        changePrivacy.setOnClickListener(this);
        popup = new Dialog(this);

        // Load JSON
        if (listMaster == null) {
            String jsonString = SharedCode.read(this, "lists.json");
            try {
                listMaster = new JSONObject(jsonString);
                Log.v("JSON", "Successfully loaded lists mapping");
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse listMaster JSON file");
                return;
            }
        }

//         Get name and lists passed from bucket
        try {
            // Have bucket name at top
            String dictionary = getIntent().getStringExtra("dict");
            JSONObject dict = new JSONObject(dictionary);
            String name = dict.getString("name");
            listID = dict.getString("listID");
            list.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        // Initialize other components
        goalContainer = (LinearLayout) findViewById(R.id.goalContainer);
        newGoalButton = (ImageButton) findViewById(R.id.addGoal);
        deleteGoalButton = (ImageButton) findViewById(R.id.deleteGoal);

        newGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                createGoal();
                addGoal();
            }
        });

        deleteGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteGoal();
            }
        });

        // Load in the saved goals or say "Create your first goal"
        loadOrBlank();

        // Populate things
        try {
            JSONObject thislist = listMaster.getJSONObject(listID);
            String privacy = thislist.getString("privacy");
            if (privacy != "") {
                updatePrivacy(-1, privacy);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    // Load in the saved goals or say "Create your first goal"
    private void loadOrBlank() {
        // Get the master directory if it exists, otherwise create one
        // DO NOT MAKE THIS GOALS
        boolean masterExists = listMaster != null;
        // If master already existed, populate the buckets
        if (masterExists) {
            // Iterate through keys and make goals of their name
//            Iterator<String> keys = listMaster.keys();
//            if (keys.hasNext()) {
//                do {
//                    String goalID = keys.next();
//                    try {
//                        JSONObject goal = listMaster.getJSONObject(goalID);
//                        String name = goal.getString("name");
//                        listMaster(name, false);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                } while(keys.hasNext());
//            }
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
                    addGoal();
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
//                            goalMaster.remove(Integer.toString(goalID));

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
//        if (addToJson)
//            createGoalJSON(goalID, name);

        // Add the newly created goal "button" to an array so that we can access it for deletion
        buttonGoal.add(goal);

        // Update privacy icon if saved

    }

    @Override
    public void onClick(View v) {
        int myid = v.getId();

        if (goalDiffIndicIds.contains(myid))
            changeDifficulty(myid);

        if (goalCrossOutIds.contains(myid)) {
            Toast.makeText(this, "cross out clicked", Toast.LENGTH_SHORT).show();
            crossOut(myid);
        }

        if (myid == R.id.addPhoto) {
            verifyStoragePermissions(this);
            selectImage(this);
        } else if (myid == R.id.changePrivacy) {
            // Create the dialog that allows user to click on each
            popup.setContentView(R.layout.change_privacy_popup);

            // By default, show the popup
            popup.show();

            // Add id's to each button, give them a listener for clicks, and on click, change JSON and icon in list
            ImageView public_ = popup.findViewById(R.id.vispublic);
            ImageView private_ = popup.findViewById(R.id.visprivate);
            ImageView friend_ = popup.findViewById(R.id.visfriend);
            ImageView list_ = popup.findViewById(R.id.vislist);
            public_.setOnClickListener(this);
            private_.setOnClickListener(this);
            friend_.setOnClickListener(this);
            list_.setOnClickListener(this);
        } else if (myid == R.id.visfriend || myid == R.id.vislist || myid == R.id.visprivate || myid == R.id.vispublic) {
            updatePrivacy(myid, null);
            popup.dismiss();
        }

//        try {
//
////                for (int i = 0; i < buttonList.size(); i++) {
////                    Button currList = buttonList.get(i);
////                    if (currList.getId() == myid)
////                        addGoal(currList);
////
////                }
//            // show the add goal "dropdown"
//            for (int i = 0; i < buttonGoal.size(); i++) {
//                Button currGoal = buttonGoal.get(i);
//                if (myid == currGoal.getId()) {
////                    addGoal(currGoal);
////                    Intent intent = new Intent(this, SubgoalActivity.class);
////                    startActivity(intent);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
    }

    private ArrayList<Integer> goalCrossOutIds = new ArrayList<>(); // array of GOAL cross out button IDs
    private ArrayList<Integer> goalDiffIndicIds = new ArrayList<>(); // array of GOAL difficulty indicator IDs
    private ArrayList<Button> goalDiffIndicators = new ArrayList<>(); // array of GOAL difficulty indicator buttons
    private ArrayList<EditText> goalsList = new ArrayList<>(); // array of GOAL edittexts

    private void addGoal() { //Button currList
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
        final Button diffIndicator = new Button(this);
        diffIndicator.setBackground(ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_blank));
        diffIndicator.setLayoutParams(new ViewGroup.LayoutParams(130,ViewGroup.LayoutParams.WRAP_CONTENT));

        diffIndicator.setId(View.generateViewId());
        goalDiffIndicIds.add(diffIndicator.getId());
        goalDiffIndicators.add(diffIndicator);
        diffIndicator.setClickable(true);
        diffIndicator.setOnClickListener(this);
//        Toast.makeText(this, goalDiffIndicIds.toString(), Toast.LENGTH_SHORT).show();

        EditText goal = new EditText(this);
        goal.setHint(R.string.new_goal);
        goal.setSingleLine(false);
        goal.setLayoutParams(new ViewGroup.LayoutParams(750,ViewGroup.LayoutParams.WRAP_CONTENT));
        goal.setId(View.generateViewId());
        goalsList.add(goal);

        Button goal_crossOut = new Button(this);
        goal_crossOut.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        goal_crossOut.setText("x");
        goal_crossOut.setGravity(Gravity.CENTER);
        goal_crossOut.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
        goal_crossOut.setId(View.generateViewId());
        goalCrossOutIds.add(goal_crossOut.getId());
        goal_crossOut.setClickable(true);
        goal_crossOut.setOnClickListener(this);
//        Toast.makeText(this, goalCrossOutIds.toString(), Toast.LENGTH_SHORT).show();


        newGoal.addView(diffIndicator);
        newGoal.addView(goal);
        newGoal.addView(goal_crossOut);

        // Insert goal into bucket container
        if(newGoal.getParent() != null) {
            ((ViewGroup)newGoal.getParent()).removeView(newGoal); // fix for some weird error
        }

//        int idx = goalContainer.indexOfChild(currList);
//        goalContainer.addView(newGoal,idx + 1);
        goalContainer.addView((newGoal));
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


    private void changeDifficulty(int id) {
        Toast.makeText(this, "changed difficulty", Toast.LENGTH_SHORT).show();
        int idx = goalDiffIndicIds.indexOf(id);
        Button bulletPoint = goalDiffIndicators.get(idx);

        Drawable blankBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_blank);
        Drawable easyBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_easy);
        Drawable medBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_med);
        Drawable hardBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_hard);

        // check if current bullet is blank (grey)
        if (Objects.equals(bulletPoint.getBackground().getConstantState(), this.getResources().getDrawable(R.drawable.difficulty_indicator_blank).getConstantState()))
            bulletPoint.setBackground(easyBullet);

        // check if current bullet is easy (green)
        else if (Objects.equals(bulletPoint.getBackground().getConstantState(), this.getResources().getDrawable(R.drawable.difficulty_indicator_easy).getConstantState()))
            bulletPoint.setBackground(medBullet);

        // check if current bullet is med (yellow)
        else if (Objects.equals(bulletPoint.getBackground().getConstantState(), this.getResources().getDrawable(R.drawable.difficulty_indicator_med).getConstantState()))
            bulletPoint.setBackground(hardBullet);

        // check if current bullet is hard (red)
        else
            bulletPoint.setBackground(blankBullet);

    }

    private void crossOut(int id) {

    }

    // Allow user to select an image from gallery or from phone when click on Add Photo
    // Ref: https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
    private void selectImage(Context context) {
        final CharSequence[] options = {"Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Insert a picture path into this list's photos array, then update listMaster JSON
    private void addPathToPhotos(String path) {
        try {
            JSONObject thislist = listMaster.getJSONObject(listID);
            JSONArray photos = thislist.getJSONArray("photos");
            photos.put(path);

            // Remake JSON
            thislist.remove("photos");
            thislist.put("photos", photos);
            listMaster.remove(listID);
            listMaster.put(listID, thislist);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    private void addToImageGallery(String path) {
        // Decide if need to make a new row or insert into existing one
        // Do this by checking the first row
        int lastChildIndex = gallery.getChildCount() - 1;
        Log.v("child", Integer.toString(lastChildIndex));
        TableRow lastRow = null;
        if (lastChildIndex >= 0)
            lastRow = (TableRow) ((ViewGroup) gallery).getChildAt(lastChildIndex);
        if (lastChildIndex < 0 || lastRow.getChildCount() == 3) {
            // Insert a new row
            Log.v("MyList", "Last row has 3 kids, making new row");
            lastRow = new TableRow(this);
            TableRow.LayoutParams theseParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lastRow.setLayoutParams(theseParams);
            lastRow.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            lastRow.setGravity(Gravity.CENTER_VERTICAL);
            lastRow.setBackgroundColor(getResources().getColor(R.color.popupBackground));
            gallery.addView(lastRow);
        }

        // Create an ImageView and insert it into the gallery
        AppCompatImageView photo = new AppCompatImageView(this);
        lastRow.addView(photo);
        TableRow.LayoutParams theseParams = new TableRow.LayoutParams(dpToPx(0), ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        theseParams.leftMargin = dpToPx(2);
        theseParams.rightMargin = dpToPx(2);
        photo.setLayoutParams(theseParams);
        photo.setAdjustViewBounds(true);
        photo.setScaleType(ImageView.ScaleType.FIT_XY);

        File imgFile = new File(path);
        if (!imgFile.exists()) {
            Log.e("imgFile", "Does not exist");
            return;
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            photo.setImageBitmap(bitmap);
            Log.v("bitmap", "Set bitmap");
        }
        Log.v("lastrowChildCount", Integer.toString(lastRow.getChildCount()));
    }

    // Update JSON and image by id OR by string
    private void updatePrivacy(int id, String loadPrivacy) {
        // Need to change JSON and change picture
        String privacy = null;
        int image  = -1;
        if (id == R.id.vispublic || (loadPrivacy != null && loadPrivacy.equals("public"))) {
            privacy = "public";
            image = R.drawable.public_eye;
        } else if (id == R.id.vislist || (loadPrivacy != null && loadPrivacy.equals("list"))) {
            privacy = "list";
            image = R.drawable.collaborator;
        } else if (id == R.id.visfriend || (loadPrivacy != null && loadPrivacy.equals("friend"))) {
            privacy = "friend";
            image = R.drawable.friend;
        } else {
            privacy = "private";
            image = R.drawable.private_eye;
        }

        // Change profile picture
        changePrivacy.setImageResource(image);

        // Update JSON
        try {
            JSONObject thislist = listMaster.getJSONObject(listID);

            // Remake JSON
            thislist.remove("privacy");
            thislist.put("privacy", privacy);
            listMaster.remove(listID);
            listMaster.put(listID, thislist);
            SharedCode.create(this, "lists.json", listMaster.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                // Add image to list photos
                                addPathToPhotos(picturePath);

                                // Insert image into Table Row
                                addToImageGallery(picturePath);

                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    // Permissions stuff for accessing gallery
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
