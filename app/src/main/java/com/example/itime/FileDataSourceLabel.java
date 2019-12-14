package com.example.itime;

import android.content.Context;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by jszx on 2019/10/14.
 */

public class FileDataSourceLabel {
    private Context context;

    public FileDataSourceLabel(Context context) {
        this.context = context;
    }

    private ArrayList<String> items=new ArrayList<>();

    public ArrayList<String> getBooks() {
        return items;
    }

    public void save()
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Serializable2.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(items);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> load()
    {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable2.txt")
            );
            items = (ArrayList<String>) inputStream.readObject();  //这一步将这里的books和主函数的theBooks连接起来
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
