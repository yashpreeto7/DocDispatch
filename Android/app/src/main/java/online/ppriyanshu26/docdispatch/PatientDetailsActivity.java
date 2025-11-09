package online.ppriyanshu26.docdispatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PatientDetailsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_PHONE = "phone_number";

    private TextInputEditText etName, etAge, etTemperature, etDays, etTreatment, etDisease;
    private RadioGroup rgGender;
    private CheckBox cbContagious;
    private Button btnSubmit;
    private String savedPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        savedPhoneNumber = prefs.getString(KEY_PHONE, null);

        if (savedPhoneNumber == null) {
            Toast.makeText(this, "Phone number not found. Redirecting to login...", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();

        btnSubmit.setOnClickListener(v -> submitPatientDetails());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etTemperature = findViewById(R.id.etTemperature);
        etDays = findViewById(R.id.etDays);
        etTreatment = findViewById(R.id.etTreatment);
        etDisease = findViewById(R.id.etDisease);
        rgGender = findViewById(R.id.rgGender);
        cbContagious = findViewById(R.id.cbContagious);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void sendToServer(String number, String name, String age, String gender, String temperature, String days, String contagious, String treatment, String disease) {
        runOnUiThread(() -> Toast.makeText(PatientDetailsActivity.this, "Submitting...", Toast.LENGTH_SHORT).show());

        new Thread(() -> {
            try {
                String url = "http://192.168.1.13:5050/";

                OkHttpClient client = new OkHttpClient();

                JSONObject json = new JSONObject();
                json.put("phone", number);
                json.put("name", name);
                json.put("age", age);
                json.put("gender", gender);
                json.put("temperature", temperature);
                json.put("days", days);
                json.put("contagious", contagious);
                json.put("treatment", treatment);
                json.put("disease", disease);

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(PatientDetailsActivity.this, "✅ Submitted successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PatientDetailsActivity.this, "❌ Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                        finish();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(PatientDetailsActivity.this, "⚠️ Failed to connect to server", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void submitPatientDetails() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String temperatureStr = etTemperature.getText().toString().trim();
        String daysStr = etDays.getText().toString().trim();
        String treatment = etTreatment.getText().toString().trim();
        String disease = etDisease.getText().toString().trim();

        boolean hasErrors = false;

        if (name.isEmpty()) {
            etName.setError("Name is required");
            if (!hasErrors) {
                etName.requestFocus();
                hasErrors = true;
            }
        }
        if (ageStr.isEmpty()) {
            etAge.setError("Age is required");
            if (!hasErrors) {
                etAge.requestFocus();
                hasErrors = true;
            }
        }
        if (temperatureStr.isEmpty()) {
            etTemperature.setError("Temperature is required");
            if (!hasErrors) {
                etTemperature.requestFocus();
                hasErrors = true;
            }
        }
        if (daysStr.isEmpty()) {
            etDays.setError("Days are required");
            if (!hasErrors) {
                etDays.requestFocus();
                hasErrors = true;
            }
        }
        if (hasErrors) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (treatment.length() > 100) {
            etTreatment.setError("Min 100 characters");
            return;
        }
        if (disease.length() > 20) {
            etDisease.setError("Min 20 characters");
            return;
        }

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        final String gender;
        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Female";
        } else if (selectedGenderId == R.id.rbOther) {
            gender = "Other";
        } else {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isContagious = cbContagious.isChecked();
        String contagiousStatus = isContagious ? "Yes" : "No";

        int temp, age, days;
        try {
            temp = Integer.parseInt(temperatureStr);
            age = Integer.parseInt(ageStr);
            days = Integer.parseInt(daysStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for age, temperature, and days", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age < 0 || age > 100) {
            Toast.makeText(this, "Age must be between 0 and 100", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age <= 5) {
            Toast.makeText(this, "Children (<6) need immediate care", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age >= 70) {
            Toast.makeText(this, "Elderly (>69) need immediate care", Toast.LENGTH_SHORT).show();
            return;
        }
        if (temp < 28 || temp > 41) {
            Toast.makeText(this, "Temperature must be between 28°C and 41°C", Toast.LENGTH_SHORT).show();
            return;
        }
        if (days > 7) {
            Toast.makeText(this, "Symptoms (>7) days need immediate care", Toast.LENGTH_SHORT).show();
            return;
        }

        String summary = "Name: " + name + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender + "\n" +
                "Temperature: " + temp + "°C\n" +
                "Days: " + days + "\n" +
                "Contagious: " + contagiousStatus;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Submission")
                .setMessage(summary)
                .setPositiveButton("Submit", (dialog, which) -> {
                    sendToServer(savedPhoneNumber, name, String.valueOf(age), gender, String.valueOf(temp), String.valueOf(days), contagiousStatus, treatment, disease);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}