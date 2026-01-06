package com.example.kifflarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.kifflarm.alarm.Alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class FileManager {
    private Context context;
    String internalPath, externalPath;

    public FileManager(Context context){
        //internalPath = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/alarms";

        File internalPathFile = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/alarms");
        if (!internalPathFile.exists()) {
            internalPathFile.mkdirs();
        }
        internalPath = internalPathFile.getAbsolutePath();
        //internalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public void write(Object o, String name){
        //String errorMessage = "couldn't save alarm: ";
        try {
            String path = internalPath + "/" + name;

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
            os.writeObject(o);
            os.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
/*
                    String message = errorMessage + ioe.getMessage();
                    Toast toast = Toast.makeText(context,
                            message, Toast.LENGTH_SHORT);
                    toast.show();

 */
        }
        catch (Exception e){
            e.printStackTrace();
            /*
                    String message = errorMessage + e.getMessage();
                    Toast toast = Toast.makeText(context,
                            message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            */
        }
    }

    public Object read(String path){
        String errorMessage = "couldn't load alarm: ";
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
            Object o = is.readObject();
            is.close();
            return o;
        }
        catch (IOException ioe){
            ioe.printStackTrace();

            /*
                    String message = errorMessage + ioe.getMessage();
                    Toast toast = Toast.makeText(context,
                            message, Toast.LENGTH_SHORT);
                    toast.show();

             */

        }
        catch (Exception e){
            e.printStackTrace();

            /*
                String message = "ERROR LOADING";
                Toast toast = Toast.makeText(context,
                        message, Toast.LENGTH_SHORT);
                toast.show();

             */
        }

        return null;
    }

    public ArrayList<ArrayList<String>> getParamsArray(){
        File directory = new File(internalPath);
        File[] filesInDirectory = directory.listFiles();

        ArrayList<ArrayList<String>> paramsArray = new ArrayList<>();

        if(filesInDirectory != null) {
            for (File file : filesInDirectory) {
                Object o = read(file.getAbsolutePath());

                try {
                    ArrayList<String> params = (ArrayList<String>) o;
                    paramsArray.add(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return paramsArray;
    }

    public File[] getFiles(){
        Log.e("FileManager ZZZ", "patch: "+internalPath);
        File directory = new File(internalPath);
        return directory.listFiles();
    }
}
