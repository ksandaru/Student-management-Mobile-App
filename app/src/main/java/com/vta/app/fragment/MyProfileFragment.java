package com.vta.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vta.app.R;
import com.vta.app.activity.AdminPageContainerActivity;
import com.vta.app.activity.LoginActivity;
import com.vta.app.model.AuthUserRole;
import com.vta.app.utils.AppSharedPreferences;
import com.vta.app.utils.Navigation;


import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    SharedPreferences sharedPre;

    private CircleImageView userImage;
    private TextView tvOwnerName, tvOwnerEmail,tvRole;
    private TextInputEditText txtCurrentPassword, txtNewPassword, txtConfirmNewPassword;
    private Button btnLogout, btnGotoSettings,btnGoBack, btnUpdatePassoword;
    private MaterialCardView cardViewProfile, cardViewSettings;
    private static Dialog appProgressDialog;
    private static Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String  USER_ROLE= "auth-user-role";

    public MyProfileFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "MyProfileFragment";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        context = getContext();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userImage = (CircleImageView) getView().findViewById(R.id.imageDisaster);
        tvOwnerName = (TextView) getView().findViewById(R.id.tvOwnerName);
        tvOwnerEmail = (TextView) getView().findViewById(R.id.tvOwnerEmail);
        tvRole = (TextView) getView().findViewById(R.id.tvRole);
        btnGotoSettings = (Button) getView().findViewById(R.id.btnGotoSettings);
        btnGoBack = (Button) getView().findViewById(R.id.btnGoBack);
        btnUpdatePassoword = (Button) getView().findViewById(R.id.btnUpdatePassoword);
        cardViewProfile = (MaterialCardView) getView().findViewById(R.id.cardViewProfile);
        cardViewSettings = (MaterialCardView) getView().findViewById(R.id.cardViewSettings);
        txtCurrentPassword = (TextInputEditText) getView().findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (TextInputEditText) getView().findViewById(R.id.txtNewPassword);
        txtConfirmNewPassword = (TextInputEditText) getView().findViewById(R.id.txtConfirmNewPassword);
        btnLogout = (Button) getView().findViewById(R.id.btnLogout);

        getUserAccount();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSharedPreferences.getData(sharedPre,"user-id") != null){
                    // Remove user's data
                    AppSharedPreferences.removeData(sharedPre,"user-id");
                    AppSharedPreferences.removeData(sharedPre,"user-email");
                    AppSharedPreferences.removeData(sharedPre,"user-role");
                }
                signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        btnGotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewProfile.setVisibility(View.GONE);
                btnGotoSettings.setVisibility(View.GONE);
                cardViewSettings.setVisibility(View.VISIBLE);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewProfile.setVisibility(View.VISIBLE);
                btnGotoSettings.setVisibility(View.VISIBLE);
                cardViewSettings.setVisibility(View.GONE);
            }
        });

        btnUpdatePassoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{
                    if(txtNewPassword.getText().toString().trim().equals(txtConfirmNewPassword.getText().toString().trim())){
                        updatePassword(txtCurrentPassword.getText().toString().trim(), txtConfirmNewPassword.getText().toString().trim());
                    }else{
                        showDialog(context, "Oops...!","Confirm password Not match",()->{});
                    }
                },()->{

                });
            }
        });
    }


    private void signOut() {
        mAuth.signOut();
    }

    //TODO: Get from API
    public void getUserAccount(){

        if(AppSharedPreferences.getData(sharedPre,"user-id") != null){
          tvOwnerName.setText(AppSharedPreferences.getData(sharedPre,"user-email"));
          tvOwnerEmail.setText(AppSharedPreferences.getData(sharedPre,"user-email"));
          tvRole.setText(AppSharedPreferences.getData(sharedPre,"user-role"));
        //  Picasso.get().load(userProfRes.getLogin().getAvatar()).into(userImage);
        }
    }


    public void updatePassword(String oldPswd, String newPswd){

    }


    public static  void showDialog(
            @NonNull final Context context,
            String title,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }

    public static  void showConfirmDialog(
            @NonNull final Context context,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure ?")
                .setMessage("Are you sure to continue this task")
                .setCancelable(false)
                .setPositiveButton("Confirm",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Cancel",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
    }
}