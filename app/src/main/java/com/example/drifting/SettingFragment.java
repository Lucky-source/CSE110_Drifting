package com.example.drifting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drifting.ui.login.ForgotPasswordActivity;
import com.example.drifting.ui.login.LoginActivity;


import com.example.drifting.ui.login.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static  String name = null;
    private static  String age = null;
    private static  String email = null;
    private static  String gender = null;
    private static  String country = null;

    private Spinner spinner;
    private static final String[] paths = {"item 1", "item 2", "item 3"};

    protected View mView;

    Button editButton;
    /*
    Button editUsernameButton;
    Button editEmailButton;
    Button editAgeButton;
    Button editCountryButton;
    Button editGenderButton;
     */
    Button logout_button;
    Button reset_password;
    EditText nameEdit;
    EditText des_Edit;
    EditText email_Edit;
    EditText gen_Edit;
    EditText age_Edit;
    EditText coun_Edit;
    Button settingbutton;
    ImageView profileImage;
    Button changeProfileImage;


    String currentUserId;
    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    FirebaseUser firebaseUser;






    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

//        mAuth = FirebaseAuth.getInstance();
//        currentUserId = mAuth.getCurrentUser().getUid();


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name);
        outState.putString("gender", gender);
        outState.putString("country", country);
        outState.putString("email", email);
        outState.putString("age", age);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
//
//        mView = rootView;
//        return rootView;
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        ViewSwitcher nameSwitcher = getView().findViewById(R.id.my_switcher);
        TextView nameTV = nameSwitcher.findViewById(R.id.username_view);

        ViewSwitcher genderSwitcher = getView().findViewById(R.id.my_switcher_gender);
        TextView genTV = genderSwitcher.findViewById(R.id.gender_text_view);

        ViewSwitcher age_switcher = getView().findViewById(R.id.my_switcher_age);
        TextView ageTV = age_switcher.findViewById(R.id.age_text_view);

        ViewSwitcher email_switcher = getView().findViewById(R.id.my_switcher_email);
        TextView email_TV = email_switcher.findViewById(R.id.email_text_view);


        ViewSwitcher coun_switcher = getView().findViewById(R.id.my_switcher_country);
        TextView coun_TV = coun_switcher.findViewById(R.id.country_text_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UserRef = FirebaseDatabase.getInstance().getReference().child("user").child(firebaseUser.getUid());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("user_name").getValue().toString();
                gender = snapshot.child("user_gender").getValue().toString();
                country = snapshot.child("user_country").getValue().toString();
                age = snapshot.child("user_age").getValue().toString();
                email = snapshot.child("user_email").getValue().toString();

                nameTV.setText(name);
                genTV.setText(gender);
                ageTV.setText(age);
                email_TV.setText(email);
                coun_TV.setText(country);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editButton = getView().findViewById(R.id.editbutton);
        /*
        editUsernameButton = getView().findViewById(R.id.username_edit_button);
        editEmailButton = getView().findViewById(R.id.edit_email_button);
        editAgeButton = getView().findViewById(R.id.edit_age_button);
        editCountryButton = getView().findViewById(R.id.edit_country_button);
        editGenderButton = getView().findViewById(R.id.edit_gender_button);
         */
        nameEdit = getView().findViewById(R.id.username_edit);
        des_Edit = getView().findViewById(R.id.description_text_edit);
        email_Edit = getView().findViewById(R.id.email_text_edit);
        gen_Edit = getView().findViewById(R.id.gender_text_edit);
        age_Edit = getView().findViewById(R.id.age_text_edit);
        coun_Edit = getView().findViewById(R.id.country_text_edit);
        settingbutton = getView().findViewById(R.id.settingbutton);
        profileImage = getView().findViewById(R.id.profile_image);
        changeProfileImage = getView().findViewById(R.id.change_avatar);
        logout_button = getView().findViewById(R.id.log_out_button);
        reset_password = getView().findViewById(R.id.reset_password_button);

        //get the spinner from the xml.
        Spinner dropdown = getView().findViewById(R.id.spinner1);
        String[] items = new String[]{"Not visible to others", "Visible to friends only", "Visible to all"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, R.id.dropdown_item, items);
        dropdown.setAdapter(adapter);


//        if(savedInstanceState != null) {
//
//

//            ViewSwitcher nameSwitcher = getView().findViewById(R.id.my_switcher);
//            TextView nameTV = nameSwitcher.findViewById(R.id.username_view);
//
//            ViewSwitcher genderSwitcher = getView().findViewById(R.id.my_switcher_gender);
//            TextView genTV = genderSwitcher.findViewById(R.id.gender_text_view);
//
//            ViewSwitcher age_switcher = getView().findViewById(R.id.my_switcher_age);
//            TextView ageTV = age_switcher.findViewById(R.id.age_text_view);
//
//            ViewSwitcher email_switcher = getView().findViewById(R.id.my_switcher_email);
//            TextView email_TV = email_switcher.findViewById(R.id.email_text_view);
//
//
//            ViewSwitcher coun_switcher = getView().findViewById(R.id.my_switcher_country);
//            TextView coun_TV = coun_switcher.findViewById(R.id.country_text_view);
//
//            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//            UserRef = FirebaseDatabase.getInstance().getReference().child("user").child(firebaseUser.getUid());
//
//            UserRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    name = snapshot.child("user_name").getValue().toString();
//                    gender = snapshot.child("user_gender").getValue().toString();
//                    country = snapshot.child("user_country").getValue().toString();
//                    age = snapshot.child("user_age").getValue().toString();
//                    email = snapshot.child("user_email").getValue().toString();
//
//                    nameTV.setText(name);
//                    genTV.setText(gender);
//                    ageTV.setText(age);
//                    email_TV.setText(email);
//                    coun_TV.setText(country);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });




//        }




        changeProfileImage.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openGallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                openGallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                openGallaryIntent.setType("image/*");
                startActivityForResult(openGallaryIntent, 1000);
            }
        });

        editButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSwitcher nameSwitcher = getView().findViewById(R.id.my_switcher);
                nameSwitcher.showNext();
                TextView nameTV = nameSwitcher.findViewById(R.id.username_view);
                nameTV.setText(nameEdit.getText().toString());
                name = nameTV.toString();

                /*ViewSwitcher des_switcher = getView().findViewById(R.id.my_switcher_description);
                des_switcher.showNext();
                TextView des_TV = des_switcher.findViewById(R.id.description_text_view);
                des_TV.setText(des_Edit.getText().toString());
                 */

                ViewSwitcher email_switcher = getView().findViewById(R.id.my_switcher_email);
                email_switcher.showNext();
                TextView email_TV = email_switcher.findViewById(R.id.email_text_view);
                email_TV.setText(email_Edit.getText().toString());
                email = email_TV.toString();

                ViewSwitcher gen_switcher = getView().findViewById(R.id.my_switcher_gender);
                gen_switcher.showNext();
                TextView gen_TV = gen_switcher.findViewById(R.id.gender_text_view);
                gen_TV.setText(gen_Edit.getText().toString());
                gender = gen_TV.toString();

                ViewSwitcher age_switcher = getView().findViewById(R.id.my_switcher_age);
                age_switcher.showNext();
                TextView age_TV = age_switcher.findViewById(R.id.age_text_view);
                age_TV.setText(age_Edit.getText().toString());
                age = age_TV.toString();

                ViewSwitcher coun_switcher = getView().findViewById(R.id.my_switcher_country);
                coun_switcher.showNext();
                TextView coun_TV = coun_switcher.findViewById(R.id.country_text_view);
                coun_TV.setText(coun_Edit.getText().toString());
                country = coun_TV.toString();


            }
        });

        logout_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        reset_password.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        /*
        editUsernameButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSwitcher nameSwitcher = getView().findViewById(R.id.my_switcher);
                nameSwitcher.showNext();
                TextView nameTV = nameSwitcher.findViewById(R.id.username_view);
                nameTV.setText(nameEdit.getText().toString());
                nameData = nameTV;
            }
        });

        editEmailButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSwitcher email_switcher = getView().findViewById(R.id.my_switcher_email);
                email_switcher.showNext();
                TextView email_TV = email_switcher.findViewById(R.id.email_text_view);
                email_TV.setText(email_Edit.getText().toString());
            }
        });

        editAgeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ViewSwitcher age_switcher = getView().findViewById(R.id.my_switcher_age);
                age_switcher.showNext();
                TextView age_TV = age_switcher.findViewById(R.id.age_text_view);
                age_TV.setText(age_Edit.getText().toString());
            }
        });

        editCountryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ViewSwitcher coun_switcher = getView().findViewById(R.id.my_switcher_country);
                coun_switcher.showNext();
                TextView coun_TV = coun_switcher.findViewById(R.id.country_text_view);
                coun_TV.setText(coun_Edit.getText().toString());
            }
        });

        editGenderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ViewSwitcher gen_switcher = getView().findViewById(R.id.my_switcher_gender);
                gen_switcher.showNext();
                TextView gen_TV = gen_switcher.findViewById(R.id.gender_text_view);
                gen_TV.setText(gen_Edit.getText().toString());
            }
        });



         */

        /*Intent intent = new Intent(getActivity(), SettingFragment.class);

        settingbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode  == Activity.RESULT_OK){
                Uri imageUri = data.getData();
//                CropImage.activity()
//                        .setGuidelines(CropImageView, Gui)
                profileImage.setImageURI(imageUri);
            }
        }
    }
}