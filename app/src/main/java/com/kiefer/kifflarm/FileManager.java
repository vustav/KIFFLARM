package com.kiefer.kifflarm;

import android.content.Context;
import android.util.Log;

import com.kiefer.kifflarm.alarm.Alarm;

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
        //Log.e("FileManager ZZZ", "internal storage: "+internalPath);
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
        File[] filesInDirectory = getFiles(internalPath);

        ArrayList<ArrayList<String>> paramsArray = new ArrayList<>();

        Log.e("FileManaer ZZZ", "getParamsArray, size: "+filesInDirectory.length);

        if(filesInDirectory != null) {
            for (File file : filesInDirectory) {
                Object o = read(file.getAbsolutePath());

                Log.e("FileManaer ZZZ", "getParamsArray, o class: "+o.getClass());
                try {
                    ArrayList<String> params = (ArrayList<String>) o;
                    paramsArray.add(params);
                } catch (Exception e) {
                    Log.e("FileManager ZZZ", "getParamsArray, "+e);
                    e.printStackTrace();
                }
            }
        }

        return paramsArray;
    }

    public File[] getFiles(String path){
        File directory = new File(path);
        return directory.listFiles();
    }

    public void delete(Alarm alarm){
        File file = new File(internalPath + "/" + alarm.getId());
        file.delete();
        Log.e("FileManager ZZZ", "deleted: "+file.getAbsolutePath());
    }


    public static Alarm getAlarm(Context context, String id){
        //Alarm alarm = null;
        FileManager fileManager = new FileManager(context);

        try {
            for(ArrayList<String> params : fileManager.getParamsArray()){
                for(String p : params){

                if (p.length() > Alarm.ALARM_ID_TAG.length() && p.substring(0, Alarm.ALARM_ID_TAG.length()).equals(Alarm.ALARM_ID_TAG)) {
                    if(p.substring(Alarm.ALARM_ID_TAG.length()).equals(id)){
                        return new Alarm(context, params);

                        //alarm.cancelAlarm(0); //the alarm object is just to get data, so make sure it's not scheduled
                    }
                }

                 /*


                    if (p.key.equals(Alarm.ALARM_ID_TAG) && p.value.equals(id)) {
                        return new Alarm(context, params);
                    }

                  */
                }
            }
        }
        catch (Exception e){
            Log.e("FileManager ZZZ", "lkjsadhflkjsahflkjf");
        }
        return null;
    }
}
