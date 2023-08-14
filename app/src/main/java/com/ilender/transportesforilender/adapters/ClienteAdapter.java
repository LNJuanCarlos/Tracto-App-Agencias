package com.ilender.transportesforilender.adapters;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.activitys.DireccionClientesActivity;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;

import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>{

    private Context context;
    private List<Clientes> listClientes;
    private ItemDireccionAdapter direccionAdapter;
    private DatabaseReference mDatabaseRef;
    final private int REQUEST_CODE_ASK_PERMISSION=111;
    private final static int SETTINGS_REQUEST_CODE = 2;
    LinearLayoutManager lm;

    public ClienteAdapter(Context con, List<Clientes> listClientes){
        this.context=con;
        this.listClientes=listClientes;
    }


    @NonNull
    @Override
    public ClienteAdapter.ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_cliente,parent,false);
        ClienteAdapter.ClienteViewHolder holder = new ClienteAdapter.ClienteViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ClienteViewHolder holder, int position) {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Direccion");
        lm = new LinearLayoutManager(context);
        holder.rcvItemDireccion.setLayoutManager(lm);
        final Clientes clientes = listClientes.get(position);
        holder.nombresCliente.setText(clientes.getNombres());
        holder.documentoCliente.setText(clientes.getIdentificador());
        holder.contactoCliente.setText(clientes.getContacto());
        holder.telefonoCliente.setText(clientes.getTelefono());
        holder.idCliente = clientes.getIdCliente();
        holder.telefonoClientellamada = clientes.getTelefono();
        if(clientes.getEstado().equals("A")){
            holder.estadoCliente.setText("ACTIVO");
            holder.estadoCliente.setTextColor(Color.parseColor("#4CAF50"));
        }else{
            holder.estadoCliente.setText("INACTIVO");
            holder.estadoCliente.setTextColor(Color.parseColor("#D32F2F"));
        }
        if(clientes.getEstado().equals("I")){
            holder.btnAgregarDireccion.setEnabled(false);
        }
        mDatabaseRef.orderByChild("cliente").equalTo(clientes.getIdCliente()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Direccion> lstDireccion = new ArrayList<>();

                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Direccion direccion = postSnapshot.getValue(Direccion.class);
                    direccion.setIdDireccion(postSnapshot.getKey());
                    lstDireccion.add(direccion);
                }
                direccionAdapter = new ItemDireccionAdapter(context, lstDireccion,"F");
                holder.rcvItemDireccion.setAdapter(direccionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listClientes.size();
    }
    public class ClienteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView estadoCliente, nombresCliente, contactoCliente, telefonoCliente,documentoCliente;
        //public ImageButton llamarCliente;
        public Button btnAgregarDireccion, btnVisualizarDireccion;
        public LinearLayout contenidoOculto;
        public CardView cardviewclientes;
        public RecyclerView rcvItemDireccion;
        public String idCliente, telefonoClientellamada;

        public ClienteViewHolder(@NonNull View itemView){
            super(itemView);

            estadoCliente=itemView.findViewById(R.id.txtEstadoCliente);
            nombresCliente=itemView.findViewById(R.id.txtNombresCliente);
            contactoCliente=itemView.findViewById(R.id.txtContactoCliente);
            telefonoCliente=itemView.findViewById(R.id.txtTelefonoCliente);
            documentoCliente=itemView.findViewById(R.id.txtDocumentoCliente);
            btnAgregarDireccion=itemView.findViewById(R.id.btnAgregarDireccion);
            btnVisualizarDireccion=itemView.findViewById(R.id.btnVisualizarDireccion);
            rcvItemDireccion=itemView.findViewById(R.id.rcvListaDireccion);
            contenidoOculto=itemView.findViewById(R.id.layoutOcultoDirecciones);
            cardviewclientes=itemView.findViewById(R.id.cardviewclientes);
            //llamarCliente=itemView.findViewById(R.id.imgBtnllamar);
        }
        void setOnClickListeners(){
            btnAgregarDireccion.setOnClickListener(this);
            btnVisualizarDireccion.setOnClickListener(this);
           // llamarCliente.setOnClickListener(this);
        }

        public void onClick(View v){
            switch(v.getId()){
                case R.id.btnVisualizarDireccion:
                    Transition transition = new Slide(Gravity.START);
                    transition.setDuration(450);
                    transition.addTarget(R.id.cardviewclientes);
                    transition.setInterpolator(new AccelerateDecelerateInterpolator());

                    if(contenidoOculto.getVisibility() == View.VISIBLE){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            TransitionManager.beginDelayedTransition(cardviewclientes, transition);
                        }
                        contenidoOculto.setVisibility(View.GONE);
                        btnVisualizarDireccion.setText("VISUALIZAR");
                    }else{
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            TransitionManager.beginDelayedTransition(cardviewclientes,transition);
                        }
                        contenidoOculto.setVisibility(View.VISIBLE);
                        btnVisualizarDireccion.setText("OCULTAR");
                    }
                    break;

                case R.id.btnAgregarDireccion:
                    Intent intent = new Intent(context, DireccionClientesActivity.class);
                    intent.putExtra("idCliente",idCliente);
                    context.startActivity(intent);
                    break;
//                case R.id.imgBtnllamar:
//                    int permisoLlamada = ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
//                    if(permisoLlamada!= PackageManager.PERMISSION_GRANTED){
//                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                            new AlertDialog.Builder(context).setTitle("Aún no haz proporcionado los permisos necesarios!")
//                                    .setMessage("Esta aplicación requiere los permisos de llamada, por favor en permisos de la aplicación habilite la opción de Teléfono.")
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if(permisoLlamada!= PackageManager.PERMISSION_GRANTED){
//                                                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package",context.getPackageName(),null)));
//                                            }
//                                        }
//                                    })
//                                    .create()
//                                   .show();
//                            Log.d("Permiso de llamada cancelada", String.valueOf(permisoLlamada));
//                        }
//                    }else{
//                        Log.d("Permiso de llamada", String.valueOf(permisoLlamada));
//                        Intent i = new Intent(Intent.ACTION_CALL);
//                        i.setData(Uri.parse("tel:"+telefonoClientellamada.toString()));
//                        context.startActivity(i);
//                    }
//                    break;
            }
        }
        private void checkLocationPermisos() {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, android.Manifest.permission.CALL_PHONE)) {
                    Log.d("Permiso de llamada", String.valueOf("sin permisoooo"));
                    new androidx.appcompat.app.AlertDialog.Builder(context).setTitle("Proporciona los permisos para continuar").setMessage("Esta aplicación requiere los permisos de ubicación")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSION);
                                }
                            })
                            .create()
                            .show();
                } else {
                    Log.d("Permiso de llamada", String.valueOf("sin permisoooo2"));
                    ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSION);
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", context.getPackageName(), null)));
                }

            } else {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+telefonoClientellamada.toString()));
                context.startActivity(i);
            }
        }
    }
}
