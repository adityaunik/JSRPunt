package com.aditya.jsrpunt.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.aditya.jsrpunt.R;
import com.aditya.jsrpunt.adapters.RecyclerAdapterTransaction;
import com.aditya.jsrpunt.adapters.RecyclerAdapterUserId;
import com.aditya.jsrpunt.model.TransactionModel;
import com.aditya.jsrpunt.model.UserIdModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class MyIDFragment extends Fragment implements RecyclerAdapterUserId.OnItemClickListener
{

    View view;
    FirestoreRecyclerOptions<UserIdModel> options;
    RecyclerAdapterUserId adapter;
    FirebaseFirestore db;
    RecyclerView recViewMyId;
    String uid;
    FirebaseAuth mAuth;

    public MyIDFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_i_d, container, false);
        db = FirebaseFirestore.getInstance();
        recViewMyId = view.findViewById(R.id.rec_view_my_id);
        recViewMyId.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        Query query = db.collection("users").document(uid)
                .collection("ids");
        options = new FirestoreRecyclerOptions.Builder<UserIdModel>()
                .setQuery(query, UserIdModel.class)
                .build();

        adapter = new RecyclerAdapterUserId(options,this::onItemClick);
        recViewMyId.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }


    @Override
    public void onItemClick(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }
        else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(getActivity());
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);

    }
}