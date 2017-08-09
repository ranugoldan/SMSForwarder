package id.technobit.smsforwarder.model.api.services;

import id.technobit.smsforwarder.model.json.SyncResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by ranug on 27/04/2017.
 */

public interface SyncService {

    @FormUrlEncoded
    @POST
    Call<SyncResponse> syncMessage(@Url String url
            , @Field("sms_id") String sms_id
            , @Field("sender") String sender
            , @Field("content") String content
            , @Field("date") String date
            , @Field("is_optional") boolean isOptional
            , @Field("optional_address") String optionalAddress);
}
