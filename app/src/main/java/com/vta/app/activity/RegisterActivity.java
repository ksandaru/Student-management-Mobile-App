package com.vta.app.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import de.hdodenhof.circleimageview.CircleImageView;

import com.vta.app.R;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText txtNic, txtEmail,
            txt_fullName,
            txt_address,
            txt_phone,
            txt_dob,
            txt_province,
            txt_district,
            txt_city,
            txt_gender,
            txt_password,
            txt_confirmPassword;
    private TextView tvSelectImage;
    private Button btnRegister;
    private CircleImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Avoid Status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        txtNic = (TextInputEditText) findViewById(R.id.txtNic);
        txtEmail = (TextInputEditText) findViewById(R.id.tv_nic_no);
        txt_fullName = (TextInputEditText) findViewById(R.id.txt_fullName);
        txt_address = (TextInputEditText) findViewById(R.id.txt_address);
        txt_phone = (TextInputEditText) findViewById(R.id.txt_phone);
        txt_dob = (TextInputEditText) findViewById(R.id.txt_dob);
        txt_province = (TextInputEditText) findViewById(R.id.txt_province);
        txt_district = (TextInputEditText) findViewById(R.id.txt_district);
        txt_city = (TextInputEditText) findViewById(R.id.txt_city);
        txt_gender = (TextInputEditText) findViewById(R.id.txt_gender);
        txt_password = (TextInputEditText) findViewById(R.id.txt_password);
        txt_confirmPassword = (TextInputEditText) findViewById(R.id.txt_confirmPassword);
        userImage = (CircleImageView) findViewById(R.id.imageDisaster);
        tvSelectImage = (TextView) findViewById(R.id.tvSelectImage);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NIC = txtNic.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String fullName = txt_fullName.getText().toString().trim();
                String address = txt_address.getText().toString().trim();
                String phone = txt_phone.getText().toString().trim();
                String dob = txt_dob.getText().toString().trim();
                String province = txt_province.getText().toString().trim();
                String district = txt_district.getText().toString().trim();
                String city = txt_city.getText().toString().trim();
                String gender = txt_gender.getText().toString().trim();
                String password = txt_password.getText().toString().trim();
                String confirmPassword = txt_confirmPassword.getText().toString().trim();
                if (!NIC.equals("") &&
                        !email.equals("") &&
                        !fullName.equals("") &&
                        !address.equals("") &&
                        !phone.equals("") &&
                        !dob.equals("") &&
                        !province.equals("") &&
                        !district.equals("") &&
                        !city.equals("") &&
                        !gender.equals("") &&
                        !password.equals("") &&
                        !confirmPassword.equals("")) {
                    if (!password.equals(confirmPassword)) {
                        showDialog(RegisterActivity.this, "Opps..!", "Password doesn't match", () -> {
                        });
                    } else {
                        register("", NIC, email, fullName, address, phone, dob, province, district, city, gender, confirmPassword);
                    }
                } else {
                    showDialog(RegisterActivity.this, "Opps..!", "All fields are required", () -> {
                    });
                }
            }
        });


    }


    private void register(String imagePath,  String NIC, String email, String fullName, String address, String phone, String dob, String province, String district, String city, String gender, String password) {

    }





    public static void showDialog(
            @NonNull final Context context,
            String heading,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible
        new MaterialAlertDialogBuilder(context)
                .setTitle(heading)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }
}