package com.ilender.transportesforilender.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.activitys.AsignarRutasActivity;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Transportistas;
import com.ilender.transportesforilender.model.Usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ChoferAdapter extends RecyclerView.Adapter<ChoferAdapter.ChoferViewHolder>{

    private Context context;
    private List<Choferes> listChoferes;
    private String fecha;
    final private int REQUEST_CODE_ASK_PERMISSION=111;
    private final static int SETTINGS_REQUEST_CODE = 2;
    private RutaAdapter rutaAdapter;
    LinearLayoutManager lm;
    private DatabaseReference mDatabaseRef;
    private String usuario;
    private String tipousuario;

    public ChoferAdapter(Context con, List<Choferes> listChoferes, String fecha){
        this.context=con;
        this.listChoferes=listChoferes;
        this.fecha=fecha;
    }

    public void updateData(List<Choferes> newChoferesList) {
        this.listChoferes = newChoferesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChoferAdapter.ChoferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_chofer,parent,false);
        ChoferViewHolder holder = new ChoferViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChoferAdapter.ChoferViewHolder holder, int position) {
        final Choferes chofer = listChoferes.get(position);
        lm = new LinearLayoutManager(context);
        holder.rcvRutasChofer.setLayoutManager(lm);
        holder.nombresChofer.setText(chofer.getNombres());
        holder.telefonoChofer.setText(chofer.getTelefono());
        holder.licenciaChofer.setText(chofer.getLicencia());
        holder.id = chofer.getIdChofer();
        holder.tipo = chofer.getTipo();
        holder.telefonoChoferllamada = chofer.getTelefono();
        if(chofer.getTipo().equals("I")){
            holder.tipoChofer.setText("INTERNO");
        }else{
            holder.tipoChofer.setText("EXTERNO");
        }

        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios usuarioObtenido = snapshot.getValue(Usuarios.class);
                tipousuario = usuarioObtenido.getTipo();

                if(tipousuario != null && tipousuario.equals("auxiliar")){
                    holder.layoutTelefonoChoferes.setVisibility(View.GONE);
                }else{
                    holder.layoutTelefonoChoferes.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Query mRefTrans = FirebaseDatabase.getInstance().getReference().child("Transportistas").child(chofer.getTransportista());
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


        Query mRefChoferVehi = FirebaseDatabase.getInstance().getReference().child("Ruta")
                .orderByChild("fecha").equalTo(fecha);

        mRefChoferVehi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Ruta> al = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Ruta ruta = postSnapshot.getValue(Ruta.class);
                    if(ruta.getChofer().equals(chofer.getIdChofer())){
                        ruta.setIdRuta(postSnapshot.getKey());
                        al.add(ruta);
                    }
                }
                if(al.size()==0){
                    holder.estadoChofer.setText("LIBRE");
                    holder.estadoChofer.setTextColor(Color.parseColor("#33FFB8"));
                    holder.btnVisualizarRutas.setEnabled(false);
                }else{
                    holder.estadoChofer.setText("OCUPADO");
                    holder.estadoChofer.setTextColor(Color.parseColor("#FF3838"));
                }
                rutaAdapter = new RutaAdapter(context, al);
                holder.rcvRutasChofer.setAdapter(rutaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listChoferes.size();
    }

    public class ChoferViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView estadoChofer, nombresChofer, licenciaChofer, telefonoChofer, tipoChofer, transportista;
        public Button btnAsignarRuta, btnVisualizarRutas;
        public ImageButton llamarChofer;
        public String id, telefonoChoferllamada;
        public LinearLayout layouOcultoRutasChofer,layoutTelefonoChoferes;
        public RecyclerView rcvRutasChofer;
        public String tipo;

        public ChoferViewHolder(@NonNull View itemView){
            super(itemView);

            estadoChofer=itemView.findViewById(R.id.txtEstadoChofer);
            nombresChofer=itemView.findViewById(R.id.txtNombresChofer);
            licenciaChofer=itemView.findViewById(R.id.txtLicenciaChofer);
            telefonoChofer=itemView.findViewById(R.id.txtTelefonoChofer);
            btnAsignarRuta=itemView.findViewById(R.id.btnAsignarRuta);
            btnVisualizarRutas=itemView.findViewById(R.id.btnVisualizarRutas);
            layouOcultoRutasChofer=itemView.findViewById(R.id.layoutOcultoRutasChofer);
            rcvRutasChofer=itemView.findViewById(R.id.rcvListaRutasChofer);
            tipoChofer = itemView.findViewById(R.id.txtTipoChofer);
            llamarChofer = itemView.findViewById(R.id.imgBtnllamarChofer);
            transportista = itemView.findViewById(R.id.txtTransportistaChofer);
            layoutTelefonoChoferes = itemView.findViewById(R.id.layoutTelefonoChoferes);
        }

        void setOnClickListeners(){
            btnAsignarRuta.setOnClickListener(this);
            btnVisualizarRutas.setOnClickListener(this);
            llamarChofer.setOnClickListener(this);
        }

        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnAsignarRuta:
                    Intent i = new Intent(context, AsignarRutasActivity.class);
                    i.putExtra("idChofer",id);
                    context.startActivity(i);
                    break;
                case R.id.btnVisualizarRutas:
                    if(layouOcultoRutasChofer.getVisibility()==View.GONE){
                        layouOcultoRutasChofer.setVisibility(View.VISIBLE);
                        btnVisualizarRutas.setText("OCULTAR RUTAS");
                    }else{
                        layouOcultoRutasChofer.setVisibility(View.GONE);
                        btnVisualizarRutas.setText("VER RUTAS");
                    }
                    break;
                case R.id.imgBtnllamarChofer:
                    int permisoLlamada = ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                    if(permisoLlamada!= PackageManager.PERMISSION_GRANTED){
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            new AlertDialog.Builder(context).setTitle("Aún no haz proporcionado los permisos necesarios!")
                                    .setMessage("Esta aplicación requiere los permisos de llamada, por favor en permisos de la aplicación habilite la opción de Teléfono.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if(permisoLlamada!=PackageManager.PERMISSION_GRANTED){
                                                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package",context.getPackageName(),null)));
                                            }
                                        }
                                    })
                                    .create()
                                    .show();
                            Log.d("Permiso de llamada cancelada",String.valueOf(permisoLlamada));
                        }
                    }else{
                        Log.d("Permiso de llamada",String.valueOf(permisoLlamada));
                        Intent iv = new Intent(Intent.ACTION_CALL);
                        iv.setData(Uri.parse("tel:"+telefonoChoferllamada.toString()));
                        context.startActivity(iv);
                    }
                    break;
            }
        }

        private void checkLocationPermisos(){
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, Manifest.permission.CALL_PHONE)){
                    Log.d("Permiso de llamada", String.valueOf("sin permisooo"));
                    new AlertDialog.Builder(context).setTitle("Proporciona los permisos para continuar").setMessage("Esta aplicación requiere los permisos de ubicación")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSION);
                                }
                            })
                            .create()
                            .show();
                }else{
                    Log.d("Permiso de llamada",String.valueOf("sin permisoooo2"));
                    ActivityCompat.requestPermissions((AppCompatActivity)context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSION);
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package",context.getPackageName(),null)));
                }
            }else{
                Intent is = new Intent(Intent.ACTION_CALL);
                is.setData(Uri.parse("tel:"+telefonoChoferllamada.toString()));
                context.startActivity(is);
            }
        }
    }
}
