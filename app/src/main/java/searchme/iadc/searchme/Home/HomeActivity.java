package searchme.iadc.searchme.Home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import searchme.iadc.searchme.R;

public class HomeActivity extends AppCompatActivity {
    private String UserEmail; //we need this as its our Primary key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UserEmail = getIntent().getStringExtra("UserEmail");
        Toast.makeText(this, "Welcome"+UserEmail, Toast.LENGTH_SHORT).show();

    }
}
