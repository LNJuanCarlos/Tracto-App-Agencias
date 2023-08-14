package com.ilender.transportesforilender.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.ui.cliente.ClienteFragment;

public class FormularioClienteActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private EditText edtNombresCliente, edtTelefonoCliente, edtContactoCliente, edtDocumentoCliente;
    private Button btnGrabarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_cliente);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Clientes");
        edtNombresCliente = findViewById(R.id.edtNombreClienteR);
        edtTelefonoCliente = findViewById(R.id.edtTelefonoContactoClienteR);
        edtContactoCliente = findViewById(R.id.edtContactoClienteR);
        edtDocumentoCliente = findViewById(R.id.edtDocumentoClienteR);
        btnGrabarCliente = findViewById(R.id.btnGrabarCliente);

        btnGrabarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombres = edtNombresCliente.getText().toString();
                String telefono = edtTelefonoCliente.getText().toString();
                String contacto = edtContactoCliente.getText().toString();
                String documento = edtDocumentoCliente.getText().toString();
                if(nombres.equals("")||telefono.equals("")||contacto.equals("")
                ||documento.equals("")||nombres.equals(null)||telefono.equals(null)
                ||contacto.equals(null)||documento.equals(null)){
                    Toast.makeText(FormularioClienteActivity.this, "Todos los campos son obligatorios!", Toast.LENGTH_SHORT).show();
                }else{
                    Clientes nuevoCliente = new Clientes(contacto,"A",documento,nombres,telefono);
                    String id = mDatabaseReference.push().getKey();

                    mDatabaseReference.child(id).setValue(nuevoCliente).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(FormularioClienteActivity.this, "Se ha registrado exitosamente al cliente!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FormularioClienteActivity.this, ClienteFragment.class);
                            FormularioClienteActivity.this.startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

    }
}