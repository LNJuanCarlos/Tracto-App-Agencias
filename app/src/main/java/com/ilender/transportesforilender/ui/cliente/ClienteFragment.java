package com.ilender.transportesforilender.ui.cliente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.activitys.FormularioClienteActivity;
import com.ilender.transportesforilender.adapters.ClienteAdapter;
import com.ilender.transportesforilender.databinding.FragmentClienteBinding;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Clientes;

import java.util.ArrayList;


public class ClienteFragment extends Fragment {

    private FragmentClienteBinding binding;
    private RecyclerView mRecyclerView;
    private ClienteAdapter clienteAdapter;
    private DatabaseReference mDatabaseRef;
    private FloatingActionButton fabAgregarCliente;
    final private int REQUEST_CODE_ASK_PERMISSION=111;
    private View root;
    LinearLayoutManager lm;

    private EditText edtTerminoCliente;
    private Button btnBuscarCliente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClienteBinding.inflate(inflater, container,false);
        root = binding.getRoot();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Clientes");
        mRecyclerView = root.findViewById(R.id.rcvClientes);
        fabAgregarCliente = root.findViewById(R.id.floatingActionButtonAgregarCliente);
        lm = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(lm);
        edtTerminoCliente = root.findViewById(R.id.edtTerminoCliente);
        btnBuscarCliente = root.findViewById(R.id.btnBuscarCliente);

        edtTerminoCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = s.toString();
                searchDatabase(searchTerm);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnBuscarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // Realizar la búsqueda utilizando el valor ingresado en el EditText
                    String searchTerm = edtTerminoCliente.getText().toString();
                    searchDatabase(searchTerm);

            }
        });

        fabAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(root.getContext(), FormularioClienteActivity.class);
                root.getContext().startActivity(i);
            }
        });
        listarClientes();
        SolicitarPermiso();
        return root;
    }

    private void searchDatabase(String searchTerm) {
        Query query = mDatabaseRef.orderByChild("nombres");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Clientes> listClientes = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Clientes clientes = postSnapshot.getValue(Clientes.class);
                    clientes.setIdCliente(postSnapshot.getKey());

                    // Realiza una búsqueda flexible en la aplicación Android
                    if (clientes.getNombres().toLowerCase().contains(searchTerm.toLowerCase())) {
                        listClientes.add(clientes);
                    }
                }

                // Actualiza los datos en el adaptador en lugar de crear uno nuevo.
                clienteAdapter.updateDataCliente(listClientes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo de errores, si es necesario.
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void SolicitarPermiso(){
        int permisoLlamada = ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.CALL_PHONE);
        if(permisoLlamada!= PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},REQUEST_CODE_ASK_PERMISSION);
            }
        }
    }

    private void listarClientes(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Clientes> lstClientes = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Clientes cliente = postSnapshot.getValue(Clientes.class);
                    cliente.setIdCliente(postSnapshot.getKey());
                    lstClientes.add(cliente);
                }

                clienteAdapter = new ClienteAdapter(root.getContext(), lstClientes);
                mRecyclerView.setAdapter(clienteAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}