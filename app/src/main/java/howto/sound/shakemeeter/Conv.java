package howto.sound.shakemeeter;

/**
 * Created by !13 on 17/08/2015.
 */
public class Conv {
    private Categorie catégorie;
    private String conv;
    private int id = -1;

    public Conv(){}

    public Conv(Categorie cat, String conv) {
       this.catégorie = cat;
        this.conv = conv;
    }

    public Categorie getCategorie() {
        return catégorie;
    }

    public String getConv(){
        return conv;
    }

    public Integer getImg(){
        return catégorie.getImg();
    }

    public String getCategorieString() {
        return catégorie.getCategorie();
    }

    public void setConv(String conv) {
        this.conv = conv;
    }

    public void setCategorie(String cat, int img) {
        catégorie = new Categorie(cat, img);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
