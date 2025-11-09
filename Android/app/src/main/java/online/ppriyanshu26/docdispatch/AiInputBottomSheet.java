package online.ppriyanshu26.docdispatch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
        TextInputEditText etAiResponse = view.findViewById(R.id.etAiResponse);
        AutoCompleteTextView actvGender = view.findViewById(R.id.actvGender);
        AutoCompleteTextView actvSymptom1 = view.findViewById(R.id.actvSymptom1);
        AutoCompleteTextView actvSymptom2 = view.findViewById(R.id.actvSymptom2);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        ImageButton btnInfo = view.findViewById(R.id.btnInfo);

        etAiResponse.setKeyListener(null);       
        etAiResponse.setTextIsSelectable(true);
        etAiResponse.setCursorVisible(false);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext().getApplicationContext(), "Long press to view Privacy warning", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, GENDER_OPTIONS);
        actvGender.setAdapter(genderAdapter);

        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, SYMPTOM_SUGGESTIONS);
        actvSymptom1.setAdapter(symptomAdapter);
        actvSymptom2.setAdapter(symptomAdapter);

        boolean[] hasReceivedResponse = {false};

        btnSubmit.setOnClickListener(v -> {
            if (hasReceivedResponse[0]) return;

            etAiResponse.setText("Processing...\nPlease wait.");
            etAiResponse.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            String age = etAge.getText().toString().trim();
            String gender = actvGender.getText().toString().trim();
            String symptom1 = actvSymptom1.getText().toString().trim();

            if (age.isEmpty()) {
                Toast.makeText(getContext(), "Age is required", Toast.LENGTH_SHORT).show();
                etAiResponse.setText("");
                return;
            }
            if (gender.isEmpty()) {
                Toast.makeText(getContext(), "Gender is required", Toast.LENGTH_SHORT).show();
                etAiResponse.setText("");
                return;
            }
            if (symptom1.isEmpty()) {
                Toast.makeText(getContext(), "At least one symptom is required", Toast.LENGTH_SHORT).show();
                etAiResponse.setText("");
                return;
            }

            btnSubmit.setEnabled(false);
            btnSubmit.setText("Processing...");

            String medHistory = etMedicalHistory.getText().toString().trim();
            String symptom2 = actvSymptom2.getText().toString().trim();
            List<String> symptoms = new ArrayList<>();
            symptoms.add(symptom1);
            if (!symptom2.isEmpty()) symptoms.add(symptom2);

            sendToGemini(age, gender, medHistory, symptoms, etAiResponse, btnSubmit, hasReceivedResponse);
        });

        return view;
    }

    private void sendToGemini(String age, String gender, String medHistory, List<String> symptoms,
                              TextInputEditText etAiResponse, Button btnSubmit, boolean[] hasReceivedResponse) {

        // API key from BuildConfig
        String API_KEY = BuildConfig.GEMINI_API_KEY;
        if (API_KEY == null || API_KEY.isEmpty()) {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    etAiResponse.setText("❌ API key not configured. Add it to local.properties.");
                    etAiResponse.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                    hasReceivedResponse[0] = true;
                });
            }
            return;
        }

        String prompt = "You are a medical AI assistant. Analyze the following patient details and provide a concise explanation in a simple language and in a professional manner in less than 50 words. Do not diagnose definitively.\n\n" +
                "Patient Details:\n" +
                "- Age: " + age + "\n" +
                "- Gender: " + gender + "\n" +
                "- Medical History: " + (medHistory.isEmpty() ? "None provided" : medHistory) + "\n" +
                "- Symptoms: " + String.join(", ", symptoms) + "\n\n" +
                "Response:";

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
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            etAiResponse.setText("❌ Network error: " + e.getMessage());
                            etAiResponse.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                            hasReceivedResponse[0] = true;
                        });
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                if (!response.isSuccessful()) {
                                    etAiResponse.setText("❌ API Error: " + response.code());
                                    hasReceivedResponse[0] = true;
                                    return;
                                }

                                String responseBody = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                                if (candidates.length() == 0) throw new Exception("No response from model");

                                JSONObject contentObj = candidates.getJSONObject(0).getJSONObject("content");
                                JSONArray partsArray = contentObj.getJSONArray("parts");
                                if (partsArray.length() == 0) throw new Exception("Empty response");

                                String aiText = partsArray.getJSONObject(0).getString("text");
                                aiText += "\nThis is an AI response and it can be incorrect, it is advisable to consult a doctor if the condition persists.";
                                btnSubmit.setText("Processed");
                                etAiResponse.setText(aiText.trim());
                                etAiResponse.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                            } catch (Exception ex) {
                                etAiResponse.setText("❌ Error: " + ex.getMessage());
                                etAiResponse.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                            } finally {
                                hasReceivedResponse[0] = true;
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    etAiResponse.setText("❌ Request setup failed: " + e.getMessage());
                    etAiResponse.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                    hasReceivedResponse[0] = true;
                });
            }
        }
    }
}