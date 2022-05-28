package com.vta.app.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import com.vta.app.R;


public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText txtCurrentPassword, txtNewPassword, txtConfirmNewPassword;
    private Button btnUpdatePassoword;

    private String loginID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.loginID = getIntent().getStringExtra("loginId");

        txtCurrentPassword = (TextInputEditText) findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (TextInputEditText) findViewById(R.id.txtNewPassword);
        txtConfirmNewPassword = (TextInputEditText) findViewById(R.id.txtConfirmNewPassword);
        txtConfirmNewPassword = (TextInputEditText) findViewById(R.id.txtConfirmNewPassword);
        btnUpdatePassoword = (Button) findViewById(R.id.btnUpdatePassoword);

        btnUpdatePassoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(ResetPasswordActivity.this, ()->{
                    if(txtNewPassword.getText().toString().trim().equals(txtConfirmNewPassword.getText().toString().trim())){
                        updatePassword(txtCurrentPassword.getText().toString().trim(), txtConfirmNewPassword.getText().toString().trim());
                    }else{
                        showDialog(ResetPasswordActivity.this, "Oops...!","Confirm password Not match",()->{});
                    }
                },()->{

                });
            }
        });
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