package howto.sound.shakemeeter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by !13 on 17/08/2015.
 */
public class ConvSQlite extends SQLiteOpenHelper {

    private static final String CONV_TABLE = "Conversations";
    private static final String ID_COL = "ID";
    private static final String CONV_COL = "Conversation";
    private static final String IMG_COL = "Image";
    private static final String CAT_COL = "Categorie";

    private static final String CREATE_BDD = "CREATE TABLE IF NOT EXISTS " + CONV_TABLE + "( "
            + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CONV_COL + " TEXT NOT NULL, "
            + CAT_COL  + " TEXT NOT NULL, "
            + IMG_COL + " INTEGER);";

    public ConvSQlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE" +CONV_TABLE);
        onCreate(db);
    }
}