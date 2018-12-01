package com.x10host.burghporter31415.fileactions;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class File {
    private String fileName;

    public File(String fileName) {
        this.fileName = fileName;
    }

    /*https://stackoverflow.com/questions/8867334/check-if-a-file-exists-before-calling-openfileinput*/
    public boolean fileExists(Context context) {

        java.io.File file = context.getFileStreamPath(fileName);

        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public boolean deleteFile(Context context, String filename) {

        java.io.File file = context.getFileStreamPath(filename);
        return file.delete();

    }

    public void writeToFile(Context context,  String[] args) {
        try {
            FileOutputStream fileout = context.openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

            for(String s : args) {
                outputWriter.write(s);
                outputWriter.write("\n");
            }
            outputWriter.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToFileSingleLine(Context context,  String[] args) {
        try {
            FileOutputStream fileout = context.openFileOutput(fileName, MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

            for(String s : args) {
                if(s == null) {
                    outputWriter.write("null");
                } else {
                    outputWriter.write(s);
                }
                outputWriter.write(" ");
            }
            outputWriter.write("\n");
            outputWriter.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile(Context context) {

        try {

            FileInputStream fileIn = context.openFileInput(fileName);
            InputStreamReader inputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100];
            String s="";
            int charRead;

            while ((charRead=inputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s += readstring;
            }

            inputRead.close();

            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
