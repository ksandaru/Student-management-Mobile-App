package com.vta.app.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vta.app.R;

import com.vta.app.model.AuthUserRole;
import com.vta.app.utils.AppSharedPreferences;

public class LoginActivity extends AppCompatActivity {

    /* TODO :
       TODO :   Tools -> Firebase -> Authentication -> Authentication using a custom authentication system

       TODO:  ->step 2) connect to firebase , add the Firebase auth SDK

       TODO:  ->step 3) goto firebase in browser and select project
       TODO  build -> Authentication get started -> choose email passsword -> enable email password -> ok
      */

    private Button loginBtn, btnFogerPassword, btnGotoRegister;
    private TextInputEditText txtUsername, txtPassword;
    private ProgressDialog progressDialog;

    private Context context;
    private SharedPreferences sharedPre;
    private String apiBaseUrl="";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String  USER_ROLE= "auth-user-role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Avoid Status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        context = this;
        sharedPre = getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(context);

        /**--------------Hooks------------------------------------------*/
        loginBtn = findViewById(R.id.btnUserLogin);
        btnFogerPassword = findViewById(R.id.btnFogerPassword);
        btnGotoRegister = findViewById(R.id.btnGotoRegister);

        txtUsername = (TextInputEditText) findViewById(R.id.txtUsername);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_text =  txtUsername.getText().toString().trim();
                String password_text =  txtPassword.getText().toString().trim();
                if(username_text.equals("") || password_text.equals(""))
                {
                    showDialog(context, "Make sure credentials are not Empty", ()->{});
                }
                else{
                 //   startActivity(new Intent(LoginActivity.this, AdminPageContainerActivity.class));
                 //   finish();// Cant came back here after visiting Home page
                    loginRequest(username_text, password_text);
                }
            }
        });

        btnFogerPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPassowrdActivity.class));
            }
        });

        btnGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        if(AppSharedPreferences.getData(sharedPre, "user-id")!=null){
            if(!AppSharedPreferences.getData(sharedPre, "user-id").equals("")){
                //Redirect to Home
                if(AppSharedPreferences.getData(sharedPre, "user-role")!= null)
                {
                    switch (AppSharedPreferences.getData(sharedPre, "user-role")){
                        case "ADMIN":
                            startActivity(new Intent(LoginActivity.this, AdminPageContainerActivity.class));
                            finish();// Cant came back here after visiting Home page
                            break;
                        case "STUDENT":
                            startActivity(new Intent(LoginActivity.this, StudentPageContainerActivity.class));
                            finish();// Cant came back here after visiting Home page
                            break;
                        case "INSTRUCTOR":
                            startActivity(new Intent(LoginActivity.this, InstructorPageContainerActivity.class));
                            finish();// Cant came back here after visiting Home page
                            break;
                    }
                }
            }
        }
    }

    private void loginRequest(String email, String password){
        progressDialog.setTitle("Login...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MSG", "Login successs");
                        //    Toast.makeText(Login.this, "Authentication successs.", Toast.LENGTH_LONG).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserAccount(user.getUid());
                          //  startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("MSG", "signIn failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void getUserAccount(String uid){
        mDatabase.child(USER_ROLE).child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    AuthUserRole user = task.getResult().getValue(AuthUserRole.class);
                    if(user != null){
                       // Toast.makeText(LoginActivity.this, "Role :"+user.getRole(), Toast.LENGTH_LONG).show();
                       postLogin(user,  uid);
                    }
                }
                else {
                    progressDialog.dismiss();
                    // Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(LoginActivity.this, "Data fetching failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void postLogin(AuthUserRole user, String uid){

        try {
            //Save User'Data
            AppSharedPreferences.saveData(sharedPre,"user-id", uid);
            AppSharedPreferences.saveData(sharedPre,"user-email", user.getEmail());
            AppSharedPreferences.saveData(sharedPre,"user-role", user.getRole());
            progressDialog.dismiss();
            //Redirect to Home
            switch (user.getRole()){
                case "INSTRUCTOR":
                    startActivity(new Intent(LoginActivity.this, InstructorPageContainerActivity.class));
                    finish();// Cant came back here after visiting Home page
                    break;
                case "STUDENT":
                    startActivity(new Intent(LoginActivity.this, StudentPageContainerActivity.class));
                    finish();// Cant came back here after visiting Home page
                    break;
                case "ADMIN":
                    startActivity(new Intent(LoginActivity.this, AdminPageContainerActivity.class));
                    finish();// Cant came back here after visiting Home page
                    break;
            }
        }catch (Exception e){
            System.out.println("_================== Exception appSharedPreferences ==================");
            e.printStackTrace();
        }

    }

    public static  void showDialog(
            @NonNull final Context context,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle("Opps!")
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