package id.technobit.smsforwarder.model.api;

import id.technobit.smsforwarder.model.api.services.SyncService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ranug on 27/04/2017.
 */

public class RestClient {
    public static Retrofit retrofit;
    public static SyncService syncService;

    public static void initialize(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.7/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        syncService = retrofit.create(SyncService.class);
    }
}
