package searchme.iadc.searchme.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import searchme.iadc.searchme.Home.HomeActivity;
import searchme.iadc.searchme.R;
import searchme.iadc.searchme.SystemFile.DeviceModule;
import searchme.iadc.searchme.SystemFile.EmailValidation;
import searchme.iadc.searchme.SystemFile.ShowNotification;

public class LoginActivity extends AppCompatActivity {
    /*
    Define Variables
     */
    public static final String TAG = "SearchMeDebug";
    private FirebaseAuth firebaseAuth;
    private TextInputEditText EmailInput;
    private String UserEmail, MacAddress;
    private DatabaseReference databaseReference, databaseReferencequickaccess; //this for checking email address from our db
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        EmailInput = findViewById(R.id.UserEmail);
        MacAddress = DeviceModule.getMacAddress(); //Checkout The Package SystemFile
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        progressBar = findViewById(R.id.progressBar);
        relativeLayout = findViewById(R.id.login_root);
        databaseReferencequickaccess = FirebaseDatabase.getInstance().getReference().child("Quickaccess");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        floatingActionButton = findViewById(R.id.refresh);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Refresh Working", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //we will check the user already logged in or not
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // updateUI(currentUser); todo if user already loggedIN
    }

    public void LogInProcess(View view) {

        progressBar.setVisibility(View.VISIBLE);
        hideKeyBorad();
        /*
        here is the full login process
        we have MacAddress
        Now We will Check the Email
         */

        UserEmail = EmailInput.getText().toString();

        if (TextUtils.isEmpty(UserEmail)) {

            EmailInput.requestFocus();
            EmailInput.setError("Please Enter Your Email Address");
            progressBar.setVisibility(View.GONE);
            ShowNotification.setSnackBar(relativeLayout, "Enter your Email Address");
        } else {

            if (EmailValidation.isEmailValid(UserEmail)) {
                Log.d(TAG, "Now Everything is fine for log in");

                UserExits(UserEmail);
                //now 2nd step for checking and finally logged in the user

            } else {


                EmailInput.requestFocus();
                EmailInput.setError("Invalid Email!");
                progressBar.setVisibility(View.GONE);
                ShowNotification.setSnackBar(relativeLayout, "Invalid Email");

            }


        }


    }

    private void hideKeyBorad() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void UserExits(String userEmail) {
        //this is a important step in this step we are checking email and logged in
        databaseReference.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                /*
                need to use encode decode email for full authentication system
                static String encodeUserEmail(String userEmail) {
                return userEmail.replace(".", ",");
            }

            static String decodeUserEmail(String userEmail) {
                return userEmail.replace(",", ".");
            }
                 */
                    Log.d(TAG, "this user Exits");
                    //check this email macaddress there or not


                    String email = encodeUserEmail(UserEmail); //encode needed
                    Query query = databaseReferencequickaccess.child(email).orderByChild("macaddress").equalTo(MacAddress);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.d(TAG, "User Logged In");
                                if (mAuth.getCurrentUser() == null) {
                                    ShowNotification.setSnackBar(relativeLayout, "Logged In");
                                    progressBar.setVisibility(View.GONE);

                                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                    finish();
                                } else {
                                    CheckEmailVerification();
                                }
                            } else {

                                Log.d(TAG, "this mac user not Exits");
                                EmailInput.requestFocus();
                                EmailInput.setError("You are Not a Registered User! ");
                                progressBar.setVisibility(View.GONE);
                                ShowNotification.setSnackBar(relativeLayout, "User not Exits");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    Log.d(TAG, "this user not Exits");
                    EmailInput.requestFocus();
                    EmailInput.setError("You Not a Registered User! ");
                    progressBar.setVisibility(View.GONE);
                    ShowNotification.setSnackBar(relativeLayout, "User not Exits");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "db erru", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "This QUery MEhtod Not working");
            }
        });
    }


    private void CheckEmailVerification() {
        //checking email verifiction
        Task<Void> usertask = mAuth.getCurrentUser().reload();
            usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        // user is verified, so you can finish this activity or send user to activity which you want.
                        //todo go to home
                        ShowNotification.setSnackBar(relativeLayout, "Logged In");
                        progressBar.setVisibility(View.GONE);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                intent.putExtra("UserEmail",UserEmail);
                                startActivity(intent);
                                finish();
                            }
                        }, 1500);



                    } else {
                        // email is not verified, so just prompt the message to the user and restart this activity.

                        Log.d(TAG, "Email is not verified");
                        ShowNotification.setSnackBar(relativeLayout, "Your Email is not Verified");
                        progressBar.setVisibility(View.GONE);
                        floatingActionButton.show();
                    }
                }
            });



    }

    public void GoToSignUp(View view) {
        Log.d(TAG, "Registration Will Open For Request");
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        finish();
    }

    public void PasswordSetUp(View view) {

        startActivity(new Intent(LoginActivity.this, ForgetPassword.class));
        finish();
    }


    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public void FreeService(View view) {
        //TODO Implement later
    }
}
