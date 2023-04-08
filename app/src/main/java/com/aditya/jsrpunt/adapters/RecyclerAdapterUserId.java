package com.aditya.jsrpunt.adapters;

import static com.aditya.jsrpunt.fcm.Constants.TOPIC_2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;


import com.aditya.jsrpunt.DepositIdFragmentDirections;
import com.aditya.jsrpunt.R;
import com.aditya.jsrpunt.fcm.ApiUtilities;
import com.aditya.jsrpunt.model.NotificationDataModel;
import com.aditya.jsrpunt.model.PushNotificatioDataModel;
import com.aditya.jsrpunt.model.UserIdModel;
import com.aditya.jsrpunt.views.IDsFragmentDirections;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerAdapterUserId extends FirestoreRecyclerAdapter<UserIdModel, RecyclerAdapterUserId.myViewholder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    NavHostFragment navHostFragment;
    NavController navController;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String title, message;
    View view;
    private OnItemClickListener listener;

    public RecyclerAdapterUserId(@NonNull FirestoreRecyclerOptions<UserIdModel> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


//    public RecyclerAdapterUserId(@NonNull FirestoreRecyclerOptions<UserIdModel> options) {
//        super(options);
//    }
    public interface OnItemClickListener {
        void onItemClick(String url);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewholder holder, int position, @NonNull UserIdModel model) {

        holder.txtUrl.setText(model.getUrl());
        holder.txtId.setText(model.getId());
        holder.txtPass.setText(model.getPass());

        holder.txtUrl.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {
                Log.d("datata", "" + position +"  "+model.getUrl());

                String url = holder.txtUrl.getText().toString().trim();
                if (checkValidUrl(url)) {
                    listener.onItemClick(url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(url));
//                    view.getContext().startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navHostFragment = (NavHostFragment) ((AppCompatActivity) view.getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                navController = navHostFragment.getNavController();

                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.txtMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deposit:
                                NavDirections action = IDsFragmentDirections.actionIdToDepositIdFragment(
                                        model.getUrl(), model.getName());
                                navController.navigate(action);
                                return true;
                            case R.id.withdraw:
                                NavDirections action1 = IDsFragmentDirections.actionIdToWithdrawIdFragment(
                                        model.getUrl(), model.getName());
                                navController.navigate(action1);
                                return true;
                            case R.id.pass:

                                Dialog dialog = new Dialog(view.getContext());
                                dialog.setContentView(R.layout.custom_update_pass_dialog);

                                title = "Password change Request";
                                message = "Password change Request";

                                AppCompatButton btnYes, btnNo;
                                btnYes = dialog.findViewById(R.id.btn_yes_pass);
                                btnNo = dialog.findViewById(R.id.btn_no_pass);

                                dialog.show();

                                btnYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db = FirebaseFirestore.getInstance();
                                        mAuth = FirebaseAuth.getInstance();

                                        DocumentReference documentReference = db.collection("changePasswordRequests").document(Objects.requireNonNull(mAuth.getUid()));
                                        Map<String, Object> bank = new HashMap<>();
                                        bank.put("userName", model.getId());
                                        bank.put("uid", mAuth.getUid());
                                        bank.put("siteUrl", model.getUrl());
                                        bank.put("siteName", model.getName());
                                        documentReference.set(bank).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        dialog.dismiss();
                                                        PushNotificatioDataModel notification = new PushNotificatioDataModel(new NotificationDataModel(title, message), TOPIC_2);
                                                        sendNotification(notification);

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(view.getContext(), "Request sent failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });

                                btnNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog.dismiss();
                                    }
                                });

                                return true;
                            case R.id.closeId:

                                Dialog dialog1 = new Dialog(view.getContext());
                                dialog1.setContentView(R.layout.custom_update_password_dialog);

                                title = "Close ID Request";
                                message = "Close ID Request";

                                AppCompatButton btnYes1, btnNo1;
                                btnYes1 = dialog1.findViewById(R.id.btn_yes);
                                btnNo1 = dialog1.findViewById(R.id.btn_no);

                                dialog1.show();

                                btnYes1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db = FirebaseFirestore.getInstance();
                                        mAuth = FirebaseAuth.getInstance();

                                        DocumentReference documentReference = db.collection("closeIdRequests").document(Objects.requireNonNull(mAuth.getUid()));
                                        Map<String, Object> bank = new HashMap<>();
                                        bank.put("userName", model.getId());
                                        bank.put("uid", mAuth.getUid());
                                        bank.put("siteUrl", model.getUrl());
                                        bank.put("siteName", model.getName());
                                        documentReference.set(bank).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        dialog1.dismiss();
                                                        PushNotificatioDataModel notification = new PushNotificatioDataModel(new NotificationDataModel(title, message), TOPIC_2);
                                                        sendNotification(notification);

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog1.dismiss();
                                                        Toast.makeText(view.getContext(), "Request sent failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });

                                btnNo1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog1.dismiss();
                                    }
                                });

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

    }

    @NonNull
    @Override
    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_id_row, parent, false);
        return new myViewholder(view);
    }

    public class myViewholder extends RecyclerView.ViewHolder {

        TextView txtUrl, txtId, txtPass, txtMenu;

        public myViewholder(@NonNull View itemView) {
            super(itemView);

            txtUrl = itemView.findViewById(R.id.txt_id_url);
            txtId = itemView.findViewById(R.id.txt_id_id);
            txtPass = itemView.findViewById(R.id.txt_id_pass);
            txtMenu = itemView.findViewById(R.id.menu_id);


        }
    }

    private void sendNotification(PushNotificatioDataModel notification) {

        ApiUtilities.getClient().sendNotification(notification)
                .enqueue(new Callback<PushNotificatioDataModel>() {
                    @Override
                    public void onResponse(@NonNull Call<PushNotificatioDataModel> call, @NonNull Response<PushNotificatioDataModel> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Request Sent", Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    navController.navigate(R.id.action_id_to_home);
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(view.getContext(), "Request not Sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PushNotificatioDataModel> call, Throwable t) {

                        Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkValidUrl(String s) {

        return URLUtil.isValidUrl(s);

    }
}
