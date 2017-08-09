package id.technobit.smsforwarder.util;

import android.support.v4.app.Fragment;

/**
 * Created by bradhawk on 9/19/2016.
 */
public interface MainFragmentControl {
    void changeFragment(Fragment fragment, String TAG);
    void changeFragmentBackstack(Fragment fragment, String TAG);
    void popAllBackStack();
}
