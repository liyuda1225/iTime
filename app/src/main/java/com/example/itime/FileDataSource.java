package com.example.itime;

import android.content.Context;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by jszx on 2019/10/14.
 */

public class FileDataSource {
    private Context context;

    public FileDataSource(Context context) {
        this.context = context;
    }

    private ArrayList<Item_time_count_down> items=new ArrayList<Item_time_count_down>();

    public ArrayList<Item_time_count_down> getBooks() {
        return items;
    }

    public void save()
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Serializable.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(items);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Item_time_count_down> load()
    {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable.txt")
            );
            items = (ArrayList<Item_time_count_down>) inputStream.readObject();  //这一步将这里的books和主函数的theBooks连接起来
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
