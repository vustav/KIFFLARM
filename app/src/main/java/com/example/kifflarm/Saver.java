package com.example.kifflarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.kifflarm.alarm.Alarm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Saver {
    private KIFFLARM kifflarm;
    String internalPath;

    public Saver(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        //path = Objects.requireNonNull(kifflarm.getExternalFilesDir(null)).getAbsolutePath();
        internalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public void write(Object o){
        String errorMessage = "couldn't save alarm: ";
        try {
            String path = internalPath + "/" + ((Alarm)o).getAlarmId();

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
            os.writeObject(o);
            os.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();

            kifflarm.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String message = errorMessage + ioe.getMessage();
                    Toast toast = Toast.makeText(kifflarm,
                            message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();

            kifflarm.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = errorMessage + e.getMessage();
                    Toast toast = Toast.makeText(kifflarm,
                            message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
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

            kifflarm.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = errorMessage + ioe.getMessage();
                    Toast toast = Toast.makeText(kifflarm,
                            message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
            kifflarm.runOnUiThread(() -> {
                String message = "ERROR LOADING";
                Toast toast = Toast.makeText(kifflarm,
                        message, Toast.LENGTH_SHORT);
                toast.show();
            });
        }

        return null;
    }


}
