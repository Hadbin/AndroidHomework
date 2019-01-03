
package nbut.hdb.memo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nbut.hdb.memo.R;
import nbut.hdb.memo.adapter.MemoAdapter;
import nbut.hdb.memo.dao.DBHelper;
import nbut.hdb.memo.dao.GroupDao;
import nbut.hdb.memo.dao.MemoDao;
import nbut.hdb.memo.entity.Group;
import nbut.hdb.memo.entity.Memo;

public class MainActivity extends AppCompatActivity {
    private ListView listView;          //listView列表
    Memo memo;
    MemoAdapter memoAdapter;            //备忘录适配器
    ArrayAdapter<String> adapter;       //下拉列表适配器
    private FloatingActionButton fab;   //悬浮按钮, 添加备忘信息
    private DBHelper dbHelper;          //创建数据库的帮助对象
    private SQLiteDatabase db;          //对数据库进行CRUD操作的对象
    private Spinner mainSpinner;        //下拉列表
    List<Group> spinnerList;             //存放分组的id和组名;
    List<Memo> memos = new ArrayList<>(); //得到的memos列表
    List<String> groups = new ArrayList<>(); //存放组名
    GroupDao groupDao = new GroupDao(MainActivity.this);
    MemoDao memoDao = new MemoDao(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // dbHelper=new DBHelper(this,"memo.db",null,1);//创建数据库

        init();//初始化方法
    }

    private void init() {
        /**
         * 如果标题栏存在，就隐藏它
         */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // MemoDao memoDao = new MemoDao();
        /**
         * 初始化数据库 MemoTable
         * 数据库版本默认设置为1
         */
        dbHelper = new DBHelper(this, "memo.db", null, 2);
        db = dbHelper.getWritableDatabase();
        /**
         * 绑定控件
         */
        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.fab);
        mainSpinner = findViewById(R.id.mainSpinner);
        /**
         * 初始化下拉列表
         */
        setMainSpinner();
        /**
         * 显示（默认分组）所有备忘录信息
         */


        memos = memoDao.getAllMemo();
        System.out.println("memos=" + memos);
        memoAdapter = new MemoAdapter(MainActivity.this, R.layout.list_view, memos);//listView 初始适配
        listView.setAdapter(memoAdapter);

        /**
         * ListView点击事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                memo = memos.get(position);
                Intent intent = new Intent(MainActivity.this, UpdateMemoActivity.class);
                intent.putExtra("mId", memo.getmId());
                startActivity(intent);             //跳转到修改页面进行修改
            }
        });
        /**
         * ListView长按事件
         * return  true 长按执行，false 长按和点击同时进行
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                memo = memos.get(position);
                dialog();
                memos.remove(position);
                return true;
            }
        });

        /**
         * 触发事件——添加备忘信息
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMemoActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 警告删除事件
     */

    public void dialog(){
        AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
                .setTitle("删除").setMessage("确认删除该条记录吗?")
                .setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        memoDao.delMemo(memo.getmId()); //删除数据库的内容
                        memos = memoDao.getAllMemo();
                        memoAdapter.setList(memos);     //刷新布局
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }
    /**
     * 得到下拉列表框内的所有子项
     */
    private void setMainSpinner() {

        spinnerList = groupDao.getAllGroups();

        for (Group group : spinnerList) {
            groups.add(group.getItem());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//设置样式
        mainSpinner.setAdapter(adapter); //适配

        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {    //子项选中事件

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerList.get(position).getItem().equals("默认分组")) { //显示所有分组信息
                    System.out.println("选择默认分组");
                    memos = memoDao.getAllMemo();
                    //System.out.println("备忘信息="+memos);
                    memoAdapter = new MemoAdapter(MainActivity.this, R.layout.list_view, memos);
                    listView.setAdapter(memoAdapter);

                } else if (spinnerList.get(position).getItem().equals("管理分组")) {
                    System.out.println("选择管理分组");
                    Intent intent = new Intent(MainActivity.this, GroupManageActivity.class);
                    startActivity(intent);
                }else if (spinnerList.get(position).getItem().equals("新建分组")) {
                    System.out.println("选择新建分组");
                    newGroupDialog();   //选中的是“+新建分组”就弹出对话框新建。

                }else{
                    memos=memoDao.getGroupMemos(spinnerList.get(position).getgId());//得到该分组的所有记录
                    memoAdapter = new MemoAdapter(MainActivity.this, R.layout.list_view, memos);//listView 初始适配
                    listView.setAdapter(memoAdapter);
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
        final View groupLayout = View.inflate(MainActivity.this, R.layout.new_group, null);//新建分组对话框中的布局
        newGroupDialog.setTitle("新建分组");
        newGroupDialog.setView(groupLayout);//装入自定义布局
        newGroupDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText newGroup = groupLayout.findViewById(R.id.DialogNewGroup);
                String groupName = newGroup.getText().toString().trim();         //得到分组名
                if (!groupName.equals("") && groupName != null) {
                    if (groupDao.insertGroup(groupName) == 1) {//增加分组
                        groups.add(groupName);
                        mainSpinner.setSelection(0,true);//设置spinner的值为"默认分组"
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(MainActivity.this, "添加失败，该组名已存在", Toast.LENGTH_SHORT).show();
                    onStart();
                } else Toast.makeText(MainActivity.this, "分组名不能为空", Toast.LENGTH_SHORT).show();


            }
        });

        newGroupDialog.create();
        newGroupDialog.show();
    }

    /**
     * 当新建分组后重新从数据库拿到值，刷新下拉列表
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }


}
