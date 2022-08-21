package com.moksh.pager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moksh.pager.R;
import com.moksh.pager.databinding.ActivitySignInBinding;
import com.moksh.pager.utilities.Constants;
import com.moksh.pager.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

//    ViewBinding is true so there is class relative the XML Acitivity
//    creating the binding view of ActivitySingIn
    private ActivitySignInBinding binding;

//    Creating a private object of Preference Manager
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Initialisation of binding object
//        LayoutInflater class is used to instansiate the contents of layout XML files into their corresponding view objects
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());

//        Checking if the user is already signed in
//        if a user already signed in the getBoolean will return true
//        and if it's true we directly launch the main activity
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
//          Intent class is used to communicate between to activities mainly used to call another activity from one
//          passing the current activity and the activity needs to open to intents objectt
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);

//            startActivity() method is called to start the MainActivity
            startActivity(intent);
            finish();
        }

//        if the user is not signed in we set the view on the scrren using setContentView
        setContentView(binding.getRoot());

//        setListers() method is used to listene different activities on the screen
        setListeners();
    }

    private void setListeners(){

//        if the user is signing up for the first time then he clicks on creat new account
//        and redirected to signup activity
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));

//        if the user is signing in again then we just check if the credentials entered are correct or not
        binding.buttonSignIn.setOnClickListener(v -> {

//            to check the credentials we call a method isValidSignIndDetails() defined below which returns a
//            boolean true if the credentials are correct else false
            if(isValidSignInDetails()){

//                if credentials are right and we call a function signIn() to singIn into the account
                signIn();
            }
        });
    }

//    showToast() method is created to show the toast if required
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

//    Implimentation of singIn() method
    private void signIn(){

//        while we signIn we can call the method() loading and can make screen loading until our works gets completed
        loading(true);

//        creating the object of FirebaseFirestore where the data is stored
        FirebaseFirestore database = FirebaseFirestore.getInstance();

//        Since the data is stored in the form of collection
//        so we are calling the collection() method of FirebaseFirestore
//        we are retrieving the data on the basis of condition like
//        where the email and password should be equal to the email entered by the user if so the we get the data
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()

//                if the data got feteched we call the on completeListener() method to do our task
                .addOnCompleteListener(task ->{

//                    if the data fetch is successfull and it's non empty we do our work
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0){

//                          getting the data of the user
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

//                        as the user is now signedInd so we putBoolean as true with singedIN
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);

//                        Storing the id of the user of lateral uses
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());

//                        storing the name of the user
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));

//                        Storing the byte data of the Image from the data base into SP
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));

//                        and on storing everything we simply goes to our MainActivity
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);

//                        adding flags so that we can't come back over this activity while pressing back button from MainActivity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{

//                        if we won't able to signIn due to some technical erroe we simply stop loading
//                        and throw a toast saying unable to sign in
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }

//    loading() method to show the loading image on button while we singing into our account
    private void loading(boolean isLoading){
        if(isLoading){
             binding.buttonSignIn.setVisibility(View.INVISIBLE);
             binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

//    Implementation of isValidSignInDetails()
    private boolean isValidSignInDetails(){

//        if the email entered is empty then we return false and generate a toast
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        }

//        if the email address pattern isn't correct then the method() return false and generate a toast
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }

//        if the entered password is empty we return false and generate a toast
        else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }

//        and if nothing goes wrong we simply return true
        else return true;
    }
}














