package nbut.hdb.memo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nbut.hdb.memo.R;

public class AlarmActivity extends AppCompatActivity {
    MediaPlayer mAlarmMusic;  //音乐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Intent intent=getIntent();
        System.out.println("##############AlarmActivity的title="+intent.getStringExtra("title"));

        mAlarmMusic = MediaPlayer.create(this, R.raw.alarm);
        mAlarmMusic.setLooping(true);
        mAlarmMusic.start();
        new AlertDialog.Builder(AlarmActivity.this).setTitle(intent.getStringExtra("title"))
                .setView(R.layout.activity_alarm)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlarmMusic.stop();
                        AlarmActivity.this.finish();       //点击确定后音乐停止，该活动结束
                    }
                }).show();
    }
}
