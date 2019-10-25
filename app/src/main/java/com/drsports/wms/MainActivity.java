package com.drsports.wms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author vson
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SuspensionWindowUtil mWindowUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.showWms).setOnClickListener(this);
        findViewById(R.id.hiddenWms).setOnClickListener(this);
        mWindowUtil=new SuspensionWindowUtil(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showWms:
                mWindowUtil.showSuspensionView();
                break;
            case R.id.hiddenWms:
                mWindowUtil.hiddenSuspensionView();
                break;
            default:
                break;
        }

    }
}
