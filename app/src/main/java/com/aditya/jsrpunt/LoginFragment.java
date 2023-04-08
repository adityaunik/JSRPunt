package com.aditya.jsrpunt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends Fragment {

    View view;
    NavHostFragment navHostFragment;
    NavController navController;
    Button btnLogin;
    TextView txtRegister;
    TextView txtOTP;
    String userid;
    FirebaseAuth mAuth;
    EditText edtEmailLogin, edtPassLogin;
    FirebaseFirestore db;
    ProgressBar progressBar;

    public LoginFragment() {
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
        view = inflater.inflate(R.layout.fragment_login, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        btnLogin = view.findViewById(R.id.btn_login);
        txtRegister = view.findViewById(R.id.txt_goto_register);
        txtOTP = view.findViewById(R.id.txt_goto_otp);
        progressBar = view.findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.INVISIBLE);
        db = FirebaseFirestore.getInstance();


        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        txtOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_enterNumberFragment);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        edtEmailLogin = view.findViewById(R.id.edt_email_login);
        edtPassLogin = view.findViewById(R.id.edt_pass_login);
        progressBar.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmailLogin.getText().toString().trim();
                String pass = edtPassLogin.getText().toString().trim();

                if (!email.isEmpty() && !pass.isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        if (isAdded() && !isDetached()){

                                        Toast.makeText(getActivity(), "Token generation failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    // Get new FCM registration token
                                    String token = task.getResult();

                                    mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            userid = mAuth.getCurrentUser().getUid();
                                            DocumentReference documentReference = db.collection("users").document(userid);
                                            Map<String,Object> user = new HashMap<>();
                                            user.put("token",token);
                                            documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("data store","user is created with id: "+ userid);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("data store","failure: "+ e.toString());
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                            navController.navigate(R.id.action_loginFragment_to_home);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (isAdded() && !isDetached()){
                                                progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });

                                }
                            });

                }else {

                    if (isAdded() && !isDetached()){

                    Toast.makeText(getActivity(), "Please enter details", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
}