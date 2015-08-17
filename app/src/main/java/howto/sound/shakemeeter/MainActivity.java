package howto.sound.shakemeeter;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private TextView question;
    private ImageView image;
    private ArrayList<String> questions = new ArrayList<String>();
    private ArrayList<Integer> images = new ArrayList<Integer>();
    private HandleFile file;
    private Integer selection = R.mipmap.ic_launcher;
    private float xAxis = 0.0f;
    private float yAxis = 0.0f;
    private float zAxis = 0.0f;
    private Random random = new Random();
    private Hashtable<String, Integer> myImageList = new Hashtable<String, Integer>();

    //private String[] MenuSections;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private DrawerAdapter drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<Categorie> categorieList = new ArrayList<Categorie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
        question = (TextView) findViewById(R.id.question);

        initHashTable();

        file = new HandleFile(this, "new_questions", myImageList);
        if (!file.isFileCreated()) {

            InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier("raw/questions",
                            "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));


//                Log.e("error", "reader null");
//                try {
//                initList();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Log.e("error", "reader non null");
                file.setReader(reader);
                file.createFile(questions, images);
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        file.readFile(questions, images);
        file.addToArrays(questions, images);
        initDrawer();
        initAccelometerSensor();
    }

    private void initHashTable() {
        myImageList.put("Amour", R.drawable.amour);
        myImageList.put("Film", R.drawable.film);
        myImageList.put("Fou", R.drawable.fou);
        myImageList.put("Hot", R.drawable.hot);
        myImageList.put("Livres", R.drawable.livres);
        myImageList.put("Loisir", R.drawable.loisir);
        myImageList.put("Musique", R.drawable.musique);
        myImageList.put("Reseau", R.drawable.reseau);
        myImageList.put("Reve", R.drawable.reve);
        myImageList.put("Sport", R.drawable.sport);
        myImageList.put("Technique", R.drawable.technique);
        myImageList.put("Voyage", R.drawable.voyage);
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
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)){
            return (true);
        }

        switch (id) {
            case R.id.add :
                questions.add("test");
                images.add(R.drawable.livres);
                file.writeFile("\r\ntest;Livres");
                file.readFile(questions, images);
                Toast.makeText(this,"Option non encore fini. Come back later Dude!", Toast.LENGTH_SHORT).show();
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
        int q = Math.abs(random.nextInt()) % questions.size();
        if (selection != R.mipmap.ic_launcher)
            while (images.get(q) != selection || images.get(q) == null)
                q = Math.abs(random.nextInt()) % questions.size();
        else if (images.get(q) == null)
            while (images.get(q) == null)
                q = Math.abs(random.nextInt()) % questions.size();
        question.setText(questions.get(q));
        image.setImageResource(images.get(q));
    }

    private void initList() throws IOException {

        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("raw/questions",
                        "raw", getPackageName()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                questions.add(split[0]);
                images.add(myImageList.get(split[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ins.close();
    }

    private DrawerAdapter getAdapter(){
        return drawerAdapter;
    }

}
