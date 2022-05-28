package com.vta.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vta.app.R;
import com.vta.app.activity.AdminPageContainerActivity;
import com.vta.app.activity.InstructorPageContainerActivity;
import com.vta.app.activity.LoginActivity;
import com.vta.app.activity.StudentPageContainerActivity;
import com.vta.app.adapter.CourcesRecyclerviewAdapter;
import com.vta.app.model.Cource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vta.app.model.CourceWithKey;
import com.vta.app.utils.AppSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class CourceListingsFragment extends Fragment {

    private ChipGroup chip_group_choice;
    private Chip chip_location, chip_facilities, chip_gender;
    private ImageSlider sliderPostImages;
    private CircularProgressIndicator progress_circular;
    private LinearLayout cardLayout;
    private SwipeRefreshLayout swipeContainer;
    private TextView noListingsItemsLabel;
    private Chip chip_add_cource;

    private SharedPreferences sharedPre;
    private static Dialog appCustomDialog;
    private ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;

    private HorizontalScrollView bg_btn_add;

    private static RecyclerView courceRecycler;
    private static CourcesRecyclerviewAdapter courcesRecyclerviewAdapter;

    private static Context context;
    private static Activity activity;

    private static final String  COURCES= "cources";
    private Uri image_data;

    private List<CourceWithKey> courceList = new ArrayList<CourceWithKey>();

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ActivityResultLauncher<Intent> activityResultLaunch;

    public CourceListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        activity =getActivity();
        progressDialog = new ProgressDialog(context);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        fragmentManager = getActivity().getSupportFragmentManager();

        initActivityResultLaunch();
        initCourceAddDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cource_listing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courceRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

        //  cardLayout = (LinearLayout) getView().findViewById(R.id.cardLayout);
        swipeContainer = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        noListingsItemsLabel = (TextView) getView().findViewById(R.id.noListingsItemsLabel);
        chip_add_cource = (Chip) getView().findViewById(R.id.chip_add_cource);
        bg_btn_add = (HorizontalScrollView) getView().findViewById(R.id.bg_btn_add);

        //Image slider : Docs https://github.com/denzcoskun/ImageSlideshow
        sliderPostImages= (ImageSlider) getView().findViewById(R.id.sliderPostImages);

        progress_circular = (CircularProgressIndicator) getView().findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);

        manageAddNewPostButton();

        List<SlideModel> slideModels= new ArrayList<>();
        //TODO: Can add more than one images as bellow
        slideModels.add(new SlideModel("https://www.vtasl.gov.lk/public/uploads/slider/Fri-Jun-2020-1593163485-679615200.png"));
        slideModels.add(new SlideModel("https://www.vtasl.gov.lk/public/uploads/slider/hirusha.png"));
        slideModels.add(new SlideModel("https://www.vtasl.gov.lk/public/uploads/slider/IMG-20211230-WA0001.jpg"));
        slideModels.add(new SlideModel("https://www.vtasl.gov.lk/public/uploads/slider/vta%20final1.jpg"));
        sliderPostImages.setImageList(slideModels, true);

        chip_add_cource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });

        getAllCources();
    }

    private void manageAddNewPostButton(){
        if(AppSharedPreferences.getData(sharedPre,"user-id") != null){
            switch (AppSharedPreferences.getData(sharedPre, "user-role")){
                case "ADMIN":
                case "INSTRUCTOR":
                    bg_btn_add.setVisibility(View.VISIBLE);
                    break;
                case "STUDENT":
                    bg_btn_add.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void getAllCources(){
        progressDialog.setTitle("Fetching Cources...");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(COURCES);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progress_circular.setVisibility(View.VISIBLE);
                courceList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //  System.out.println("=========================Key " + dataSnapshot.getKey());
                    Cource cource = dataSnapshot.getValue(Cource.class);

                    CourceWithKey courceWithKey = new CourceWithKey(dataSnapshot.getKey(), cource.getName(), cource.getNvq_level(), cource.getLanguage(), cource.getDuration(), cource.getImageUrl());

                    courceList.add(courceWithKey);
                    //dataSnapshot.getKey();
                }

                if(courceList.size() > 0){
                    setCourceRecycler();
                }else{
                    Toast.makeText(context, "No Cources to show", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    progress_circular.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                progress_circular.setVisibility(View.GONE);
            }
        });
    }

    private void saveCource(Cource cource) {
        uploadMedia(cource);
    }

    private void insertCourceDetails(Cource cource){
        FirebaseDatabase.getInstance().getReference().child(COURCES)  //. push() <- auto key  // child <- custom key
                .push() // TODO : set key as Auth user's UID , userprofile can uniquely identify now.
                .setValue(cource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Cource added successfully!", Toast.LENGTH_LONG).show();
                          appCustomDialog.dismiss();
                          //TODO: realod card list
                          getAllCources();
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

    private void initCourceAddDialog() {
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_cource);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        TextInputEditText txt_name = appCustomDialog.findViewById(R.id.txt_name);
        TextInputEditText txt_nvq_level = appCustomDialog.findViewById(R.id.txt_nvq_level);
        TextInputEditText txt_language = appCustomDialog.findViewById(R.id.txt_language);
        TextInputEditText txt_duration = appCustomDialog.findViewById(R.id.txt_duration);
        Chip chip_add_image = appCustomDialog.findViewById(R.id.chip_add_image);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

        // labelFormHeading.setText("Add New ...");

        chip_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
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
                if( txt_name.equals("") ||
                        txt_nvq_level.getText().toString().trim().equals("") ||
                        txt_language.getText().toString().trim().equals("") ||
                        txt_duration.getText().toString().trim().equals("")
                ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{

                    if(image_data == null){
                        showDialog(getActivity(),"Opps..!", "Choose an Image", ()->{});
                    }else{
                        //TODO: Display Confirm Dialog
                        showConfirmDialog(getActivity(),() -> {
                            //TODO: confirmCall
                            Cource cource = new Cource();
                            cource.setName(txt_name.getText().toString().trim());
                            cource.setNvq_level(txt_nvq_level.getText().toString().trim());
                            cource.setLanguage(txt_language.getText().toString().trim());
                            cource.setImageUrl("");

                            saveCource(cource);

                            txt_name.setText("");
                            txt_nvq_level.setText("");
                            txt_language.setText("");
                            txt_duration.setText("");
                            image_data = null;
                        }, ()->{
                            //TODO: cancelCall
                            appCustomDialog.dismiss();
                            txt_name.setText("");
                            txt_nvq_level.setText("");
                            txt_language.setText("");
                            txt_duration.setText("");
                            image_data = null;
                        });
                    }

                }
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
    }


    private void selectFile() {
       // Intent intent = new Intent();
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //   intent.setType("application/pdf");
        //   intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(gallery);
    }

    private void uploadMedia(Cource cource) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading media..");
        progressDialog.show();

        StorageReference reference = storageReference.child("cource-images/"+ System.currentTimeMillis()+ ".jpg");

        reference.putFile(image_data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        //TODO : Save URL in Database
                        cource.setImageUrl(url.toString());
                        insertCourceDetails(cource);
                      //  Media mediaFile = new Media(txtFileName.getText().toString(), url.toString());
                      // databaseReference.child(databaseReference.push().getKey()).setValue(mediaFile);
                      //   Toast.makeText(UploadPDFActivity.this, "File Uploaded!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress= (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded: " + (int)progress+"%");
            }
        });
    }

    private void initActivityResultLaunch(){
        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == -1) {
                           // uploadMedia(result.getData().getData());
                            image_data =result.getData().getData();

                        } else if (result.getResultCode() == 0) { // Not selected
                            // TODO : do stuff when file not Selected
                        }
                    }
                });
    }

    public static void listItemOnClick(String key){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new CourceMaterialsFragment(key));
        fragmentTransaction.commit();
    }

    private void  setCourceRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        courceRecycler.setLayoutManager(layoutManager);
        courcesRecyclerviewAdapter = new CourcesRecyclerviewAdapter(context, courceList);
        courceRecycler.setAdapter(courcesRecyclerviewAdapter);
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