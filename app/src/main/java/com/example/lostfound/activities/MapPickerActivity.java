package com.example.lostfound.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.lostfound.api.ApiService;
import com.example.lostfound.api.RetrofitClient;
import com.example.lostfound.models.LostObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText etAddress;
    Button btnSearch, btnSave;
    GoogleMap mMap;

    String title, description, category;
    double lat = 0, lng = 0;
    boolean locationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");

        etAddress = findViewById(R.id.etAddress);
        btnSearch = findViewById(R.id.btnSearch);
        btnSave = findViewById(R.id.btnSave);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnSearch.setOnClickListener(v -> searchAddress());
        btnSave.setOnClickListener(v -> saveObject());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(45.9432, 24.9668), 6));
    }

    private void searchAddress() {
        String addressText = etAddress.getText().toString().trim();
        if (addressText.isEmpty()) {
            Toast.makeText(this, "Scrie o adresa!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> results = geocoder.getFromLocationName(addressText, 1);

            if (results != null && !results.isEmpty()) {
                lat = results.get(0).getLatitude();
                lng = results.get(0).getLongitude();
                locationSelected = true;

                LatLng latLng = new LatLng(lat, lng);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(addressText));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                Toast.makeText(this, "Adresa nu a fost gasita!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Eroare!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveObject() {
        if (!locationSelected) {
            Toast.makeText(this, "Cauta adresa mai intai!", Toast.LENGTH_SHORT).show();
            return;
        }

        LostObject obj = new LostObject();
        obj.setTitle(title);
        obj.setDescription(description);
        obj.setCategory(category);
        obj.setAddress(etAddress.getText().toString().trim());
        obj.setLatitude(lat);
        obj.setLongitude(lng);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.addObject(obj).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MapPickerActivity.this, "Salvat!", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                } else {
                    Toast.makeText(MapPickerActivity.this, "Eroare server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MapPickerActivity.this, "Conexiune esuata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}