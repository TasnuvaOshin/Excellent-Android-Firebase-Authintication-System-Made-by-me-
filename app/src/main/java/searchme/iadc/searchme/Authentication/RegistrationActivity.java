package searchme.iadc.searchme.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import searchme.iadc.searchme.Model.UserDataModel;
import searchme.iadc.searchme.R;
import searchme.iadc.searchme.SystemFile.DeviceModule;
import searchme.iadc.searchme.SystemFile.EmailValidation;
import searchme.iadc.searchme.SystemFile.ShowNotification;

public class RegistrationActivity extends AppCompatActivity {
    public static final String TAG = "SearchMeDebug";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
 private RelativeLayout relativeLayout;
    /*
    Define Variables -include Firebase Auth -MacAddress For Password
     */

    private TextInputEditText UserNameEditText, UserEmailEditText;
    private String UserName, UserEmail, MacAddress;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        UserEmailEditText = findViewById(R.id.UserEmail);
        UserNameEditText = findViewById(R.id.UserName);
        MacAddress = DeviceModule.getMacAddress(); //Checkout The Package SystemFile
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        progressBar = findViewById(R.id.progressBar);
        relativeLayout = findViewById(R.id.registration_root);
    }


    /*

    this is the main Registration Process We will get the password Automitically and
    place it to password sector.
     */
    public void RegistrationProcess(View view) {
        progressBar.setVisibility(View.VISIBLE);
        hideKeyBoard();
        UserName = UserNameEditText.getText().toString();
        UserEmail = UserEmailEditText.getText().toString();
        if (TextUtils.isEmpty(UserName)) {


            UserNameEditText.requestFocus();
            UserNameEditText.setError("Please Insert Your Username");
            Log.d(TAG, "Username Not Given");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Username");

        } else if (TextUtils.isEmpty(UserEmail)) {


            UserEmailEditText.requestFocus();
            UserEmailEditText.setError("Please Insert Your Email Address");
            Log.d(TAG, "Useremail Not Given");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Email Address");



        } else {
            //1st of check this email is valid or not for more go to packasge SystemFile -EmailValidation
            if (EmailValidation.isEmailValid(UserEmail)) {
                //here everything is Valid So Now we Will Create Account and Password will be the Mac Address
                Log.d(TAG, "Input Fields are filled and working");
                // Toast.makeText(this, "" + MacAddress, Toast.LENGTH_SHORT).show();


                firebaseAuth.createUserWithEmailAndPassword(UserEmail, MacAddress).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //    Toast.makeText(RegistrationActivity.this, "Successfully Signup", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Successfully Registered");

                            UserDataModel userDataModel = new UserDataModel(UserName, UserEmail, MacAddress);
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(userDataModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.d(TAG, "Successfully Data Stored Info Firebase Database User");
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Log.d(TAG, String.valueOf(user));
                                    Log.d("user", String.valueOf(user));
                                     firebaseAuth.getCurrentUser().sendEmailVerification();
                                    //after this we will go to verification
                                       Intent intent = new Intent(RegistrationActivity.this,VerificationActivity.class);
                                       intent.putExtra("email",UserEmail);
                                       startActivity(intent);
                                       finish();

                                }
                            });


                        } else {

                            //  Toast.makeText(RegistrationActivity.this, "Problem With SignUp", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Problem With Signup CreateUserwithEmailandpassword");


                        }
                    }
                });


            } else {

                UserEmailEditText.requestFocus();
                UserEmailEditText.setError("Invalid Email Address");
                Log.d(TAG, "Useremail Not Valid");
                ShowNotification.setSnackBar(relativeLayout, "Invalid Email Address");



            }

        }
    }

    private void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // updateUI(currentUser); todo if user already loggedIN
    }

    public void AlreadyHasAccount(View view) {
        startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
        finish();
    }
}
