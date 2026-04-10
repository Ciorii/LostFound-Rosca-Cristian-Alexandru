package com.example.lostfound.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PostDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String title, description, category, address;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");
        address = getIntent().getStringExtra("address");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvAddress = findViewById(R.id.tvAddress);

        if (tvTitle != null) tvTitle.setText(title);
        if (tvCategory != null) tvCategory.setText(category);
        if (tvDescription != null) tvDescription.setText(description);
        if (tvAddress != null) tvAddress.setText(address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetail);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }
}