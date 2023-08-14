package com.ilender.transportesforilender.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Seguimientoruta;
import com.ilender.transportesforilender.model.Vehiculos;

import java.util.ArrayList;
import java.util.List;

public class RutaTrayectoriaAdapter extends RecyclerView.Adapter<RutaTrayectoriaAdapter.RutaTrayectoriaViewHolder>{

    private Context context;
    private List<Ruta> listRuta;
    LinearLayoutManager lm;

    private SeguimientoAdapter seguimientoAdapter;

    public RutaTrayectoriaAdapter(Context con, List<Ruta> listRuta){
        this.context=con;
        this.listRuta=listRuta;
    }

    @NonNull
    @Override
    public RutaTrayectoriaAdapter.RutaTrayectoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_rutas_trayectoria,parent,false);
        RutaTrayectoriaAdapter.RutaTrayectoriaViewHolder holder = new RutaTrayectoriaAdapter.RutaTrayectoriaViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RutaTrayectoriaAdapter.RutaTrayectoriaViewHolder holder, int position) {
        final Ruta ruta = listRuta.get(position);
        lm = new LinearLayoutManager(context);
        holder.rcvTrayectoria.setLayoutManager(lm);


        if(ruta.getEstado().equals("P")){
            holder.txtestadoruta.setText("NO INICIADA");
            holder.txtestadoruta.setTextColor(Color.parseColor("#4B63FF"));
        }else if(ruta.getEstado().equals("I")){
            holder.txtestadoruta.setText("INICIADA");
            holder.txtestadoruta.setTextColor(Color.parseColor("#A2FF33"));
        }else if(ruta.getEstado().equals("S")){
            holder.txtestadoruta.setText("LLEGO AL CLIENTE");
            holder.txtestadoruta.setTextColor(Color.parseColor("#FC9C1B"));
        }else if(ruta.getEstado().equals("A")){
            holder.txtestadoruta.setText("ATENDIDO");
            holder.txtestadoruta.setTextColor(Color.parseColor("#FF2A3B"));
        }else{
            holder.txtestadoruta.setText("FINALIZADA");
            holder.txtestadoruta.setTextColor(Color.parseColor("#FC3929"));
        }

        DatabaseReference mrefVehiculo = FirebaseDatabase.getInstance().getReference().child("Vehiculos").child(ruta.getVehiculo());

        mrefVehiculo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vehiculos vehiculo = snapshot.getValue(Vehiculos.class);
                holder.txtVehiculoRutaTr.setText(vehiculo.getMarca() + " - " + vehiculo.getPlaca());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference mrefDireccion = FirebaseDatabase.getInstance().getReference().child("Direccion").child(ruta.getDireccion());

        mrefDireccion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Direccion direccion = snapshot.getValue(Direccion.class);
                holder.txtDireccionClienteRutaTr.setText(direccion.getDescripcion());
                holder.txtDistritoRutaTr.setText(direccion.getDistrito());
                DatabaseReference mrefCliente = FirebaseDatabase.getInstance().getReference().child("Clientes").child(direccion.getCliente());

                mrefCliente.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Clientes cliente = snapshot.getValue(Clientes.class);
                        holder.txtClienteTrayectoria.setText(cliente.getNombres());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query mrefSeguimientos = FirebaseDatabase.getInstance().getReference().child("Seguimientoruta");

                mrefSeguimientos.orderByChild("ruta").equalTo(ruta.getIdRuta()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Seguimientoruta> al = new ArrayList<>();
                        for(DataSnapshot postSnapshot : snapshot.getChildren()){
                            Seguimientoruta seguimiento = postSnapshot.getValue(Seguimientoruta.class);
                            seguimiento.setIdSeguimiento(postSnapshot.getKey());
                            al.add(seguimiento);
                        }
                        seguimientoAdapter = new SeguimientoAdapter(context, al);
                        holder.rcvTrayectoria.setAdapter(seguimientoAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listRuta.size();
    }

    public class RutaTrayectoriaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtVehiculoRutaTr, txtDistritoRutaTr, txtDireccionClienteRutaTr, txtClienteTrayectoria, txtestadoruta;
        public String id;
        public RecyclerView rcvTrayectoria;
        public LinearLayout layoutOculto;
        public Button btnVisualizarTrayectoriaRuta;

        public RutaTrayectoriaViewHolder(@NonNull View itemView){
            super(itemView);

            txtVehiculoRutaTr=itemView.findViewById(R.id.txtVehiculoRutaTr);
            txtDistritoRutaTr=itemView.findViewById(R.id.txtDistritoRutaTr);
            txtDireccionClienteRutaTr=itemView.findViewById(R.id.txtDireccionClienteRutaTr);
            txtestadoruta=itemView.findViewById(R.id.txtestadoruta);
            txtClienteTrayectoria=itemView.findViewById(R.id.txtClienteTrayectoria);
            btnVisualizarTrayectoriaRuta=itemView.findViewById(R.id.btnVisualizarTrayectoriaRuta);
            rcvTrayectoria=itemView.findViewById(R.id.rcvSeguimiento);
            layoutOculto=itemView.findViewById(R.id.layoutOcultoTrayectoria);
        }

        void setOnClickListeners(){
            btnVisualizarTrayectoriaRuta.setOnClickListener(this);
        }

        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnVisualizarTrayectoriaRuta:
                    if(layoutOculto.getVisibility()==View.GONE){
                        layoutOculto.setVisibility(View.VISIBLE);
                        btnVisualizarTrayectoriaRuta.setText("OCULTAR TRAYECTORIA");
                    }else{
                        layoutOculto.setVisibility(View.GONE);
                        btnVisualizarTrayectoriaRuta.setText("VISUALIZAR TRAYECTORIA");
                    }
                    break;
            }
        }
    }
}
