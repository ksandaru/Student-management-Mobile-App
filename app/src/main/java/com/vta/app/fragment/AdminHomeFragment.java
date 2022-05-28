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
import com.vta.app.R;

import com.vta.app.utils.AppSharedPreferences;
import com.vta.app.utils.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHomeFragment extends Fragment {

    private SharedPreferences sharedPre;
    private String apiBaseUrl = "";
    static FragmentManager fragmentManager;

    private TextView tvOwnerName, tvOwnerEmail, txtDetail2,tvDivisionName, tvRegNo, tvProvicne, tv_district, tvPersonCount;
    private MaterialCardView cardCources, cardStudents, cardInstructers;
    private CircleImageView userImage;
    private String myDivisionID;

    public AdminHomeFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "AdminHomeFragment";
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
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardCources = (MaterialCardView) getView().findViewById(R.id.cardCources);
        cardStudents = (MaterialCardView) getView().findViewById(R.id.cardStudents);
        cardInstructers = (MaterialCardView) getView().findViewById(R.id.cardInstructers);

        tvOwnerName = (TextView) getView().findViewById(R.id.tvOwnerName);
        tvOwnerEmail = (TextView) getView().findViewById(R.id.tvOwnerEmail);
        txtDetail2 = (TextView) getView().findViewById(R.id.txtDetail2);

        tvDivisionName = (TextView) getView().findViewById(R.id.tvDivisionName);
        tvRegNo = (TextView) getView().findViewById(R.id.tvRegNo);
        tvProvicne = (TextView) getView().findViewById(R.id.tvProvicne);
        tv_district = (TextView) getView().findViewById(R.id.tv_district);
        tvPersonCount = (TextView) getView().findViewById(R.id.tvPersonCount);

       //  userImage = (CircleImageView) getView().findViewById(R.id.image);

        cardCources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { gotoFragment(new CourceListingsFragment()); }
        });

        cardStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             gotoFragment(new StudentsFragment());
            }
        });

        cardInstructers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {gotoFragment(new InstructorsFragment());}
        });

        getUserAccount();
    }

    public void getUserAccount(){

//        tvOwnerName.setText(userProfRes.getFullName());
//        tvOwnerEmail.setText(userProfRes.getLogin().getEmail());
//
//        tvDivisionName.setText(userProfRes.getDivision().getName());
//        tvRegNo.setText(userProfRes.getDivision().getRegNumber());
//        tvProvicne.setText(userProfRes.getDivision().getProvince());
//        tv_district.setText(userProfRes.getDivision().getDistrict());
//        myDivisionID = userProfRes.getDivision().getId();
//        Picasso.get().load(userProfRes.getLogin().getAvatar()).into(userImage);

        if(AppSharedPreferences.getData(sharedPre,"user-id") != null){
            tvOwnerName.setText(AppSharedPreferences.getData(sharedPre,"user-email"));
            tvOwnerEmail.setText(AppSharedPreferences.getData(sharedPre,"user-role"));
            //  Picasso.get().load(userProfRes.getLogin().getAvatar()).into(userImage);
        }
    }

    private void gotoFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}