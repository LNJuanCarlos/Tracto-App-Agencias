package com.ilender.transportesforilender.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ilender.transportesforilender.R;

import java.util.Timer;
import java.util.TimerTask;

public class Presentacion extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Presentacion.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer tiempo = new Timer();
        tiempo.schedule(tarea,3000);

    }
}
