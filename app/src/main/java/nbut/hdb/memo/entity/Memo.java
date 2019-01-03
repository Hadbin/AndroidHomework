package nbut.hdb.memo.entity;

public class Memo {
    private Integer mId;            //主键
    private String title;           //标题
    private String content;         //内容
    private String createTime;      //创建时间
    private String alarmTime;       //闹钟时间
    private Integer isAlarm;        //是否设置提醒闹钟 0 不设置 （默认）; 1 设置
    private Integer groupId;        //分组号

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Integer getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(Integer isAlarm) {
        this.isAlarm = isAlarm;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "mId=" + mId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                ", isAlarm=" + isAlarm +
                ", groupId=" + groupId +
                '}';
    }
}
