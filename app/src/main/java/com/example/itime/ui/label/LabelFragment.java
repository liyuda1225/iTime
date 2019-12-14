package com.example.itime.ui.label;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.itime.FileDataSource;
import com.example.itime.FileDataSourceLabel;
import com.example.itime.Item_time_count_down;
import com.example.itime.R;
import com.example.itime.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class LabelFragment extends Fragment {

    private FileDataSource fileDataSource;
    private FileDataSourceLabel fileDataSourceLabel;
    private ItemAdapter adapter;
    private ArrayList<String> list_fragment_label;
    private ListView listView_fragment_label;
    private ArrayList<Item_time_count_down> theItems;
    private ArrayAdapter<String> label_adapter_fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_label, container, false);
        listView_fragment_label=view.findViewById (R.id.listView_fragment_label);

        init();

        listView_fragment_label.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //HomeFragment homeFragment=(HomeFragment)getActivity ().getSupportFragmentManager ().findFragmentByTag ("homeFragment");
                //homeFragment.setData ("123");
                final ListView listView=new ListView (getActivity ());
                //对标签进行分类
                ArrayList<Item_time_count_down> theItems_label=new ArrayList<> ();
                for (Item_time_count_down item:theItems){
                    if (item.getLabel ().equals (list_fragment_label.get (i)))
                        theItems_label.add (item);
                }
                adapter = new ItemAdapter (getActivity ( ), R.layout.list_view_time, theItems_label);
                listView.setAdapter (adapter);
                new AlertDialog.Builder(getActivity ())
                        .setView (listView)
                        .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText (getActivity (),"啦啦啦",Toast.LENGTH_SHORT).show ();
                            }
                        })
                        .setNegativeButton ("取消", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create ().show ();
            }
        });

        return view;
    }

    private void init() {
//        if (theItems.size ()==0){
//            theItems.add (new Item_time_count_down ("空","空",R.drawable.time,0,0,0,0,0,"空","空","空" ));
//        }
        label_adapter_fragment=new ArrayAdapter<> (getActivity (),android.R.layout.simple_list_item_1, list_fragment_label);
        listView_fragment_label.setAdapter (label_adapter_fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        //导入theItem
        fileDataSource = new FileDataSource (getActivity ());
        theItems = fileDataSource.load ( );
        //导入标签
        fileDataSourceLabel=new FileDataSourceLabel (getActivity ());
        list_fragment_label=fileDataSourceLabel.load ();
    }
    @Override
    public void onStop() {
        super.onStop ( );
        fileDataSource.save ();
    }
    @Override
    public void onDestroy() {
        super.onDestroy ( );
        fileDataSource.save ();
    }


    //定义一个Book对象适配器
    class ItemAdapter extends ArrayAdapter<Item_time_count_down> {
        private int resourceId;

        public ItemAdapter(Context context, int resource, List<Item_time_count_down> objects) {
            super (context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {  //很重要，当有对象进入listview布局时自动适配
            Item_time_count_down item = getItem (position);//获取当前项的实例
            View view = LayoutInflater.from (getContext ( )).inflate (resourceId, parent, false);
            ((ImageView) view.findViewById (R.id.imageView_item)).setImageResource (R.drawable.time);
            ((TextView) view.findViewById (R.id.textView_title)).setText (item.getTitle ( ));
            ((TextView) view.findViewById (R.id.textView_time)).setText (item.getYear ( )+"年"+item.getMonth ()+"月"+item.getDay ()+"日");
            ((TextView) view.findViewById (R.id.textView_message)).setText (item.getMessage ( ));
            return view;
        }
    }
}