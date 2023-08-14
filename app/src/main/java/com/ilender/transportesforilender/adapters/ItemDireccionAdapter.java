package com.ilender.transportesforilender.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Direccion;

import java.util.List;

public class ItemDireccionAdapter extends RecyclerView.Adapter<ItemDireccionAdapter.ItemDireccionViewHolder>{

    private Context context;
    private List<Direccion> listDireccion;
    private String estado;

    public ItemDireccionAdapter(Context con, List<Direccion> listDireccion, String estado){
        this.context=con;
        this.listDireccion=listDireccion;
        this.estado=estado;
    }

    @NonNull
    @Override
    public ItemDireccionAdapter.ItemDireccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_direccion,parent,false);
        ItemDireccionAdapter.ItemDireccionViewHolder holder = new ItemDireccionAdapter.ItemDireccionViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDireccionAdapter.ItemDireccionViewHolder holder, int position) {
        final Direccion direccion = listDireccion.get(position);
        holder.direccionCliente.setText(direccion.getDescripcion());
        holder.distritoCliente.setText(direccion.getDistrito());
        if(estado.equals("A")){
            holder.btnEliminarDireccion.setVisibility(View.VISIBLE);
        }
        holder.idDireccion = direccion.getIdDireccion();
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listDireccion.size();
    }
    public class ItemDireccionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView direccionCliente, distritoCliente;
        public Button btnEliminarDireccion;
        public String idDireccion;

        public ItemDireccionViewHolder(@NonNull View itemView){

            super(itemView);

            direccionCliente=itemView.findViewById(R.id.txtDireccionCliente);
            distritoCliente=itemView.findViewById(R.id.txtDistrito);
            btnEliminarDireccion=itemView.findViewById(R.id.btnEliminarDireccion);
        }

        void setOnClickListeners(){
            btnEliminarDireccion.setOnClickListener(this);
        }

        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnEliminarDireccion:
                    DatabaseReference mReferenceDirecciones = FirebaseDatabase.getInstance().getReference().child("Direccion");

                    mReferenceDirecciones.child(idDireccion).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Se ha eliminado la ruta seleccionada del cliente!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    }
}
