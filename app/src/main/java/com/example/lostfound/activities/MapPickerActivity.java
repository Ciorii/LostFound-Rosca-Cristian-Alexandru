package com.example.lostfound.activities;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.BuildConfig;
import com.example.lostfound.R;
import com.example.lostfound.api.ApiService;
import com.example.lostfound.api.ImgBBApiService;
import com.example.lostfound.api.ImgBBClient;
import com.example.lostfound.api.ImgBBResponse;
import com.example.lostfound.api.RetrofitClient;
import com.example.lostfound.models.LostObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText etAddress;
    Button btnSearch, btnSave;
    GoogleMap mMap;

    String title, description, category, contactEmail, contactPhone;
    double lat = 0, lng = 0;
    boolean locationSelected = false;

    ArrayList<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");
        contactEmail = getIntent().getStringExtra("contactEmail");
        contactPhone = getIntent().getStringExtra("contactPhone");
        imageUris = getIntent().getParcelableArrayListExtra("imageUris");

        etAddress = findViewById(R.id.etAddress);
        btnSearch = findViewById(R.id.btnSearch);
        btnSave = findViewById(R.id.btnSave);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSearch.setOnClickListener(v -> searchAddress());
        btnSave.setOnClickListener(v -> saveObject());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
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

        if (imageUris == null || imageUris.isEmpty()) {
            sendObject(new ArrayList<>());
        } else {
            uploadAllImages(imageUris);
        }
    }

    private void uploadAllImages(List<Uri> uris) {
        List<String> uploadedUrls = new ArrayList<>();
        uploadNext(uris, 0, uploadedUrls);
    }

    private void uploadNext(List<Uri> uris, int index, List<String> uploadedUrls) {
        if (index >= uris.size()) {
            sendObject(uploadedUrls);
            return;
        }

        Uri uri = uris.get(index);

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                uploadNext(uris, index + 1, uploadedUrls);
                return;
            }
            byte[] bytes = readBytes(inputStream);

            String mimeType = getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "image/jpeg";

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse(mimeType),
                    bytes
            );

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("image", "photo.jpg", requestBody);

            ImgBBApiService api = ImgBBClient.getApi();
            api.uploadImage(BuildConfig.IMGBB_API_KEY, body).enqueue(new Callback<ImgBBResponse>() {
                @Override
                public void onResponse(@NonNull Call<ImgBBResponse> call, @NonNull Response<ImgBBResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        uploadedUrls.add(response.body().data.url);
                        uploadNext(uris, index + 1, uploadedUrls);
                    } else {
                        Toast.makeText(MapPickerActivity.this, "Upload esuat!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ImgBBResponse> call, @NonNull Throwable t) {
                    Toast.makeText(MapPickerActivity.this, "Eroare upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Eroare imagine!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendObject(List<String> imageUrls) {
        LostObject obj = new LostObject();
        obj.setTitle(title);
        obj.setDescription(description);
        obj.setCategory(category);
        obj.setAddress(etAddress.getText().toString().trim());
        obj.setLatitude(lat);
        obj.setLongitude(lng);
        obj.setImageUrls(imageUrls);
        obj.setContactEmail(contactEmail);
        obj.setContactPhone(contactPhone);

        String uid = FirebaseAuth.getInstance().getUid();
        obj.setOwnerUid(uid);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.addObject(obj).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MapPickerActivity.this, "Salvat!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MapPickerActivity.this, "Eroare server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(MapPickerActivity.this, "Conexiune esuata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] readBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}