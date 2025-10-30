package online.ppriyanshu26.docdispatch;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiInputBottomSheet extends BottomSheetDialogFragment {

    private static final String[] GENDER_OPTIONS = {"Male", "Female", "Other", "Prefer not to say"};
    private static final String[] SYMPTOM_SUGGESTIONS = {
            "Fever", "Cough", "Headache", "Fatigue", "Nausea", "Vomiting", "Dizziness",
            "Chest pain", "Shortness of breath", "Abdominal pain", "Rash", "Sore throat",
            "Runny nose", "Joint pain", "Muscle ache", "Chills", "Loss of appetite",
            "Weight loss", "Night sweats", "Swelling", "Bleeding", "Itching", "NONE"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_ai_input, container, false);

        TextInputEditText etAge = view.findViewById(R.id.etAge);
        TextInputEditText etMedicalHistory = view.findViewById(R.id.etMedicalHistory);
        AutoCompleteTextView actvGender = view.findViewById(R.id.actvGender);
        AutoCompleteTextView actvSymptom1 = view.findViewById(R.id.actvSymptom1);
        AutoCompleteTextView actvSymptom2 = view.findViewById(R.id.actvSymptom2);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        // Setup gender dropdown
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, GENDER_OPTIONS);
        actvGender.setAdapter(genderAdapter);

        // Setup symptom auto-complete
        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, SYMPTOM_SUGGESTIONS);
        actvSymptom1.setAdapter(symptomAdapter);
        actvSymptom2.setAdapter(symptomAdapter);

        btnSubmit.setOnClickListener(v -> {
            String age = etAge.getText().toString().trim();
            String gender = actvGender.getText().toString().trim();
            String medHistory = etMedicalHistory.getText().toString().trim();
            String symptom1 = actvSymptom1.getText().toString().trim();
            String symptom2 = actvSymptom2.getText().toString().trim();

            if (age.isEmpty()) {
                Toast.makeText(getContext(), "Please enter age", Toast.LENGTH_SHORT).show();
                return;
            }
            if (gender.isEmpty()) {
                Toast.makeText(getContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                return;
            }
            if (symptom1.isEmpty()) {
                Toast.makeText(getContext(), "Please enter at least one symptom", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> symptoms = new ArrayList<>();
            symptoms.add(symptom1);
            if (!symptom2.isEmpty()) symptoms.add(symptom2);

            sendToGemini(age, gender, medHistory, symptoms);
            dismiss();
        });

        return view;
    }

    private void sendToGemini(String age, String gender, String medHistory, List<String> symptoms) {
        String prompt = "Patient: Age " + age + ", Gender " + gender +
                ". Medical History: " + (medHistory.isEmpty() ? "None" : medHistory) +
                ". Symptoms: " + String.join(", ", symptoms) +
                ". What could this indicate? Provide a brief, professional medical insight.";

        // 🔑 REPLACE WITH YOUR GEMINI API KEY
        String API_KEY = "AIzaSyDogb52PLovq2NAM38OhLDF23Ih9EC8RXg";
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", prompt));
            content.put("parts", parts).put("role", "user");
            contents.put(content);
            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder().url(url).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) throw new IOException("Error: " + response.code());
                        String resp = response.body().string();
                        String aiResponse = new JSONObject(resp)
                                .getJSONArray("candidates").getJSONObject(0)
                                .getJSONArray("content").getJSONObject(0)
                                .getJSONArray("parts").getJSONObject(0)
                                .getString("text");

                        requireActivity().runOnUiThread(() ->
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("AI Response")
                                        .setMessage(aiResponse.trim())
                                        .setPositiveButton("OK", null)
                                        .show());
                    } catch (Exception ex) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Failed to get response", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show());
        }
    }
}