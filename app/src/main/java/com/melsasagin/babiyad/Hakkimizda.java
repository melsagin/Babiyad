package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Hakkimizda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hakkimizda);
    }

    //SSS ye göz atın text'inin onclick metodu
    public void gonderSss(View view) {
        startActivity(new Intent(Hakkimizda.this, SSS.class));
    }
}
