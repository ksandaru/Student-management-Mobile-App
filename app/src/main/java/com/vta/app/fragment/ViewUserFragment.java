package com.vta.app.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vta.app.R;
import com.vta.app.model.Instructor;
import com.vta.app.model.Student;
import com.vta.app.utils.Navigation;


public class ViewUserFragment extends Fragment {

    static String apiBaseUrl = "";
    private SharedPreferences sharedPre;
    private static Context context;

    String userType = "";

    private static final String  STUDENTS= "students";
    private static final String  INSTRUCTORS= "instructors";

    private TextView tv_fullName,tv_nic,tv_Email,tv_studentID,tv_cource,tv_dob,tv_address,tv_contactNo;
    private ImageView imageUser;

    public ViewUserFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "ViewUserFragment";
    }

    public ViewUserFragment(String email, String userType) {
        Navigation.currentScreen = "ViewUserFragment";
        this.userType = userType;

        if(userType.equals(STUDENTS)){
            Navigation.currentScreen = "InstructorsFragment";
            getStudentAccount(email);
        }else if(userType.equals(INSTRUCTORS)){
            Navigation.currentScreen = "StudentsFragment";
            getInstructorAccount(email);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_fullName =  (TextView) getView().findViewById(R.id.tv_fullName);
        tv_nic =  (TextView) getView().findViewById(R.id.tv_nic);
        tv_Email =  (TextView) getView().findViewById(R.id.tv_Email);
        tv_studentID =  (TextView) getView().findViewById(R.id.tv_studentID);
        tv_cource =  (TextView) getView().findViewById(R.id.tv_cource);
        tv_dob =  (TextView) getView().findViewById(R.id.tv_dob);
        tv_address =  (TextView) getView().findViewById(R.id.tv_address);
        tv_contactNo =  (TextView) getView().findViewById(R.id.tv_contactNo);

        imageUser= (ImageView) getView().findViewById(R.id.imageUser);
    }

    public void populateUIForStudent(Student student){

        tv_fullName.setText(student.getFullName());
        tv_nic.setText("NIC: " + student.getNIC());
        tv_Email.setText("Email: " + student.getEmail());
        tv_studentID.setText("Student ID: " + student.getStudentID());
        tv_cource.setText("Cource: " + student.getCource());
        tv_dob.setText("DOB: " + student.getDob());
        tv_address.setText("Address: " + student.getAddress());
        tv_contactNo.setText("Contact No: " + student.getContactNo());
     // Picasso.get().load(userProfRes.getLogin().getAvatar()).into(imageUser);
    }
    public void populateUIForInstructor(Instructor student){

        tv_fullName.setText(student.getFullName());
        tv_nic.setText("NIC: " + student.getNIC());
        tv_Email.setText("Email: " + student.getEmail());
        tv_studentID.setText("");
        tv_cource.setText("Cource: " + student.getCource());
        tv_dob.setText("");
        tv_address.setText("");
        tv_contactNo.setText("Contact No: " + student.getContactNo());
        // Picasso.get().load(userProfRes.getLogin().getAvatar()).into(imageUser);
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
                    System.out.println("========================================= ");
                    System.out.println(student.getFullName());
                }
                if(student != null)
                    populateUIForStudent(student);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getInstructorAccount(String email){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(INSTRUCTORS).orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Instructor instructor = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    instructor = instructor = dataSnapshot.getValue(Instructor.class);
                    System.out.println("========================================= ");
                    System.out.println(instructor.getFullName());
                }
                if(instructor != null)
                    populateUIForInstructor(instructor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}