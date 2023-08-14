package com.ilender.transportesforilender.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.adapters.ItemDireccionAdapter;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.ui.cliente.ClienteFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class DireccionClientesActivity extends AppCompatActivity {

    private String idCliente;
    private LinearLayout layoutDireccion, layoutDistrito;
    private EditText edtDistritoClienteAdd, edtDireccionClienteAdd;
    private EditText edtNombresCliente, edtTelefonoCliente, edtContactoCliente, edtDocumentoCliente;
    private Button btnGrabarCliente;
    private FloatingActionButton floatingActionButton;
    private Button btnGrabarDireccionAdd;
    private RecyclerView mRecyclerView;
    private ItemDireccionAdapter itemDireccionAdapter;
    private DatabaseReference mReferenceCliente, mReferenceDirecciones, mReferenceClienteRaiz;
    private View root;

    LinearLayoutManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_clientes);

        idCliente = getIntent().getExtras().getString("idCliente");
        mReferenceCliente = FirebaseDatabase.getInstance().getReference().child("Clientes").child(idCliente);
        mReferenceDirecciones = FirebaseDatabase.getInstance().getReference().child("Direccion");
        mRecyclerView = findViewById(R.id.rcvDireccionClientesA);
        lm = new LinearLayoutManager(DireccionClientesActivity.this);
        mRecyclerView.setLayoutManager(lm);
        edtNombresCliente = findViewById(R.id.edtNombreClienteAC);
        edtTelefonoCliente = findViewById(R.id.edtTelefonoContactoClienteAC);
        edtContactoCliente = findViewById(R.id.edtContactoClienteAC);
        edtDocumentoCliente = findViewById(R.id.edtDocumentoClienteAC);
        btnGrabarCliente = findViewById(R.id.btnActualizarClienteAC);
        edtDistritoClienteAdd = findViewById(R.id.edtDistritoClienteAdd);
        edtDireccionClienteAdd = findViewById(R.id.edtDireccionClienteAdd);
        btnGrabarDireccionAdd = findViewById(R.id.btnGranarDireccionAdd);
        layoutDireccion = findViewById(R.id.layoutOcultoDireccionAdd);
        layoutDistrito = findViewById(R.id.layoutDistritoClienteAdd);
        floatingActionButton = findViewById(R.id.floatingActionButtonDireccion);

        obtenerDatos();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarLayout();
            }
        });

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
                    Toast.makeText(DireccionClientesActivity.this, "Todos los campos son obligatorios!", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap map = new HashMap();
                    map.put("contacto", contacto);
                    map.put("estado", "A");
                    map.put("identificador", documento);
                    map.put("nombres",nombres);
                    map.put("telefono", telefono);

                    mReferenceCliente.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(DireccionClientesActivity.this, "Se ha actualizado exitosamente al cliente!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DireccionClientesActivity.this, ClienteFragment.class);
                            DireccionClientesActivity.this.startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        btnGrabarDireccionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtDireccionClienteAdd.getText().toString().equals("") && edtDistritoClienteAdd.getText().toString().equals("") ||
                edtDireccionClienteAdd.getText().equals(null) && edtDistritoClienteAdd.getText().equals(null)){
                    Toast.makeText(DireccionClientesActivity.this, "Los campos son obligatorios!", Toast.LENGTH_SHORT).show();
                }else{
                    Direccion nuevaDireccion = new Direccion(idCliente,edtDireccionClienteAdd.getText().toString(),edtDistritoClienteAdd.getText().toString());
                    String id = mReferenceDirecciones.push().getKey();

                    mReferenceDirecciones.child(id).setValue(nuevaDireccion).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DireccionClientesActivity.this, "Se ha agregado exitosamente la dirección del cliente!", Toast.LENGTH_SHORT).show();
                            edtDireccionClienteAdd.setText("");
                            edtDistritoClienteAdd.setText("");
                            ocultarLayout();
                        }
                    });
                }
            }
        });
    }

    public void mostrarLayout(){
        layoutDireccion.setVisibility(View.VISIBLE);
        layoutDistrito.setVisibility(View.VISIBLE);
        btnGrabarDireccionAdd.setVisibility(View.VISIBLE);
    }

    public void ocultarLayout(){
        layoutDireccion.setVisibility(View.GONE);
        layoutDistrito.setVisibility(View.GONE);
        btnGrabarDireccionAdd.setVisibility(View.GONE);
    }

    private void obtenerDatos(){
        mReferenceCliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Clientes cliente = snapshot.getValue(Clientes.class);
                edtNombresCliente.setText(cliente.getNombres());
                edtDocumentoCliente.setText(cliente.getIdentificador());
                edtContactoCliente.setText(cliente.getContacto());
                edtTelefonoCliente.setText(cliente.getTelefono());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mReferenceDirecciones.orderByChild("cliente").equalTo(idCliente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Direccion> lstDireccion = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Direccion direccion = postSnapshot.getValue(Direccion.class);
                    direccion.setIdDireccion(postSnapshot.getKey());
                    lstDireccion.add(direccion);
                }
                itemDireccionAdapter = new ItemDireccionAdapter(DireccionClientesActivity.this,lstDireccion,"A");
                mRecyclerView.setAdapter(itemDireccionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}