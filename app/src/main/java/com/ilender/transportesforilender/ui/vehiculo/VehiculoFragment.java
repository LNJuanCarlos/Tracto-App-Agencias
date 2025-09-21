package com.ilender.transportesforilender.ui.vehiculo;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.adapters.VehiculoAdapter;
import com.ilender.transportesforilender.databinding.FragmentVehiculoBinding;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Transportistas;
import com.ilender.transportesforilender.model.Vehiculos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class VehiculoFragment extends Fragment {

    private AdView mAdView8;
    private FragmentVehiculoBinding binding;
    private RecyclerView mRecyclerView;
    private VehiculoAdapter vehiculoAdapter;
    private Spinner spTipoVehiculo;
    private DatabaseReference mDatabaseRef;
    private Button btnConsultarDispo;
    private EditText fechaDisponibilidad;
    private View root;
    private String tipo, idTransportista;

    LinearLayoutManager lm;

    private EditText edtTerminoVehiculoPlaca;
    private Button btnBuscarVehiculoPlaca;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentVehiculoBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        fechaDisponibilidad = root.findViewById(R.id.edtFechaDisponibilidad);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Vehiculos");
        mRecyclerView = root.findViewById(R.id.rcvVehiculos);
        lm = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(lm);
        spTipoVehiculo = root.findViewById(R.id.spTipoVehiculo);
        edtTerminoVehiculoPlaca = root.findViewById(R.id.edtTerminoVehiculoPlaca);
        btnBuscarVehiculoPlaca = root.findViewById(R.id.btnBuscarVehiculoPlaca);

        MobileAds.initialize(getContext());

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        /*mAdView8 = root.findViewById(R.id.adView8);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView8.loadAd(adRequest);*/


        spTipoVehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Transportistas a = (Transportistas)parent.getItemAtPosition(position);
                idTransportista=a.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnConsultarDispo = root.findViewById(R.id.btnConsultarDisponibilidadVehiculo);

        edtTerminoVehiculoPlaca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String searchTerm = s.toString();
                searchDatabase(searchTerm);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnBuscarVehiculoPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Realizar la búsqueda utilizando el valor ingresado en el EditText
                String searchTerm = edtTerminoVehiculoPlaca.getText().toString();
                searchDatabase(searchTerm);
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
        listarVehiculos();
        listarTransportistas();
        return root;
    }

    private void searchDatabase(String searchTerm) {
        Query query = mDatabaseRef.orderByChild("placa");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Vehiculos> lstVehiculos = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Vehiculos vehi = postSnapshot.getValue(Vehiculos.class);
                    vehi.setIdVehiculos(postSnapshot.getKey());

                    // Realiza una búsqueda flexible en la aplicación Android
                    if (vehi.getPlaca().toLowerCase().contains(searchTerm.toLowerCase())) {
                        lstVehiculos.add(vehi);
                    }
                }

                // Actualiza los datos en el adaptador en lugar de crear uno nuevo.
                vehiculoAdapter.updateDataVehiculo(lstVehiculos);
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

    private void listarTransportistas(){
        DatabaseReference mTransportistas = FirebaseDatabase.getInstance().getReference().child("Transportistas");
        mTransportistas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Transportistas> lstTransportistas = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    Transportistas transportista = ds.getValue(Transportistas.class);
                    transportista.setId(ds.getKey());
                    lstTransportistas.add(transportista);
                }

                ArrayAdapter<Transportistas> adapter = new ArrayAdapter<Transportistas>(root.getContext(),androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lstTransportistas);
                adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                spTipoVehiculo.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CargarFecha() {
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
                fechaDisponibilidad.setText(DateFormat.format("dd/MM/yyyy", calendar1));
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }
    private void listarVehiculos(){
        String strDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        fechaDisponibilidad.setText(strDate);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Vehiculos> lstVehiculos = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Vehiculos vehiculo = postSnapshot.getValue(Vehiculos.class);
                    vehiculo.setIdVehiculos(postSnapshot.getKey());
                    lstVehiculos.add(vehiculo);
                }
                vehiculoAdapter = new VehiculoAdapter(root.getContext(), lstVehiculos,strDate);
                mRecyclerView.setAdapter(vehiculoAdapter);
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
                ArrayList<Vehiculos> lstVehiculos = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Vehiculos vehiculo = postSnapshot.getValue(Vehiculos.class);
                    vehiculo.setIdVehiculos(postSnapshot.getKey());
                    lstVehiculos.add(vehiculo);
                }
                vehiculoAdapter = new VehiculoAdapter(root.getContext(),lstVehiculos, fechaDisponibilidad.getText().toString());
                mRecyclerView.setAdapter(vehiculoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
