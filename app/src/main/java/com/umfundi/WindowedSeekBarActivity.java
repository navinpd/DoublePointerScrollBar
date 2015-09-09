package com.umfundi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WindowedSeekBarActivity extends Activity {

	TextView tv1,tv2,tv3,tv4;
	WindowedSeekBar wsb;
    double floor = 10.5;
    double ceiling = 30.0;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        wsb = (WindowedSeekBar) findViewById(R.id.windowedseekbar);

        wsb.updateViews(tv1, tv2, tv3);

        wsb.updateBar(5000, 1000, 4000, 10000);

    }
    

}