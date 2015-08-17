package howto.sound.shakemeeter;

import android.content.Context;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by !13 on 15/08/2015.
 */
public class HandleFile {
    private String FileName;
    private Context context;
    private StringBuilder content;
    private Hashtable<String, Integer> myImageList;
    private FileOutputStream fos;
    private BufferedReader reader;

    public HandleFile(Context context, String Filename, Hashtable<String, Integer> list) {
        this.FileName = Filename;
        this.context = context;
        content = new StringBuilder();
        myImageList = list;
    }

    public void readFile(ArrayList<String> questions, ArrayList<Integer> images) {
        FileInputStream fis;

        try {
            fis = context.openFileInput(FileName);
            byte[] buffer = new byte[1024];
            while ((fis.read(buffer)) != -1){
                content.append(new String(buffer));
            }
            Log.v("readFile", content.toString());
            fis.close();
        }
        catch (FileNotFoundException e) {
            createFile(questions, images);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(ArrayList<String> questions, ArrayList<Integer> images) {
        try {
            fos = context.openFileOutput(FileName, context.MODE_APPEND);

            String line = null;

                while (reader != null && (line = reader.readLine()) != null) {
                    String[] split = line.split(";");
                    questions.add(split[0]);
                    images.add(myImageList.get(split[1]));
                    writeFile(line + "\r\n");
                    fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String Data) {
        try {
            fos = context.openFileOutput(FileName, context.MODE_APPEND);
            fos.write(Data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int addToArrays(ArrayList<String> question, ArrayList<Integer> img) {
        String file = content.toString();
        String[] lines = file.split("\r\n");
        String[] line;
        int j = 0;
        for (int i = 0; i < lines.length ; i++) {
            line = lines[i].split(";");
            if (line.length == 2) {
                j++;
                question.add(line[0]);
                img.add(myImageList.get(line[1]));
            }
        }
        return j;
    }

    public boolean isFileCreated(){
        try {
            FileInputStream fis = context.openFileInput(FileName);
            fis.close();
        }
        catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (true);
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
