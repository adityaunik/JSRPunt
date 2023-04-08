package com.aditya.jsrpunt.views;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewTreeViewModelKt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.aditya.jsrpunt.R;
import com.aditya.jsrpunt.adapters.RecyclerAdapterTransaction;
import com.aditya.jsrpunt.model.TransactionModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class TransactionsFragment extends Fragment {

    View view;
    FirestoreRecyclerOptions<TransactionModel> options;
    RecyclerAdapterTransaction adapter;
    FirebaseFirestore db;
    RecyclerView recViewTransaction;
    String uid;
    FirebaseAuth mAuth;

    public TransactionsFragment() {
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
        view = inflater.inflate(R.layout.fragment_transactions, container, false);
        db = FirebaseFirestore.getInstance();
        recViewTransaction = view.findViewById(R.id.rec_view_transactions);
        recViewTransaction.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        recViewTransaction.setItemAnimator(null);

        Query query = db.collection("users").document(uid)
                .collection("transactions");
        options = new FirestoreRecyclerOptions.Builder<TransactionModel>()
                .setQuery(query, TransactionModel.class)
                .build();

        adapter = new RecyclerAdapterTransaction(options);
        recViewTransaction.setAdapter(adapter);

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
}