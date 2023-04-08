package com.aditya.jsrpunt.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aditya.jsrpunt.R;
import com.aditya.jsrpunt.adapters.CreateIdRecyclerAdapter;
import com.aditya.jsrpunt.model.Site;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class CreateIDFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    FirestoreRecyclerOptions<Site> options;
    CreateIdRecyclerAdapter adapter;
    AppCompatButton btnCreateID;

    public CreateIDFragment() {
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
        view = inflater.inflate(R.layout.fragment_create_i_d, container, false);
        recyclerView = view.findViewById(R.id.rec_view_create_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnCreateID = view.findViewById(R.id.btn_create_id);

//        recyclerView.setItemAnimator(null);


        Query query = FirebaseFirestore.getInstance().collection("sites");
        options = new FirestoreRecyclerOptions.Builder<Site>()
                .setQuery(query,Site.class)
                .build();

        adapter = new CreateIdRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

}