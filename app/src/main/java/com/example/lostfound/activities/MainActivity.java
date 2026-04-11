package com.example.lostfound.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfound.R;
import com.example.lostfound.adapter.PostAdapter;
import com.example.lostfound.api.ApiService;
import com.example.lostfound.api.RetrofitClient;
import com.example.lostfound.models.LostObject;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private final List<LostObject> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    mAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(list);
        recyclerView.setAdapter(adapter);

        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v ->
                    startActivity(new Intent(this, AddObjectActivity.class))
            );
        }

        loadObjects();
    }

    private void loadObjects() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getObjects().enqueue(new Callback<List<LostObject>>() {
            @Override
            public void onResponse(Call<List<LostObject>> call, Response<List<LostObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (list.isEmpty()) {
                        View tvEmpty = findViewById(R.id.tvEmpty);
                        if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        View tvEmpty = findViewById(R.id.tvEmpty);
                        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LostObject>> call, Throwable t) {
            }
        });
    }
}
