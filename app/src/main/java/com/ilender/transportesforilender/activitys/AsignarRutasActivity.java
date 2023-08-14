package com.ilender.transportesforilender.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Vehiculochofer;
import com.ilender.transportesforilender.model.Vehiculos;
import com.ilender.transportesforilender.providers.ImageProvider;
import com.ilender.transportesforilender.ui.chofer.ChoferFragment;
import com.ilender.transportesforilender.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AsignarRutasActivity extends AppCompatActivity {

    private String idChofer, idVehiculo, idDireccion, idCliente, strDate,currentPhotoPath, idRutaIntent;
    private EditText fechaDisponibilidad;
    private StorageReference mStorage;
    private TextView txtfechaAsignacion, titulo;
    private MaterialCardView card;
    private Button btnConsultar, btnGrabar, btnSubirImagenSB;
    private Spinner spVehiculos, spDireccion, spClientes;
    ImageProvider mImageProvider;
    private int position;
    private LinearLayout layoutOculto, linearLayoutImagen;
    private ConstraintLayout layoutImagenes;
    private Button btnVisualizar, btnSiguiente, btnTomarImagenSB, btnAnterior;
    private ArrayList<File> mImageFiles;
    private ArrayList<Uri> imagesUri;
    private ImageView imageSwitcher;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private int positionis;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignar_rutas);
        idChofer = getIntent().getExtras().getString("idChofer");
        idRutaIntent = getIntent().getExtras().getString("ruta");
        fechaDisponibilidad = findViewById(R.id.edtFechaDisponibilidadChofer);
        txtfechaAsignacion = findViewById(R.id.txtfechaAsignacion);
        btnConsultar = findViewById(R.id.btnConsultarVehiculosChofer);
        btnGrabar = findViewById(R.id.btnGrabarAsignarRuta);
        spVehiculos = findViewById(R.id.spVehiculosRutas);
        spDireccion = findViewById(R.id.spDireccion);
        spClientes = findViewById(R.id.spClientes);
        titulo = findViewById(R.id.textViewTitulo);
        layoutImagenes = findViewById(R.id.layoutImagenes);
        card = findViewById(R.id.materialCardView);
        layoutOculto = findViewById(R.id.layoutOcultoAsignarRutas);
        strDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        fechaDisponibilidad.setText(strDate);
        imageSwitcher = findViewById(R.id.isIncidenciasAS);
        btnAnterior = findViewById(R.id.btnAnteriorAS);
        btnSiguiente = findViewById(R.id.btnSiguienteAS);
        linearLayoutImagen = findViewById(R.id.linearLayoutImagenAS);
        btnTomarImagenSB = findViewById(R.id.btnTomarImagenAS);
        btnVisualizar = findViewById(R.id.btnVisualizarAdjuntosAS);
        mImageFiles = new ArrayList<>();
        imagesUri = new ArrayList<>();
        mImageProvider = new ImageProvider();
        btnSubirImagenSB = findViewById(R.id.btnSubirImagenAS);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        if(idRutaIntent!=null){
            Log.d("PruebaASDADASDASDSADASDSAD", idRutaIntent);
            titulo.setText("MODIFICACION DE CONTENIDO DE RUTA");
            layoutImagenes.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
            txtfechaAsignacion.setVisibility(View.GONE);
            mStorage = FirebaseStorage.getInstance().getReference().child("Guias");
            StorageReference filepath = mStorage.child(idRutaIntent).child("Inicio");

            filepath.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    ArrayList<Uri> uris = new ArrayList<>();
                    for(StorageReference prefix : listResult.getPrefixes()){
                    }
                    for(StorageReference item : listResult.getItems()){
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(uri.equals(null)){
                                    linearLayoutImagen.setVisibility(View.GONE);
                                    btnAnterior.setVisibility(View.GONE);
                                    btnSiguiente.setVisibility(View.GONE);
                                }else{
                                    linearLayoutImagen.setVisibility(View.VISIBLE);
                                    btnAnterior.setVisibility(View.VISIBLE);
                                    btnSiguiente.setVisibility(View.VISIBLE);
                                    btnSubirImagenSB.setEnabled(false);
                                    btnTomarImagenSB.setEnabled(false);
                                    btnVisualizar.setEnabled(false);
                                    btnGrabar.setEnabled(false);
                                }
                                imagesUri.add(uri);
                                uris.add(uri);
                                if(imagesUri.size()==1){
                                    Glide.with(AsignarRutasActivity.this)
                                            .load(uri)
                                            .into(imageSwitcher);
                                }
                            }
                        });
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        fechaDisponibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CargarFecha();
            }
        });
        spVehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Vehiculos a = (Vehiculos) parent.getItemAtPosition(position);
                idVehiculo= a.getIdVehiculos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Direccion d = (Direccion) parent.getItemAtPosition(position);
                idDireccion=d.getIdDireccion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Clientes c = (Clientes) parent.getItemAtPosition(position);
                idCliente=c.getIdCliente();
                CargarDirecciones(idCliente);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtfechaAsignacion.setText(fechaDisponibilidad.getText().toString());
                Query mrefVehiculoChofer = FirebaseDatabase.getInstance().getReference().child("Vehiculochofer");

                mrefVehiculoChofer.orderByChild("fecha").equalTo(fechaDisponibilidad.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Vehiculochofer> vh = new ArrayList<>();
                        ArrayList<Vehiculos> vs2 = new ArrayList<>();
                        for(DataSnapshot postSnapshot : snapshot.getChildren()){
                            Vehiculochofer vehiculochofer = postSnapshot.getValue(Vehiculochofer.class);
                            if(vehiculochofer.getChofer().equals(idChofer)){
                                vehiculochofer.setId(postSnapshot.getKey());
                                vh.add(vehiculochofer);
                                DatabaseReference mrefVehiculo = FirebaseDatabase.getInstance().getReference().child("Vehiculos");
                                mrefVehiculo.child(vehiculochofer.getVehiculo()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Vehiculos vehiculoObtenido = snapshot.getValue(Vehiculos.class);
                                        vehiculoObtenido.setIdVehiculos(snapshot.getKey());
                                        vs2.add(vehiculoObtenido);

                                        ArrayAdapter<Vehiculos> adapter = new ArrayAdapter<Vehiculos>(AsignarRutasActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,vs2);
                                        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                                        spVehiculos.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        if(vh.size()==0){
                            Toast.makeText(AsignarRutasActivity.this, "El chofer no tiene vehículos asignados para esta fecha!", Toast.LENGTH_SHORT).show();
                            layoutOculto.setVisibility(View.GONE);
                        }else{
                            CargarClientes();
                            layoutOculto.setVisibility(View.VISIBLE);
                            layoutImagenes.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idRutaIntent!=null){
                    saveImage(idRutaIntent);
                    Toast.makeText(AsignarRutasActivity.this, "Se ha grabado correctamente la información!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AsignarRutasActivity.this, ChoferFragment.class);
                    onBackPressed();
                }else{
                    if(idChofer.equals("")||idDireccion.equals("")||fechaDisponibilidad.getText().toString().equals("")||idVehiculo.equals("")){
                        Toast.makeText(AsignarRutasActivity.this, "Todos los datos son obligatorios!", Toast.LENGTH_SHORT).show();
                    }else{
                        Ruta rutaActual = new Ruta(idChofer,idDireccion,"P",fechaDisponibilidad.getText().toString(),idVehiculo);
                        DatabaseReference mrefRuta = FirebaseDatabase.getInstance().getReference().child("Ruta");
                        String idRuta = mrefRuta.push().getKey();

                        mrefRuta.child(idRuta).setValue(rutaActual).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                saveImage(idRuta);
                                Toast.makeText(AsignarRutasActivity.this, "Se ha grabado correctamente la información!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(AsignarRutasActivity.this,ChoferFragment.class);
                                onBackPressed();
                            }
                        });
                    }
                }
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
        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mImageFiles.size()>0){
                    Glide.with(getApplicationContext())
                            .load(imagesUri.get(1))
                            .into(imageSwitcher);
                    linearLayoutImagen.setVisibility(View.VISIBLE);
                    btnAnterior.setVisibility(View.VISIBLE);
                    btnSiguiente.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(AsignarRutasActivity.this, "No ha tomado o seleccionado ninguna foto!", Toast.LENGTH_SHORT).show();
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
                }catch (IOException e){
                    throw new RuntimeException(e);
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
                }else{
                    Toast.makeText(AsignarRutasActivity.this, "No hay imagen previa", Toast.LENGTH_SHORT).show();
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
                }else{
                    Toast.makeText(AsignarRutasActivity.this, "No hay más imágenes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void CargarDirecciones(String idCliente){
        Log.d("IDCLIENTEEEEEEEEEEEE",idCliente);
        Query mrefDirecciones = FirebaseDatabase.getInstance().getReference().child("Direccion");

        mrefDirecciones.orderByChild("cliente").equalTo(idCliente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Direccion> dr = new ArrayList<>();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Direccion direccion = postSnapshot.getValue(Direccion.class);
                    direccion.setIdDireccion(postSnapshot.getKey());
                    dr.add(direccion);
                }
                ArrayAdapter<Direccion> adapter2 = new ArrayAdapter<Direccion>(AsignarRutasActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,dr);
                adapter2.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                spDireccion.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void CargarClientes(){
        DatabaseReference mrefClientes = FirebaseDatabase.getInstance().getReference().child("Clientes");

        mrefClientes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Clientes> cl = new ArrayList<>();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Clientes cliente = postSnapshot.getValue(Clientes.class);
                    cliente.setIdCliente(postSnapshot.getKey());
                    cl.add(cliente);
                }
                ArrayAdapter<Clientes> adapterCliente = new ArrayAdapter<Clientes>(AsignarRutasActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,cl);
                adapterCliente.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                spClientes.setAdapter(adapterCliente);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void CargarFecha(){
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                fechaDisponibilidad.setText("");
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR,year);
                calendar1.set(Calendar.MONTH,month);
                calendar1.set(Calendar.DATE,date);
                fechaDisponibilidad.setText(DateFormat.format("dd/MM/yyyy",calendar1));
            }
        },YEAR , MONTH , DATE);
        datePickerDialog.show();
    }

    private void dispatchTakePictureIntent() throws IOException{
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) !=null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){

            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ilender.transportesforilender.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
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
    private void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(Intent.createChooser(galleryIntent,"Seleccione la(s) imágene(s)"));

    }

    private void saveImage(String idRuta){
        Log.d("IDRUTAAAAAAAAAAAAAAAAAAAAAAa",idRuta);
        for(int i=0; i<mImageFiles.size();i++){
            mImageProvider.saveInicio(AsignarRutasActivity.this,mImageFiles.get(i),i,idRuta).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        
                    }else{
                        Toast.makeText(AsignarRutasActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
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
                                int contador = result.getData().getClipData().getItemCount();
                                for(int i=0;i<contador;i++){
                                    File imagefil = FileUtil.from(AsignarRutasActivity.this,result.getData().getClipData().getItemAt(i).getUri());
                                    Uri imageUri = Uri.fromFile(imagefil);
                                    mImageFiles.add(imagefil);
                                    imagesUri.add(imageUri);
                                    Glide.with(getApplicationContext())
                                            .load(imagefil)
                                            .into(imageSwitcher);
                                    position = 0;
                                }
                            }else{
                                File imagefil = FileUtil.from(AsignarRutasActivity.this,result.getData().getData());
                                mImageFiles.add(imagefil);
                                Glide.with(getApplicationContext())
                                        .load(imagefil)
                                        .into(imageSwitcher);
                                position = 0;
                            }
                        }catch (Exception e){
                            Log.d("Error","Se produjo un error" +e.getMessage());
                            Toast.makeText(AsignarRutasActivity.this, "Se produjo un error" +e.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );
    public void checkPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{permission},requestCode);
        }else{

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
