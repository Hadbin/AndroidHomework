package nbut.hdb.memo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nbut.hdb.memo.R;
import nbut.hdb.memo.activity.MainActivity;
import nbut.hdb.memo.dao.GroupDao;
import nbut.hdb.memo.entity.Group;

public class GroupAdapter extends ArrayAdapter<Group> {
    private int resourceId;
    private Context context=this.getContext();
    private GroupDao groupDao=new GroupDao(context);
    List<Group> groups;

  //  Group group=new Group();
    public GroupAdapter(Context context, int resource, List<Group> groupList) {
        super(context, resource, groupList);
        groups=groupList;
        resourceId = resource;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Group group=groups.get(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageButton deleteBtn = view.findViewById(R.id.delete);
        TextView tvGroup = view.findViewById(R.id.deletegroup);

        tvGroup.setText(group.getItem());

       deleteBtn.setOnClickListener(new View.OnClickListener() {
         //  AlertDialog dialog = new AlertDialog.Builder(context);//创建构造器的对象
            @Override
            public void onClick(View v) {
                System.out.println("gId="+group.getgId());
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("删除分组")
                        .setMessage("确认删除该分组吗，删除后该分组的所有备忘信息将一起被删除！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                groupDao.deleteGroup(group.getgId());
                                Intent intent=new Intent(context,MainActivity.class);
                                context.startActivity(intent);
                                System.out.println("删除成功");
                                Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
            //return convertView;            //返回布局
        });
        tvGroup.setOnClickListener(new View.OnClickListener() {
            final AlertDialog.Builder newGroupDialog=new AlertDialog.Builder(context);//创建构造器的对象
            final View groupLayout=View.inflate(context,R.layout.new_group,null);//新建分组对话框中的布局
            @Override
            public void onClick(View v) {
                    newGroupDialog.setTitle("编辑分组信息")
                    .setView(groupLayout)//装入自定义布局
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText newGroup=groupLayout.findViewById(R.id.DialogNewGroup);
                                group.setItem(newGroup.getText().toString());
                                groupDao.updateGroup(group.getgId(),group.getItem());
                                Intent intent=new Intent(context,MainActivity.class);
                                context.startActivity(intent);
                                Toast.makeText(context,"编辑成功",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                newGroupDialog.create();
                newGroupDialog.show();

            }
        });
        return view;
    }
}
