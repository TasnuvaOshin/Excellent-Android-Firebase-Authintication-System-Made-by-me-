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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import searchme.iadc.searchme.Model.QuickAccessModel;
import searchme.iadc.searchme.Model.VerificationModel;
import searchme.iadc.searchme.R;
import searchme.iadc.searchme.SystemFile.DeviceModule;
import searchme.iadc.searchme.SystemFile.ShowNotification;

public class VerificationActivity extends AppCompatActivity {
    private TextInputEditText textInputEditTextNickName, textInputEditTextHobby, textInputEditTextfriend;
    private String UserNickName, UserHobby, UserFriend, UserEmail, MacAddress;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, databaseReferenceforverification;
    public static final String TAG = "SearchMeDebug";
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        textInputEditTextNickName = findViewById(R.id.UserNickName);
        textInputEditTextHobby = findViewById(R.id.UserHobby);
        textInputEditTextfriend = findViewById(R.id.UserFriend);
        UserEmail = getIntent().getStringExtra("email");
        MacAddress = DeviceModule.getMacAddress();
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        String email = encodeUserEmail(UserEmail);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Quickaccess").child(email);
        databaseReferenceforverification = FirebaseDatabase.getInstance().getReference().child("Verification").child(email);
        relativeLayout = findViewById(R.id.verification_root);
        ShowNotification.setSnackBar(relativeLayout, "Please Verify Your Email");

    }

                /*
                need to use encode decode email for full authentication system
                static String encodeUserEmail(String userEmail) {
                return userEmail.replace(".", ",");
            }

            static String decodeUserEmail(String userEmail) {
                return userEmail.replace(",", ".");
            }
                 */
    public void StartVerification(View view) {
        /*
        this is the validation
         */

        hideKeyBorad();
        progressBar.setVisibility(View.VISIBLE);
        UserNickName = textInputEditTextNickName.getText().toString();
        UserHobby = textInputEditTextHobby.getText().toString();
        UserFriend = textInputEditTextfriend.getText().toString();
        if (TextUtils.isEmpty(UserNickName)) {
            textInputEditTextNickName.requestFocus();
            textInputEditTextNickName.setError("Enter Your Nick Name");
            ShowNotification.setSnackBar(relativeLayout, "Enter Your Nick Name");

        }
        if (TextUtils.isEmpty(UserHobby)) {

            textInputEditTextHobby.requestFocus();
            textInputEditTextHobby.setError("Enter Your Hobby");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Hobby");

        }
        if (TextUtils.isEmpty(UserFriend)) {

            textInputEditTextfriend.requestFocus();
            textInputEditTextfriend.setError("Enter Your Best Friend Name");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Best Friend Name");

        } else {
            VerificationModel verificationModel = new VerificationModel(UserNickName, UserHobby, UserFriend, UserEmail);

            databaseReferenceforverification.setValue(verificationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "Security Answer Done");
                        //now we will add this mac to quick access

                        QuickAccessModel quickAccessModel = new QuickAccessModel(UserEmail, MacAddress);
                        databaseReference.push().setValue(quickAccessModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Mac Address & Email Address also stored");
                                    progressBar.setVisibility(View.GONE);
                                    //send email verification
                                    sendVerificationEmail();
                                } else {

                                    Log.d(TAG, "Proble with Mac Address & Email Address  stored");

                                }
                            }
                        });


                    } else {

                        Log.d(TAG, "Security Answer Failed to Store");


                    }
                }
            });


        }


    }

    private void hideKeyBorad() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void sendVerificationEmail() {
        ShowNotification.setSnackBar(relativeLayout, "Answer Saved !");
        //email sent show in msg

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500);
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}
