package com.example.buckit.ui.achievementWall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.buckit.R;
import com.example.buckit.SharedCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

import static com.example.buckit.SharedCode.dpToPx;

public class AchievementWallFragment extends Fragment {

    private AchievementWallViewModel mViewModel;
    private JSONObject achievementMaster = null;
    private Context thisContext;
    private LinearLayout achievementContainer;

    public void FirstFragment() {
        // Required empty public constructor
    }

    @Override
    // This and the method above enable creating new elements by replacing "this" with "thisContext"
    public void onAttach(Context context) {
        super.onAttach(context);
        thisContext=context;
    }

    private void loadAchievementMaster() {
        boolean masterExists = SharedCode.fileExists(thisContext, "achievements.json");
        String jsonString;
        // Read if exists
        if (masterExists) {
            jsonString = SharedCode.read(thisContext, "achievements.json");
            try {
                achievementMaster = new JSONObject(jsonString);
            } catch (Throwable t) {
                Log.e("JSON", "Failed to parse JSON file");
                return;
            }
        } else {
            // Create
            boolean fileCreated = SharedCode.create(thisContext, "achievements.json", "{}");
            if (!fileCreated) {
                Log.e("JSON", "Failed to create master JSON file");
                return;
            } else {
                jsonString = SharedCode.read(thisContext, "lists.json");
                try {
                    achievementMaster = new JSONObject(jsonString);
                } catch (Throwable t) {
                    Log.e("JSON", "Failed to parse master JSON file");
                    t.printStackTrace();
                    return;
                }
            }
        }
    }

    // Inserts completed achievements
    private void loadCompleted() {
            Iterator<String> keys = achievementMaster.keys();
            if (keys.hasNext()) {
                do {
                    try {
                        String listName = keys.next();
                        JSONObject item = achievementMaster.getJSONObject(listName);
                        String date = item.getString("date");
                        JSONArray photos = item.getJSONArray("photos");
                        insertList(listName, date, photos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                } while (keys.hasNext());
            }
    }

    private void insertList(String name, String date, JSONArray photos) {
        LinearLayout item = new LinearLayout(thisContext);
        LinearLayout.LayoutParams theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        theseParams.setMargins(dpToPx(5), 0, dpToPx(5), dpToPx(10));
        item.setLayoutParams(theseParams);
        item.setGravity(Gravity.CENTER_HORIZONTAL);
        item.setBackgroundResource(R.drawable.round_normal_outline_button);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(dpToPx(80), dpToPx(15), dpToPx(80), dpToPx(15));

        // Insert a TextView at top
        TextView listname = new TextView(thisContext);
        theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        theseParams.setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
        item.setLayoutParams(theseParams);
        listname.setText(name);
        listname.setTypeface(null, Typeface.BOLD);
        listname.setTextColor(getResources().getColor(R.color.textPrimary));
        listname.setGravity(Gravity.CENTER_HORIZONTAL);
        item.addView(listname);

        // Make room for photos
        LinearLayout row1 = new LinearLayout(thisContext);
        theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(theseParams);
        LinearLayout row2 = new LinearLayout(thisContext);
        row2.setLayoutParams(theseParams);
        item.addView(row1);
        item.addView(row2);

        // Insert photos
        int arrLen = photos.length();
        if (arrLen > 0) {
            ImageView photo = getPhoto(photos, 0);
            theseParams.setMargins(dpToPx(5), 0, dpToPx(5), dpToPx(5));
            photo.setLayoutParams(theseParams);
            row1.addView(photo);
        }
        if (arrLen > 1) {
            ImageView photo = getPhoto(photos, 1);
            theseParams.setMargins(dpToPx(5), 0, dpToPx(5), dpToPx(5));
            photo.setLayoutParams(theseParams);
            row1.addView(photo);
        }
        if (arrLen > 2) {
            ImageView photo = getPhoto(photos, 2);
            theseParams.setMargins(dpToPx(5), 0, dpToPx(5), dpToPx(5));
            photo.setLayoutParams(theseParams);
            row2.addView(photo);
        }
        if (arrLen > 3) {
            ImageView photo = getPhoto(photos, 3);
            theseParams.setMargins(dpToPx(5), 0, dpToPx(5), dpToPx(5));
            photo.setLayoutParams(theseParams);
            row2.addView(photo);
        }

        // Insert completion date
        TextView fin = new TextView(thisContext);
        theseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fin.setLayoutParams(theseParams);
        String cleanDate = formatDate(date);
        String bottom = "Completed " + cleanDate;
        fin.setText(bottom);
        fin.setTypeface(null, Typeface.BOLD);
        fin.setGravity(Gravity.CENTER_HORIZONTAL);
        fin.setTextColor(getResources().getColor(R.color.textSecondary));
        item.addView(fin);

        // Insert into container
        achievementContainer.addView(item);
    }

    private String formatDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        if (day.charAt(0) == '0') day = day.substring(1, 2);

        String monthAbrev;
        if (month.equals("01"))
            monthAbrev = "Jan.";
        else if (month.equals("02"))
            monthAbrev = "Feb.";
        else if (month.equals("03"))
            monthAbrev = "Mar.";
        else if (month.equals("04"))
            monthAbrev = "Apr.";
        else if (month.equals("05"))
            monthAbrev = "May";
        else if (month.equals("06"))
            monthAbrev = "June";
        else if (month.equals("07"))
            monthAbrev = "July";
        else if (month.equals("08"))
            monthAbrev = "Aug.";
        else if (month.equals("09"))
            monthAbrev = "Sep.";
        else if (month.equals("10"))
            monthAbrev = "Oct.";
        else if (month.equals("11"))
            monthAbrev = "Nov.";
        else
            monthAbrev = "Dec.";

        String output = monthAbrev + " " + day + " " + year;
        return output;
    }

    private ImageView getPhoto(JSONArray list, int index) {
            String path = null;
            try {
                path = list.getString(index);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            ImageView photo = new ImageView(thisContext);
            TableRow.LayoutParams theseParams = new TableRow.LayoutParams(dpToPx(0), ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            theseParams.leftMargin = dpToPx(2);
            theseParams.rightMargin = dpToPx(2);
            photo.setLayoutParams(theseParams);
            photo.setAdjustViewBounds(true);
            photo.setScaleType(ImageView.ScaleType.FIT_XY);

            File imgFile = new File(path);
            if (!imgFile.exists()) {
                Log.e("imgFile", "Does not exist");
                return null;
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                photo.setImageBitmap(bitmap);
                return photo;
            }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel =
                ViewModelProviders.of(this).get(AchievementWallViewModel.class);
        View root = inflater.inflate(R.layout.fragment_achievement_wall, container, false);
        achievementContainer = root.findViewById(R.id.achievementContainer);

        loadAchievementMaster();

        // Insert all loaded instances from achievement Master
        loadCompleted();
        return root;
    }
}

