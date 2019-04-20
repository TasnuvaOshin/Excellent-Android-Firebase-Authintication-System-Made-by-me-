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
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import searchme.iadc.searchme.Model.QuickAccessModel;
import searchme.iadc.searchme.R;
import searchme.iadc.searchme.SystemFile.DeviceModule;
import searchme.iadc.searchme.SystemFile.EmailValidation;
import searchme.iadc.searchme.SystemFile.ShowNotification;

public class ForgetPassword extends AppCompatActivity {
    private TextInputEditText EmailInput, textInputEditTextNickName, textInputEditTextHobby, textInputEditTextFirend;
    private String email, nickname, hobby, friend;
    public static final String TAG = "SearchMeDebug";
    private RelativeLayout relativeLayout, EmailVerifyLayout, QueryVerifyLayout;
    private DatabaseReference databaseReference, databaseReferenceverify, databaseReferenceQuickAccess;
    private String MacAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        relativeLayout = findViewById(R.id.forget_root);
        EmailVerifyLayout = findViewById(R.id.email_verify_layout);
        QueryVerifyLayout = findViewById(R.id.query_verify_layout);
        EmailInput = findViewById(R.id.UserEmail);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        textInputEditTextNickName = findViewById(R.id.UserNickName);
        textInputEditTextHobby = findViewById(R.id.UserHobby);
        textInputEditTextFirend = findViewById(R.id.UserFriend);
        databaseReferenceverify = FirebaseDatabase.getInstance().getReference().child("Verification");
        //now we will match with our db
        MacAddress = DeviceModule.getMacAddress(); //Checkout The Package SystemFile
        databaseReferenceQuickAccess = FirebaseDatabase.getInstance().getReference().child("Quickaccess");

    }
    /*


     */

    public void AddMacAddress(View view) {
        hideKeyBoard();
        nickname = textInputEditTextNickName.getText().toString();
        hobby = textInputEditTextHobby.getText().toString();
        friend = textInputEditTextFirend.getText().toString();
        email = EmailInput.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            textInputEditTextNickName.requestFocus();
            textInputEditTextNickName.setError("Enter your nickname");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Nickname");

        }
        if (TextUtils.isEmpty(hobby)) {
            textInputEditTextHobby.requestFocus();
            textInputEditTextHobby.setError("Enter your hobby");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Hobby");

        }
        if (TextUtils.isEmpty(friend)) {
            textInputEditTextFirend.requestFocus();
            textInputEditTextFirend.setError("Enter your best friend name");
            ShowNotification.setSnackBar(relativeLayout, "Enter your best friend name");

        } else {


            final String EmailAddress = encodeUserEmail(email);
            databaseReferenceverify.child(EmailAddress).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String dbnick = String.valueOf(dataSnapshot.child("nickname").getValue());
                        String dbhobby = String.valueOf(dataSnapshot.child("hobby").getValue());
                        String dbfriend = String.valueOf(dataSnapshot.child("friend").getValue());
                        if (dbnick.equals(nickname) && dbhobby.equals(hobby) && dbfriend.equals(dbfriend)) {
                            //information matched now we will add this device macaddress
                            QuickAccessModel QuickAccessModel = new QuickAccessModel(email, MacAddress);

                            databaseReferenceQuickAccess.child(EmailAddress).push().setValue(QuickAccessModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ShowNotification.setSnackBar(relativeLayout, "Successfully Password Saved");
                                    startActivity(new Intent(ForgetPassword.this, LoginActivity.class));
                                    finish();

                                }
                            });

                        } else {
                            ShowNotification.setSnackBar(relativeLayout, "Information is not Correct !");

                        }


                    } else {
                        ShowNotification.setSnackBar(relativeLayout, "Email Not Found !");

                        Log.d(TAG, "couldnot found the email in verification");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }


    public void CheckRequest(View view) {
        /*
        Checking this email is valid or not and this email is in our db or not
         */
        hideKeyBoard();

        email = EmailInput.getText().toString();

        if (TextUtils.isEmpty(email)) {
            EmailInput.requestFocus();
            EmailInput.setError("Enter Your Email Address");
            ShowNotification.setSnackBar(relativeLayout, "Enter your Email Address");

        } else {
            if (EmailValidation.isEmailValid(email)) {
                //checking email available in our database or not
                databaseReference.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //yes its exits

                            EmailVerifyLayout.setVisibility(View.GONE);
                            QueryVerifyLayout.setVisibility(View.VISIBLE);
                            ShowNotification.setSnackBar(relativeLayout, "Answer the Following Question");


                        } else {

                            EmailInput.requestFocus();
                            EmailInput.setError("Invalid Email !");
                            ShowNotification.setSnackBar(relativeLayout, "Invalid Email");


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            } else {


                EmailInput.requestFocus();
                EmailInput.setError("Invalid Email !");
                ShowNotification.setSnackBar(relativeLayout, "Invalid Email");

            }


        }
    }

    private void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public void LogIn(View view) {

        startActivity(new Intent(ForgetPassword.this, LoginActivity.class));
        finish();
    }

    public void SignUp(View view) {
        startActivity(new Intent(ForgetPassword.this, RegistrationActivity.class));
        finish();
    }
}








