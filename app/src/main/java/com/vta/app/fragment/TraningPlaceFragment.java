package com.vta.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vta.app.R;
import com.vta.app.activity.LoginActivity;
import com.vta.app.model.AuthUserRole;
import com.vta.app.model.Student;
import com.vta.app.model.TraningPlace;
import com.vta.app.utils.Navigation;

public class TraningPlaceFragment extends Fragment {

    private DatabaseReference mDatabase;
    private static final String  STUDENTS= "students";
    private static final String  TRANING_PL= "std_traning_place";
    private String studentObjKey = "";

    private Button btn_add_training;
    private TextView tv_fullName,tv_nic,tv_Email,tv_cource, tv_org_name,  tv_org_address, tv_time_duration,  tv_start_date, tv_end_date, tv_tplace_status;

    private static Context context;
    private static Activity activity;

    private static Dialog appCustomDialog;
    private ProgressDialog progressDialog;

    private DatePickerDialog datePickerDialog_stratD;
    private DatePickerDialog datePickerDialog_endD;

    private String email;

    public TraningPlaceFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "TraningPlaceFragment";
    }

    public TraningPlaceFragment(String email) {
        Navigation.currentScreen = "TraningPlaceFragment";
        this.email =email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initTrPlaceAddDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traning_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_fullName =  (TextView) getView().findViewById(R.id.tv_fullName);
        tv_nic =  (TextView) getView().findViewById(R.id.tv_nic);
        tv_Email =  (TextView) getView().findViewById(R.id.tv_Email);
        tv_cource =  (TextView) getView().findViewById(R.id.tv_cource);

        tv_org_name=  (TextView) getView().findViewById(R.id.tv_org_name);
        tv_org_address=  (TextView) getView().findViewById(R.id.tv_org_address);
        tv_time_duration=  (TextView) getView().findViewById(R.id.tv_time_duration);
        tv_start_date=  (TextView) getView().findViewById(R.id.tv_start_date);
        tv_end_date=  (TextView) getView().findViewById(R.id.tv_end_date);
        tv_tplace_status=  (TextView) getView().findViewById(R.id.tv_tplace_status);

        tv_org_name.setVisibility(View.GONE);
        tv_org_address.setVisibility(View.GONE);
        tv_time_duration.setVisibility(View.GONE);
        tv_start_date.setVisibility(View.GONE);
        tv_end_date.setVisibility(View.GONE);

        btn_add_training=  (Button) getView().findViewById(R.id.btn_add_training);

        btn_add_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });
        getStudentAccount(this.email);
    }

    private void getStudentAccount(String email){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(STUDENTS).orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    student = dataSnapshot.getValue(Student.class);
                    studentObjKey= dataSnapshot.getKey();
                }
                if(student != null){
                    populateUIForStudent(student);
                    getTraningPlace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTraningPlace(){
        //TODO: TraningPlaceas are lsited under UID Node
        mDatabase.child(TRANING_PL).child(studentObjKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    TraningPlace traningPlace = task.getResult().getValue(TraningPlace.class);
                    if(traningPlace != null){
                        tv_tplace_status.setText("Training Place details:");
                        btn_add_training.setVisibility(View.GONE);

                        tv_org_name.setVisibility(View.VISIBLE);
                        tv_org_address.setVisibility(View.VISIBLE);
                        tv_time_duration.setVisibility(View.VISIBLE);
                        tv_start_date.setVisibility(View.VISIBLE);
                        tv_end_date.setVisibility(View.VISIBLE);

                        tv_org_name.setText("Organization: "+traningPlace.getOrg_name());
                        tv_org_address.setText("Address: "+traningPlace.getOrg_address());
                        tv_time_duration.setText("Duration: "+traningPlace.getTime_duration());
                        tv_start_date.setText("Start: "+traningPlace.getStart_date());
                        tv_end_date.setText("End: "+traningPlace.getEnd_date());
                    }else{
                        tv_tplace_status.setText("No Training Place Assigned yet.");
                        tv_org_name.setVisibility(View.GONE);
                        tv_org_address.setVisibility(View.GONE);
                        tv_time_duration.setVisibility(View.GONE);
                        tv_start_date.setVisibility(View.GONE);
                        tv_end_date.setVisibility(View.GONE);
                    }
                }
                else {
                    // Log.e("firebase", "Error getting data", task.getException());
                  //  Toast.makeText(LoginActivity.this, "Data fetching failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void populateUIForStudent(Student student){
        tv_fullName.setText(student.getFullName());
        tv_nic.setText("NIC: " + student.getNIC());
        tv_Email.setText("Email: " + student.getEmail());
        tv_cource.setText("Cource: " + student.getCource());
        // Picasso.get().load(userProfRes.getLogin().getAvatar()).into(imageUser);
    }


    private void saveTraningPlace(TraningPlace TraningPlace) {

        FirebaseDatabase.getInstance().getReference().child(TRANING_PL)  //. push() <- auto key  // child <- custom key
                .child(studentObjKey) // TODO : set key as Auth user's UID , userprofile can uniquely identify now.
                .setValue(TraningPlace)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                         Toast.makeText(context, "Traning Place Assigned successfully!", Toast.LENGTH_LONG).show();
                          appCustomDialog.dismiss();
                        getTraningPlace();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void initTrPlaceAddDialog() {
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_tr_palce);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);

        TextInputEditText tv_org_name = appCustomDialog.findViewById(R.id.tv_org_name);
        TextInputEditText tv_org_address = appCustomDialog.findViewById(R.id.tv_org_address);
        TextInputEditText tv_time_duration = appCustomDialog.findViewById(R.id.tv_time_duration);
        TextInputEditText txt_start_date = appCustomDialog.findViewById(R.id.txt_start_date);
        TextInputEditText txt_end_data = appCustomDialog.findViewById(R.id.txt_end_data);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

        //labelFormHeading.setText("Add New Student");

        initDatePicker_startD(txt_start_date);
        initDatePicker_endDate(txt_end_data);

        txt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_stratD.show();
            }
        });

        txt_start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                datePickerDialog_stratD.show();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txt_end_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_endD.show();
            }
        });

        txt_end_data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                datePickerDialog_endD.show();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( tv_org_name.equals("") ||
                        tv_org_address.getText().toString().trim().equals("") ||
                        tv_time_duration.getText().toString().trim().equals("") ||
                        txt_start_date.getText().toString().trim().equals("") ||
                        txt_end_data.getText().toString().trim().equals("")
                ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{

                        //TODO: Display Confirm Dialog
                        showConfirmDialog(getActivity(),() -> {
                            //TODO: confirmCall
                            TraningPlace traningPlace = new TraningPlace();

                            traningPlace.setOrg_name(tv_org_name.getText().toString().trim());
                            traningPlace.setOrg_address(tv_org_address.getText().toString().trim());
                            traningPlace.setTime_duration(tv_time_duration.getText().toString().trim());
                            traningPlace.setStart_date(txt_start_date.getText().toString().trim());
                            traningPlace.setEnd_date(txt_end_data.getText().toString().trim());

                            // appCustomDialog.dismiss();

                            saveTraningPlace(traningPlace);

                            tv_org_name.setText("");
                            tv_org_address.setText("");
                            tv_time_duration.setText("");
                            txt_start_date.setText("");
                            txt_end_data.setText("");
                        }, ()->{
                            //TODO: cancelCall
                            appCustomDialog.dismiss();

                            tv_org_name.setText("");
                            tv_org_address.setText("");
                            tv_time_duration.setText("");
                            txt_start_date.setText("");
                            txt_end_data.setText("");
                        });
                }
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
    }


    private void initDatePicker_startD(TextInputEditText txt_start_date )
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);

                txt_start_date.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog_stratD = new DatePickerDialog(context, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }


    private void initDatePicker_endDate(TextInputEditText txt_end_data )
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                txt_end_data.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog_endD = new DatePickerDialog(context, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
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