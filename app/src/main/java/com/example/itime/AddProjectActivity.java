package com.example.itime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddProjectActivity extends AppCompatActivity {

    private int year=-1,month=-1,day=-1,hour=-1,minute=-1,hour_index=-1,minute_index=-1;
    private String week="无";
    private String repeat="无",repeat_index="无";
    private String label="无",label_index="无";
    private String img;
    private List<Item_list_view> theItems;
    private ArrayList<String > repeat_list;
    private ArrayList<String > label_list;
    private ArrayAdapter<String> repeat_adapter;
    private ArrayAdapter<String> label_adapter;
    private ItemAdapter adapter;
    private FileDataSourceLabel fileDataSourceLabel;         //标签列表序列化

    private String INTENT_TYPE  = "image/*";
    private int REQUESTCODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_project);

        final EditText editText_title = findViewById (R.id.editText_festival);
        final EditText editText_message = findViewById (R.id.editText_something_to_say);
        ImageButton imageButton_back=findViewById (R.id.imageButton_back);
        ImageButton imageButton_ok=findViewById (R.id.imageButton_ok);

        init();

        //如果是修改，各个组件应显示原来的内容
        String option=getIntent ().getStringExtra ("option");
        if (option==null){}
        else {
            if (option.equals ("change")){
                String title_previous=getIntent ().getStringExtra ("title");
                String message_previous=getIntent ().getStringExtra ("message");
                int year_previous=getIntent ().getIntExtra ("year",-1);
                int month_previous=getIntent ().getIntExtra ("month",-1);
                int day_previous=getIntent ().getIntExtra ("day",-1);
                int hour_previous=getIntent ().getIntExtra ("hour",-1);
                int minute_previous=getIntent ().getIntExtra ("minute",-1);
                String week_previous=getIntent ().getStringExtra ("week");
                String repeat_previous=getIntent ().getStringExtra ("repeat");
                String label_previous=getIntent ().getStringExtra ("label");
                year=year_previous;
                month=month_previous;
                day=day_previous;
                hour=hour_previous;
                minute=minute_previous;
                week=week_previous;
                repeat=repeat_previous;
                label=label_previous;

                editText_title.setText (title_previous);
                editText_message.setText (message_previous);
                theItems.get (0).setMessage (year_previous+"年"+month_previous+"月"+day_previous+"日 "+hour_previous+":"+minute_previous+" "+week_previous);  //日期
                theItems.get (1).setMessage (repeat_previous);//重复设置
                theItems.get (3).setMessage (label_previous);//标签
            }
        }

        imageButton_back.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                AddProjectActivity.this.finish ();
            }
        });
        imageButton_ok.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                if (year==-1||month==-1||day==-1||editText_title.getText().toString().equals ("    想说的话,目标,格言...")){       //警告没设置时间或标题
                    new AlertDialog.Builder (view.getContext ())
                            .setIcon (android.R.drawable.ic_dialog_alert)
                            .setTitle ("警告")
                            .setMessage ("请输入标题和日期")
                            .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                            })
                            .create ().show ();
                }
                else {
                    int position = getIntent ( ).getIntExtra ("position", -1);
                    Intent intent = new Intent ( );
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    if (position != -1) intent.putExtra ("position", position);
                    intent.putExtra ("title", editText_title.getText ( ).toString ( ).trim ( ));
                    intent.putExtra ("message", editText_message.getText ( ).toString ( ).trim ( ));
                    intent.putExtra ("year", year);
                    intent.putExtra ("month", month);
                    intent.putExtra ("day", day);
                    intent.putExtra ("hour", hour);
                    intent.putExtra ("minute", minute);
                    intent.putExtra ("week",week);
                    intent.putExtra ("repeat",repeat);
                    intent.putExtra ("label",label);
                    intent.putExtra ("img",img);
                    setResult (RESULT_OK, intent);
                    AddProjectActivity.this.finish ( );
                }
            }
        });


        //为添加item界面设置适配器
        adapter=new ItemAdapter (this,R.layout.list_view_item,theItems);
        ListView listViewItem = this.findViewById (R.id.list_view_item);
        listViewItem.setAdapter (adapter);
        //为重复提醒周期设置适配器
        repeat_adapter=new ArrayAdapter<> (this,android.R.layout.simple_list_item_1, repeat_list);
        //为标签设置适配器
        label_adapter=new ArrayAdapter<> (this,android.R.layout.simple_list_item_1,label_list);
    }

    private void init() {
        //要添加的内容
        theItems=new ArrayList<Item_list_view> ();
        theItems.add(new Item_list_view ("日期","长按使用日期计算器",R.drawable.time));
        theItems.add(new Item_list_view ("重复设置","无",R.drawable.time));
        theItems.add(new Item_list_view ("图片","",R.drawable.time));
        theItems.add(new Item_list_view ("添加标签","",R.drawable.time));

        repeat_list=new ArrayList<> ();
        repeat_list.add ("每周");
        repeat_list.add ("每月");
        repeat_list.add ("每年");
        repeat_list.add ("自定义");


        //为标签序列化
        fileDataSourceLabel=new FileDataSourceLabel (this);
        label_list=fileDataSourceLabel.load ();
        if (label_list.size ()==0) {
            label_list.add ("生日");
            label_list.add ("学习");
            label_list.add ("工作");
            label_list.add ("节假日");
            //label_list.add ("长按删除标签");
        }
    }

    @Override
    public void onStop() {
        super.onStop ( );
        fileDataSourceLabel.save ();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        fileDataSourceLabel.save ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            Log.e("TAG--->onresult","ActivityResult resultCode error");
            return;
        }

        //获得图片
        Bitmap bitmap = null;
        ContentResolver resolver = getContentResolver();
        if(requestCode == REQUESTCODE){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver,uri);//获得图片
                img=convertIconToString(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //获得路径
        if(requestCode == REQUESTCODE){
            Uri uri = data.getData();
            uri = geturi(data);//解决方案
            String[] pro = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(uri,pro,null,null,null);
            Cursor cursor1 = getContentResolver().query(uri,pro,null,null,null);
            //拿到引索
            int index =  cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //移动到光标开头
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(index);
//            Log.d("Tag--->path",path);
        }
    }
    //获得路径
    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
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
    //设置日历
    protected void showDatePickDlg() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddProjectActivity.this, new DatePickerDialog.OnDateSetListener () {

            @Override
            public void onDateSet(DatePicker view, int year1, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat ("EEEE");
                Date date=new Date (year1,monthOfYear,dayOfMonth);
                String dayOfWeek=simpleDateFormat.format (date);
                year=year1;
                month=monthOfYear+1;
                day=dayOfMonth;
                week=dayOfWeek;
                //设置钟表
                final TimePicker timePicker=new TimePicker (AddProjectActivity.this);
                timePicker.setOnTimeChangedListener (new TimePicker.OnTimeChangedListener ( ) {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                        hour_index=hour;
                        minute_index=minute;
                    }
                });
                new AlertDialog.Builder(AddProjectActivity.this)
                        .setView (timePicker)
                        .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hour=hour_index;
                                minute=minute_index;
                                Toast.makeText (AddProjectActivity.this,hour+":"+minute+week,Toast.LENGTH_SHORT).show ();
                            }
                        })
                        .setNegativeButton ("取消", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create ().show ();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    //定义适配器
    class ItemAdapter extends ArrayAdapter<Item_list_view> {
        private int resourceId;
        public ItemAdapter(Context context, int resource, List<Item_list_view> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {  //很重要，当有对象进入listview布局时自动适配
            Item_list_view item = getItem(position);//获取当前项的实例
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            ((ImageView) view.findViewById(R.id.imageView_item)).setImageResource(item.getCoverResourceId());
            ((TextView) view.findViewById(R.id.textView_item)).setText(item.getTitle());
            ((TextView) view.findViewById(R.id.textView_item2)).setText(item.getMessage ());
            if(position==0){
                view.setOnTouchListener (new View.OnTouchListener ( ) {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            showDatePickDlg();
                            return true;
                        }
                        return false;
                    }
                });
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            showDatePickDlg();
                        }
                    }
                });
            }
            if(position==1){
                view.setOnClickListener (new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View view) {
                        //ShowDialog();
                        final ListView listView=new ListView (AddProjectActivity.this);
                        listView.setAdapter (repeat_adapter);
                        listView.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Toast.makeText (AddProjectActivity.this,repeat_list.get (i),Toast.LENGTH_SHORT).show ();
                                repeat_index=repeat_list.get (i);
                            }
                        });
                        new AlertDialog.Builder(AddProjectActivity.this)
                                .setTitle ("周期")
                                .setView (listView)
                                .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        repeat=repeat_index;
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
            }
            if(position==2){
                view.setOnClickListener (new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View view) {
                        //使用intent调用系统提供的相册功能，
                        //使用startActivityForResult是为了获取用户选择的图片
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType(INTENT_TYPE);
                        startActivityForResult(intent,REQUESTCODE);
                    }
                });
            }
            if(position==3){
                view.setOnClickListener (new View.OnClickListener ( ) {
                    @Override
                    public void onClick(final View view) {
                        final ListView listView=new ListView (AddProjectActivity.this);
                        listView.setAdapter (label_adapter);
                        //为listview设置点击事件
                        listView.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                               Toast.makeText (AddProjectActivity.this,label_list.get (i),Toast.LENGTH_SHORT).show ();
                                label_index=label_list.get (i);
                            }
                        });
                        //为listview设置长按菜单
                        AddProjectActivity.this.registerForContextMenu (listView);
                        new AlertDialog.Builder (AddProjectActivity.this)
                                .setIcon (android.R.drawable.ic_dialog_alert)
                                .setMessage ("长按删除标签")
                                .setView (listView)
                                //输入框，添加新标签
                                .setNeutralButton ("添加新标签", new DialogInterface.OnClickListener ( ) {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final EditText editText=new EditText (AddProjectActivity.this);
                                        new AlertDialog.Builder(AddProjectActivity.this)
                                                .setTitle ("新标签")
                                                .setMessage ("请输入新标签")
                                                .setView (editText)
                                                .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        label_list.add (editText.getText ().toString ().trim ());
                                                    }
                                                })
                                                .setNegativeButton ("取消", new DialogInterface.OnClickListener ( ) {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                })
                                                .create ().show ();
                                    }
                                })
                                .setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        label=label_index;
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
            }
            return view;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu (menu, v, menuInfo);
        //AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.add (0, 1, 0, "删除");
        menu.add (0, 2, 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(AddProjectActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
        switch (item.getItemId ()){
            case 1:{
                AdapterView.AdapterContextMenuInfo menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo ();
                final int itemPosition=menuInfo.position;
                label_list.remove (itemPosition);
                adapter.notifyDataSetChanged ();
                label_adapter.notifyDataSetChanged ();
                Toast.makeText(AddProjectActivity.this, "删除成功！"+itemPosition, Toast.LENGTH_SHORT).show();
                break;
            }
            case 2:{
                Toast.makeText(AddProjectActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onContextItemSelected (item);
    }
}
