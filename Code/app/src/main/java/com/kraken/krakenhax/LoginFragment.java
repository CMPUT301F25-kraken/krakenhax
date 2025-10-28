package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    public Button signup;
    public Button login;
    public EditText unText;
    public EditText pwdText;
    public Button guest;

    public LoginFragment(){

    }

    //public static LoginFragment newInstance(Profile profile) {
      //  LoginFragment fragment = new LoginFragment();
        //Bundle args = new Bundle();
        //args.putSerializable("profile", profile);
        //fragment.setArguments(args);
        //return fragment;
    //}
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = getLayoutInflater().inflate(R.layout.fragment_login, container, false);
        signup = view.findViewById(R.id.signup_button);
        login = view.findViewById(R.id.login_button);
        unText = view.findViewById(R.id.UsernameEditText);
        pwdText = view.findViewById(R.id.PasswordEditText);
        guest = view.findViewById(R.id.guest_button);




        return view;

    }

}

