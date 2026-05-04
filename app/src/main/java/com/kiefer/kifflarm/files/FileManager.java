package com.kiefer.kifflarm.files;

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
    String internalPath;

    public FileManager(Context context){
        this.context = context;
/*
        File internalPathFile = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/alarms");
        if (!internalPathFile.exists()) {
            internalPathFile.mkdirs();
        }
        internalPath = internalPathFile.getAbsolutePath();

 */
        File internalPathFile = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath());
        if (!internalPathFile.exists()) {
            internalPathFile.mkdirs();
        }
        internalPath = internalPathFile.getAbsolutePath();
    }

    public void write(Object o, String folder, String name){
        //String errorMessage = "couldn't save alarm: ";
        Log.e("FileManager ZZZ", "write");
        try {
            File folderFile = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/" + folder);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }

            String fullPath = internalPath + "/" + folder + "/" + name;

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fullPath));
            os.writeObject(o);
            os.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            Log.e("FileManager ZZZ", "write, ioe: " + ioe);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("FileManager ZZZ", "write, e: " + e);
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

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<ArrayList<Param>> getParamsArrayFromFolder(String folder){
        File[] filesInDirectory = getFiles(internalPath + "/" + folder);

        ArrayList<ArrayList<Param>> paramsArray = new ArrayList<>();

        if(filesInDirectory != null) {
            for (File file : filesInDirectory) {
                Object o = read(file.getAbsolutePath());

                try {
                    ArrayList<Param> params = (ArrayList<Param>) o;
                    paramsArray.add(params);
                } catch (Exception e) {
                    Log.e("FileManager ZZZ", "getParamsArray, "+e);
                    e.printStackTrace();
                }
            }
        }

        return paramsArray;
    }

    public ArrayList<ArrayList<Param>> getAllParamsArrays(){
        //File[] filesInDirectory = getFiles(internalPath);

        return new ArrayList<>(getAllParamsArraysInner(internalPath));

        /*
        if(filesInDirectory != null) {
            for (File file : filesInDirectory) {
                Object o = read(file.getAbsolutePath());

                try {
                    ArrayList<Param> params = (ArrayList<Param>) o;
                    paramsArray.add(params);
                } catch (Exception e) {
                    Log.e("FileManager ZZZ", "getParamsArray, "+e);
                    e.printStackTrace();
                }
            }
        }

         */

        //return paramsArray;
    }

    public ArrayList<ArrayList<Param>> getAllParamsArraysInner(String path){
        File[] filesInDirectory = getFiles(path);

        ArrayList<ArrayList<Param>> paramsArray = new ArrayList<>();

        if(filesInDirectory != null) {
            for (File file : filesInDirectory) {

                if(file.isDirectory()){
                    paramsArray.addAll(getAllParamsArraysInner(file.getAbsolutePath()));
                }
                else {
                    Object o = read(file.getAbsolutePath());

                    try {
                        ArrayList<Param> params = (ArrayList<Param>) o;
                        paramsArray.add(params);
                    } catch (Exception e) {
                        Log.e("FileManager ZZZ", "getParamsArray, " + e);
                        e.printStackTrace();
                    }
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
        File file = new File(internalPath + "/" + alarm.getFullPath());
        file.delete();
        Log.e("FileManager ZZZ", "deleted: "+file.getAbsolutePath());
    }


    public static Alarm getAlarm(Context context, String id){
        FileManager fileManager = new FileManager(context);

        try {
            for(ArrayList<Param> params : fileManager.getAllParamsArrays()){
                for(Param p : params){
                    if (p.key.equals(Alarm.ALARM_ID_TAG) && p.value.equals(id)) {
                        return new Alarm(context, params);
                    }
                }
            }
        }
        catch (Exception e){
            Log.e("FileManager ZZZ", "lkjsadhflkjsahflkjf");
        }
        return null;
    }
}
