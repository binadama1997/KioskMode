package kiosk.mode_single.purpose.app;

import android.os.Bundle;

import kiosk.mode_single.purpose.app.utils.BaseActivity;

/**
 */

public class NextActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        findViewById(R.id.back).setOnClickListener(view -> finish());
    }
}
