package com.vta.app.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vta.app.R;
import com.vta.app.model.Student;
import com.vta.app.utils.AppSharedPreferences;
import com.vta.app.utils.Navigation;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentHomeFragment extends Fragment {

    private SharedPreferences sharedPre;
    private String apiBaseUrl = "";
    static FragmentManager fragmentManager;

    private TextView tvOwnerName, tvOwnerEmail, tvMyCource, tvMyStdID, tv_myNic;
    private MaterialCardView cardMaterials, cardCources;
    private CircleImageView userImage;

    private static final String  STUDENTS= "students";

    public StudentHomeFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "InstructorHomeFragment";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardCources = (MaterialCardView) getView().findViewById(R.id.cardCources);
        cardMaterials = (MaterialCardView) getView().findViewById(R.id.cardMaterials);

        tvOwnerName = (TextView) getView().findViewById(R.id.tvOwnerName);
        tvOwnerEmail = (TextView) getView().findViewById(R.id.tvOwnerEmail);
        tvMyCource = (TextView) getView().findViewById(R.id.tvMyCource);
        tvMyStdID = (TextView) getView().findViewById(R.id.tvMyStdID);
        tv_myNic = (TextView) getView().findViewById(R.id.tv_myNic);

       //  userImage = (CircleImageView) getView().findViewById(R.id.image);

        cardCources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new CourceListingsFragment());
            }
        });

        cardMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new CourceListingsFragment());
            }
        });

        getUserAccount();
    }

    public void getUserAccount(){

        if(AppSharedPreferences.getData(sharedPre,"user-id") != null){
            String email = AppSharedPreferences.getData(sharedPre,"user-email");
            tvOwnerName.setText(email);
            tvOwnerEmail.setText(AppSharedPreferences.getData(sharedPre,"user-role"));
            //  Picasso.get().load(userProfRes.getLogin().getAvatar()).into(userImage);
            getStudentAccount(email);
        }
    }

    private void getStudentAccount(String email){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(STUDENTS).orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    student = student = dataSnapshot.getValue(Student.class);
                    System.out.println("========================================= ");
                    System.out.println(student.getFullName());
                }
                if(student != null)
                {
                    tvMyCource.setText("My Cource: " + student.getCource());
                    tvMyStdID.setText("Student ID: " + student.getStudentID());
                    tv_myNic.setText("NIC: " + student.getNIC());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gotoFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}