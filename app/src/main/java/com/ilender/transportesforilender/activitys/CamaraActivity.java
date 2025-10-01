package com.ilender.transportesforilender.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.ilender.transportesforilender.R;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CamaraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;

    private LinearLayout layoutThumbnails;
    private ImageButton btnCapturar;
    private Button btnListo;
    private Uri uriFoto;
    private File photoFile; // 👉 guardo el archivo creado
    private ArrayList<String> listaFotos = new ArrayList<>(); // ✅ paths absolutos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        btnListo = findViewById(R.id.btnListo);
        previewView = findViewById(R.id.previewView);
        btnCapturar = findViewById(R.id.btnCapturar);
        layoutThumbnails = findViewById(R.id.layoutThumbnails);


        // ✅ Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            iniciarCameraX(); // primera vez
        }

        // Inicializar CameraX
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);


        btnCapturar = findViewById(R.id.btnCapturar);

        btnCapturar.setOnClickListener(v -> {
            // 1. Crear el archivo donde guardar la foto
            File photoFile = new File(getExternalFilesDir(null),
                    "foto_" + System.currentTimeMillis() + ".jpg");

            // 2. Preparar opciones de salida
            ImageCapture.OutputFileOptions outputOptions =
                    new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            // 3. Tomar la foto con CameraX
            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            listaFotos.add(photoFile.getAbsolutePath());

                            runOnUiThread(() -> {
                                // Crear un ImageView para la miniatura
                                ImageView thumb = new ImageView(CamaraActivity.this);
                                int size = getResources().getDimensionPixelSize(R.dimen.thumbnail_size);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                                params.setMargins(8, 8, 8, 8);
                                thumb.setLayoutParams(params);
                                thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                thumb.setImageURI(Uri.fromFile(photoFile));

                                // Si quieres que al hacer click se elimine
                                thumb.setOnClickListener(v -> {
                                    layoutThumbnails.removeView(thumb);
                                    listaFotos.remove(photoFile.getAbsolutePath());
                                });

                                layoutThumbnails.addView(thumb);
                            });

                            Toast.makeText(CamaraActivity.this, "Foto capturada", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            exception.printStackTrace();
                        }
                    });
        });

        btnListo.setOnClickListener(v -> {
            if (!listaFotos.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra("fotos_uris", listaFotos); // 🔹 paths absolutos
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No tomaste ninguna foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = new File(getExternalFilesDir(null),
                "foto_" + System.currentTimeMillis() + ".jpg");

        uriFoto = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                photoFile
        );

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null && photoFile.exists()) {
                listaFotos.add(photoFile.getAbsolutePath()); // 🔹 guardo el path absoluto
                Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
    private void iniciarCameraX() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarCameraX();
            } else {
                Toast.makeText(this, "Se requiere el permiso de cámara", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}