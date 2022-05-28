package com.vta.app.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vta.app.R;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;

public class ForgetPassowrdActivity extends AppCompatActivity {

    private Button btnReqReset;
    private TextInputEditText txtLoginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passowrd);

        txtLoginEmail = (TextInputEditText) findViewById(R.id.txtLoginEmail);
        btnReqReset = (Button) findViewById(R.id.btnReqReset);

        btnReqReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
    }

    public void forgetPassword(){
        String email_text =  txtLoginEmail.getText().toString().trim();

        if(email_text.equals(""))
        {
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_LONG).show();
        }

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
}