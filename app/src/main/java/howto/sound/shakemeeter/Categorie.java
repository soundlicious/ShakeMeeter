package howto.sound.shakemeeter;

import android.widget.ImageView;

/**
 * Created by !13 on 15/08/2015.
 */
public class Categorie {
    private String categorie;
    private Integer img;

    public Categorie(String categorie, Integer img)
    {
        this.categorie = categorie;
        this.img = img;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }
}
