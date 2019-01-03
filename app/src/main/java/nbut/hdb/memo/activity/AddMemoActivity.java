package nbut.hdb.memo.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nbut.hdb.memo.R;
import nbut.hdb.memo.dao.DBHelper;
import nbut.hdb.memo.dao.GroupDao;
import nbut.hdb.memo.dao.MemoDao;
import nbut.hdb.memo.entity.Group;
import nbut.hdb.memo.entity.Memo;
import nbut.hdb.memo.entity.Time;
import nbut.hdb.memo.service.AlarmService;

public class AddMemoActivity extends AppCompatActivity {

    private EditText memoTitle,memoContent;     //备忘录的标题和内容
    private TextView clockTime;                 //闹钟提醒时间
    private ImageButton addMemo,clock;          //备忘录信息提交按钮
    private Spinner spinnerItem;                //下拉分组的标签
    List<Group>spinnerList;                     //存放分组的id和组名;
    private Memo memo=new Memo();
    private DBHelper dbHelper;                  //创建数据库的帮助对象
    private SQLiteDatabase db;                  //对数据库进行CRUD操作的对象
    private Calendar calendar;                  //日期
    Integer groupId;                            //组ID

    private Time time=new Time();               //时间
    GroupDao groupDao=new GroupDao(AddMemoActivity.this);
    MemoDao memoDao=new MemoDao(AddMemoActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);
        init();
    }
    private void init() {
        //如果标题栏存在，就隐藏它
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        /**
         * 连接数据库
         */
        dbHelper = new DBHelper(this, "memo.db", null, 2);
        db=dbHelper.getWritableDatabase();

        /**
         * 绑定控件
         */
        addMemo=findViewById(R.id.commit);
        memoTitle=findViewById(R.id.memoTitle);
        memoContent=findViewById(R.id.memoContent);
        clockTime=findViewById(R.id.clockTime);
        spinnerItem=findViewById(R.id.group);
        clock=findViewById(R.id.clock);

        /**
         * 初始化分组的下拉列表
         */
        setSpinnerItem();


        /**
         * 闹钟按钮的点击事件
         */
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar= Calendar.getInstance();
                getNowTime();
            }
        });



        /**
         * 提交按钮的点击事件，增加记录
         */
        addMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(memoTitle.getText()!=null && memoContent.getText()!=null){
                    memo.setTitle(memoTitle.getText().toString());
                    memo.setContent(memoContent.getText().toString());

                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String now=sdf.format(date);
                    memo.setCreateTime(now);

                    memo.setAlarmTime(time.getTime());
                    memo.setGroupId(groupId);


                    ContentValues values=new ContentValues();
                    values.put("title",memo.getTitle());
                    values.put("content",memo.getContent());
                    values.put("createTime",memo.getCreateTime());
                    values.put("alarmTime",memo.getAlarmTime());
                    values.put("isAlarm",memo.getIsAlarm());
                    values.put("groupId",memo.getGroupId());

                    db.insert("memosTable","null",values);
                    Toast.makeText(AddMemoActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AddMemoActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setSpinnerItem() {
        /**
         * 得到下拉列表框内的所有子项
         */
        spinnerList=groupDao.getAllGroups();
        List<String> groups=new ArrayList<>();
        for(Group group:spinnerList){
            groups.add(group.getItem());
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//设置样式
        spinnerItem.setAdapter(adapter); //适配

        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {    //子项选中事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupId=spinnerList.get(position).getgId(); //保存组ID
                if(spinnerList.get(position).getItem().equals("管理分组")){
                    Intent intent=new Intent(AddMemoActivity.this,GroupManageActivity.class);
                    startActivity(intent);//选中的是“管理分组”就跳到另一个Activity。
                }
                if(spinnerList.get(position).getItem().equals("新建分组")){
                    newGroupDialog();   //选中的是“新建分组”就弹出对话框新建。
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 新建分组
     */
    private void newGroupDialog() {
        final AlertDialog.Builder newGroupDialog = new AlertDialog.Builder(this);//创建构造器的对象
        final View groupLayout = View.inflate(AddMemoActivity.this, R.layout.new_group, null);//新建分组对话框中的布局
        newGroupDialog.setTitle("新建分组");
        newGroupDialog.setView(groupLayout);//装入自定义布局
        newGroupDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText newGroup = groupLayout.findViewById(R.id.DialogNewGroup);
                String groupName = newGroup.getText().toString().trim();         //得到分组名
                if (!groupName.equals("") && groupName != null) {
                    if (groupDao.insertGroup(groupName) == 1) {//增加分组
                        Toast.makeText(AddMemoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(AddMemoActivity.this, "添加失败，该组名已存在", Toast.LENGTH_SHORT).show();
                    onStart();
                } else Toast.makeText(AddMemoActivity.this, "分组名不能为空", Toast.LENGTH_SHORT).show();
                onStart();

            }
        });
        newGroupDialog.create();
        newGroupDialog.show();
    }
    //得到时间
    public void getNowTime(){
        DatePickerDialog dpd= new DatePickerDialog(AddMemoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                time.setYear(String.valueOf(year));         //设置年份。
                time.setMonth(String.valueOf(month+1));      //月份默认为0 开始
                time.setDay(String.valueOf(dayOfMonth));      //保存选中的日期

            }
        }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dpd.show();
        dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TimePickerDialog timePickerDialog= new TimePickerDialog(AddMemoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(hourOfDay<10){
                                    time.setHour("0"+String.valueOf(hourOfDay));
                                }else{
                                    time.setHour(String.valueOf(hourOfDay));
                                }
                                if(minute<10){
                                    time.setMinte("0"+String.valueOf(minute));
                                } else{
                                    time.setMinte(String.valueOf(minute));
                                }                                       //保存选中的时间

                                clockTime.setText(time.getTime());      //在文本上显示选中的时间

                                Intent intent=new Intent(AddMemoActivity.this,AlarmService.class);
                                System.out.println("########time.getTime="+time.getTime());
                                memo.setIsAlarm(1); //设置启动闹钟
                                memo.setAlarmTime(time.getTime()); //闹钟时间
                                System.out.println("###############################设置完闹钟的memo="+memo);
                                intent.putExtra("startTime",time.getClockTime());//把时间戳传给服务
                                intent.putExtra("title",memo.getTitle());
                                intent.putExtra("mId",memo.getmId());             //把mId传给服务,设置多个闹钟
                                startService(intent);

                                Toast.makeText(AddMemoActivity.this,time.getTime(),Toast.LENGTH_LONG).show();

                            }
                        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
                timePickerDialog.show();
            }
        });
    }

}
