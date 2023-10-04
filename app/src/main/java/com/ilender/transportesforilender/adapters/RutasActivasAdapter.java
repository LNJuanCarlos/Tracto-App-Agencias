package com.ilender.transportesforilender.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.activitys.AtencionActivity;
import com.ilender.transportesforilender.activitys.IniciarActivity;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.model.Ruta;

import java.util.List;

public class RutasActivasAdapter extends RecyclerView.Adapter<RutasActivasAdapter.RutasActivasViewHolder>{

    private Context context;
    private List<Ruta> listRuta;

    public RutasActivasAdapter(Context con, List<Ruta> listRuta){
        this.context=con;
        this.listRuta=listRuta;
    }


    @NonNull
    @Override
    public RutasActivasAdapter.RutasActivasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.lista_rutas_activas,parent,false);
        RutasActivasAdapter.RutasActivasViewHolder holder = new RutasActivasAdapter.RutasActivasViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull RutasActivasAdapter.RutasActivasViewHolder holder, int position) {

        final Ruta ruta = listRuta.get(position);
        holder.id = ruta.getIdRuta();

        if(ruta.getEstado().equals("P")){
            holder.txtEstadoRutaActiva.setText("NO INICIADA");
        }else if(ruta.getEstado().equals("I")){
            holder.txtEstadoRutaActiva.setText("INICIADA");
        }else if(ruta.getEstado().equals("S")){
            holder.txtEstadoRutaActiva.setText("EN DESTINO");
        }else if(ruta.getEstado().equals("A")){
            holder.txtEstadoRutaActiva.setText("ATENDIDO");
        }else{
            holder.txtEstadoRutaActiva.setText("FINALIZADA");
        }

        if(ruta.getEstado().equals("P")){
            holder.btnLlegada.setEnabled(false);
            holder.btnAtendido.setEnabled(false);
            holder.btnFinalizar.setEnabled(false);
        }else if(ruta.getEstado().equals("I")){
            holder.btnIniciar.setEnabled(false);
            holder.btnAtendido.setEnabled(false);
        }else if(ruta.getEstado().equals("S")){
            holder.btnIniciar.setEnabled(false);
            holder.btnLlegada.setEnabled(false);
        }else if(ruta.getEstado().equals("A")){
            holder.btnIniciar.setEnabled(false);
            holder.btnLlegada.setEnabled(false);
            holder.btnAtendido.setEnabled(false);
            holder.btnFinalizar.setEnabled(false);
        }else{
            holder.btnLlegada.setEnabled(false);
            holder.btnAtendido.setEnabled(false);
            holder.btnFinalizar.setEnabled(false);
        }

        DatabaseReference mrefDireccion = FirebaseDatabase.getInstance().getReference().child("Direccion").child(ruta.getDireccion());

        mrefDireccion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Direccion direccion = snapshot.getValue(Direccion.class);
                holder.txtDireccionClienteRutaActiva.setText(direccion.getDescripcion());
                holder.txtDistritoRutaActiva.setText(direccion.getDistrito());
                holder.setOnClickListeners();

                DatabaseReference mrefCliente = FirebaseDatabase.getInstance().getReference("Clientes").child(direccion.getCliente());
                mrefCliente.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Clientes cliente = snapshot.getValue(Clientes.class);
                        holder.txtVerCliente.setText(cliente.getNombres());
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

    }

    @Override
    public int getItemCount() {
        return listRuta.size();
    }

    public class RutasActivasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView txtEstadoRutaActiva, txtDistritoRutaActiva, txtDireccionClienteRutaActiva, txtVerCliente;
        public Button btnVisualizarOpcionesRutaActiva, btnIniciar, btnLlegada,btnAtendido,btnFinalizar;
        public LinearLayout layoutOcultoPrimeroRutas, layoutOcultoSegundoRutas;
        public String id;

        public RutasActivasViewHolder(@NonNull View itemView){
            super(itemView);

            txtEstadoRutaActiva=itemView.findViewById(R.id.txtEstadoRutaActiva);
            txtDistritoRutaActiva=itemView.findViewById(R.id.txtDistritoRutaActiva);
            txtDireccionClienteRutaActiva=itemView.findViewById(R.id.txtDireccionClienteRutaActiva);
            txtVerCliente=itemView.findViewById(R.id.txtVerCliente);
            btnVisualizarOpcionesRutaActiva=itemView.findViewById(R.id.btnVisualizarOpcionesRutaActiva);
            layoutOcultoPrimeroRutas=itemView.findViewById(R.id.layoutOcultoPrimeroRutas);
            layoutOcultoSegundoRutas=itemView.findViewById(R.id.layoutOcultoSegundoRutas);
            btnIniciar=itemView.findViewById(R.id.btnIniciar);
            btnLlegada=itemView.findViewById(R.id.btnLlegada);
            btnAtendido=itemView.findViewById(R.id.btnAtendido);
            btnFinalizar=itemView.findViewById(R.id.btnFinalizar);
        }

        void setOnClickListeners(){
            btnVisualizarOpcionesRutaActiva.setOnClickListener(this);
            btnIniciar.setOnClickListener(this);
            btnLlegada.setOnClickListener(this);
            btnAtendido.setOnClickListener(this);
            btnFinalizar.setOnClickListener(this);
        }

        public void onClick(View v) {

            switch(v.getId()){
                case R.id.btnVisualizarOpcionesRutaActiva:
                    if(layoutOcultoPrimeroRutas.getVisibility()==View.GONE || layoutOcultoSegundoRutas.getVisibility()==View.GONE){
                        layoutOcultoPrimeroRutas.setVisibility(View.VISIBLE);
                        layoutOcultoSegundoRutas.setVisibility(View.VISIBLE);
                        btnVisualizarOpcionesRutaActiva.setText("OCULTAR OPCIONES");
                    }else {
                        layoutOcultoPrimeroRutas.setVisibility(View.GONE);
                        layoutOcultoSegundoRutas.setVisibility(View.GONE);
                        btnVisualizarOpcionesRutaActiva.setText("VISUALIZAR OPCIONES");
                    }

                    break;

                case R.id.btnIniciar:
                    Intent i = new Intent(context, IniciarActivity.class);
                    i.putExtra("idRuta",id);
                    i.putExtra("tipo", "inicio");
                    context.startActivity(i);
                    break;

                case R.id.btnLlegada:
                    Intent l = new Intent(context, IniciarActivity.class);
                    l.putExtra("idRuta",id);
                    l.putExtra("tipo","llegada");
                    context.startActivity(l);
                    break;

                case R.id.btnAtendido:
                    Intent ll = new Intent(context, AtencionActivity.class);
                    ll.putExtra("idRuta",id);
                    ll.putExtra("tipo","atencion");
                    context.startActivity(ll);
                    break;

                case R.id.btnFinalizar:
                    Intent F = new Intent(context, IniciarActivity.class);
                    F.putExtra("idRuta",id);
                    F.putExtra("tipo","finalizar");
                    context.startActivity(F);
                    break;
            }
        }
    }
}
