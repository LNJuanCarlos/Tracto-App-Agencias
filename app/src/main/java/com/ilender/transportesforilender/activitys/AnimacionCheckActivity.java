package com.ilender.transportesforilender.activitys;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.airbnb.lottie.LottieAnimationView;
import com.ilender.transportesforilender.R;



public class AnimacionCheckActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animacion_check);

        animationView = findViewById(R.id.lottie);
        animationView.setAnimation("check.json");
        animationView.playAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Regresar a la actividad anterior
                finish();
            }
        }, 1100); // 3000 milisegundos = 3 segundos

    }
}