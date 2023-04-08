package com.aditya.jsrpunt;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPFragment extends Fragment {

   View view;
   EditText edtOTP;
   TextView txtResendOTP;
   AppCompatButton btnLoginOTP;
   FirebaseAuth mAuth;
   FirebaseUser currentUser;
    FirebaseFirestore db;
   String otpid;
    String phoneN;
    String userid;
   NavHostFragment navHostFragment;
   NavController navController;
   ProgressBar progressBar;


    public OTPFragment() {
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
        view = inflater.inflate(R.layout.fragment_o_t_p, container, false);
        edtOTP = view.findViewById(R.id.edt_otp);
        txtResendOTP = view.findViewById(R.id.txt_resend_otp);
        btnLoginOTP = view.findViewById(R.id.btn_login_with_otp);
        progressBar = view.findViewById(R.id.progressBar_otp);
        progressBar.setVisibility(View.INVISIBLE);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        phoneN = OTPFragmentArgs.fromBundle(getArguments()).getPhone();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        progressBar.setVisibility(View.INVISIBLE);

        initiateOTP(phoneN);

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateOTP(phoneN);
            }
        });

        btnLoginOTP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edtOTP.getText().toString().isEmpty()){
                    if (isAdded() && !isDetached()){

                    Toast.makeText(getActivity(), "OTP not entered", Toast.LENGTH_SHORT).show();
                    }
                }else if (edtOTP.getText().toString().length()!=6){
                    if (isAdded() && !isDetached()){

                    Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otpid,edtOTP.getText().toString().trim());
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });

    }

    public  void initiateOTP(String phone){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                otpid = s;

                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                if (isAdded() && !isDetached()){

                                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (!task.isSuccessful()) {
                            if (isAdded() && !isDetached()){

                                progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Token generation failed", Toast.LENGTH_SHORT).show();
                            }
                        }else {


                            // Get new FCM registration token
                            String token = task.getResult();

                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser users = task.getResult().getUser();
                                                userid = task.getResult().getUser().getUid();
                                                assert users != null;
                                                long creationTimestamp = users.getMetadata().getCreationTimestamp();
                                                long lastSignInTimestamp = users.getMetadata().getLastSignInTimestamp();
                                                DocumentReference documentReference = db.collection("users").document(userid);
                                                Map<String,Object> user = new HashMap<>();
                                                if (creationTimestamp == lastSignInTimestamp) {
                                                    user.put("fName","null");
                                                    user.put("phone",phoneN);
                                                    user.put("email","null");
                                                    user.put("uid",userid);
                                                    user.put("wallet",0);
                                                    user.put("token",token);
                                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Log.d("data store","user is created with id: "+ userid);
                                                            navController.navigate(R.id.action_OTPFragment_to_home);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener(){
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("data store","failure: "+ e);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            if (isAdded() && !isDetached()){
                                                                Toast.makeText(getActivity(), "Error storing data", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    user.put("token",token);
                                                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("data store","user is updated with id: "+ userid);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            navController.navigate(R.id.action_OTPFragment_to_home);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("data store","failure: "+ e);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            if (isAdded() && !isDetached()){
                                                                Toast.makeText(getActivity(), "Error storing data", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {

                                                if (isAdded() && !isDetached()){

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(requireActivity(), "Invalid Code", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });

                        }

                    }
                });

    }
}