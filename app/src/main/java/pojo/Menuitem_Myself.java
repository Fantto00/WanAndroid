package pojo;

import android.widget.ImageView;
import android.widget.TextView;

public class Menuitem_Myself {

    private int imageResId;
    private String title;

    public Menuitem_Myself(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
