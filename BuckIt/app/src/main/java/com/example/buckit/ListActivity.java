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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.example.buckit.SharedCode.dpToPx;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView list;
    private Button addPhoto;
    private String listID;
    private JSONObject listMaster = null;
    private TableLayout gallery;
    private ImageButton changePrivacy;
    private Dialog popup;

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

        // TODO: Populate the other stuff from the JSON

        // Get name and lists passed from bucket
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
    }

    @Override
    public void onClick(View v) {
        int myid = v.getId();

        if (myid == R.id.addPhoto) {
            verifyStoragePermissions(this);
            selectImage(this);
        } else if (myid == R.id.changePrivacy) {
            // Create the dialog that allows user to click on each
            popup.setContentView(R.layout.change_privacy_popup);

            // By default, show the popup
            popup.show();

            // TODO: Add id's to each button, give them a listener for clicks, and on click, change JSON and icon in list
        }
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
