package id.technobit.smsforwarder.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import id.technobit.smsforwarder.model.Message;
import id.technobit.smsforwarder.model.Whitelist;
import id.technobit.smsforwarder.model.api.RestClient;
import id.technobit.smsforwarder.model.json.SyncResponse;
import id.technobit.smsforwarder.util.DBHandler;
import id.technobit.smsforwarder.util.DateFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ranug on 27/04/2017.
 */

public class SmsListenerService extends Service {
    private SMSreceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate()
    {
        super.onCreate();

        //SMS event receiver
        mSMSreceiver = new SMSreceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister the SMS receiver
        unregisterReceiver(mSMSreceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SMSreceiver extends BroadcastReceiver
    {
        private final String TAG = this.getClass().getSimpleName();
        DBHandler dbHandler;
        SharedPreferences sharedPreferences;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle extras = intent.getExtras();
            dbHandler = new DBHandler(context);
            String strMessage = "";
            RestClient.initialize();
            sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

            if ( extras != null )
            {
                Object[] smsextras = (Object[]) extras.get( "pdus" );

                for ( int i = 0; i < smsextras.length; i++ )
                {
                    SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);
                    Message message = new Message();
                    message.setId(smsmsg.getIndexOnIcc()+"");
                    message.setContent(smsmsg.getMessageBody().toString());
                    message.setSenderNumber(smsmsg.getOriginatingAddress());
                    message.setDate(DateFormat.getDate(smsmsg.getTimestampMillis(), "dd/MM/yyyy"));

                    List<Whitelist> whitelists = dbHandler.getAllWhitelist();

                    for (Whitelist whitelist : whitelists){
                        if (message.getSenderNumber().equalsIgnoreCase(whitelist.getNumber())){
                            final Message msg = message;
                            String url = sharedPreferences.getString("url", "http://technobit.id");
                            Boolean isOptional = sharedPreferences.getBoolean("optional", false);
                            String optionalAddress = sharedPreferences.getString("optional_address", "");
                            Call<SyncResponse> call = RestClient.syncService.syncMessage(url, message.getId(), message.getSenderNumber(), message.getContent(), message.getDate(), isOptional, optionalAddress);
                            call.enqueue(new Callback<SyncResponse>() {
                                @Override
                                public void onResponse(Call<SyncResponse> call, Response<SyncResponse> response) {
                                    Log.d("Request", call.request().toString());
                                    Log.d("ServiceSMS", response.message());
                                    if (response.code()==200){
                                        dbHandler.addSMS(msg);
                                    } else {
                                        Toast.makeText(getBaseContext()
                                                ,"Error while forwarding message with sender "+ String.valueOf(msg.getSenderNumber()) +". HTTP status: "+ response.code()
                                                , Toast.LENGTH_SHORT).show();
                                        Log.d("UnsuccessfulResp", ""+response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<SyncResponse> call, Throwable t) {
                                    Log.d("MessageStatusFragment", t.getMessage());
                                    Log.d("MessageStatusFragment", call.request().toString());

                                    //Toast.makeText(getActivity(),"Error while forwarding message with sender "+ String.valueOf(msg.getSenderNumber()), Toast.LENGTH_SHORT).show();
                                }
                            });
//                            try {
//                                SyncResponse response;
//                                response = RestClient.syncService.syncMessage(message.getId(), message.getSenderNumber(), message.getContent(), message.getDate()).execute().body();
//                                if (!(response.getStatus().equalsIgnoreCase("1"))){
//                                    break;
//                                } else {
//                                    dbHandler.addSMS(message);
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }


                    Log.i(TAG, message.getContent());
                }

            }

        }

    }
}
