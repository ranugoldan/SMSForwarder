package id.technobit.smsforwarder.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.technobit.smsforwarder.R;
import id.technobit.smsforwarder.util.ToolbarControl;
import id.technobit.smsforwarder.util.Util;

public class BaseActivity extends AppCompatActivity implements ToolbarControl{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void changeElevation(float dp) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(Util.pxFromDp(this.getBaseContext(), dp));
        }
    }

    @Override
    public void changeTitle(@StringRes int resourceId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(resourceId);
        }
    }
}
