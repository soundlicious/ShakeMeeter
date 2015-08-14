package howto.sound.shakemeeter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private TextView question;
    private ImageView image;
    private ArrayList<String> questions = new ArrayList<String>();
    private ArrayList<Integer> images = new ArrayList<Integer>();
    private float xAxis = 0.0f;
    private float yAxis = 0.0f;
    private float zAxis = 0.0f;
    private Random random = new Random();

    private final SensorEventListener accelerometre = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if (Math.abs(y - yAxis) > 15.0f || Math.abs(x - xAxis) > 15.0f || Math.abs(z - zAxis) > 15.0f) {
                    int q = Math.abs(random.nextInt()) % questions.size();
                    question.setText(questions.get(q));
                    image.setImageResource(images.get(q));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
        question = (TextView) findViewById(R.id.question);

        initList();

        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(accelerometre, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initList() {
        Hashtable<String, Integer> myImageList = new Hashtable<String, Integer>();
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

    }

}
