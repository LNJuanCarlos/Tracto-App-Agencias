package com.ilender.transportesforilender.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Seguimientoruta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeguimientoAdapter extends RecyclerView.Adapter<SeguimientoAdapter.SeguimientoViewHolder>{

    private Context context;
    private List<Seguimientoruta> lstSeguimiento;

    public SeguimientoAdapter(Context con, List<Seguimientoruta> lstSeguimiento){
        this.context=con;
        this.lstSeguimiento=lstSeguimiento;
    }

    @NonNull
    @Override
    public SeguimientoAdapter.SeguimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_seguimiento,parent,false);
        SeguimientoAdapter.SeguimientoViewHolder holder = new SeguimientoAdapter.SeguimientoViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoAdapter.SeguimientoViewHolder holder, int position) {

        final Seguimientoruta seguimiento = lstSeguimiento.get(position);
        String fecha = seguimiento.getFecha().substring(0,10);
        String hora = new SimpleDateFormat("HH: mm: ss", Locale.getDefault()).format(Date.parse(seguimiento.getFecha()));
        holder.latitud = seguimiento.getLatitud();
        holder.idRuta = seguimiento.getRuta();
        holder.longitud = seguimiento.getLongitud();
        holder.txtKilometrajeTr.setText(seguimiento.getKilometraje().toString());
        holder.txtFechaTr.setText(fecha);
        holder.txtHoraTr.setText(hora);

        if(seguimiento.getObservacion()!=null){
            holder.layoutObservacion.setVisibility(View.VISIBLE);
            holder.txtObservacion.setText(seguimiento.getObservacion());
        }

        if(seguimiento.getEstado().equals("I")){
            holder.txtEstadoSeguimiento.setText("INICIO");
        }else if(seguimiento.getEstado().equals("L")){
            holder.txtEstadoSeguimiento.setText("LLEGADA");
        }else if(seguimiento.getEstado().equals("A")){
            holder.txtEstadoSeguimiento.setText("ATENDIDO");
            holder.btnVisualizarLayout.setVisibility(View.VISIBLE);
        }else{
            holder.txtEstadoSeguimiento.setText("FINALIZO");
        }
        holder.setOnClickListeners();
    }
    @Override
    public void onViewRecycled(SeguimientoViewHolder holder){
        if(holder.map !=null){
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    @Override
    public int getItemCount() {
        return lstSeguimiento.size();
    }

    public class SeguimientoViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback, View.OnClickListener{

        private GoogleMap map;
        public TextView txtKilometrajeTr, txtFechaTr, txtHoraTr, txtEstadoSeguimiento, txtObservacion;
        public String latitud, longitud;
        public String idRuta;
        public LinearLayout layoutGuias, layoutObservacion;

        StorageReference mStorage;

        public ImageView imgGuias;
        private ArrayList<Uri> imagesUri;
        public Button btnAnterior, btnSiguiente, btnVisualizarLayout;
        private MapView mapView;

        private int position;

        public SeguimientoViewHolder(@NonNull View itemView){
            super(itemView);

            txtEstadoSeguimiento=itemView.findViewById(R.id.txtEstadoSeguimiento);
            txtKilometrajeTr=itemView.findViewById(R.id.txtKilometrajeTr);
            txtFechaTr=itemView.findViewById(R.id.txtFechaTr);
            txtHoraTr=itemView.findViewById(R.id.txtHoraTr);
            layoutGuias=itemView.findViewById(R.id.linearLayoutGuias);
            btnAnterior=itemView.findViewById(R.id.btnAnteriorGuia);
            btnSiguiente=itemView.findViewById(R.id.btnSiguienteGuia);
            imgGuias=itemView.findViewById(R.id.isGuias);
            btnVisualizarLayout=itemView.findViewById(R.id.btnVisualizarGuia);
            txtObservacion=itemView.findViewById(R.id.txtObservacion);
            layoutObservacion=itemView.findViewById(R.id.layoutObservacion);
            imagesUri = new ArrayList<>();
            mapView = itemView.findViewById(R.id.mapdriverTr);

            if(mapView != null){
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }

        }
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap){
            MapsInitializer.initialize(context.getApplicationContext());
            map = googleMap;
            setMapLocation(latitud, longitud);
        }

        private void setMapLocation(String latitud, String longitud){
            if(map == null) return;
            LatLng miUbicacion = new LatLng(Float.parseFloat(latitud), Float.parseFloat(longitud));
            if(miUbicacion == null) return;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion,13f));
            map.addMarker(new MarkerOptions().position(miUbicacion));
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        void setOnClickListeners(){
            btnVisualizarLayout.setOnClickListener(this);
            btnSiguiente.setOnClickListener(this);
            btnAnterior.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.btnVisualizarGuia:
                    if(layoutGuias.getVisibility()==View.GONE){
                        layoutGuias.setVisibility(View.VISIBLE);
                        btnVisualizarLayout.setText("OCULTAR ADJUNTOS");
                        mStorage = FirebaseStorage.getInstance().getReference().child("Guias");
                        StorageReference filepath = mStorage.child(idRuta).child("Final");

                        filepath.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                ArrayList<Uri> uris = new ArrayList<>();
                                for(StorageReference prefix : listResult.getPrefixes()){

                                }
                                for(StorageReference item : listResult.getItems()){
                                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imagesUri.add(uri);
                                            uris.add(uri);
                                            if(imagesUri.size()==1){
                                                Glide.with(context)
                                                        .load(uri)
                                                        .into(imgGuias);
                                            }
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }else{
                        layoutGuias.setVisibility(View.GONE);
                        btnVisualizarLayout.setText("VISUALIZAR ADJUNTOS");
                    }
                    break;

                case R.id.btnAnteriorGuia:
                    if(position>0){
                        position--;
                        Glide.with(context)
                                .load(imagesUri.get(position))
                                .into(imgGuias);
                    }else{
                        Toast.makeText(context, "No hay imagen previa", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    
                case R.id.btnSiguienteGuia:
                    if(position<imagesUri.size()-1){
                        position++;
                        Glide.with(context)
                                .load(imagesUri.get(position))
                                .into(imgGuias);
                    }else{
                        Toast.makeText(context, "No hay más imágenes", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}
