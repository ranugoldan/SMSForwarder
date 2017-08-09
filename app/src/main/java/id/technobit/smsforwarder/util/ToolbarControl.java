package id.technobit.smsforwarder.util;

import android.support.annotation.StringRes;

/**
 * Created by bradhawk on 9/17/2016.
 */
public interface ToolbarControl {
    void changeElevation(float dp);

    void changeTitle(@StringRes int resourceId);
}
