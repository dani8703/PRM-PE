package com.example.sp25_trandangquocdat_njs1706;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        
        // Thiết lập lỗi xử lý sự kiện Map để tránh crash
        try {
            mMap.setOnMapClickListener(latLng -> {
                // Xử lý an toàn khi click trên map
                try {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Địa điểm đã chọn"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi tương tác với Map: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Không thể thiết lập tương tác Map", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableMyLocation() {
        if (mMap == null) {
            return;
        }

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                showCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Lỗi quyền vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi không xác định: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Cần quyền vị trí để hiển thị vị trí của bạn", Toast.LENGTH_LONG).show();
                // Hiển thị vị trí mặc định thay vì crash
                showDefaultLocation();
            }
        }
    }

    private void showDefaultLocation() {
        if (mMap == null) return;
        
        // Hiển thị vị trí mặc định (FPT HCM)
        LatLng fpt = new LatLng(10.8531, 106.6299);
        mMap.addMarker(new MarkerOptions().position(fpt).title("FPT HCM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fpt, 15));
    }

    private void showCurrentLocation() {
        if (mMap == null) return;
        
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            
            Toast.makeText(this, "Đang tải vị trí...", Toast.LENGTH_SHORT).show();
            
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null && mMap != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.clear(); // Xóa các marker cũ để tránh lỗi
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Vị trí hiện tại"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        } else {
                            Toast.makeText(MapsActivity.this, "Không thể lấy vị trí hiện tại, hiển thị vị trí mặc định", Toast.LENGTH_SHORT).show();
                            showDefaultLocation();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MapsActivity.this, "Lỗi khi lấy vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        showDefaultLocation();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showDefaultLocation();
        }
    }
}
