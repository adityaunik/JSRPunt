package com.aditya.jsrpunt.views;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.jsrpunt.R;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    View view;
    ImageSlider imgSlider;
    NavController navController;
    NavHostFragment navHostFragment;
    TextView txtGotoID, txtGotoWallet;
    CardView openWhatsapp;
    AppCompatImageView img2;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<SlideModel> slidemodels;

    public HomeFragment() {
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

        view = inflater.inflate(R.layout.fragment_home, container, false);
        imgSlider = view.findViewById(R.id.image_slider);
        img2 = view.findViewById(R.id.img2);
        db = FirebaseFirestore.getInstance();
        slidemodels = new ArrayList<>();// Create image list
        mAuth = FirebaseAuth.getInstance();


        DocumentReference docRef = db.collection("bannerimg2").document("img");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(isAdded() && !isDetached()){
                            Glide
                                    .with(getContext())
                                    .load(document.getData().get("url"))
                                    .placeholder(R.drawable.scroll_image_3)
                                    .into(img2);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        db.collection("bannerimg1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                        slidemodels.add(new SlideModel(queryDocumentSnapshot.getString("url"),ScaleTypes.CENTER_CROP));
                        imgSlider.setImageList(slidemodels, ScaleTypes.CENTER_CROP);
                    }
                    }else{

                    if(isAdded() && !isDetached()) {
                        Toast.makeText(getActivity(), "Can't load images", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(isAdded() && !isDetached()) {
                            Toast.makeText(getActivity(), "Can't load images", Toast.LENGTH_SHORT).show();
                        }

                        }
                });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String contact = "+44 7537105005"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        openWhatsapp = view.findViewById(R.id.open_whatsapp);
        txtGotoID = view.findViewById(R.id.txt_goto_id);
        txtGotoWallet = view.findViewById(R.id.txt_goto_wallet);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        txtGotoID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_home_to_id);
            }
        });

        openWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse(url));
                startActivity(Intent.createChooser(sendIntent, ""));
            }
        });

        txtGotoWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_home_to_wallet);
            }
        });

    }

}