package id.technobit.smsforwarder.fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.technobit.smsforwarder.R;
import id.technobit.smsforwarder.activity.MainActivity;
import id.technobit.smsforwarder.base.BaseActivity;
import id.technobit.smsforwarder.model.Message;
import id.technobit.smsforwarder.model.Whitelist;
import id.technobit.smsforwarder.model.api.RestClient;
import id.technobit.smsforwarder.model.json.SyncResponse;
import id.technobit.smsforwarder.util.DBHandler;
import id.technobit.smsforwarder.util.DateFormat;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageStatusFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DBHandler dbHandler;
    View rootView;
    RecyclerView recyclerMessage;
    List<Message> listMessage;
    TelephonyProvider telephonyProvider;
    List<Sms> listSms;
    FloatingActionButton buttonSync;
    SharedPreferences sharedPreferences;


    public MessageStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageStatusFragment newInstance() {
        MessageStatusFragment fragment = new MessageStatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dbHandler = new DBHandler(getContext());
        telephonyProvider = new TelephonyProvider(getContext());
        RestClient.initialize();
        sharedPreferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message_status, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BaseActivity)getActivity()).changeTitle(R.string.language_set_menu_message_status);

        buttonSync = (FloatingActionButton) rootView.findViewById(R.id.button_sync);
        buttonSync.setOnClickListener(this);
        recyclerMessage = (RecyclerView) rootView.findViewById(R.id.recycler_message);
        //initializeDummy();
        TelephonyProvider.Filter filter = TelephonyProvider.Filter.INBOX;

        Data<Sms> smsData = telephonyProvider.getSms(filter);
        List<Sms> unfiltered = smsData.getList();
        //listSms = filterSms(unfiltered);
        listMessage = filterMessage(unfiltered);

        StatusAdapter statusAdapter = new StatusAdapter(getContext(),listMessage);
        recyclerMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMessage.setAdapter(statusAdapter);
    }

    private List<Message> filterMessage(List<Sms> listSms) {
        List<Whitelist> whitelists = dbHandler.getAllWhitelist();
        List<Message> filteredSms = new ArrayList<>();
        for (Sms sms : listSms){
            for (Whitelist whitelist : whitelists){
                if (sms.address.equalsIgnoreCase(whitelist.getNumber())){
                    Message message = new Message();
                    message.setId(String.valueOf(sms.id));
                    message.setSenderNumber(sms.address);
                    message.setContent(sms.body);
                    message.setDate(DateFormat.getDate(sms.receivedDate, "dd/MM/yyyy"));
                    message.setForwarded(dbHandler.isSmsSynced(sms));
                    filteredSms.add(message);
                    break;
                }
            }
        }
        return filteredSms;
    }

    private List<Sms> filterSms(List<Sms> listSms) {
        List<Whitelist> whitelists = dbHandler.getAllWhitelist();
        List<Sms> filteredSms = new ArrayList<>();
        for (Sms sms : listSms){
            for (Whitelist whitelist : whitelists){
                if (sms.address.equalsIgnoreCase(whitelist.getNumber())){
                    filteredSms.add(sms);
                    break;
                }
            }
        }
        return filteredSms;
    }

    private void initializeDummy() {
        listMessage = new ArrayList<>();
        Message message = new Message();
        message.setDate("15/04/2017");
        message.setSenderNumber("222");
        message.setContent("Trm ksh telah memilih Indosat Ooredoo 4Gplus. Scr keseluruhan, sberapa mungkin Anda merekomendasikan kami ke rekan/klrg? Balas N0 sd N10 (N10=tertinggi),GRATIS");
        message.setForwarded(false);
        listMessage.add(message);
        Message message2 = new Message();
        message2.setDate("13/04/2017");
        message2.setSenderNumber("222");
        message2.setContent("Anda baru sj akses Youtube dgn IndosatOoredoo. Berdasar pngalaman Anda, sberapa puas Anda thdp kualitas akses Youtube kami? Balas Y1 sd Y5 (Y5=Sgt puas. FREE");
        message2.setForwarded(false);
        listMessage.add(message2);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSync){
            if (sharedPreferences.getString("url", "").equalsIgnoreCase("")){
                Toast.makeText(getContext(), "You must fill forwarder URL in Settings section", Toast.LENGTH_LONG).show();
            } else {
                for (Message message : listMessage){
                    if (!message.isForwarded()){
                        final Message msg = message;
                        String url = sharedPreferences.getString("url", "http://technobit.id");
                        Boolean isOptional = sharedPreferences.getBoolean("optional", false);
                        String optionalAddress = sharedPreferences.getString("optional_address", "");
                        Call<SyncResponse> call = RestClient.syncService.syncMessage(url, message.getId(), message.getSenderNumber(), message.getContent(), message.getDate(), isOptional, optionalAddress);
                        call.enqueue(new Callback<SyncResponse>() {
                            @Override
                            public void onResponse(Call<SyncResponse> call, Response<SyncResponse> response) {
                                Log.d("Request", call.request().toString());
                                Log.d("MessageStatusFragment", response.message());
                                if (response.code()==200){
                                    dbHandler.addSMS(msg);
                                    refreshView();
                                } else {
                                    Toast.makeText(getActivity()
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
                    }
                }
                refreshView();
            }
        }
    }

    public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder>{
        Context context;
        List<Message> listMessage;

        public StatusAdapter(Context context, List<Message> listMessage){
            this.context = context;
            this.listMessage = listMessage;
        }

        @Override
        public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_message_status, parent, false);
            return new StatusViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StatusViewHolder holder, int position) {
            Message message = listMessage.get(position);
            holder.textSender.setText(message.getSenderNumber());

            holder.textDate.setText(message.getDate());
            holder.textContent.setText(message.getContent());
            if (message.isForwarded()){
                holder.imageStatus.setImageResource(R.drawable.ic_message_forwarded);
            } else {
                holder.imageStatus.setImageResource(R.drawable.ic_message_pending);
            }
        }

        @Override
        public int getItemCount() {
            return listMessage.size();
        }

        public class StatusViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageStatus;
            public TextView textSender;
            public TextView textDate;
            public TextView textContent;
            public StatusViewHolder(View itemView) {
                super(itemView);
                imageStatus = (ImageView)itemView.findViewById(R.id.image_message_status);
                textSender = (TextView)itemView.findViewById(R.id.text_sender);
                textDate = (TextView)itemView.findViewById(R.id.text_datetime);
                textContent = (TextView)itemView.findViewById(R.id.text_content);
            }
        }
    }
    public void refreshView() {
        MainActivity activity = (MainActivity)getActivity();
        activity.changeFragment(MessageStatusFragment.newInstance(), "MessageStatus");
    }

}
