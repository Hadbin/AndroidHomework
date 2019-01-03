package nbut.hdb.memo.entity;

public class Group {
    private Integer gId;    //组Id
    private String item;    //子项

    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Group{" +
                "gId=" + gId +
                ", item='" + item + '\'' +
                '}';
    }
}
