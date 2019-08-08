package com.js.smart.progress.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.js.smart.progress.JSProgressBar;


public class MainActivity extends AppCompatActivity {

    private int step = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final JSProgressBar pb1 = findViewById(R.id.pb1);
        final JSProgressBar pb2 = findViewById(R.id.pb2);
        final JSProgressBar pb3 = findViewById(R.id.pb3);
        final JSProgressBar pb4 = findViewById(R.id.pb4);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step --;
                pb1.setProgressStep(step);
                pb2.setProgressStep(step);
                pb3.setProgress(step);
                pb4.setProgressStep(step);

            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step ++;
                pb1.setProgressStep(step);
                pb2.setProgressStep(step);
                pb3.setProgress(step);
                pb4.setProgressStep(step);
            }
        });
    }
}
