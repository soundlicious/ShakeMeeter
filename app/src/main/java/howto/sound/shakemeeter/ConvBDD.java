package howto.sound.shakemeeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by !13 on 17/08/2015.
 */
public class ConvBDD {
    private static final int VERSION = 1;
    private static final String BDD_NAME = "conversations.db";

    private static final String CONV_TABLE = "conversations";
    private static final String ID_COL = "ID";
    private static final int NUM_ID_COL = 0;
    private static final String CONV_COL = "Conversation";
    private static final int NUM_CONV_COL = 1;
    private static final String IMG_COL = "Image";
    private static final int NUM_IMG_COL = 3;
    private static final String CAT_COL = "Categorie";
    private static final int NUM_CAT_COL = 2;

    private SQLiteDatabase bdd;
    private ConvSQlite conversation;

    public ConvBDD(Context context){
        conversation = new ConvSQlite(context, BDD_NAME, null, VERSION);
    }

    public void openForRead(){
        bdd = conversation.getReadableDatabase();
    }

    public void openForWrite(){
        bdd = conversation.getWritableDatabase();
    }

    public void close() {
        bdd.close();
    }

    public SQLiteDatabase getBdd(){
        return bdd;
    }

    public long insertConversation(Conv conv){
        ContentValues content = new ContentValues();
        content.put(CONV_COL, conv.getConv());
        content.put(CAT_COL, conv.getCategorieString());
        content.put(IMG_COL, conv.getImg());
        return bdd.insert(CONV_TABLE, null, content);
    }

    public int updateConv(int id, Conv conv){
        ContentValues content = new ContentValues();
        content.put(CONV_COL, conv.getConv());
        content.put(CAT_COL, conv.getCategorieString());
        content.put(IMG_COL, conv.getImg());
        return bdd.update(CONV_TABLE, content, ID_COL + " = " + id, null);
    }

    public int removeConv(String conv) {
        return bdd.delete(CONV_TABLE, CONV_COL + " LIKE " + "\"" + conv + "\"", null);
    }

    public int removeConv(int id) {
        return bdd.delete(CONV_TABLE, ID_COL + " = " + id, null);
    }

    public int countCategorie(String cat){
        Cursor cursor = bdd.query(CONV_TABLE, null, CAT_COL + " LIKE " + cat, null, null, null, null);
        int nb = cursor.getCount();
        cursor.close();
        return nb;
    }

    public int countRow(){
        //Cursor cursor = bdd.rawQuery("SELECT " + CAT_COL + " FROM " + CONV_TABLE, null);
        Cursor cursor = bdd.query(CONV_TABLE, new String[]{CAT_COL}, null, null, null, null, null);
        int nb = cursor.getCount();
        cursor.close();
        return nb;
    }

    public boolean duplicateConv (Conv conv) {
        Cursor cursor = bdd.rawQuery("SELECT " + CONV_COL + " FROM " + CONV_TABLE + " WHERE " + CONV_COL + " LIKE " + conv.getConv(), null);
        int nb = cursor.getCount();
        cursor.close();
        if (nb > 0) {
            return false;
        }
        return true;
    }

    public Conv getConv(String conv) {
        Cursor cursor = bdd.query(CONV_TABLE, new String[]{ID_COL, CONV_COL, CAT_COL, IMG_COL}, CONV_COL + " LIKE \"" + conv + "\"", null, null, null, CONV_COL);
        return cursorToConv(cursor);
    }

    public Conv getConv(int id) {
        Cursor cursor = bdd.query(CONV_TABLE, new String[]{ID_COL, CONV_COL, CAT_COL, IMG_COL}, ID_COL + " = " + id, null, null, null, null);
        return cursorToConv(cursor);
    }

    public Conv randomConv(Integer selection){
        Cursor cursor;
        if (selection == R.mipmap.ic_launcher)
            cursor =  bdd.query(CONV_TABLE, new String[] {ID_COL, CONV_COL, CAT_COL, IMG_COL}, null, null, null, null, "RANDOM() LIMIT 1");
        else
            cursor = bdd.query(CONV_TABLE, new String[]{ID_COL, CONV_COL, CAT_COL, IMG_COL}, IMG_COL + " = " + selection, null, null, null, "RANDOM() LIMIT 1");
        return cursorToConv(cursor);
    }

    private Conv cursorToConv(Cursor cursor) {
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Conv conv = new Conv();
        conv.setCategorie(cursor.getString(NUM_CAT_COL), cursor.getInt(NUM_IMG_COL));
        conv.setId(cursor.getInt(NUM_ID_COL));
        conv.setConv(cursor.getString(NUM_CONV_COL));
        cursor.close();
        return conv;
    }

    public ArrayList<Conv> getAllConv() {
        Cursor cursor = bdd.query(CONV_TABLE, new String[] {ID_COL, CONV_COL, CAT_COL, IMG_COL}, null, null, null, null, null);
        if (cursor.getCount() == 0)
        {
            cursor.close();
            return null;
        }
        ArrayList<Conv> convList = new ArrayList<Conv>();
        while (cursor.moveToNext()) {
            Conv conv =  new Conv();
            conv.setCategorie(cursor.getString(NUM_CAT_COL), cursor.getInt(NUM_IMG_COL));
            conv.setId(cursor.getInt(NUM_ID_COL));
            conv.setConv(cursor.getString(NUM_CONV_COL));
            convList.add(conv);
        }
        cursor.close();
        return convList;
    }
}
