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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Agencia;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Vehiculochofer;
import com.ilender.transportesforilender.model.Vehiculos;
import com.ilender.transportesforilender.providers.ImageProvider;
import com.ilender.transportesforilender.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AsignarRutasActivity extends AppCompatActivity {

    private String idChofer, idVehiculo, idDireccion, idCliente, strDate,currentPhotoPath, idRutaIntent, tipoEntrega = "Delivery", idAgencia = "";
    private EditText fechaDisponibilidad;
    private StorageReference mStorage;
    private TextView txtfechaAsignacion, titulo;
    private MaterialCardView card;
    private Button btnConsultar, btnGrabar, btnSubirImagenSB;
    private Spinner spVehiculos, spDireccion, spClientes , spTipoEntrega, spAgencias;
    ImageProvider mImageProvider;
    private int position;
    private LinearLayout layoutOculto, linearLayoutImagen , layoutAgencia, layoutClientes, layoutDirecciones, layoutComprobante;
    private ConstraintLayout layoutImagenes;
    private Button btnVisualizar, btnSiguiente, btnTomarImagenSB, btnAnterior;
    private ArrayList<File> mImageFiles;
    private ArrayList<Uri> imagesUri;
    private ImageView imageSwitcher;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private int positionis;
    private int currentIndex = 0;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE_CAMARA = 100;
    private List<Uri> fotosUris = new ArrayList<>();

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
        spTipoEntrega = findViewById(R.id.spTipoEntrega);
        spAgencias = findViewById(R.id.spAgencias);
        layoutAgencia = findViewById(R.id.layoutAgencia);
        layoutClientes = findViewById(R.id.layoutClientes);
        layoutDirecciones = findViewById(R.id.layoutDirecciones);
        layoutComprobante = findViewById(R.id.layoutComprobante);





        if (idRutaIntent != null) {
            titulo.setText("MODIFICACION DE CONTENIDO DE RUTA");
            layoutImagenes.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
            txtfechaAsignacion.setVisibility(View.GONE);

            mStorage = FirebaseStorage.getInstance().getReference().child("Guias");
            StorageReference filepath = mStorage.child(idRutaIntent).child("Inicio");

            filepath.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        imagesUri.add(uri);

                        if (imagesUri.size() == 1) { // primera foto remota
                            currentIndex = 0;
                            mostrarFotoActualRemoto();
                        }
                    });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error cargando imágenes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

        fechaDisponibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CargarFecha();
            }
        });

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"AGENCIA", "DELIVERY"});
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoEntrega.setAdapter(adapterTipo);

        spTipoEntrega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoEntrega = parent.getItemAtPosition(position).toString();

                if(tipoEntrega.equals("AGENCIA")){
                    // Mostrar todos los layouts de Agencia
                    layoutAgencia.setVisibility(View.VISIBLE);
                    layoutClientes.setVisibility(View.VISIBLE);      // Spinner de clientes
                    layoutDirecciones.setVisibility(View.VISIBLE);   // Spinner de direcciones
                    layoutComprobante.setVisibility(View.VISIBLE);   // SELECCIONE COMO DESEA ADJUNTAR SU COMPROBANTE

                    // Ocultar layouts de Delivery
                    layoutOculto.setVisibility(View.GONE);

                    // Cargar datos de agencias y clientes asociados si es necesario
                    CargarAgencias();
                    CargarClientes(); // si quieres refrescar la lista de clientes
                } else {
                    // Mostrar layouts de Delivery
                    layoutOculto.setVisibility(View.VISIBLE);

                    // Ocultar layouts de Agencia
                    layoutAgencia.setVisibility(View.GONE);
                    layoutClientes.setVisibility(View.VISIBLE);
                    layoutDirecciones.setVisibility(View.VISIBLE);
                    layoutComprobante.setVisibility(View.VISIBLE); // ✅ ahora lo ocultas

                    // Cargar datos de clientes para Delivery
                    CargarClientes();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
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
                Log.d("DEBUG_APP", "-> Consultando Vehiculochofer para fecha: " + fechaDisponibilidad.getText().toString());

                Query mrefVehiculoChofer = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Vehiculochofer")
                        .orderByChild("fecha")
                        .equalTo(fechaDisponibilidad.getText().toString());

                mrefVehiculoChofer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Vehiculochofer> vh = new ArrayList<>();
                        ArrayList<Vehiculos> vs2 = new ArrayList<>();

                        Log.d("DEBUG_APP", "-> Total Vehiculochofer encontrados: " + snapshot.getChildrenCount());

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Vehiculochofer vehiculochofer = postSnapshot.getValue(Vehiculochofer.class);
                            if (vehiculochofer != null && vehiculochofer.getChofer().equals(idChofer)) {
                                vehiculochofer.setId(postSnapshot.getKey());
                                vh.add(vehiculochofer);

                                Log.d("DEBUG_APP", "-> Vehiculochofer válido -> chofer: "
                                        + vehiculochofer.getChofer()
                                        + " vehiculo: " + vehiculochofer.getVehiculo());

                                DatabaseReference mrefVehiculo = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Vehiculos");

                                Log.d("DEBUG_APP", "-> Iniciando cargarVehiculos para ID: " + vehiculochofer.getVehiculo());

                                mrefVehiculo.child(vehiculochofer.getVehiculo())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Vehiculos vehiculoObtenido = snapshot.getValue(Vehiculos.class);
                                                if (vehiculoObtenido != null) {
                                                    vehiculoObtenido.setIdVehiculos(snapshot.getKey());
                                                    vs2.add(vehiculoObtenido);

                                                    ArrayAdapter<Vehiculos> adapter =
                                                            new ArrayAdapter<>(AsignarRutasActivity.this,
                                                                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                                                    vs2);
                                                    adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                                                    spVehiculos.setAdapter(adapter);

                                                    Log.d("DEBUG_APP", "-> Vehiculo obtenido: "
                                                            + vehiculoObtenido.getIdVehiculos()
                                                            + " - " + vehiculoObtenido.getMarca()
                                                            + " - " + vehiculoObtenido.getPlaca());
                                                    Log.d("DEBUG_APP", "-> Spinner cargado con " + vs2.size() + " vehículos");

                                                    // ⚡ Aquí ya es seguro mostrar los layouts y cargar clientes
                                                    Log.d("DEBUG_APP", "-> Vehículos encontrados, cargando clientes...");
                                                    CargarClientes();
                                                    layoutOculto.setVisibility(View.VISIBLE);
                                                    layoutImagenes.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("DEBUG_APP", "-> Error cargando vehículo: " + error.getMessage());
                                            }
                                        });
                            }
                        }

                        if (vh.size() == 0) {
                            Toast.makeText(AsignarRutasActivity.this,
                                    "El chofer no tiene vehículos asignados para esta fecha!",
                                    Toast.LENGTH_SHORT).show();
                            layoutOculto.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DEBUG_APP", "-> Error consultando Vehiculochofer: " + error.getMessage());
                    }
                });
            }
        });
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idRutaIntent != null) {
                    saveImage(idRutaIntent);
                    // ...
                } else {
                    // valida campos necesarios
                    if (idChofer == null || idChofer.isEmpty()
                            || idVehiculo == null || idVehiculo.isEmpty()
                            || fechaDisponibilidad.getText().toString().isEmpty()) {

                        Toast.makeText(AsignarRutasActivity.this, "Todos los datos son obligatorios!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference mrefRuta = FirebaseDatabase.getInstance().getReference().child("Ruta");
                    final String idRuta = mrefRuta.push().getKey();

                    // Construir objeto Ruta DE FORMA EXPLÍCITA CON SETTERS
                    Ruta rutaActual = new Ruta(); // constructor vacío
                    rutaActual.setChofer(idChofer);
                    rutaActual.setVehiculo(idVehiculo);
                    rutaActual.setEstado("P");
                    rutaActual.setFecha(fechaDisponibilidad.getText().toString());

                    if ("AGENCIA".equalsIgnoreCase(tipoEntrega)) {
                        rutaActual.setTipoEntrega("AGENCIA");
                        rutaActual.setAgencia(idAgencia);      // asigna agencia
                        // asegurarse de no dejar campos de delivery
                        rutaActual.setCliente(null);
                        rutaActual.setDireccion(idDireccion);

                    } else { // DELIVERY
                        rutaActual.setTipoEntrega("DELIVERY");
                        rutaActual.setCliente(idCliente);            // id del cliente seleccionado
                        rutaActual.setDireccion(idDireccion); // id o texto de la dirección seleccionada
                        // eliminar/limpiar el campo agencia para que no se guarde en Firebase
                        rutaActual.setAgencia(null);
                        // si quieres mantener compatibilidad con un campo 'direccion', puedes:
                        //rutaActual.setDireccion(idDireccion); // opcional: si tu negocio requiere llenar 'direccion'
                    }

                    // Guardar en Firebase (campos null no se guardan)
                    mrefRuta.child(idRuta).setValue(rutaActual).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                saveImage(idRuta);
                                Toast.makeText(AsignarRutasActivity.this, "Se ha grabado correctamente la información!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AsignarRutasActivity.this, AnimacionCheckActivity.class);
                                startActivity(intent);
                                onBackPressed();
                            } else {
                                Toast.makeText(AsignarRutasActivity.this, "Error al grabar: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
        btnVisualizar.setOnClickListener(v -> {
            if (!mImageFiles.isEmpty()) {
                // 👉 Mostrar fotos locales
                currentIndex = 0;
                mostrarFotoActualLocal();

                linearLayoutImagen.setVisibility(View.VISIBLE);
                btnAnterior.setVisibility(View.VISIBLE);
                btnSiguiente.setVisibility(View.VISIBLE);

            } else if (!imagesUri.isEmpty()) {
                // 👉 Mostrar fotos remotas
                currentIndex = 0;
                mostrarFotoActualRemoto();

                linearLayoutImagen.setVisibility(View.VISIBLE);
                btnAnterior.setVisibility(View.VISIBLE);
                btnSiguiente.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(this, "No has tomado, subido o guardado ninguna foto", Toast.LENGTH_SHORT).show();
            }
        });

        btnTomarImagenSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AsignarRutasActivity.this, CamaraActivity.class);
                startActivityForResult(intent, 100);
                //abrirCamara();
            }
        });



        btnAnterior.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                if (!mImageFiles.isEmpty()) {
                    mostrarFotoActualLocal();
                } else {
                    mostrarFotoActualRemoto();
                }
            } else {
                Toast.makeText(this, "Ya estás en la primera foto", Toast.LENGTH_SHORT).show();
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (!mImageFiles.isEmpty() && currentIndex < mImageFiles.size() - 1) {
                currentIndex++;
                mostrarFotoActualLocal();
            } else if (mImageFiles.isEmpty() && currentIndex < imagesUri.size() - 1) {
                currentIndex++;
                mostrarFotoActualRemoto();
            } else {
                Toast.makeText(this, "Ya estás en la última foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarFotoActualLocal() {
        if (!mImageFiles.isEmpty() && currentIndex >= 0 && currentIndex < mImageFiles.size()) {
            File file = mImageFiles.get(currentIndex);
            imageSwitcher.setImageURI(Uri.fromFile(file));
        }
    }

    private void mostrarFotoActualRemoto() {
        if (!imagesUri.isEmpty() && currentIndex >= 0 && currentIndex < imagesUri.size()) {
            Uri uri = imagesUri.get(currentIndex);
            Glide.with(this).load(uri).into(imageSwitcher);
        }
    }

    private void mostrarFotoActual() {
        if (!mImageFiles.isEmpty() && currentIndex >= 0 && currentIndex < mImageFiles.size()) {
            File file = mImageFiles.get(currentIndex);
            imageSwitcher.setImageURI(Uri.fromFile(file));
        }
    }
    private void CargarAgencias(){
        DatabaseReference mrefAgencias = FirebaseDatabase.getInstance().getReference().child("Agencias");
        mrefAgencias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Agencia> ags = new ArrayList<>();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Agencia ag = postSnapshot.getValue(Agencia.class);
                    ag.setIdAgencia(postSnapshot.getKey());
                    ags.add(ag);
                }
                ArrayAdapter<Agencia> adapterAgencia = new ArrayAdapter<>(AsignarRutasActivity.this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ags);
                spAgencias.setAdapter(adapterAgencia);

                spAgencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Agencia a = (Agencia) parent.getItemAtPosition(position);
                        idAgencia = a.getIdAgencia();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
    private void CargarClientes() {
        DatabaseReference mrefClientes = FirebaseDatabase.getInstance()
                .getReference()
                .child("Clientes");

        mrefClientes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Clientes> cl = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Clientes cliente = postSnapshot.getValue(Clientes.class);
                    if (cliente != null) {
                        cliente.setIdCliente(postSnapshot.getKey());
                        cl.add(cliente);
                        Log.d("DEBUG_APP", "-> Cliente cargado: " + cliente.getNombres());
                    }
                }

                // ⚡️ Mostrar layouts **una sola vez** después de cargar todos los clientes
                if (!cl.isEmpty()) {
                    layoutOculto.setVisibility(View.VISIBLE);
                    layoutImagenes.setVisibility(View.VISIBLE);
                }

                // Adapter para Spinner
                ArrayAdapter<Clientes> adapterCliente = new ArrayAdapter<>(
                        AsignarRutasActivity.this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        cl
                );
                adapterCliente.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                spClientes.setAdapter(adapterCliente);

                // Seleccionar primer cliente por defecto
                if (!cl.isEmpty()) {
                    spClientes.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DEBUG_APP", "Error al cargar clientes: " + error.getMessage());
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> fotosUris = data.getStringArrayListExtra("fotos_uris");

            if (fotosUris != null && !fotosUris.isEmpty()) {
                for (String path : fotosUris) {
                    File file = new File(path);
                    if (file.exists()) {
                        mImageFiles.add(file); // ✅ solo guardamos en la lista
                    }
                }
                Toast.makeText(this, "Se guardaron " + fotosUris.size() + " fotos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void lanzarIntentCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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

    // Manejar la respuesta del permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarIntentCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}