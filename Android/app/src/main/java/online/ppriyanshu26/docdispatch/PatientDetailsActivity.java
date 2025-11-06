package online.ppriyanshu26.docdispatch;

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

public class PatientDetailsActivity extends AppCompatActivity {

    private TextInputEditText etName, etAge, etTemperature, etDays, etOthers, etTreatment, etDisease;
    private RadioGroup rgGender;
    private CheckBox cbContagious;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        initViews();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPatientDetails();
            }
        });
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etTemperature = findViewById(R.id.etTemperature);
        etDays = findViewById(R.id.etDays);
        etOthers = findViewById(R.id.etOthers);
        etTreatment = findViewById(R.id.etTreatment);
        etDisease = findViewById(R.id.etDisease);
        rgGender = findViewById(R.id.rgGender);
        cbContagious = findViewById(R.id.cbContagious);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void submitPatientDetails() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String temperatureStr = etTemperature.getText().toString().trim();
        String daysStr = etDays.getText().toString().trim();
        String others = etOthers.getText().toString().trim();
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

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
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

        int temp = Integer.parseInt(temperatureStr);
        int age =  Integer.parseInt(ageStr);
        int days =  Integer.parseInt(daysStr);

        if (temp < 28 || temp > 41) {
            Toast.makeText(this, "Temperature range from 28 to 41", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age < 0 || age > 100) {
            Toast.makeText(this, "Age range from 0 to 100", Toast.LENGTH_SHORT).show();
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
        if (days > 7) {
            Toast.makeText(this, "More than a week needs immediate care", Toast.LENGTH_SHORT).show();
            return;
        }

        String summary = "Name: " + name + "\n" +
                "Age: " + ageStr + "\n" +
                "Gender: " + gender + "\n" +
                "Temperature: " + temperatureStr + "°C\n" +
                "Days: " + daysStr + "\n" +
                "Contagious: " + contagiousStatus;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to submit?")
                .setMessage(summary)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PatientDetailsActivity.this, "Patient details submitted successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}