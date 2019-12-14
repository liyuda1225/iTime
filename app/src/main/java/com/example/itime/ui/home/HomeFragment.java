package com.example.itime.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.itime.AddProjectActivity;
import com.example.itime.ChoiceActivity;
import com.example.itime.FileDataSource;
import com.example.itime.Item_time_count_down;
import com.example.itime.MainActivity;
import com.example.itime.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView textView_time_count_down,textView_title,textView_data;
    private ImageView imageView_background;
    private ArrayList<Item_time_count_down> theItems;
    private FileDataSource fileDataSource;
    private ItemAdapter adapter;
    private int time_add=0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of (this).get (HomeViewModel.class);
        View view = inflater.inflate (R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById (R.id.text_home);
//        homeViewModel.getText ( ).observe (this, new Observer<String> ( ) {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText (s);
//            }
//        });

        Button button_add = view.findViewById (R.id.button_add);
        textView_time_count_down=view.findViewById (R.id.textView_time_count_down);
        textView_title=view.findViewById (R.id.textView_title);
        textView_data=view.findViewById (R.id.textView_data);
        imageView_background=view.findViewById (R.id.imageView_background);
        ImageView imageView_background = view.findViewById (R.id.imageView_background);

        init();
        ListView listViewTime = view.findViewById (R.id.list_view_time);

        adapter = new ItemAdapter (getActivity ( ), R.layout.list_view_time, theItems);
        listViewTime.setAdapter (adapter);

        button_add.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent (getActivity (), AddProjectActivity.class);
                startActivityForResult (intent,901);
            }
        });
        listViewTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent (getActivity (), ChoiceActivity.class);
                intent.putExtra ("title",theItems.get (position).getTitle ().trim ());
                intent.putExtra ("message",theItems.get (position).getMessage ().trim ());
                intent.putExtra ("year",theItems.get (position).getYear ());
                intent.putExtra ("month",theItems.get (position).getMonth ());
                intent.putExtra ("day",theItems.get (position).getDay ());
                intent.putExtra ("hour",theItems.get (position).getHour ());
                intent.putExtra ("minute",theItems.get (position).getMinute ());
                intent.putExtra ("week",theItems.get (position).getWeek ());
                intent.putExtra ("repeat",theItems.get (position).getRepeat ());
                intent.putExtra ("label",theItems.get (position).getLabel ());
                intent.putExtra ("img",theItems.get (position).getImg ());
                intent.putExtra ("position",position);
                startActivityForResult (intent,902);
            }
        });
        return view;
    }

    private void init() {
        fileDataSource = new FileDataSource (getActivity ());
        theItems = fileDataSource.load ( );
//        if (theItems.size ()==0){
//            String img_0=convertIconToString(BitmapFactory.decodeResource (getResources (),R.drawable.time));
//            theItems.add (new Item_time_count_down ("空","空",img_0,0,0,0,0,0,"空","空","空" ));
//        }

        //用线程进行倒计时刷新
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler ( ) {
            public void handleMessage(Message msg) {
                //计算剩余时间
                if (msg.arg1<0) msg.arg1+=time_add;
                int day_left=msg.arg1/60/60/24;
                int hour_left=(msg.arg1-day_left*24*60*60)/60/60;
                int minute_left=(msg.arg1-day_left*24*60*60-hour_left*60*60)/60;
                int second_left=msg.arg1-day_left*24*60*60-hour_left*60*60-minute_left*60;
                textView_time_count_down.setText (day_left + "天"+hour_left + "小时"+minute_left + "分钟"+second_left + "秒");
                super.handleMessage (msg);
            }
        };
        class timeCountDown extends Thread {
            public void run() {
                try {
                    while (true) {
                        Message msg = new Message ( );
                        //系统当前时间
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH)+1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        //第一个item设置的时间
                        int year2=theItems.get (0).getYear ();
                        int month2=theItems.get (0).getMonth ();
                        int day2=theItems.get (0).getDay ();
                        int hour2=theItems.get (0).getHour ();
                        int minute2=theItems.get (0).getMinute ();
                        //系统当前时间总秒数
                        int all_second=year*60*60*24*30*12+month*60*60*24*30+day*60*60*24+hour*60*60+minute*60+second;//默认每月30天
                        //设置时间的总秒数
                        int all_second2=year2*60*60*24*30*12+month2*60*60*24*30+day2*60*60*24+hour2*60*60+minute2*60;

                        msg.arg1=all_second2-all_second;
                        handler.sendMessage (msg);
                        Thread.sleep (1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace ( );
                }
            }
        }
        //如果item不为空，就设置TextView的值的为第一个item的数据
        if (theItems.size ()!=0) {
            textView_title.setText (theItems.get (0).getTitle ());
            textView_data.setText (theItems.get(0).getYear ()+"年"+theItems.get(0).getMonth ()+"月"+theItems.get(0).getDay ()+"日");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //bitmap转drawable
                imageView_background.setBackground (new BitmapDrawable (getResources ( ), convertStringToIcon (theItems.get (0).getImg ())));
            }

            if (theItems.get (0).getRepeat ().equals ("每周")) time_add=7*24*60*60;
            if (theItems.get (0).getRepeat ().equals ("每月")) time_add=30*24*60*60;
            if (theItems.get (0).getRepeat ().equals ("每年")) time_add=365*24*60*60;
            new timeCountDown ().start ();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        switch (requestCode){
            case 901:     //添加item
                if (resultCode==RESULT_OK){
                    String title=data.getStringExtra ("title");
                    String message=data.getStringExtra ("message");
                    int year=data.getIntExtra ("year",0);//使用getIntent.getIntExtra的情况：只有上一个activity用了startActivity，第二个activity才能用getIntent获得传过去的Intent，
                    // 否则第二个activity结束时的setResult(OK,intent)中的intent作为第一个activity的onActivityResult(data)的data
                    int month=data.getIntExtra ("month",0);
                    int day=data.getIntExtra ("day",0);
                    int hour=data.getIntExtra ("hour",0);
                    int minute=data.getIntExtra ("minute",0);
                    String label=data.getStringExtra ("label");
                    String week=data.getStringExtra ("week");
                    String repeat=data.getStringExtra ("repeat");
                    String img=data.getStringExtra ("img");
                    //theItems.add (new Item_time_count_down (title,message,BitmapFactory.decodeResource (getResources (),R.drawable.time),year,month,day,"11","22" ));
                    theItems.add (new Item_time_count_down (title,message,img,year,month,day,hour,minute,week,repeat,label ));
                    adapter.notifyDataSetChanged ();

                    if (theItems.size ()==1){
                        textView_title.setText (theItems.get (0).getTitle ());
                        textView_data.setText (theItems.get(0).getYear ()+"年"+theItems.get(0).getMonth ()+"月"+theItems.get(0).getDay ()+"日");
                        textView_time_count_down.setText ("请退出后重新打开");

                        if (theItems.get (0).getRepeat ().equals ("每周")) time_add=7*24*60*60;
                        if (theItems.get (0).getRepeat ().equals ("每月")) time_add=30*24*60*60;
                        if (theItems.get (0).getRepeat ().equals ("每年")) time_add=365*24*60*60;
                        //new ChoiceActivity.timeCountDown ().start ();
                    }
                }
                break;
            case 902:     //点击item后删除+修改
                if(resultCode==RESULT_OK){
                    String option=data.getStringExtra ("option");
                    int position=data.getIntExtra ("position",-1);
                    if (option.equals ("delete")){                           //删除
                        theItems.remove (position);
                        adapter.notifyDataSetChanged ();
                        Toast.makeText(getActivity (), "删除成功！"+position, Toast.LENGTH_SHORT).show();
                    }
                    if (option.equals ("change")){                           //修改
                        String title=data.getStringExtra ("title");
                        String message=data.getStringExtra ("message");
                        int year=data.getIntExtra ("year",0);
                        int month=data.getIntExtra ("month",0);
                        int day=data.getIntExtra ("day",0);
                        int hour=data.getIntExtra ("hour",0);
                        int minute=data.getIntExtra ("minute",0);
                        String week=data.getStringExtra ("week");
                        String repeat=data.getStringExtra ("repeat");
                        String label=data.getStringExtra ("label");

                        theItems.get (position).setTitle (title);
                        theItems.get (position).setMessage (message);
                        theItems.get (position).setYear (year);
                        theItems.get (position).setMonth (month);
                        theItems.get (position).setDay (day);
                        theItems.get (position).setHour (hour);
                        theItems.get (position).setMinute (minute);
                        theItems.get (position).setWeek (week);
                        theItems.get (position).setRepeat (repeat);
                        theItems.get (position).setLabel (label);
                        adapter.notifyDataSetChanged ();
                        Toast.makeText(getActivity (), "修改成功！"+position, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
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

    //bitmap转string
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }
    //string转bitmap
    public static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
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
//            if (item.getImg ().equals ("无"))
//                ((ImageView) view.findViewById (R.id.imageView_item)).setImageResource (R.drawable.dog);
//            else
                ((ImageView) view.findViewById (R.id.imageView_item)).setImageBitmap (convertStringToIcon(item.getImg ())); //将string转为bitmap并适配
            ((TextView) view.findViewById (R.id.textView_title)).setText (item.getTitle ( ));
            ((TextView) view.findViewById (R.id.textView_time)).setText (item.getYear ( )+"年"+item.getMonth ()+"月"+item.getDay ()+"日");
            ((TextView) view.findViewById (R.id.textView_message)).setText (item.getMessage ( ));
            return view;
        }
    }
}