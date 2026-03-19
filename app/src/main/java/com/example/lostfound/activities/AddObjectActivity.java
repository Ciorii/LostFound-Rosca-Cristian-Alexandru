package com.example.lostfound.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.R;

public class AddObjectActivity extends AppCompatActivity {

    EditText etTitle, etDescription;
    RadioButton rbLost, rbFound;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String category = rbLost.isChecked() ? "pierdut" : "gasit";

            if (title.isEmpty()) {
                Toast.makeText(this, "Scrie denumirea obiectului!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MapPickerActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("category", category);
            startActivity(intent);
        });
    }
}