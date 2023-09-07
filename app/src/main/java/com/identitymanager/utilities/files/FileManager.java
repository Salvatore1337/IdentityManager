package com.identitymanager.utilities.files;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileManager {

    public static File createAppInternalStorageFile(File path, String filename) {
        return new File(path, filename);
    }

    public static void  writeToAppInternalStorageFile(String filename, String content, Context context) {

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void clearAppInternalStorageFile(String filename) {

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //printWriter.close();
    }
}
