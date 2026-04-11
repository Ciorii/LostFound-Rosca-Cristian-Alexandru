package com.example.lostfound.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AddObjectActivity extends AppCompatActivity {

    EditText etTitle, etDescription, etContactEmail, etContactPhone;
    RadioButton rbLost, rbFound;
    Button btnNext, btnAddPhotos;

    private final ArrayList<Uri> selectedUris = new ArrayList<>();

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImages =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), uris -> {
                selectedUris.clear();
                if (uris != null) selectedUris.addAll(uris);
                Toast.makeText(this, "Imagini selectate: " + selectedUris.size(), Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etContactEmail = findViewById(R.id.etContactEmail);
        etContactPhone = findViewById(R.id.etContactPhone);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);
        btnNext = findViewById(R.id.btnNext);
        btnAddPhotos = findViewById(R.id.btnAddPhotos);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && etContactEmail != null) {
            etContactEmail.setText(user.getEmail());
        }

        btnAddPhotos.setOnClickListener(v ->
                pickImages.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );

        btnNext.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String email = etContactEmail.getText().toString().trim();
            String phone = etContactPhone.getText().toString().trim();
            String category = rbLost.isChecked() ? "Pierdut" : "Gasit";

            if (title.isEmpty()) {
                Toast.makeText(this, "Titlul este obligatoriu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MapPickerActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("category", category);
            intent.putExtra("contactEmail", email);
            intent.putExtra("contactPhone", phone);
            intent.putParcelableArrayListExtra("imageUris", selectedUris);
            startActivity(intent);
        });
    }
}
