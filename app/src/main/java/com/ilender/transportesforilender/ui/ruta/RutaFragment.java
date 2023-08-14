package com.ilender.transportesforilender.ui.ruta;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.adapters.ChoferAdapter;
import com.ilender.transportesforilender.adapters.RutaTrayectoriaAdapter;
import com.ilender.transportesforilender.databinding.FragmentRutaBinding;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Ruta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RutaFragment extends Fragment {

    private FragmentRutaBinding binding;
    private View root;
    private RutaTrayectoriaAdapter rutaAdapter;
    private RecyclerView mRecyclerView;
    private ChoferAdapter choferAdapter;
    private DatabaseReference mDatabaseRef;
    public RecyclerView rcvRutasChofer;
    public List<Ruta> listaRutas;
    private Button btnConsultarDispo;
    private EditText fechaDisponibilidad;
    private String strDate;
    private Spinner spChofer;
    LinearLayoutManager lm;
    private String idChofer;

    public RutaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRutaBinding.inflate(inflater, container,false);
        root = binding.getRoot();
        fechaDisponibilidad = root.findViewById(R.id.edtFechaTrayectoria);
        strDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        fechaDisponibilidad.setText(strDate);
        spChofer = root.findViewById(R.id.spChoferTrayectoria);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Choferes");
        mRecyclerView = root.findViewById(R.id.rcvRutasTrayectoria);
        lm = new LinearLayoutManager(root.getContext());
        btnConsultarDispo = root.findViewById(R.id.btnConsultarTrayectoria);
        mRecyclerView.setLayoutManager(lm);

        fechaDisponibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CargarFecha();
            }
        });
        btnConsultarDispo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fechaSeleccionada = fechaDisponibilidad.getText().toString();
                consultarDisponibilidad(fechaSeleccionada);
            }
        });



        spChofer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                Choferes a = (Choferes)parent.getItemAtPosition(position);
                idChofer=a.getIdChofer();
                //////////////////////////////////////////////////////////////////////
                String fechaSeleccionada = fechaDisponibilidad.getText().toString();
                if (a.getNombres().equals("Mostrar todos")) {
                    idChofer = null;
                }

                consultarDisponibilidad(fechaSeleccionada);
                //////////////////////////////////////////////////////////////////////
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        listarChoferes();
        return root;
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
                fechaDisponibilidad.setText(DateFormat.format("dd/MM/yyyy", calendar1));

                String selectedDate = DateFormat.format("dd/MM/yyyy",calendar1).toString();

                Query mrefChoferVehi = FirebaseDatabase.getInstance().getReference().child("Ruta")
                        .orderByChild("fecha").equalTo(selectedDate);

                mrefChoferVehi.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Ruta> al = new ArrayList<>();
                        for(DataSnapshot postSnapshot : snapshot.getChildren()){
                            Ruta ruta = postSnapshot.getValue(Ruta.class);
                            if(ruta.getChofer().equals(idChofer)){
                                ruta.setIdRuta(postSnapshot.getKey());
                                al.add(ruta);
                            }
                        }
                        rutaAdapter = new RutaTrayectoriaAdapter(root.getContext(), al);
                        mRecyclerView.setAdapter(rutaAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }


    private void listarChoferes(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Choferes> lstChoferes = new ArrayList<>();

                // Agregar el elemento adicional "Mostrar todos" al inicio de la lista
                Choferes mostrarTodos = new Choferes();
                mostrarTodos.setNombres("---MOSTRAR TODOS---");
                lstChoferes.add(mostrarTodos);

                // Luego, agregar los choferes reales a la lista
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Choferes chofer = postSnapshot.getValue(Choferes.class);
                    chofer.setIdChofer(postSnapshot.getKey());
                    lstChoferes.add(chofer);
                }

                ArrayAdapter<Choferes> adapter = new ArrayAdapter<>(
                        root.getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        lstChoferes
                );
                spChofer.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
            });
        }

    private void consultarDisponibilidad(String fechaSeleccionada){
        Query query = FirebaseDatabase.getInstance().getReference().child("Ruta")
                .orderByChild("fecha").equalTo(fechaSeleccionada);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Ruta> al = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Ruta ruta = dataSnapshot.getValue(Ruta.class);
                    if(idChofer == null || ruta.getChofer().equals(idChofer)){
                        ruta.setIdRuta(dataSnapshot.getKey());
                        al.add(ruta);
                    }
                }
                rutaAdapter = new RutaTrayectoriaAdapter(root.getContext(),al);
                mRecyclerView.setAdapter(rutaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}