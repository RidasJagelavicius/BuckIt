package com.example.buckit;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SharedCode {
    // This method is called by setMargins to convert px to dp
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // Used to check if master JSON (list of buckets and their inner lists) exists
    public static boolean fileExists(Context context, String filename) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + filename;
        File file = new File(path);
        return file.exists();
    }

    // Returns a file's contents as a string
    public static String read(Context context, String fileName) {
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

    // Creates a JSON file in local storage with contents jsonString
    public static boolean create(Context context, String fileName, String jsonString){
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
}
