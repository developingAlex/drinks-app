package com.example.user1.bevreq;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;

/**
 * made this class to handle the implementation of how the state is stored on the file, and read back from the file.*/
public class MyStateStorer {
    private static String CONFIG_FILE = "ConfigFile.ini";

    public static void  storeState(ArrayList<String> valuesToStore, Context cx){
        int i;
        if(valuesToStore != null){
            System.out.println("valuesToStore isn't null");
            System.out.println("valuesToStore.size = "+valuesToStore.size());
            System.out.println(cx.toString());
        }

        try{

            System.out.println("attempting to open file for writing");
            FileOutputStream fos = cx.openFileOutput(CONFIG_FILE, Context.MODE_PRIVATE);
            System.out.println("succeeded in opening the file");

            for(i=0;i<valuesToStore.size();i++){
                fos.write(valuesToStore.get(i).getBytes());

                fos.write(1);
            }
            fos.flush();
            fos.close();
            System.out.println("Successfully wrote ini file: "+CONFIG_FILE);
        }
        catch(Exception e){
            if(e.getMessage() != null)
                System.out.println(e.getMessage());
            System.out.println("error trying to write state to file");
            System.out.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.exit(2);
        }
    }
    public static ArrayList<String> retrieveState(Context cx){

        try{
            FileInputStream fis = cx.openFileInput(CONFIG_FILE);
            int next = fis.read();
            ArrayList<String> retArr = new ArrayList<String>();
            if (next==-1){
                System.out.println("somethings wrong (MyStateStorer)");
                throw new Exception();
            }
            while (next != -1){
                String word = "";
                while(next != 1){
                    word += String.valueOf((char)next);
                    next = fis.read();
                }
                System.out.println("read word:"+word);
                retArr.add(word);
                next = fis.read();
            }
            System.out.println("finished retrieving state (MyStateStorer)");
            fis.close();
            return retArr;
        }
        catch(Exception e ){
            if(e.getMessage() != (null))
                System.out.println(e.getMessage());

            System.out.println("exception attempting to read from file");
        }
        System.out.println("returned null, couldn't read file");
        return null;
    }
}
