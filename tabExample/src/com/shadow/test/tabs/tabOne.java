package com.shadow.test.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class tabOne extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Artists tab");
        setContentView(textview);
    }
}
