package com.moksh.pager.activities;

import static com.moksh.pager.utilities.Constants.KEY_COLLECTION_USERS;
import static com.moksh.pager.utilities.Constants.KEY_EMAIL;
import static com.moksh.pager.utilities.Constants.KEY_IMAGE;
import static com.moksh.pager.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.moksh.pager.utilities.Constants.KEY_NAME;
import static com.moksh.pager.utilities.Constants.KEY_PASSWORD;
import static com.moksh.pager.utilities.Constants.KEY_USER_ID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.moksh.pager.R;
import com.moksh.pager.databinding.ActivitySignUpBinding;
import com.moksh.pager.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding the view to the ActivitySingUp XML file
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        initialising the preferenceManger object to store variables
        preferenceManager = new PreferenceManager(getApplicationContext());

//        method() to call all the listeners
        setListeners();
    }

    private void setListeners(){

//        if the user wants to go back to the signIn Activity then he can click on sinngIn Text to go back
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

//        Checking if the users entered data is correct if so then we call signUp() method
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()) signUp();
        });

//        to pick up the image from user device
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        loading(true);

//        Firestore object being created
        FirebaseFirestore database = FirebaseFirestore.getInstance();

//        HashMap being created to store the user data
        HashMap<String,Object> user = new HashMap<>();
        user.put(KEY_NAME,binding.inputName.getText().toString());
        user.put(KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(KEY_PASSWORD,binding.inputPassword.getText().toString());
        user.put(KEY_IMAGE,encodedImage);

//        adding the HashMap data to database
        database.collection(KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);

//                    putting the data in shared memory/SP file
                    preferenceManager.putBoolean(KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(KEY_IMAGE,encodedImage);

//                    after uploading data to database
//                    launching MainActivity
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

//    converting the bitmap data into bytes and returning it
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

//    declaring pickImage to pickImage from users device
//    then converting the data into bytecode and storing it in String encodedImage
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK)
                {
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.imageText.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

//    isValidSignUpDetails() method is used to check whether the entered details are valid or not
//    it return boolean true of false value
    private boolean isValidSignUpDetails(){
        if(encodedImage == null){
            showToast("Select profile image");
            return false;
        }
        else if(binding.inputName.getText().toString().trim().isEmpty())
        {
            showToast("Enter name");
            return false;
        }
        else if(binding.inputEmail.getText().toString().trim().isEmpty())
        {
            showToast("Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }
        else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }
        else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Confirm your password");
            return false;
        }
        else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            showToast("Password & confirm password must be same");
            return false;
        }
        else return  true;
    }

    private void loading(boolean isLoading){
        if(isLoading)
        {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}











