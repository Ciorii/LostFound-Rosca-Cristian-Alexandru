package com.example.lostfound.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lostfound.R;
import com.example.lostfound.api.ApiService;
import com.example.lostfound.api.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String id, title, description, category, address, contactEmail, contactPhone, ownerUid;
    private double latitude, longitude;
    private ArrayList<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");
        address = getIntent().getStringExtra("address");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        imageUrls = getIntent().getStringArrayListExtra("imageUrls");
        contactEmail = getIntent().getStringExtra("contactEmail");
        contactPhone = getIntent().getStringExtra("contactPhone");
        ownerUid = getIntent().getStringExtra("ownerUid");

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvContactEmail = findViewById(R.id.tvContactEmail);
        TextView tvContactPhone = findViewById(R.id.tvContactPhone);
        ImageView ivPhoto = findViewById(R.id.ivPhoto);
        Button btnDelete = findViewById(R.id.btnDelete);

        tvTitle.setText(title);
        tvCategory.setText(category);
        tvDescription.setText(description);
        tvAddress.setText(address);
        tvContactEmail.setText("Email: " + (contactEmail == null ? "-" : contactEmail));
        tvContactPhone.setText("Telefon: " + (contactPhone == null ? "-" : contactPhone));

        if (imageUrls != null && !imageUrls.isEmpty()) {
            Glide.with(this).load(imageUrls.get(0)).into(ivPhoto);
        } else {
            findViewById(R.id.ivPhoto).setVisibility(View.GONE);
        }

        String currentUid = FirebaseAuth.getInstance().getUid();
        
        if (currentUid != null && ownerUid != null && currentUid.trim().equals(ownerUid.trim())) {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> deletePost());
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetail);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void deletePost() {
        ApiService api = RetrofitClient.getApiService();
        String currentUid = FirebaseAuth.getInstance().getUid();

        api.deleteObject(id, currentUid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this, "Sters!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Eroare la stergere!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Eroare: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
