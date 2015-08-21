package howto.sound.shakemeeter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class MainActivity extends ActionBarActivity {
    private TextView question;
    private ImageView image;
    private Integer selection = R.mipmap.ic_launcher;
    private float xAxis = 0.0f;
    private float yAxis = 0.0f;
    private float zAxis = 0.0f;
    private Hashtable<String, Integer> myImageList = new Hashtable<String, Integer>();
    private AlertDialog.Builder  AlertDelete;
    private Dialog AlertAdd;
    private Spinner spinner;
    private EditText edit;
    private ArrayAdapter<CharSequence> adapter;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private DrawerAdapter drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<Categorie> categorieList = new ArrayList<Categorie>();

    private ConvBDD convDB;
    private Conv new_conv = null;
    private Conv conv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
        question = (TextView) findViewById(R.id.question);

        initHashTable();

        convDB = new ConvBDD(this);
        convDB.openForRead();
        if (convDB.countRow() == 0){
            convDB.close();
            InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier("raw/questions",
                            "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

            String line = null;
            try {
                convDB.openForWrite();
                while (reader != null && (line = reader.readLine()) != null) {
                    String[] split = line.split(";");
                    if (split.length == 2) {
                        convDB.insertConversation(new Conv(new Categorie(split[1], myImageList.get(split[1])), split[0]));
                    }
                }
                convDB.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initDrawer();
        initAccelometerSensor();
        initAlerts();
    }

    private void initAlerts() {
        AlertDelete = new AlertDialog.Builder(this);
        AlertDelete.setMessage(R.string.alerte_delete).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                convDB.openForRead();
                convDB.removeConv(conv.getId());
                convDB.close();
                selectCategorie();
                dialog.cancel();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertAdd = new Dialog(this);
        AlertAdd.setContentView(R.layout.new_question);
        AlertAdd.setTitle(R.string.add);
        spinner = (Spinner) AlertAdd.findViewById(R.id.spinner);
        edit = (EditText) AlertAdd.findViewById(R.id.edit_conv);
        ButtonRectangle addButton = (ButtonRectangle) AlertAdd.findViewById(R.id.button_add);
        ButtonRectangle cancelButton = (ButtonRectangle) AlertAdd.findViewById(R.id.button_cancel);
        adapter = ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convDB.openForWrite();
                String oldConv = edit.getText().toString();
                String oldCat = spinner.getSelectedItem().toString();
                if (conv != null && conv == new_conv && (oldConv != conv.getConv() || oldCat != conv.getCategorieString())) {
                    convDB.updateConv(conv.getId(), new Conv(new Categorie(oldCat, myImageList.get(oldCat)), oldConv));
                    question.setText(conv.getConv());
                    image.setImageResource(conv.getImg());
                } else if (new_conv == null) {
                    convDB.insertConversation(new Conv(new Categorie(oldCat, myImageList.get(oldCat)), oldConv));
                }
                convDB.close();
                new_conv = null;
                AlertAdd.cancel();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_conv = null;
                AlertAdd.cancel();
            }
        });
    }

    private void initHashTable() {
        myImageList.put(getResources().getString(R.string.amour), R.drawable.amour);
        myImageList.put(getResources().getString(R.string.film), R.drawable.film);
        myImageList.put(getResources().getString(R.string.fou), R.drawable.fou);
        myImageList.put(getResources().getString(R.string.hot), R.drawable.hot);
        myImageList.put(getResources().getString(R.string.livres), R.drawable.livres);
        myImageList.put(getResources().getString(R.string.loisir), R.drawable.loisir);
        myImageList.put(getResources().getString(R.string.musique), R.drawable.musique);
        myImageList.put(getResources().getString(R.string.reseau), R.drawable.reseau);
        myImageList.put(getResources().getString(R.string.reve), R.drawable.reve);
        myImageList.put(getResources().getString(R.string.sport), R.drawable.sport);
        myImageList.put(getResources().getString(R.string.technique), R.drawable.technique);
        myImageList.put(getResources().getString(R.string.voyage), R.drawable.voyage);
    }

    private void initAccelometerSensor() {
        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(accelerometre, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initDrawer() {
        Iterator<Hashtable.Entry<String, Integer>> it = myImageList.entrySet().iterator();

        categorieList.add(new Categorie("All", R.mipmap.ic_launcher));
        while (it.hasNext()) {
            Hashtable.Entry<String, Integer> entry = it.next();
            Log.v("Categorie classe", entry.getKey() + " " + entry.getValue());
            categorieList.add(new Categorie(entry.getKey(), entry.getValue()));
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerAdapter = new DrawerAdapter(this, R.layout.left_drawer, categorieList);
        drawerList.setAdapter(drawerAdapter);

        drawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Categorie item = (Categorie) adapter.getItemAtPosition(position);
                if (selection != item.getImg()) {
                    selection = item.getImg();
                    selectCategorie();
                    drawerLayout.closeDrawers();
                }
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open, R.string.open) {
            public void onDrawerClosed(View view) {
                Log.d("HomeActivity", "onDrawerClosed");
            }
            public void onDrawerOpened(View drawerView) {
                Log.d("HomeActivity", "onDrawerOpened");
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle saveInstanceState) {
        super.onPostCreate(saveInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return (true);
        }

        switch (id) {
            case R.id.add:
                    new_conv = null;
                    AlertAdd.show();
                return true;
            case R.id.delete:
                if (conv != null) {
                    if (conv.getId() < 52)
                        Toast.makeText(this, R.string.delete_conv_base, Toast.LENGTH_LONG).show();
                    else
                        AlertDelete.show();
                }
                return true;
            case R.id.edit:
                if (conv != null && conv.getId() < 52) {
                    Toast.makeText(this, R.string.edit_conv_base, Toast.LENGTH_LONG).show();
                }
                else if (conv != null){
                    new_conv = conv;
                    spinner.setSelection(adapter.getPosition(conv.getCategorieString()));
                    edit.setText(conv.getConv());
                    AlertAdd.show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final SensorEventListener accelerometre = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if (Math.abs(y - yAxis) > 15.0f || Math.abs(x - xAxis) > 15.0f || Math.abs(z - zAxis) > 15.0f) {
                    selectCategorie();
                    xAxis = x;
                    yAxis = y;
                    zAxis = z;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void selectCategorie() {
        convDB.openForRead();
        conv = convDB.randomConv(selection);
        convDB.close();
        if (conv != null) {
            question.setText(conv.getConv());
            image.setImageResource(conv.getImg());
        }
    }

    private DrawerAdapter getAdapter(){
        return drawerAdapter;
    }
}
