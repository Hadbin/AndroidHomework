package nbut.hdb.memo.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UpdateMemoActivity extends AppCompatActivity {
    private EditText newTitle,newContent;   //标题和内容输入框
    private ImageButton updateMemo,clock;   //备忘录信息提交按钮、闹钟按钮
    private int mId;                        //修改记录的id
    private Spinner spinnerItem;                //下拉分组的标签
    List<Group> spinnerList;                     //存放分组的id和组名;
    private TextView clockTime;             //显示闹钟时间
    private Calendar calendar;              //日期
    Integer groupId;                            //组ID
    private DBHelper dbHelper;                  //创建数据库的帮助对象
    private SQLiteDatabase db;                  //对数据库进行CRUD操作的对象

    private Time time=new Time();                      //时间
    private Memo memo=new Memo();
    GroupDao groupDao=new GroupDao(UpdateMemoActivity.this);
    MemoDao memoDao=new MemoDao(UpdateMemoActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_memo);
        Intent intent=getIntent();
        mId=intent.getIntExtra("mId",0);       //得到intent传过来的被点击的记录的id
        init();
    }
    private void init(){
        //如果标题栏存在，就隐藏它
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        System.out.println("mId="+mId);
        /**
         * 连接数据库
         */
        dbHelper = new DBHelper(this, "memo.db", null, 2);
        db=dbHelper.getWritableDatabase();
        /**
         * 绑定控件
         */
        updateMemo=findViewById(R.id.commit);
        spinnerItem=findViewById(R.id.group);
        newTitle=findViewById(R.id.newTitle);
        newContent=findViewById(R.id.newContent);
        clock=findViewById(R.id.clock);
        clockTime=findViewById(R.id.clockTime);
        /**
         * 获取原备忘信息
         */
        memo=memoDao.selectMemoById(mId);
        groupId=memo.getGroupId();
        newTitle.setText(memo.getTitle());
        newContent.setText(memo.getContent());
        if(memo.getIsAlarm()==0){ clockTime.setText(""); }
        else{ clockTime.setText(memo.getAlarmTime()); }

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
         * 提交更新后的备忘录信息
         */
        updateMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newTitle.getText() != null && newContent.getText() != null) {
                    memo.setTitle(newTitle.getText().toString());
                    memo.setContent(newContent.getText().toString());
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String now = sdf.format(date);
                    memo.setCreateTime(now);
                    memo.setGroupId(groupId);
                    memoDao.updateMemo(mId,memo);
                    Toast.makeText(UpdateMemoActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UpdateMemoActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    /**
     * 得到下拉列表框内的所有子项
     */
    private void setSpinnerItem() {
        spinnerList=groupDao.getAllGroups();
        List<String> groups=new ArrayList<>();
        for(Group group:spinnerList){
            groups.add(group.getItem());
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//设置样式
        spinnerItem.setAdapter(adapter); //适配

       // spinnerItem.setSelection(0,true);//设置spinner的值为"默认分组"
        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {    //子项选中事件

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                groupId=spinnerList.get(position).getgId(); //保存组ID
                if(spinnerList.get(position).getItem().equals("管理分组")){
                    System.out.println("选择管理分组");
                    Intent intent=new Intent(UpdateMemoActivity.this,GroupManageActivity.class);
                    startActivity(intent);
                }
                if(spinnerList.get(position).getItem().equals("新建分组")){
                    System.out.println("选择新建分组");
                    newGroupDialog();   //选中的是“+新建分组”就弹出对话框新建。
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
        final View groupLayout = View.inflate(UpdateMemoActivity.this, R.layout.new_group, null);//新建分组对话框中的布局
        newGroupDialog.setTitle("新建分组");
        newGroupDialog.setView(groupLayout);//装入自定义布局
        newGroupDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText newGroup = groupLayout.findViewById(R.id.DialogNewGroup);
                String groupName = newGroup.getText().toString().trim();         //得到分组名
                if (!groupName.equals("") && groupName != null) {
                    if (groupDao.insertGroup(groupName) == 1) {//增加分组
                        Toast.makeText(UpdateMemoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(UpdateMemoActivity.this, "添加失败，该组名已存在", Toast.LENGTH_SHORT).show();
                    onStart();
                } else Toast.makeText(UpdateMemoActivity.this, "分组名不能为空", Toast.LENGTH_SHORT).show();
                onStart();

            }
        });
        newGroupDialog.create();
        newGroupDialog.show();
    }
    /**
     *得到时间
     */
    public void getNowTime(){
        DatePickerDialog dpd= new DatePickerDialog(UpdateMemoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                time.setYear(String.valueOf(year));
                time.setMonth(String.valueOf(month+1));      //月份默认为0 开始
                time.setDay(String.valueOf(dayOfMonth));      //保存选中的日期

            }
        }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dpd.show();
        dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TimePickerDialog timePickerDialog= new TimePickerDialog(UpdateMemoActivity.this,
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

                                Intent intent=new Intent(UpdateMemoActivity.this,AlarmService.class);
                                System.out.println("########time.getTime="+time.getTime());
                                memo.setIsAlarm(1); //设置启动闹钟
                                memo.setAlarmTime(time.getTime()); //闹钟时间
                                System.out.println("###############################设置完闹钟的memo="+memo);

                                intent.putExtra("startTime",time.getClockTime());//把时间戳传给服务
                                intent.putExtra("mId",memo.getmId());             //把mId传给服务,设置多个闹钟
                                intent.putExtra("title",memo.getTitle());
                                startService(intent);

                                Toast.makeText(UpdateMemoActivity.this,time.getTime(),Toast.LENGTH_LONG).show();

                            }
                        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
                timePickerDialog.show();
            }
        });
    }


}
