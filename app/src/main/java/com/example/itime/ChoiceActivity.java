package com.example.itime;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class ChoiceActivity extends AppCompatActivity {
    TextView textView_title2,textView_data2,textView_time_count_down2;
    ImageButton imageButton_back,imageButton_delete,imageButton_change;
    ConstraintLayout constraintLayout;
    private int time_add=0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_choice);

        final String title=getIntent ().getStringExtra ("title");
        final String message=getIntent ().getStringExtra ("message");
        final int year=getIntent ().getIntExtra ("year",0);
        final int month=getIntent ().getIntExtra ("month",0);
        final int day=getIntent ().getIntExtra ("day",0);
        final int hour=getIntent ().getIntExtra ("hour",0);
        final int minute=getIntent ().getIntExtra ("minute",0);
        final String week=getIntent ().getStringExtra ("week");
        final String repeat=getIntent ().getStringExtra ("repeat");
        final String label=getIntent ().getStringExtra ("label");
        final String img=getIntent ().getStringExtra ("img");
        final int position=getIntent ().getIntExtra ("position",-1);

        textView_title2=findViewById (R.id.textView_title2);
        textView_data2=findViewById (R.id.textView_data2);
        textView_time_count_down2=findViewById (R.id.textView_time_count_down2);
        constraintLayout=findViewById (R.id.constraintLayout);
        imageButton_back=findViewById (R.id.imageButton_back);
        imageButton_back.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {                               //返回
                ChoiceActivity.this.finish ();
            }
        });
        imageButton_delete=findViewById (R.id.imageButton_delete);
        imageButton_delete.setOnClickListener (new View.OnClickListener ( ) {          //删除
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder (view.getContext ())
                        .setIcon (android.R.drawable.ic_dialog_alert)
                        .setTitle ("询问")
                        .setMessage ("确定要删除吗？")
                        .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent=new Intent ();
                                intent.putExtra ("position",position);
                                intent.putExtra ("option","delete");
                                setResult (RESULT_OK,intent);
                                ChoiceActivity.this.finish ();
                            }
                        })
                        .setNegativeButton ("取消", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ChoiceActivity.this.finish ();
                            }
                        })
                        .create ().show ();
            }
        });
        imageButton_change=findViewById (R.id.imageButton_change);
        imageButton_change.setOnClickListener (new View.OnClickListener ( ) {            //修改
            @Override
            public void onClick(View view) {
                Intent intent=new Intent (ChoiceActivity.this,AddProjectActivity.class);
                intent.putExtra ("position",position);
                intent.putExtra ("option","change");
                intent.putExtra ("title",title);
                intent.putExtra ("message",message);
                intent.putExtra ("year",year);
                intent.putExtra ("month",month);
                intent.putExtra ("day",day);
                intent.putExtra ("hour",hour);
                intent.putExtra ("minute",minute);
                intent.putExtra ("week",week);
                intent.putExtra ("repeat",repeat);
                intent.putExtra ("label",label);
                startActivityForResult (intent,901);
           }
        });
        //设置显示的信息
        textView_title2.setText (title);
        textView_data2.setText (year+"年"+month+"月"+day+"日 "+hour+":"+minute+" "+week+"\n"+repeat+"  "+label);
        //设置背景图片
        if (img != null )
            constraintLayout.setBackground (new BitmapDrawable (getResources ( ), convertStringToIcon (img)));
//        else
//            constraintLayout.setBackgroundColor (Color.BLUE);

        new timeCountDown ().start ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        switch (requestCode){
            case 901:                        //修改item
                if (resultCode==RESULT_OK){
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
                    int position=data.getIntExtra ("position",-1);

                    Intent intent=new Intent (ChoiceActivity.this, MainActivity.class);
                    intent.putExtra ("title",title);
                    intent.putExtra ("message",message);
                    intent.putExtra ("year",year);
                    intent.putExtra ("month",month);
                    intent.putExtra ("day",day);
                    intent.putExtra ("position",position);
                    intent.putExtra ("hour",hour);
                    intent.putExtra ("minute",minute);
                    intent.putExtra ("week",week);
                    intent.putExtra ("repeat",repeat);
                    intent.putExtra ("label",label);
                    intent.putExtra ("option","change");
                    setResult (RESULT_OK,intent);
                    ChoiceActivity.this.finish ();
                }
                break;

        }
    }

    //用线程进行倒计时刷新
    final Handler handler = new Handler ( ) {
        public void handleMessage(Message msg) {
            //计算剩余时间
            String repeat=getIntent ().getStringExtra ("repeat");
            if (repeat.equals ("每周")) time_add=7*24*60*60;
            if (repeat.equals ("每月")) time_add=30*24*60*60;
            if (repeat.equals ("每年")) time_add=365*24*60*60;
            if (msg.arg1<0) msg.arg1+=time_add;
            int day_left=msg.arg1/60/60/24;
            int hour_left=(msg.arg1-day_left*24*60*60)/60/60;
            int minute_left=(msg.arg1-day_left*24*60*60-hour_left*60*60)/60;
            int second_left=msg.arg1-day_left*24*60*60-hour_left*60*60-minute_left*60;
            textView_time_count_down2.setText (day_left + "天"+hour_left + "小时"+minute_left + "分钟"+second_left + "秒");
            super.handleMessage (msg);
        }
    };
    public class timeCountDown extends Thread {
        public void run() {
            try {
                while (true) {
                    Message msg = new Message ( );
                    //系统当前时间
                    Calendar calendar = Calendar.getInstance();
                    int year2 = calendar.get(Calendar.YEAR);
                    int month2 = calendar.get(Calendar.MONTH)+1;
                    int day2 = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute2 = calendar.get(Calendar.MINUTE);
                    int second2 = calendar.get(Calendar.SECOND);

                    int year=getIntent ().getIntExtra ("year",0);
                    int month=getIntent ().getIntExtra ("month",0);
                    int day=getIntent ().getIntExtra ("day",0);
                    int hour=getIntent ().getIntExtra ("hour",0);
                    int minute=getIntent ().getIntExtra ("minute",0);
                    //系统当前时间总秒数
                    int all_second=year2*60*60*24*30*12+month2*60*60*24*30+day2*60*60*24+hour2*60*60+minute2*60+second2;//默认每月30天
                    //设置时间的总秒数
                    int all_second2=year*60*60*24*30*12+month*60*60*24*30+day*60*60*24+hour*60*60+minute*60;

                    msg.arg1=all_second2-all_second;
                    handler.sendMessage (msg);
                    Thread.sleep (1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace ( );
            }
        }
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
}



//ic_menu_revert
