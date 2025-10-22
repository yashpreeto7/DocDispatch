package online.ppriyanshu26.docdispatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etOtp;
    private Button btnSendOtp, btnVerifyOtp;
    private TextView tvError;
    private FirebaseAuth auth;
    private String storedVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        auth = FirebaseAuth.getInstance();

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etOtp = findViewById(R.id.etOtp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvError = findViewById(R.id.tvError);

        btnSendOtp.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText().toString().trim();
            if (!phone.startsWith("+")) {
                showError("Phone must start with country code (e.g., +91...)");
                return;
            }
            sendOTP(phone);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (otp.length() != 6) {
                showError("Please enter a 6-digit OTP");
                return;
            }
            verifyOTP(otp);
        });
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        showError("OTP send failed: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        storedVerificationId = verificationId;
                        etOtp.setVisibility(View.VISIBLE);
                        btnVerifyOtp.setVisibility(View.VISIBLE);
                        showError("OTP sent successfully!");
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String otp) {
        if (storedVerificationId == null) {
            showError("Verification ID missing. Please resend OTP.");
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(storedVerificationId, otp);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showError("✅ Login successful!");
                        // Go to main screen
                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // optional: prevent back to OTP screen
                    } else {
                        showError("Login failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    private void showError(String message) {
        tvError.setText(message);
        // Optional: Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}