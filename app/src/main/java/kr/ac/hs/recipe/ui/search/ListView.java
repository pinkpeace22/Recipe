package kr.ac.hs.recipe.ui.search;

import android.graphics.Bitmap;
import org.parceler.Parcel;

@Parcel
public class ListView {
    //private String Img;
    private Bitmap Img;
    private String nameStr;
    private String aboutStr ;
    private String seq;
    public boolean isChecked;

    //public void setBImg(String img) { Img = img; }
    public void setBImg(Bitmap img) { Img = img; }
    public void setName(String name) {
        nameStr = name ;
    }
    public void setAbout(String about) {
        aboutStr = about ;
    }
    public void setSeq(String seq) {
        this.seq = seq;
    }

    //public String getBImg() { return this.Img ; }
    public Bitmap getBImg() { return this.Img ; }
    public String getName() {
        return this.nameStr ;
    }
    public String getAbout() {
        return this.aboutStr ;
    }
    public String getSeq() {
        return seq;
    }

    @Override
    public String toString() {
        return "ListView{" +
                "Img=" + Img +
                ", nameStr='" + nameStr + '\'' +
                ", aboutStr='" + aboutStr + '\'' +
                ", seq='" + seq + '\'' +
                '}';
    }
}
