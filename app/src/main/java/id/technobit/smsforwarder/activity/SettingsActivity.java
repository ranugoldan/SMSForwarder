package id.technobit.smsforwarder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import id.technobit.smsforwarder.R;
import id.technobit.smsforwarder.base.BaseActivity;
import id.technobit.smsforwarder.service.SmsListenerService;

public class SettingsActivity extends BaseActivity {

    Switch switchBackground;
    Switch switchOptionalAddress;
    Boolean switchChecked;
    EditText editURL;
    EditText editOptionalAddress;
    Button buttonSave;
    String settingsPreference = "Settings";
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeTitle(R.string.language_set_menu_settings);

        sharedPreferences = getSharedPreferences(settingsPreference, Context.MODE_PRIVATE);

        switchBackground = (Switch)findViewById(R.id.switch_background);
        switchOptionalAddress = (Switch)findViewById(R.id.switch_optional_address);
        editURL = (EditText)findViewById(R.id.edit_url_forward);
        editOptionalAddress = (EditText)findViewById(R.id.edit_optional_address);
        buttonSave = (Button)findViewById(R.id.button_save);

        String url = sharedPreferences.getString("url", "");
        editURL.setText(url);
        switchBackground.setChecked(sharedPreferences.getBoolean("background", false));
        switchOptionalAddress.setChecked(sharedPreferences.getBoolean("optional", false));
        switchChecked = switchBackground.isChecked();

        String opt = sharedPreferences.getString("optional_address", "");
        editOptionalAddress.setText(opt);

        if (switchOptionalAddress.isChecked()){
            editOptionalAddress.setEnabled(true);
        } else {
            editOptionalAddress.setEnabled(false);
        }

        switchOptionalAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editOptionalAddress.setEnabled(b);
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsListener = new Intent(SettingsActivity.this, SmsListenerService.class);
                if (switchBackground.isChecked() == true && switchChecked == false){
                    startService(smsListener);
                } else if (switchBackground.isChecked() == false && switchChecked == true){
                    stopService(smsListener);
                }
                if (String.valueOf(editURL.getText()).equalsIgnoreCase("http://")){
                    editURL.setText("");
                }
                if (switchOptionalAddress.isChecked() && String.valueOf(editOptionalAddress.getText()).equalsIgnoreCase("")){
                    switchOptionalAddress.setChecked(false);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("url", String.valueOf(editURL.getText()));
                editor.putBoolean("background", switchBackground.isChecked());
                editor.putBoolean("optional", switchOptionalAddress.isChecked());
                editor.putString("optional_address", String.valueOf(editOptionalAddress.getText()));
                editor.commit();
                Toast.makeText(getBaseContext(), "Settings saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
