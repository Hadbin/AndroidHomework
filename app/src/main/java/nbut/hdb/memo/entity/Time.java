package nbut.hdb.memo.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Time {
    private String year,month,day,hour,minte;
    private String msg="";

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinte() {
        return minte;
    }

    public void setMinte(String minte) {
        this.minte = minte;
    }
    public String getTime(){
        msg=year+"-"+month+"-"+day+" "+hour+":"+minte;
        return msg;
    }


    /**
     * 时间转时间戳（毫秒）
     * @return
     */
    public long getClockTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
        long timeLong = 0;
        try {
            if(Integer.valueOf(month) <10){ month="0"+month;}
            if(Integer.valueOf(day) <10){ day="0"+day;}
            if(Integer.valueOf(hour) <10){ hour="0"+hour;}
            if(Integer.valueOf(minte) <10){minte="0"+minte;}
            timeLong = sdf.parse(year+""+month+""+day+""+hour+""+minte+"").getTime();
        } catch (ParseException e) { e.printStackTrace(); }
        return timeLong;
    }
    @Override
    public String toString() {
        return "Time{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minte=" + minte +
                '}';
    }
}
