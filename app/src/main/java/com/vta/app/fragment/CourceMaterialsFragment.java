package com.vta.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vta.app.R;
import com.vta.app.adapter.CourceMaterialsRecyclerviewAdapter;
import com.vta.app.model.Cource;
import com.vta.app.model.CourceMaterial;
import com.vta.app.utils.AppSharedPreferences;
import com.vta.app.utils.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CourceMaterialsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String  STUDENTS= "students";
    private static final String  COURCES= "cources";
    private static final String  MATERIALS= "cource_materials";

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private static Context context;
    private static Activity activity;

    private static Dialog appCustomDialog;
    private ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;
    private SharedPreferences sharedPre;

    private static RecyclerView userRecycler;
    private static CourceMaterialsRecyclerviewAdapter courceMaterialsRecyclerviewAdapter;

    private static ContentResolver contentResolver;

    private CharSequence search="";
    private TextInputEditText txtSearch;
    private TextView tv_cource_name;
    private Button btnAddNew;
    private ImageView img_cource;

    private String selectedCource ="";
    private String key ="";
    private Uri image_data;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ActivityResultLauncher<Intent> activityResultLaunch;

    public CourceMaterialsFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "CourceMaterialsFragment";
    }

    public CourceMaterialsFragment(String key) {
        // Required empty public constructor
        Navigation.currentScreen = "CourceMaterialsFragment";
        this.key = key;
        System.out.println("========================================= KEY " + key);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        activity =getActivity();
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        //Initialize
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fragmentManager = getActivity().getSupportFragmentManager();
        progressDialog = new ProgressDialog(context);
        contentResolver= context.getContentResolver();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(MATERIALS);
        PRDownloader.initialize(context);

        initActivityResultLaunch();
        initCourceMaterialAddDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cource_materials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);
        userRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);
        btnAddNew = (Button) getView().findViewById(R.id.btnAddNew);
        tv_cource_name = (TextView) getView().findViewById(R.id.tv_cource_name);
        img_cource = (ImageView) getView().findViewById(R.id.img_cource);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                courceMaterialsRecyclerviewAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });
        manageAddNewPostButton();
        getCource();
        getAllMaterials();

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
    }

    private void getAllMaterials() {

        progressDialog.setTitle("Fetching cource materials...");
        progressDialog.show();

       List<CourceMaterial> courceMaterialList = new ArrayList<>();

     //  DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MATERIALS);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(MATERIALS).orderByChild("courceKey").equalTo(key); //TODO get only materiasl under current cource by key
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                courceMaterialList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                  //  System.out.println("=========================Key " + dataSnapshot.getKey());

                    CourceMaterial courceMaterial = dataSnapshot.getValue(CourceMaterial.class);
                    courceMaterialList.add(courceMaterial);
                    //dataSnapshot.getKey();
                }

                if(courceMaterialList.size() > 0){
                    setUserRecycler(courceMaterialList);
                }else{
                    Toast.makeText(context, "No materials to show", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }


    private void manageAddNewPostButton(){
        if(AppSharedPreferences.getData(sharedPre,"user-id") != null){
            switch (AppSharedPreferences.getData(sharedPre, "user-role")){
                case "ADMIN":
                case "INSTRUCTOR":
                    btnAddNew.setVisibility(View.VISIBLE);
                    break;
                case "STUDENT":
                    btnAddNew.setVisibility(View.GONE);
                    break;
            }
        }
    }

    // Call this function form Adatapter
    public static void DownlaodBtnClick(String downloadUrl){
            if(Build.VERSION.SDK_INT>23){
              if(hasPermistion()){
                  startDownlload(downloadUrl);
              }else{


              }
            } else {
                startDownlload(downloadUrl);
            }
    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[] { permission }, requestCode);
        }
        else {
           // Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    static  int downloadId = 0;

    private static void startDownlload(String downloadUrl) {

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Downloading...");
        dialog.setMessage("Preparing...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        dialog.dismiss();
                        PRDownloader.cancel(downloadId);
                    }
        });
//        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        dialog.show();

    // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(context, config);

         downloadId = PRDownloader.download(downloadUrl, directory(), fileName(downloadUrl))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        dialog.setTitle("Download Stared");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(context, "Download Canceled", Toast.LENGTH_LONG).show();
                    }
                })
                .setOnProgressListener(new com.downloader.OnProgressListener() {

                    @Override
                    public void onProgress(Progress progress) {
                      Long progressPrecent = progress.currentBytes * 100 / progress.currentBytes;
                        dialog.setProgress(progressPrecent.intValue());
                        dialog.setMessage(toMB(progress.currentBytes) + "/" + toMB(progress.totalBytes));
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        dialog.setTitle("Download Completed");
                        // Change button text cancel to ok
                        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText("ok");
                    }

                    @Override
                    public void onError(Error error) {
                        dialog.setTitle("Download Error!");
                       // Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static String directory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }


    private static String fileName(String url){
        return URLUtil.guessFileName(url,url, contentResolver.getType(Uri.parse(url)));
    }

    private static boolean hasPermistion(){
        return ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }

    private static String toMB(long bytes){
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    private void getCource(){
        mDatabase.child(COURCES).child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Cource cource = task.getResult().getValue(Cource.class);
                    if(cource != null){
                        // Toast.makeText(LoginActivity.this, "Role :"+user.getRole(), Toast.LENGTH_LONG).show();
                        tv_cource_name.setText(cource.getName());
                        Picasso.get().load(cource.getImageUrl()).into(img_cource);
                    }
                }
                else {
                    // Log.e("firebase", "Error getting data", task.getException());
                  //  Toast.makeText(LoginActivity.this, "Data fetching failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initCourceMaterialAddDialog() {
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_cource_material);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

       // TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        TextInputEditText txt_title = appCustomDialog.findViewById(R.id.txt_title);
        TextInputEditText txt_desc = appCustomDialog.findViewById(R.id.txt_desc);
        Chip chip_choose_file = appCustomDialog.findViewById(R.id.chip_choose_file);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

        // labelFormHeading.setText("Add New ...");

        chip_choose_file.setOnClickListener(new View.OnClickListener() {
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
                if( txt_title.equals("") ||
                        txt_desc.getText().toString().trim().equals("")
                ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{

                    if(image_data == null){
                        showDialog(getActivity(),"Opps..!", "Choose an Image", ()->{});
                    }else{
                        //TODO: Display Confirm Dialog
                        showConfirmDialog(getActivity(),() -> {
                            //TODO: confirmCall
                            CourceMaterial courceMaterial = new CourceMaterial();
                            courceMaterial.setTitle(txt_title.getText().toString().trim());
                            courceMaterial.setDescription(txt_desc.getText().toString().trim());
                            courceMaterial.setDocumentUrl("");
                            courceMaterial.setCourceKey(key);

                            saveCourceMaterial(courceMaterial);

                            txt_title.setText("");
                            txt_desc.setText("");
                            image_data = null;
                        }, ()->{
                            //TODO: cancelCall
                            appCustomDialog.dismiss();
                            txt_title.setText("");
                            txt_desc.setText("");
                            image_data = null;
                        });
                    }

                }
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
    }

    private void saveCourceMaterial(CourceMaterial courceMaterial) {
        uploadMedia(courceMaterial);
    }

    private void selectFile() {
         Intent intent = new Intent();
       // Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
           intent.setType("application/pdf");
           intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(intent);
    }

    private void uploadMedia(CourceMaterial courceMaterial) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading media..");
        progressDialog.show();

        StorageReference reference = storageReference.child(MATERIALS +"/"+ System.currentTimeMillis()+ ".pdf");

        reference.putFile(image_data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        //TODO : Save URL in Database
                        courceMaterial.setDocumentUrl(url.toString());
                        insertCourceDetails(courceMaterial);
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


    private void insertCourceDetails(CourceMaterial courceMaterial){
        progressDialog.setTitle("Saving...");
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference().child(MATERIALS)  //. push() <- auto key  // child <- custom key
                .push() // TODO : set key as Auth user's UID , userprofile can uniquely identify now.
                .setValue(courceMaterial)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Cource added successfully!", Toast.LENGTH_LONG).show();
                        appCustomDialog.dismiss();
                        //TODO: realod card list
                        getAllMaterials();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
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


    private static void  setUserRecycler(List<CourceMaterial> courceMaterialList){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        userRecycler.setLayoutManager(layoutManager);
        courceMaterialsRecyclerviewAdapter = new CourceMaterialsRecyclerviewAdapter(context, courceMaterialList);
        userRecycler.setAdapter(courceMaterialsRecyclerviewAdapter);
    }

    public static  void showItemActionDialog(
            @NonNull final Context context,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable neutralCallback
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("ACTIONS")
                .setMessage("Choose an Action")
                .setCancelable(true)
                .setPositiveButton("Training Place?",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
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