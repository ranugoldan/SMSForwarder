package id.technobit.smsforwarder.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import id.technobit.smsforwarder.R;
import id.technobit.smsforwarder.activity.AddWhitelistActivity;
import id.technobit.smsforwarder.activity.MainActivity;
import id.technobit.smsforwarder.base.BaseActivity;
import id.technobit.smsforwarder.model.Whitelist;
import id.technobit.smsforwarder.util.DBHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WhitelistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WhitelistFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DBHandler dbHandler;

    View rootView;
    ListView listViewWhitelist;
    FloatingActionButton buttonAdd;
    List<Whitelist> listWhitelist = new ArrayList<>();


    public WhitelistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WhitelistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WhitelistFragment newInstance() {
        WhitelistFragment fragment = new WhitelistFragment();
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
        listWhitelist = dbHandler.getAllWhitelist();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_whitelist, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BaseActivity)getActivity()).changeTitle(R.string.language_set_menu_whitelist);
        //initializeDummy();
        listViewWhitelist = (ListView) rootView.findViewById(R.id.list_whitelist);
        buttonAdd = (FloatingActionButton) rootView.findViewById(R.id.button_add);

        List<String> list = new ArrayList<>();
        for (Whitelist whitelist : listWhitelist){
            list.add(whitelist.getNumber());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        listViewWhitelist.setAdapter(arrayAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddWhitelistActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        listWhitelist = dbHandler.getAllWhitelist();
        List<String> list = new ArrayList<>();
        for (Whitelist whitelist : listWhitelist){
            list.add(whitelist.getNumber());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
        listViewWhitelist.setAdapter(arrayAdapter);
        listViewWhitelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.whitelist_item_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHandler.deleteWhitelist(listWhitelist.get(position));
                                refreshView();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void initializeDummy() {
        Whitelist whitelist =  new Whitelist();
        whitelist.setNumber("222");
        listWhitelist.add(whitelist);
    }
    public void refreshView() {
        MainActivity activity = (MainActivity)getActivity();
        activity.changeFragment(WhitelistFragment.newInstance(), "Whitelist");
    }
}
