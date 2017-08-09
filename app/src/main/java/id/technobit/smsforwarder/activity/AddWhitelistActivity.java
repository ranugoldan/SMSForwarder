package id.technobit.smsforwarder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import id.technobit.smsforwarder.R;
import id.technobit.smsforwarder.model.Whitelist;
import id.technobit.smsforwarder.util.DBHandler;

public class AddWhitelistActivity extends AppCompatActivity {

    EditText editNumber;
    Button buttonSubmit;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_whitelist);

        dbHandler = new DBHandler(this);

        editNumber = (EditText)findViewById(R.id.edit_number);
        buttonSubmit = (Button)findViewById(R.id.button_submit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Whitelist whitelist = new Whitelist();
                whitelist.setNumber(String.valueOf(editNumber.getText()));
                dbHandler.addWhitelist(whitelist);

                finish();
            }
        });

    }
}
