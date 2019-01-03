package nbut.hdb.memo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nbut.hdb.memo.R;

public class AddGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        init();
    }
    private void init(){
        //如果标题栏存在，就隐藏它
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
