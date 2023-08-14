package com.ilender.transportesforilender.ui.chofer;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.adapters.ChoferAdapter;
import com.ilender.transportesforilender.databinding.FragmentChoferBinding;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Transportistas;


import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChoferFragment extends Fragment {

    private FragmentChoferBinding binding;
    private RecyclerView mRecyclerView;
    private ChoferAdapter choferAdapter;
    private Spinner spTipoChofer;
    private DatabaseReference mDatabaseRef;
    private String tipo;
    private Button btnConsultarDispo;
    private EditText fechaDisponibilidad;
    private View root;
    private String strDate, idTransportista;
    LinearLayoutManager lm;
    final private int REQUEST_CODE_ASK_PERMISSION=111;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChoferBinding.inflate(inflater,container,false);
        root = binding.getRoot();
        fechaDisponibilidad = root.findViewById(R.id.edtFechaDisponibilidadChofer);
        strDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        fechaDisponibilidad.setText(strDate);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Choferes");
        mRecyclerView = root.findViewById(R.id.rcvChoferes);
        lm = new LinearLayoutManager(root.getContext());
        btnConsultarDispo = root.findViewById(R.id.btnConsultarDisponibilidadChofer);
        mRecyclerView.setLayoutManager(lm);
        spTipoChofer = root.findViewById(R.id.spTipoChofer);
        spTipoChofer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Transportistas a = (Transportistas)parent.getItemAtPosition(position);
                idTransportista=a.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fechaDisponibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CargarFecha();
            }
        });
        btnConsultarDispo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarDisponibilidad();
            }
        });
        listarChoferes();
        listarTransportistas();
        SolicitarPermiso();
        return root;
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

    private void CargarFecha(){
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(root.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                fechaDisponibilidad.setText("");
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                fechaDisponibilidad.setText(DateFormat.format("dd/MM/yyyy",calendar1));
            }
        }, YEAR, MONTH , DATE);
        datePickerDialog.show();
    }
    private void listarChoferes(){
        fechaDisponibilidad.setText(strDate);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Choferes> lstChoferes = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Choferes chofer = postSnapshot.getValue(Choferes.class);
                    chofer.setIdChofer(postSnapshot.getKey());
                    lstChoferes.add(chofer);
                }
                choferAdapter = new ChoferAdapter(root.getContext(),lstChoferes,strDate);
                mRecyclerView.setAdapter(choferAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listarTransportistas(){
        DatabaseReference mTransportistas = FirebaseDatabase.getInstance().getReference().child("Transportistas");
        mTransportistas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Transportistas> lstTransportistas = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Transportistas transportista = postSnapshot.getValue(Transportistas.class);
                    transportista.setId(postSnapshot.getKey());
                    lstTransportistas.add(transportista);
                }

                ArrayAdapter<Transportistas> adapter = new ArrayAdapter<Transportistas>(root.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,lstTransportistas);
                adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                spTipoChofer.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void consultarDisponibilidad(){
        Query filtro = mDatabaseRef.orderByChild("transportista").equalTo(idTransportista);
        filtro.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Choferes> lstChoferes = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Choferes chofer = postSnapshot.getValue(Choferes.class);
                    chofer.setIdChofer(postSnapshot.getKey());
                    lstChoferes.add(chofer);
                }
                choferAdapter = new ChoferAdapter(root.getContext(), lstChoferes, fechaDisponibilidad.getText().toString());
                mRecyclerView.setAdapter(choferAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}