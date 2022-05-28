package com.vta.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vta.app.R;
import com.vta.app.databinding.ActivityPageContainerAdminBinding;
import com.vta.app.fragment.AdminHomeFragment;
import com.vta.app.fragment.CourceListingsFragment;
import com.vta.app.fragment.InstructorsFragment;
import com.vta.app.fragment.MyProfileFragment;
import com.vta.app.fragment.StudentHomeFragment;
import com.vta.app.utils.Navigation;

public class StudentPageContainerActivity extends AppCompatActivity {

    ActivityPageContainerAdminBinding binding;
    private SharedPreferences sharedPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPre = getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        //TODO:::::::::::::::::::::::IMPORTANT::::::::::::::::::::::
        /**
         * Make sure enable viewBingg at build.gradle file
         *     buildFeatures{
         *         viewBinding true
         *     }
         */
        binding = ActivityPageContainerAdminBinding.inflate(getLayoutInflater());
        //Avoid Status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        //Start up view
        replaceFragment(new StudentHomeFragment());


        //Avoid changing of selected icon's color
        binding.bottomNavigationView.setItemIconTintList(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new StudentHomeFragment());
                    break;
                case R.id.cources:
                    replaceFragment(new CourceListingsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new MyProfileFragment());
                    break;
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        //TODO: Avoid Go to previouse activity - > comment -> super.onBackPressed();
        //super.onBackPressed();
        switch (Navigation.currentScreen){
            case "TraningPlaceFragment":
            case "StudentsFragment":
            case "InstructorsFragment":
                replaceFragment(new StudentHomeFragment());
                break;
            case "CourceMaterialsFragment":
                replaceFragment(new CourceListingsFragment());
                break;
            case "Fragment2":
                // super.onBackPressed();
                break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}