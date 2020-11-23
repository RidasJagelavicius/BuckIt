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
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
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
import android.widget.ProgressBar;
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
import java.util.ArrayList;
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
    private Dialog popup;
    private Dialog collaboratorsPopup;
    private LinearLayout addCollab;
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
        collaboratorsPopup = new Dialog(this);
        addCollab = findViewById(R.id.addCollaborator);

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

        newGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                createGoal();
                addGoal();
            }
        });

        addCollab.setOnClickListener(this);

        // Load in the saved goals or say "Create your first goal"
        loadOrBlank();

        // Populate things
        try {
            JSONObject thislist = listMaster.getJSONObject(listID);
            String privacy = thislist.getString("privacy");
            JSONArray photos = thislist.getJSONArray("photos");
            if (privacy != "") {
                updatePrivacy(-1, privacy);
            }
            for (int i = 0; i < photos.length(); i++) {
                String s = photos.getString(i);
                addToImageGallery(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    // Just changes the JSON to show that this has collaborators
    private void setCollaborator() {
        try {
            JSONObject thislist = listMaster.getJSONObject(listID);
            thislist.remove("collaborators");
            thislist.put("collaborators", "1");
            listMaster.remove(listID);
            listMaster.put(listID, thislist);
            SharedCode.create(this, "lists.json", listMaster.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

    }
    // Handles clicking on bell, meg, sita, or the search button
    private void collaboratorHandler() {
        EditText collaboratorName = popup.findViewById(R.id.collaboratorName);
        String input = collaboratorName.getText().toString();

        // Check not empty
        if (input.length() == 0)
            return;
        else {
            // If has an @, remove it just incase starts with one
            input = input.replace("@", "");

            // Insert our own @ into the front (this way can enter @bell or bell)
            input = "@" + input;

            String message = "Invited " + input + " to collaborate";
            Toast.makeText(ListActivity.this, message, Toast.LENGTH_SHORT).show();
            setCollaborator();
            popup.dismiss();
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
                                    //left      top        right       bottom
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


    @Override
    public void onClick(View v) {
        int myid = v.getId();

        if (diffIndicIds.contains(myid))
            changeDifficulty(myid);

        if (xButtonIds.contains(myid)) {
//            Toast.makeText(this, "x clicked", Toast.LENGTH_SHORT).show();
            deleteAction(myid);
        }

        if (plusButtonIds.contains(myid)) {
//            Toast.makeText(this, "add subgoal", Toast.LENGTH_SHORT).show();
            int idx = plusButtonIds.indexOf(myid);
            addSubgoal(linearLayoutsList.get(idx));
        }

        if (myid == R.id.addPhoto) {
            verifyStoragePermissions(this);
            selectImage(this);
        } else if (myid == R.id.changePrivacy) {
            popup.setContentView(R.layout.popup_change_privacy); // Create the dialog that allows user to click on each
            popup.show(); // By default, show the popup

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
        } else if (myid == R.id.collaboratorSearch) {
            collaboratorHandler();
        } else if (myid == R.id.addCollaborator) {
            popup.setContentView(R.layout.popup_change_collaborator);
            popup.show();

                // Add id's to each button, give them a listener for clicks, and on click, change JSON and icon in list
                TextView bell = popup.findViewById(R.id.bell);
                TextView meg = popup.findViewById(R.id.meghana);
                TextView sita = popup.findViewById(R.id.sita);
                ImageButton searchCollab = popup.findViewById(R.id.collaboratorSearch);

                bell.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(ListActivity.this, "Invited @bell to collaborate", Toast.LENGTH_SHORT).show();
                        setCollaborator();
                        collaboratorsPopup.dismiss();
                    }
                });
                meg.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(ListActivity.this, "Invited @og_meghana to collaborate", Toast.LENGTH_SHORT).show();
                        setCollaborator();
                        collaboratorsPopup.dismiss();
                    }
                });
                sita.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(ListActivity.this, "Invited @mamasita to collaborate", Toast.LENGTH_SHORT).show();
                        setCollaborator();
                        collaboratorsPopup.dismiss();
                    }
                });
                searchCollab.setOnClickListener(this);
        }
    }

    private ArrayList<Integer> xButtonIds = new ArrayList<>(); // array of GOAL cross out button IDs
    private ArrayList<Integer> diffIndicIds = new ArrayList<>(); // array of GOAL difficulty indicator IDs
    private ArrayList<Button> diffIndicators = new ArrayList<>(); // array of GOAL difficulty indicator buttons
    private ArrayList<Integer> plusButtonIds = new ArrayList<>(); // array of plus buttons IDs
    private ArrayList<EditText> goalsList = new ArrayList<>(); // array of goal/subgoal edittexts
    private ArrayList<Boolean> crossedOutItems = new ArrayList<>(); // array of booleans tracking if goal/subgoal is crossed out
    private ArrayList<LinearLayout> linearLayoutsList = new ArrayList<>();
    int numCompletedItems = 0;

    private void addGoal() {
        // each new element (a newGoal) consists of:
        // 1. difficult indicator (button)
        // 2. a goal (text)
        // 3. + (button to add subgoal)
        // 4. × (button to delete goal)
        LinearLayout newGoal = new LinearLayout(this);
//        LinearLayout.LayoutParams newGoal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(100));
        newGoal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        newGoal.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutsList.add(newGoal);

        // create difficulty button
        final Button diffInd = new Button(this);
        diffInd.setBackground(ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_blank));
        diffInd.setLayoutParams(new ViewGroup.LayoutParams(130,ViewGroup.LayoutParams.WRAP_CONTENT));
        diffInd.setId(View.generateViewId());
        diffIndicIds.add(diffInd.getId());
        diffIndicators.add(diffInd);
        diffInd.setClickable(true);
        diffInd.setOnClickListener(this);

        // goal
        EditText goal = new EditText(this);
        goal.setHint(R.string.new_goal);
        goal.setSingleLine(false);
        goal.setLayoutParams(new ViewGroup.LayoutParams(650,ViewGroup.LayoutParams.WRAP_CONTENT));
        goal.setId(View.generateViewId());
        goalsList.add(goal);

        // add subgoal button
        Button plusButton = new Button(this);
        plusButton.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        plusButton.setText("+");
        plusButton.setGravity(Gravity.CENTER);
        plusButton.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
        plusButton.setId(View.generateViewId());
        plusButton.setClickable(true);
        plusButton.setOnClickListener(this);
        plusButtonIds.add(plusButton.getId());

        // delete goal button
        Button xButton = new Button(this);
        xButton.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        xButton.setTransformationMethod(null);
        xButton.setText("×");
        xButton.setGravity(Gravity.CENTER);
        xButton.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
        xButton.setId(View.generateViewId());
        xButton.setClickable(true);
        xButton.setOnClickListener(this);
        xButtonIds.add(xButton.getId());
        crossedOutItems.add(false);

        // insert into linear layout
        newGoal.addView(diffInd);
        newGoal.addView(goal);
        newGoal.addView(plusButton);
        newGoal.addView(xButton);

        // insert goal into bucket container
        if(newGoal.getParent() != null)
            ((ViewGroup)newGoal.getParent()).removeView(newGoal); // fix for some weird error

        goalContainer.addView((newGoal));
        updateProgressBar();
    }

//    private void addSubgoal(int plusId) {
    private void addSubgoal(LinearLayout parentLayout) {
        // each new element (a newSubgoal) consists of:
        // 1. difficult indicator (button)
        // 2. a subgoal (text)
        // 3. × (button to delete goal)
        LinearLayout newSubgoal = new LinearLayout(this);
        newSubgoal.setPadding(100,0,0,0);
        newSubgoal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        newSubgoal.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutsList.add(newSubgoal);

        // create difficulty button
        final Button diffInd = new Button(this);
        diffInd.setBackground(ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_blank));
        diffInd.setLayoutParams(new ViewGroup.LayoutParams(130,ViewGroup.LayoutParams.WRAP_CONTENT));

        diffInd.setId(View.generateViewId());
        diffIndicIds.add(diffInd.getId());
        diffIndicators.add(diffInd);
        diffInd.setClickable(true);
        diffInd.setOnClickListener(this);

        // add subgoal button
        EditText subgoal = new EditText(this);
        subgoal.setHint(R.string.new_subgoal);
        subgoal.setSingleLine(false);
        subgoal.setLayoutParams(new ViewGroup.LayoutParams(650,ViewGroup.LayoutParams.WRAP_CONTENT));
        subgoal.setId(View.generateViewId());
        goalsList.add(subgoal);

        // add an invalid id val (-1) to plusButtonIds list so that everything matches
        // -1 will signify this is a subgoal, since you cannot add a subgoal to a subgoal.
        plusButtonIds.add(-1);

        // delete subgoal button
        Button xButton = new Button(this);
        xButton.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        xButton.setTransformationMethod(null);
        xButton.setText("×");
        xButton.setGravity(Gravity.CENTER);
        xButton.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
        xButton.setId(View.generateViewId());
        xButton.setClickable(true);
        xButton.setOnClickListener(this);
        xButtonIds.add(xButton.getId());
        crossedOutItems.add(false);

        // insert into linear layout
        newSubgoal.addView(diffInd);
        newSubgoal.addView(subgoal);
        newSubgoal.addView(xButton);

        // insert subgoal into bucket container
        if(newSubgoal.getParent() != null)
            ((ViewGroup)newSubgoal.getParent()).removeView(newSubgoal); // fix for some weird error

        // parentLayout - the layout that contains the GOAL that the subgoal is nested under
        // find idx of parentLayout in our goal container and then add new subgoal right below
        int idx = goalContainer.indexOfChild(parentLayout);
        goalContainer.addView(newSubgoal,idx+1);
        updateProgressBar();
    }

    private void changeDifficulty(int id) {
//        Toast.makeText(this, "changed difficulty", Toast.LENGTH_SHORT).show();
        int idx = diffIndicIds.indexOf(id);
        Button bulletPoint = diffIndicators.get(idx);

        Drawable blankBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_blank);
        Drawable easyBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_easy);
        Drawable medBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_med);
        Drawable hardBullet = ContextCompat.getDrawable(this, R.drawable.difficulty_indicator_hard);

        // if text is crossed out, remove strikethrough
        if (crossedOutItems.get(idx)) {
            EditText goal = goalsList.get(idx);
            goal.setText(goal.getText().toString());
            crossedOutItems.set(idx, false);

            if (numCompletedItems > 0)
                numCompletedItems--;
        }

        // if current bullet is blank (grey) --> change to easy (green)
        else if (Objects.equals(bulletPoint.getBackground().getConstantState(),
                this.getResources().getDrawable(R.drawable.difficulty_indicator_blank).getConstantState()))
            bulletPoint.setBackground(easyBullet);

        // easy (green) --> med (yellow)
        else if (Objects.equals(bulletPoint.getBackground().getConstantState(),
                this.getResources().getDrawable(R.drawable.difficulty_indicator_easy).getConstantState()))
            bulletPoint.setBackground(medBullet);

        // med (yellow) --> hard (red)
        else if (Objects.equals(bulletPoint.getBackground().getConstantState(),
                this.getResources().getDrawable(R.drawable.difficulty_indicator_med).getConstantState()))
            bulletPoint.setBackground(hardBullet);

        // hard (red) --> blank (grey)
        else {
            bulletPoint.setBackground(blankBullet); // always reset difficulty

            // cross out item
            if (!crossedOutItems.get(idx)) {
                EditText goal = goalsList.get(idx);
                if (goal.length() > 0) {
                    goal.getText().setSpan(new StrikethroughSpan(), 0, goal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    crossedOutItems.set(idx, true);

                    if (numCompletedItems < crossedOutItems.size())
                        numCompletedItems++;
                }
            }
        }

        updateProgressBar();
    }

    private void updateProgressBar() {
        ProgressBar bar = findViewById(R.id.progressBar);
        double percentage = (double)(numCompletedItems)/crossedOutItems.size() * 100;
        bar.setProgress((int)percentage);
    }

    private void deleteAction(int id) {
        int idx = xButtonIds.indexOf(id);
        LinearLayout goal = linearLayoutsList.get(idx);
        linearLayoutsList.remove(idx);
        goalContainer.removeView(goal); // remove goal from container

        // remove goal/subgoal info from all our arrays
        xButtonIds.remove(idx);
        diffIndicIds.remove(idx);
        diffIndicators.remove(idx);
        plusButtonIds.remove(idx);
        crossedOutItems.remove(idx);
        goalsList.remove(idx);

        updateProgressBar();

    }

    // TODO: update json :(
    private void insertGoals() {
//        try {
//            JSONObject thislist = listMaster.getJSONObject(listID);
//            JSONArray goals = thislist.getJSONArray("goals"); //include subgoals
//            goals.put(path);
//
//            // Remake JSON
//            thislist.remove("goals");
//            thislist.put("goals", goals);
//            listMaster.remove(listID);
//            listMaster.put(listID, thislist);
//            SharedCode.create(this, "lists.json", listMaster.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return;
//        }
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
            SharedCode.create(this, "lists.json", listMaster.toString());
            Toast.makeText(this, "Successfully added photo", Toast.LENGTH_SHORT).show();
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

            if (loadPrivacy.equals(""))
                Toast.makeText(this, "Updated privacy", Toast.LENGTH_SHORT).show();
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
