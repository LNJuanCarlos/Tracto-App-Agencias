package com.ilender.transportesforilender.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Seguimientoruta;
import com.ilender.transportesforilender.ui.rutas_chofer.RutasChoferFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IniciarActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mmap;
    private EditText edtKilometrajeIR, edtObservacion;
    private TextView txt56;
    private Button btnGrabarInicioRuta;
    private LinearLayout linearLayoutObservacion;
    private String idRuta, strDate, Latitud, Longitud, tipo;

    private final static int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapdriver);
        mapFragment.getMapAsync(this);
        edtKilometrajeIR = findViewById(R.id.edtKilometrajeIR);
        btnGrabarInicioRuta = findViewById(R.id.btnGrabarInicioRuta);
        linearLayoutObservacion = findViewById(R.id.linearLayoutObservacion);
        edtObservacion = findViewById(R.id.edtObservacion);
        strDate = new SimpleDateFormat("dd/MM/yyyy HH: mm: ss", Locale.getDefault()).format(new Date());
        tipo = getIntent().getExtras().getString("tipo");
        idRuta = getIntent().getExtras().getString("idRuta");
        txt56 = findViewById(R.id.txt56);

        if (tipo.equals("llegada")) {
            txt56.setText("LLEGADA A DESTINO");
        } else if (tipo.equals("finalizar")) {
            txt56.setText("FINALIZAR DESTINO");
            linearLayoutObservacion.setVisibility(View.VISIBLE);
        }
        getLocalizacion();

        btnGrabarInicioRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kilometraje = edtKilometrajeIR.getText().toString();
                String observacion = edtObservacion.getText().toString();
                String estadoS = "";
                Seguimientoruta nuevoSeguimiento = new Seguimientoruta(idRuta, strDate, Latitud, Longitud, kilometraje);

                if (tipo.equals("inicio")) {
                    estadoS = "I";
                    nuevoSeguimiento.setEstado(estadoS);
                } else if (tipo.equals("llegada")) {
                    estadoS = "L";
                    nuevoSeguimiento.setEstado(estadoS);
                } else if (tipo.equals("finalizar")) {
                    estadoS = "F";
                    nuevoSeguimiento.setEstado(estadoS);
                    nuevoSeguimiento.setObservacion(observacion);
                }

                DatabaseReference mrefSeguimiento = FirebaseDatabase.getInstance().getReference().child("Seguimientoruta");
                String id = mrefSeguimiento.push().getKey();

                mrefSeguimiento.child(id).setValue(nuevoSeguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference mrefRuta = FirebaseDatabase.getInstance().getReference().child("Ruta");
                        String estado = "";
                        if (tipo.equals("inicio")) {
                            estado = "I";
                        } else if (tipo.equals("llegada")) {
                            estado = "S";
                        } else if (tipo.equals("finalizar")) {
                            estado = "F";
                        }

                        mrefRuta.child(idRuta).child("estado").setValue(estado).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(IniciarActivity.this, "Se ha grabado correctamente la información!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(IniciarActivity.this, RutasChoferFragment.class);
                                IniciarActivity.this.startActivity(i);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void getLocalizacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setTitle("Proporciona los permisos para continuar").setMessage("Esta aplicación requiere los permisos de ubicación")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(IniciarActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(IniciarActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mmap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mmap.setMyLocationEnabled(true);

        mmap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) IniciarActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                Latitud = String.valueOf(location.getLatitude());
                Longitud = String.valueOf(location.getLongitude());
                mmap.addMarker(new MarkerOptions().position(miUbicacion).title("ubicacion actual"));
                mmap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(miUbicacion)
                        .zoom(14)
                        .bearing(90)
                        .tilt(45)
                        .build();
                mmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }
}