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

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (ageStr.isEmpty()) {
            etAge.setError("Age is required");
            return;
        }
        if (temperatureStr.isEmpty()) {
            etTemperature.setError("Temperature is required");
            return;
        }
        if (daysStr.isEmpty()) {
            etDays.setError("Number of days is required");
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

        Toast.makeText(this, "Patient details submitted successfully!", Toast.LENGTH_LONG).show();

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