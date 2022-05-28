package com.vta.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.vta.app.R;
import com.vta.app.adapter.InstructorsRecyclerviewAdapter;
import com.vta.app.model.AuthUserRole;
import com.vta.app.model.Cource;
import com.vta.app.model.Instructor;
import com.vta.app.model.SearchModel;
import com.vta.app.model.Student;
import com.vta.app.utils.Navigation;

import java.util.ArrayList;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class InstructorsFragment extends Fragment {
        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;
        private static final String  INSTRUCTORS= "instructors";
        private static final String  USER_ROLE= "auth-user-role";
        private static final String  COURCES= "cources";

        private static Context context;
        private static Activity activity;

        private static FragmentManager fragmentManager;
        private static Dialog appCustomDialog;
        private ProgressDialog progressDialog;

        private static RecyclerView userRecycler;
        private static InstructorsRecyclerviewAdapter instructorsRecyclerviewAdapter;
        private CharSequence search="";
        private TextInputEditText txtSearch;

        private TextView labelListCount;
        private Button btnAddUser, btnRefreshList;

        ArrayList<SearchModel> courceNameList = new ArrayList<>();
        private String selectedCource ="";

        public InstructorsFragment() {
            // Required empty public constructor
            Navigation.currentScreen = "InstructorsFragment";
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            context = getContext();
            activity =getActivity();
            //Initialize
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            fragmentManager = getActivity().getSupportFragmentManager();
            progressDialog = new ProgressDialog(context);

            getAllCources();
            initInstructorAddDialog();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_instructors, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);
            userRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

            labelListCount = (TextView) getView().findViewById(R.id.labelListCount);
            txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);
            btnRefreshList = (Button) getView().findViewById(R.id.btnRefreshList);
            btnAddUser = (Button) getView().findViewById(R.id.btnAdd);

            txtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    instructorsRecyclerviewAdapter.getFilter().filter(charSequence);
                    search = charSequence;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            btnAddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appCustomDialog.show();
                }
            });

            btnRefreshList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAllInstructors();
                }
            });

            getAllInstructors();
        }

        private void initInstructorAddDialog() {
            // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
            appCustomDialog = new Dialog(getContext());
            // >> TODO: Add Dialog Layout
            appCustomDialog.setContentView(R.layout.dialog_layout_add_instructor);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
            }
            appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            appCustomDialog.setCancelable(false);
            appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);

            TextInputEditText txt_fullName = appCustomDialog.findViewById(R.id.txt_fullName);
            TextInputEditText txt_nic = appCustomDialog.findViewById(R.id.txt_nic);
            TextInputEditText txt_email = appCustomDialog.findViewById(R.id.txt_email);
            TextInputEditText txt_phone = appCustomDialog.findViewById(R.id.txt_phone);
            Chip chip_choose_cource = appCustomDialog.findViewById(R.id.chip_choose_cource);
            // for Drop down
            // AutoCompleteTextView autoCompleteTextView = appCustomDialog.findViewById(R.id.autoCompleteTv);

            Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
            Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

            labelFormHeading.setText("Add New Instructor");


            btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appCustomDialog.dismiss();
                }
            });

            chip_choose_cource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Todo: Ref-> https://github.com/mirrajabi/search-dialog
                    new SimpleSearchDialogCompat(activity, "Search...",
                            "What are you looking for...?", null, courceNameList,
                            new SearchResultListener<SearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SearchModel item, int position) {
                                    // If filtering is enabled, [position] is the index of the item in the filtered result, not in the unfiltered source
                                    //   Log.d("_location_", item.getTitle().toString() );
                                    chip_choose_cource.setText(item.getTitle());
                                    selectedCource = item.getTitle();
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });

            //....... Drop down  <- work file , but I replace with choosen dialog
//        ArrayAdapter<String> courceAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item,courceList);
//        autoCompleteTextView.setAdapter(courceAdapter);
//
//        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedCource = parent.getItemAtPosition(position).toString();
//            }
//        });
            //...... Drop down

            btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( txt_fullName.equals("") ||
                            txt_nic.getText().toString().trim().equals("") ||

                            txt_email.getText().toString().trim().equals("") ||
                            txt_phone.getText().toString().trim().equals("")
                    ){
                        showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                    }else{
                        if(selectedCource.equals("")){
                            showDialog(getActivity(),"Opps..!", "Select a Cource", ()->{});
                        }
                        else{

                            //TODO: Display Confirm Dialog
                            showConfirmDialog(getActivity(),() -> {
                                //TODO: confirmCall

                                Instructor instructor = new Instructor();
                                instructor.setFullName(txt_fullName.getText().toString().trim());
                                instructor.setEmail(txt_email.getText().toString().trim());
                                instructor.setNIC(txt_nic.getText().toString().trim());
                                instructor.setCource(selectedCource);
                                instructor.setContactNo(txt_phone.getText().toString().trim());

                                // appCustomDialog.dismiss();

                                registerInstructor(instructor);

                                txt_fullName.setText("");
                                txt_nic.setText("");
                                txt_email.setText("");
                                selectedCource="";
                                txt_phone.setText("");

                            }, ()->{
                                //TODO: cancelCall
                                appCustomDialog.dismiss();

                                txt_fullName.setText("");
                                txt_nic.setText("");
                                selectedCource="";
                                txt_email.setText("");
                                txt_phone.setText("");

                            });
                        }
                    }
                }
            });
            // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
        }


    private void getAllCources(){
        courceNameList.clear();
        //   List<CourceWithKey> courceWithKeyArrayList = new ArrayList<CourceWithKey>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(COURCES);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //    courceWithKeyArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //  System.out.println("=========================Key " + dataSnapshot.getKey());
                    Cource cource = dataSnapshot.getValue(Cource.class);

                    //  CourceWithKey courceWithKey = new CourceWithKey(dataSnapshot.getKey(), cource.getName(), cource.getNvq_level(), cource.getLanguage(), cource.getDuration(), cource.getImageUrl());
                    courceNameList.add(new SearchModel(cource.getName()));
                    //   courceWithKeyArrayList.add(courceWithKey);
                    //dataSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // progressDialog.dismiss();
            }
        });
    }

        private void getAllInstructors() {

            progressDialog.setTitle("Fetching instructors");
            progressDialog.show();

            List<Instructor> instructorsList = new ArrayList<>();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(INSTRUCTORS);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    instructorsList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Instructor student = dataSnapshot.getValue(Instructor.class);
                        instructorsList.add(student);
                        //dataSnapshot.getKey();
                    }

                    if(instructorsList.size() > 0){
                        setUserRecycler(instructorsList);
                        labelListCount.setText(instructorsList.size()+"");
                    }else{
                        Toast.makeText(context, "No instructors to show", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
        }


        public static void studentsListItemOnClick(String email){
            showItemActionDialog(context, ()->{
                //TODO: DELETE
                showConfirmDialog(context, ()->{

                    // deleteItem(gn_division.getId());

                }, ()->{});
            }, ()->{
                //TODO: UPDATE
                // showUpdateDialog(gn_division);
            }, ()->{
                //TODO: View
                //TODO: Goto View User Fragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new ViewUserFragment(email, INSTRUCTORS));
                fragmentTransaction.commit();
            });
        }

        private void registerInstructor(Instructor instructor){
            //TODO: Default password for Student : student@123
            mAuth.createUserWithEmailAndPassword(instructor.getEmail(), "inst@123")
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("MSG", "createUserWithEmail:success");
                                FirebaseUser studentAuth = mAuth.getCurrentUser();
                                saveUserRole(studentAuth, instructor);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MSG", "createUserWithEmail:failure", task.getException());
                                //  Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

        private void saveUserRole(FirebaseUser studentAuth, Instructor instructor) {

            AuthUserRole authUserRole = new AuthUserRole(studentAuth.getEmail(), "INSTRUCTOR");

            FirebaseDatabase.getInstance().getReference().child(USER_ROLE)  //. push() <- auto key  // child <- custom key
                    .child(studentAuth.getUid()) // TODO : set key as Auth user's UID , userprofile can uniquely identify now.
                    .setValue(authUserRole)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Toast.makeText(context, "U-Profile added successfully!", Toast.LENGTH_LONG).show();
                            //  appCustomDialog.dismiss();
                            saveUserProfile(studentAuth, instructor);
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

        private void saveUserProfile(FirebaseUser studentAuth, Instructor instructor) {

            FirebaseDatabase.getInstance().getReference().child(INSTRUCTORS)  //. push() <- auto key  // child <- custom key
                    .child(studentAuth.getUid()) // TODO : set key as Auth user's UID , userprofile can uniquely identify now.
                    .setValue(instructor)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Instructor Registered successfully!", Toast.LENGTH_LONG).show();
                            appCustomDialog.dismiss();
                            getAllInstructors();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, "Register failed!", Toast.LENGTH_LONG).show();
                        }
                    });
        }


        private static void  setUserRecycler(List<Instructor> studentListList){
            // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
            RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            userRecycler.setLayoutManager(layoutManager);
            instructorsRecyclerviewAdapter = new InstructorsRecyclerviewAdapter(context, studentListList);
            userRecycler.setAdapter(instructorsRecyclerviewAdapter);
        }

        public static  void showItemActionDialog(
                @NonNull final Context context,
                @Nullable Runnable confirmCallback,
                @Nullable Runnable cancelCallback,
                @Nullable Runnable neutralCallback
        ) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("ACTIONS")
                    .setMessage("Choose an Action")
                    .setCancelable(true)
                    .setPositiveButton("DELETE",
                            (dialog2, which) -> {
                                dialog2.dismiss();
                                if (confirmCallback != null) confirmCallback.run();
                            })
                    .setNegativeButton("MODIFY",
                            (dialog2, which) -> {
                                dialog2.dismiss();
                                if (cancelCallback != null) cancelCallback.run();
                            })
                    .setNeutralButton("VIEW",
                            (dialog2, which) -> {
                                dialog2.dismiss();
                                if (neutralCallback != null) neutralCallback.run();
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