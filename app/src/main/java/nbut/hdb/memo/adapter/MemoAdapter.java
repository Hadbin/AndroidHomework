package nbut.hdb.memo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import nbut.hdb.memo.R;
import nbut.hdb.memo.entity.Memo;

public class MemoAdapter extends ArrayAdapter<Memo> {
    private int resourceId;
    List<Memo> memos;

    public MemoAdapter(Context context, int resource, List<Memo> memos) {
        super(context, resource, memos);
        this.memos=memos;
        resourceId = resource;
    }

    /**
     * 每当滚动屏幕时就调用
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Memo memo = getItem(position);//获取当前项的Memo实例
        View view=LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView memoTitle = view.findViewById(R.id.memoTitle);
        TextView memoContent = view.findViewById(R.id.memoContent);
        TextView memoCreateTime = view.findViewById(R.id.memoCreateTime);

        memoTitle.setText(memo.getTitle());
        memoContent.setText(memo.getContent());
        memoCreateTime.setText(memo.getCreateTime());

        return view;
    }
    public void setList(List<Memo> memos){
        this.memos=memos;
        notifyDataSetChanged();       //更新布局
    }

}
