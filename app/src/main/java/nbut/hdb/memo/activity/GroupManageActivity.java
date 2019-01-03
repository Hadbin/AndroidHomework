package nbut.hdb.memo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import nbut.hdb.memo.R;
import nbut.hdb.memo.adapter.GroupAdapter;
import nbut.hdb.memo.dao.DBHelper;
import nbut.hdb.memo.dao.GroupDao;
import nbut.hdb.memo.entity.Group;

public class GroupManageActivity extends AppCompatActivity {
    private ListView manageGroup;     //列表
    private List<Group> groups;       //分组list
    private ImageButton deleteBtn;     //删除按钮
    Group group;                    //组 对象
    GroupDao groupDao=new GroupDao(GroupManageActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        init();
    }
    private void init(){
        //如果标题栏存在，就隐藏它
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        /**
         * 绑定控件
         */
        manageGroup = findViewById(R.id.groupManage);
        deleteBtn=findViewById(R.id.delete);
        /**
         * 显示所有分组
         */

        groups=groupDao.getNormalGroups();

        /**
         * 自定义ListView
         */
        GroupAdapter groupAdapter=new GroupAdapter(GroupManageActivity.this,R.layout.delete_group,groups);
        manageGroup.setAdapter(groupAdapter);

    }
}
