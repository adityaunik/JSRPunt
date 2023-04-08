package com.aditya.jsrpunt;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.aditya.jsrpunt.views.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {

    View view;
    NavHostFragment navHostFragment;
    NavController navController;
    Button btnRegister;
    Button btnGoogleSignup;
    TextView txtLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CountryCodePicker ccpRegister;
    EditText edtNameR, edtPhoneR, edtEmailR, edtPassR;
    String userid;
    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ProgressBar progressBar;


    public RegisterFragment() {
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
        view = inflater.inflate(R.layout.fragment_register, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        txtLogin = view.findViewById(R.id.txt_goto_login);
        progressBar = view.findViewById(R.id.progressBar_register);
        progressBar.setVisibility(View.INVISIBLE);


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                firebaseAuthWithGoogle(account.getIdToken());
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });

        return view;
    }

    public  void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (!task.isSuccessful()) {
                            if (isAdded() && !isDetached()){

                            Toast.makeText(getActivity(), "Token generation failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){


                                    FirebaseUser users = task.getResult().getUser();
                                    assert users != null;
                                    userid = users.getUid();
                                    long creationTimestamp = users.getMetadata().getCreationTimestamp();
                                    long lastSignInTimestamp = users.getMetadata().getLastSignInTimestamp();


                                    DocumentReference documentReference = db.collection("users").document(userid);
                                    Map<String,Object> user = new HashMap<>();

                                    if (creationTimestamp == lastSignInTimestamp){

                                        user.put("fName",users.getDisplayName());
                                        user.put("phone",users.getPhoneNumber());
                                        user.put("email",users.getEmail());
                                        user.put("uid",userid);
                                        user.put("wallet",0);
                                        user.put("token",token);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("data store","user is created with id: "+ userid);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                navController.navigate(R.id.action_registerFragment_to_home);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("data store","failure: "+ e.toString());
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }else {
                                        user.put("token",token);
                                        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Log.d("data store","user is created with id: "+ userid);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    navController.navigate(R.id.action_registerFragment_to_home);
                                                }else
                                                {
                                                    Log.d("data store","failure: ");
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    if (isAdded() && !isDetached()){

                                    Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        processRequest();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ccpRegister = view.findViewById(R.id.ccp_register);
        edtNameR = view.findViewById(R.id.edt_name_register);
        edtPhoneR = view.findViewById(R.id.edt_phone_register);
        edtEmailR = view.findViewById(R.id.edt_email_register);
        edtPassR = view.findViewById(R.id.edt_pass_register);
        btnRegister = view.findViewById(R.id.btn_register);
        btnGoogleSignup = view.findViewById(R.id.btn_google_signup);
        progressBar.setVisibility(View.INVISIBLE);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ccpRegister.registerCarrierNumberEditText(edtPhoneR);
                String name = edtNameR.getText().toString().trim();
                String phone = ccpRegister.getFullNumberWithPlus().replace(" ","");
                String email = edtEmailR.getText().toString().trim();
                String pass = edtPassR.getText().toString().trim();

                if(!name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !pass.isEmpty()){
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
                                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                if (isAdded() && !isDetached()){

                                                Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();
                                                }
                                                FirebaseUser users = task.getResult().getUser();
                                                assert users != null;
                                                userid = users.getUid();

                                                DocumentReference documentReference = db.collection("users").document(userid);
                                                Map<String,Object> user = new HashMap<>();

                                                    user.put("fName",name);
                                                    user.put("phone",phone);
                                                    user.put("email",email);
                                                    user.put("uid",userid);
                                                    user.put("wallet",0);
                                                    user.put("token",token);
                                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("data store","user is created with id: "+ userid);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            navController.navigate(R.id.action_registerFragment_to_loginFragment);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {@Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("data store","failure: "+ e);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                            }else{
                                                if (isAdded() && !isDetached()){

                                                Toast.makeText(getActivity(), "Account creation failed", Toast.LENGTH_LONG).show();
                                                }
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

        btnGoogleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              processLogin();
            }
        });


    }

    public void processRequest(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        if (isAdded() && !isDetached()){
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);
            }

    }

    public  void  processLogin(){

        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);


    }


}