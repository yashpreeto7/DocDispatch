// LauncherActivity.java
package online.ppriyanshu26.docdispatch;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in → go to MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // No user → show OTP screen
            startActivity(new Intent(this, OtpActivity.class));
            finish();
        }
    }
}