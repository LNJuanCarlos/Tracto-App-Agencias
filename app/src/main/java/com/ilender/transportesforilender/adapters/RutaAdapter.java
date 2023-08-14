package com.ilender.transportesforilender.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.activitys.AsignarRutasActivity;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Vehiculos;

import java.util.List;

public class RutaAdapter extends RecyclerView.Adapter<RutaAdapter.RutaViewHolder>{

    private Context context;
    private List<Ruta> listRuta;

    public RutaAdapter(Context con, List<Ruta> listRuta){
        this.context=con;
        this.listRuta=listRuta;
    }

    @NonNull
    @Override
    public RutaAdapter.RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_rutas,parent,false);
        RutaAdapter.RutaViewHolder holder = new RutaAdapter.RutaViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RutaAdapter.RutaViewHolder holder, int position) {
        final Ruta ruta = listRuta.get(position);

        holder.id = ruta.getIdRuta();
        DatabaseReference mrefVehiculo = FirebaseDatabase.getInstance().getReference().child("Vehiculos").child(ruta.getVehiculo());

        mrefVehiculo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vehiculos vehiculo = snapshot.getValue(Vehiculos.class);
                holder.txtVehiculoRuta.setText(vehiculo.getMarca() + " - " + vehiculo.getPlaca());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query mrefSeguimiento = FirebaseDatabase.getInstance().getReference().child("Seguimientoruta");
        mrefSeguimiento.orderByChild("ruta").equalTo(ruta.getIdRuta()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.btnEliminarRuta.setEnabled(false);
                }
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
                holder.txtDireccionClienteRuta.setText(direccion.getDescripcion());
                holder.txtDistritoRuta.setText(direccion.getDistrito());
                DatabaseReference mrefCliente = FirebaseDatabase.getInstance().getReference().child("Clientes").child(direccion.getCliente());

                mrefCliente.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Clientes cliente = snapshot.getValue(Clientes.class);
                        holder.txtClienteRuta.setText(cliente.getNombres());
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
    public class RutaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtVehiculoRuta,txtDistritoRuta,txtDireccionClienteRuta,txtClienteRuta;

        public Button btnEliminarRuta, btnAgregarComprobante;

        public String id;

        public RutaViewHolder(@NonNull View itemView){
            super(itemView);

            txtVehiculoRuta=itemView.findViewById(R.id.txtVehiculoRuta);
            txtDistritoRuta=itemView.findViewById(R.id.txtDistritoRuta);
            txtDireccionClienteRuta=itemView.findViewById(R.id.txtDireccionClienteRuta);
            txtClienteRuta=itemView.findViewById(R.id.txtClienteRuta);
            btnEliminarRuta=itemView.findViewById(R.id.btnEliminarRuta);
            btnAgregarComprobante=itemView.findViewById(R.id.btnAgregarComprobante);
        }

        void setOnClickListeners(){
            btnEliminarRuta.setOnClickListener(this);
            btnAgregarComprobante.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.btnEliminarRuta:
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getRootView().getContext());
                    builder.setTitle("Esta acción eliminará la ruta asociada!").setMessage("Esta seguro que desea eliminar la ruta?").
                            setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference mreRuta = FirebaseDatabase.getInstance().getReference().child("Ruta");

                                    mreRuta.child(id).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(context, "Se ha eliminado exitosamentela ruta!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                    builder.show();
                    break;
                case R.id.btnAgregarComprobante:
                    Intent i = new Intent(context, AsignarRutasActivity.class);
                    i.putExtra("ruta",id);
                    context.startActivity(i);
                    break;
            }
        }
    }
}
