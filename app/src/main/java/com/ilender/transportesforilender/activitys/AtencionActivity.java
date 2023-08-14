package com.ilender.transportesforilender.activitys;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Seguimientoruta;
import com.ilender.transportesforilender.providers.ImageProvider;
import com.ilender.transportesforilender.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AtencionActivity extends AppCompatActivity implements OnMapReadyCallback {


    private String idRuta, strDate, tipo, Latitud, Longitud;

    private ArrayList<File> mImageFiles;

    private EditText edtKilometrajeLL;

    private GoogleMap mmap;

    ImageProvider mImageProvider;
    private int position;

    private Button btnregistrar;

    private Button btnVisualziar;

    private Button btnSiguiente;

    private Button btnTomarImagenSB;

    private ArrayList<Uri> imagesUri;

    private Button btnAnterior;

    private ImageView imageSwitcher;

    private LinearLayout linearLayoutImagen;

    private Button btnSubirImagenSB;

    private final static int LOCATION_REQUEST_CODE = 1;

    private static final int STORAGE_PERMISSION_CODE = 101;

    String currentPhotoPath;

    private int positionis;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atencion);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapdriver2);
        mapFragment.getMapAsync(this);
        strDate = new SimpleDateFormat("dd/MM/yyyy HH: mm: ss", Locale.getDefault()).format(new Date());
        tipo = getIntent().getExtras().getString("tipo");
        idRuta = getIntent().getExtras().getString("idRuta");
        imageSwitcher = findViewById(R.id.isIncidencias);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        edtKilometrajeLL = findViewById(R.id.edtKilometrajeLL);
        linearLayoutImagen = findViewById(R.id.linearLayoutImagen);
        btnTomarImagenSB = findViewById(R.id.btnTomarImagenSB);
        btnVisualziar = findViewById(R.id.btnVisualizarAdjuntos);
        mImageFiles = new ArrayList<>();
        mImageProvider = new ImageProvider();
        btnSubirImagenSB = findViewById(R.id.btnSubirImagenSB);
        btnregistrar = findViewById(R.id.btnRegistrarSB);
        imagesUri = new ArrayList<>();

        getLocalizacion();
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });
        btnSubirImagenSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                linearLayoutImagen.setVisibility(View.GONE);
                btnAnterior.setVisibility(View.GONE);
                btnSiguiente.setVisibility(View.GONE);
            }
        });
        btnVisualziar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mImageFiles.size()>0){
                    Glide.with(getApplicationContext())
                            .load(imagesUri.get(1))
                            .into(imageSwitcher);
                    linearLayoutImagen.setVisibility(View.VISIBLE);
                    btnAnterior.setVisibility(View.VISIBLE);
                    btnSiguiente.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AtencionActivity.this, "No ha tomado o seleccionado ninguna foto!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(positionis>0){
                    positionis--;
                    Glide.with(getApplicationContext())
                            .load(imagesUri.get(positionis))
                            .into(imageSwitcher);
                } else {
                    Toast.makeText(AtencionActivity.this, "No hay imagen previa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(positionis<imagesUri.size()-1){
                    positionis++;
                    Glide.with(getApplicationContext())
                            .load(imagesUri.get(positionis))
                            .into(imageSwitcher);
                } else {
                    Toast.makeText(AtencionActivity.this, "No hay más imágenes", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnTomarImagenSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    linearLayoutImagen.setVisibility(View.GONE);
                    btnAnterior.setVisibility(View.GONE);
                    btnSiguiente.setVisibility(View.GONE);
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ilender.transportesforilender.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                mImageFiles.clear();
                imagesUri.clear();
                imagesUri.add(photoURI);
                Uri imageUri = Uri.fromFile(photoFile);
                imagesUri.add(imageUri);
                mImageFiles.add(photoFile);

            }

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(Intent.createChooser(galleryIntent,"Seleccione la(s) imágene(s)"));
        // startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    void registrar(){
        String kilometraje = edtKilometrajeLL.getText().toString();
        Seguimientoruta nuevoSeguimiento = new Seguimientoruta(idRuta, strDate , "A", Latitud, Longitud, kilometraje);
        crear(nuevoSeguimiento);
    }

    void crear(Seguimientoruta nuevoSeguimiento) {
        DatabaseReference mrefSeguimiento = FirebaseDatabase.getInstance().getReference().child("Seguimientoruta");
        String id = mrefSeguimiento.push().getKey();
        mrefSeguimiento.child(id).setValue(nuevoSeguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference mrefRuta = FirebaseDatabase.getInstance().getReference().child("Ruta");
                String estado = "A";
                mrefRuta.child(idRuta).child("estado").setValue(estado).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveImage();
                        Toast.makeText(AtencionActivity.this, "Se ha grabado correctamente la información!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });

    }
    private void saveImage() {
        for (int i=0; i<mImageFiles.size();i++ ){
            mImageProvider.save(AtencionActivity.this,mImageFiles.get(i),i, idRuta).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                    }else{
                        Toast.makeText(AtencionActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                    }

                }

            });

        }


    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        try {
                            mImageFiles.clear();
                            imagesUri.clear();
                            if(result.getData().getClipData()!=null){
                                int contador  = result.getData().getClipData().getItemCount();
                                for(int i=0;i<contador;i++){
                                    File imagefil = FileUtil.from(AtencionActivity.this,result.getData().getClipData().getItemAt(i).getUri());
                                    Uri imageUri = Uri.fromFile(imagefil);
                                    mImageFiles.add(imagefil);
                                    imagesUri.add(imageUri);
                                    Glide.with(getApplicationContext())
                                            .load(imagefil)
                                            .into(imageSwitcher);
                                    position = 0;
                                }
                            } else {

                                File imagefil = FileUtil.from(AtencionActivity.this,result.getData().getData());
                                mImageFiles.add(imagefil);
                                Glide.with(getApplicationContext())
                                        .load(imagefil)
                                        .into(imageSwitcher);
                                position = 0;
                            }

                        }catch (Exception e){
                            Log.d("Error","Se produjo un error" + e.getMessage());
                            Toast.makeText(AtencionActivity.this, "Se produjo un error" +e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
    );

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            requestPermissions( new String[]{permission}, requestCode);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocalizacion() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setTitle("Proporciona los permisos para continuar").setMessage("Esta aplicación requiere los permisos de ubicación")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(AtencionActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(AtencionActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mmap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mmap.setMyLocationEnabled(true);

        mmap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) AtencionActivity.this.getSystemService(Context.LOCATION_SERVICE);
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