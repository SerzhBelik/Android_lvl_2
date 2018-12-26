package com.example.belikov.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends Activity {

    private int years;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        final TextView status = findViewById(R.id.status);
        final TextView temper = findViewById(R.id.temperature);

        years = getIntent().getIntExtra(MainActivity.YEARS, 0);

        final RequestMaker requestMaker = new RequestMaker(new RequestMaker.OnRequestListener() {

            @Override
            public void onStatusProgress(String updateProgress) {
                status.setText(updateProgress);
            }

            @Override
            public void onComplete(Float result) {
                temper.setText(temper.getText().toString() + " " + result);
            }
        });

        requestMaker.make(years);
    }
}
