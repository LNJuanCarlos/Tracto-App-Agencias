package com.ilender.transportesforilender.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Transportistas;
import com.ilender.transportesforilender.model.Vehiculochofer;
import com.ilender.transportesforilender.model.Vehiculos;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VehiculoAdapter extends RecyclerView.Adapter<VehiculoAdapter.VehiculoViewHolder>{

    private Context context;
    private List<Vehiculos> listVehiculos;
    private String fecha;

    public VehiculoAdapter(Context con, List<Vehiculos> listVehiculos, String fecha){
        this.context=con;
        this.listVehiculos=listVehiculos;
        this.fecha=fecha;
    }

    public void updateDataVehiculo(List<Vehiculos> newVehiculoList) {
        this.listVehiculos = newVehiculoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehiculoAdapter.VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_vehiculo,parent,false);
        VehiculoAdapter.VehiculoViewHolder holder = new VehiculoAdapter.VehiculoViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoAdapter.VehiculoViewHolder holder, int position) {
        final Vehiculos vehiculos = listVehiculos.get(position);
        holder.marcaVehiculo.setText(vehiculos.getMarca());
        holder.placaVehiculo.setText(vehiculos.getPlaca());
        holder.id = vehiculos.getIdVehiculos();
        holder.textotransportista = vehiculos.getTransportista();

        if(vehiculos.getTipo().equals("I")){
            holder.tipoVehiculo.setText("INTERNO");
        }else{
            holder.tipoVehiculo.setText("EXTERNO");
        }

        Query mRefTrans = FirebaseDatabase.getInstance().getReference().child("Transportistas").child(vehiculos.getTransportista());
        mRefTrans.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Transportistas transportista = snapshot.getValue(Transportistas.class);
                holder.transportista.setText(transportista.getNombre());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Query mRefChoferVehi = FirebaseDatabase.getInstance().getReference().child("Vehiculochofer")
                .orderByChild("fecha").equalTo(fecha);

        mRefChoferVehi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Vehiculochofer> al = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Vehiculochofer incidencia = postSnapshot.getValue(Vehiculochofer.class);
                    incidencia.setId(postSnapshot.getKey());
                    al.add(incidencia);
                }
                Boolean estado = false;
                String chofer = "";
                for(int i = 0; i<al.size();i++){
                    if(vehiculos.getIdVehiculos().equals(al.get(i).getVehiculo())){

                        estado = true;
                        chofer = al.get(i).getChofer();
                    }
                }
                if(estado == true){
                    holder.estadoVehiculo.setText("OCUPADO");
                    holder.estadoVehiculo.setTextColor(Color.parseColor("#FF3838"));
                    holder.layoutVehiculoVehiculo.setVisibility(View.VISIBLE);
                    DatabaseReference mRefChofer = FirebaseDatabase.getInstance().getReference().child("Choferes").child(chofer);

                    mRefChofer.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Choferes chofer = snapshot.getValue(Choferes.class);
                            holder.choferVehiculo.setText(chofer.getNombres());
                            holder.btnAsignarVehiculo.setEnabled(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    holder.estadoVehiculo.setText("LIBRE");
                    holder.estadoVehiculo.setTextColor(Color.parseColor("#4B63FF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        holder.setOnClickListeners();

    }

    @Override
    public int getItemCount() {
        return listVehiculos.size();
    }

    public class VehiculoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView estadoVehiculo, marcaVehiculo, placaVehiculo,choferVehiculo, tipoVehiculo, transportista;
        public Button btnAsignarVehiculo, btnGrabar;

        public View layoutVehiculoVehiculo;

        public EditText edtFechaAsignarChofer;

        public Spinner spChofer;

        public LinearLayout layoutOcultoAsignarChofer;

        public String id;

        public String idChofer;

        public String tipo, textotransportista;

        public VehiculoViewHolder(@NonNull View itemView){
            super(itemView);

            estadoVehiculo=itemView.findViewById(R.id.txtEstadoVehiculo);
            marcaVehiculo=itemView.findViewById(R.id.txtMarcaVehiculo);
            placaVehiculo=itemView.findViewById(R.id.txtPlacaVehiculo);
            choferVehiculo=itemView.findViewById(R.id.txtChoferVehiculo);
            btnAsignarVehiculo=itemView.findViewById(R.id.btnAsignarConductor);
            layoutVehiculoVehiculo=itemView.findViewById(R.id.layoutChoferVehiculo);
            layoutOcultoAsignarChofer=itemView.findViewById(R.id.layoutOcultoAsignarChofer);
            edtFechaAsignarChofer=itemView.findViewById(R.id.edtFechaAsignarChofer);
            spChofer=itemView.findViewById(R.id.spChoferVehiculo);
            btnGrabar=itemView.findViewById(R.id.btnGrabarAsignarChofer);
            tipoVehiculo = itemView.findViewById(R.id.txtTipoVehiculo);
            transportista = itemView.findViewById(R.id.txtTransportistaVehi);
        }

        void setOnClickListeners(){
            btnAsignarVehiculo.setOnClickListener(this);
            btnGrabar.setOnClickListener(this);
        }
        public void onClick(View v){
            switch(v.getId()){
                case R.id.btnAsignarConductor:
                    if(layoutOcultoAsignarChofer.getVisibility()==View.GONE){
                        layoutOcultoAsignarChofer.setVisibility(View.VISIBLE);

                        DatabaseReference choferes = FirebaseDatabase.getInstance().getReference().child("Choferes");
                        Query choferesFiltro = choferes.orderByChild("transportista").equalTo(textotransportista);

                        choferesFiltro.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Choferes> al = new ArrayList<>();
                                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                                    Choferes chofer = postSnapshot.getValue(Choferes.class);
                                    chofer.setIdChofer(postSnapshot.getKey());
                                    al.add(chofer);
                                }
                                ArrayAdapter<Choferes> adapter = new ArrayAdapter<Choferes>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,al);
                                adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                                spChofer.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        spChofer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Choferes a = (Choferes)parent.getItemAtPosition(position);
                                idChofer=a.getIdChofer();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        Calendar calendar = Calendar.getInstance();
                        int YEAR = calendar.get(Calendar.YEAR);
                        int MONTH = calendar.get(Calendar.MONTH);
                        int DATE = calendar.get(Calendar.DATE);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                edtFechaAsignarChofer.setText("");
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.YEAR, year);
                                calendar1.set(Calendar.MONTH, month);
                                calendar1.set(Calendar.DATE, date);
                                edtFechaAsignarChofer.setText(DateFormat.format("dd/MM/yyyy", calendar1));
                            }
                        }, YEAR, MONTH, DATE);
                        datePickerDialog.show();
                    }else{
                        layoutOcultoAsignarChofer.setVisibility(View.GONE);
                    }
                    break;

                case R.id.btnGrabarAsignarChofer:
                    if(idChofer.toString().equals("")||edtFechaAsignarChofer.getText().toString().equals("")){
                        Toast.makeText(context, "Todos los campos son obligarotios!!", Toast.LENGTH_SHORT).show();
                    }else{
                        Vehiculochofer vehiculochofer = new Vehiculochofer(idChofer,edtFechaAsignarChofer.getText().toString(),id);
                        DatabaseReference mDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Vehiculochofer");
                        String idVC = mDataBaseReference.push().getKey();

                        mDataBaseReference.child(idVC).setValue(vehiculochofer).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Se ha grabado correctamente!", Toast.LENGTH_SHORT).show();
                                layoutOcultoAsignarChofer.setVisibility(View.GONE);
                            }
                        });
                    }
                    break;
            }
        }
    }
}
