package com.ilender.transportesforilender.ui.home;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.databinding.FragmentHomeBinding;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Usuarios;
import com.ilender.transportesforilender.model.Vehiculos;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String strDate;
    private DatabaseReference mDatabaseRef;
    private String usuario, tipo;
    private LinearLayout layoutadm1, layoutadm2, layoutadm3, layoutCH1, layoutCH2, layoutCH3;
    private TextView rutasPendientes, rutasFinalizadas, rutasDelMes, choferTop, vehiculoTop, rutasPendientesCH, rutasDelMesCH, rutasFinalizadasCH;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario);
        rutasFinalizadas = root.findViewById(R.id.txtRutasFinalizadasDeHoy);
        rutasPendientes = root.findViewById(R.id.txtRutasPendientesDeHoy);
        rutasDelMes = root.findViewById(R.id.txtRutasRealizadasDelMes);
        rutasPendientesCH = root.findViewById(R.id.txtRutasPendientesDelDiaCH);
        rutasDelMesCH = root.findViewById(R.id.txtRutasRealizadasDelMestr);
        rutasFinalizadasCH = root.findViewById(R.id.txtRutasFinalizadasDelDiaCH);
        choferTop = root.findViewById(R.id.txtChoferTopDelMes);
        vehiculoTop = root.findViewById(R.id.txtTransporteTopDelMes);
        layoutadm1 = root.findViewById(R.id.layoutADM1);
        layoutadm2 = root.findViewById(R.id.layoutADM2);
        layoutadm3 = root.findViewById(R.id.layoutADM3);
        layoutCH1 = root.findViewById(R.id.layouttrans1);
        layoutCH2 = root.findViewById(R.id.layouttrans2);
        layoutCH3 = root.findViewById(R.id.layouttrans3);
        strDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        rutasDelDia();
        rutaDelMes();

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios usuarioObtenido = snapshot.getValue(Usuarios.class);
                tipo = usuarioObtenido.getTipo();
                if(tipo.equals("transporte")){

                    DatabaseReference rutas = FirebaseDatabase.getInstance().getReference().child("Ruta");
                    Query rutasDelDia = rutas.orderByChild("fecha").equalTo(strDate);
                    rutasDelDia.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int contadorFinalizadas = 0;
                            int contadorPendientes = 0;
                            for(DataSnapshot ds : snapshot.getChildren()){
                                Ruta ruta = ds.getValue(Ruta.class);
                                if(ruta.getEstado().equals("P") && ruta.getChofer().equals(usuarioObtenido.getChofer())){
                                    contadorPendientes+=1;
                                }else if(ruta.getEstado().equals("A") && ruta.getChofer().equals(usuarioObtenido.getChofer())){
                                    contadorFinalizadas+=1;
                                }
                            }
                            rutasPendientesCH.setText(String.valueOf(contadorPendientes));
                            rutasFinalizadasCH.setText(String.valueOf(contadorFinalizadas));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    LocalDate fechaActual = LocalDate.now();
                    LocalDate fechaInicio = fechaActual.withDayOfMonth(1);
                    LocalDate fechaFin = fechaActual.withDayOfMonth(fechaActual.lengthOfMonth());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String fechaFormateadaI = fechaInicio.format(formatter);
                    String fechaFormateadaF = fechaFin.format(formatter);
                    String añoactual = fechaFormateadaI.substring(6,10);
                    String mesActual = fechaFormateadaI.substring(3,5);
                    String diaIActual = fechaFormateadaI.substring(0,2);
                    String diaFActual = fechaFormateadaF.substring(0,2);

                    DatabaseReference rutas2 = FirebaseDatabase.getInstance().getReference().child("Ruta");
                    Query rutasc = rutas2.orderByChild("chofer").equalTo(usuarioObtenido.getChofer());
                    rutasc.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int contadorTotal2 = 0;
                            List<String> lista = new ArrayList<>();
                            List<String> listatRANSPORTE = new ArrayList<>();
                            for(DataSnapshot ds: snapshot.getChildren()){
                                Ruta ruta = ds.getValue(Ruta.class);
                                String añoActualF = ruta.getFecha().substring(6,10);
                                String mesActualF = ruta.getFecha().substring(3,5);
                                String diaIActualF = ruta.getFecha().substring(0,2);
                                if(añoActualF.equals(añoactual)){
                                    if(mesActualF.equals(mesActual)){
                                        if(Integer.parseInt(diaIActualF)>=Integer.parseInt(diaIActual)&&Integer.parseInt(diaIActualF)<=Integer.parseInt(diaFActual)){
                                            lista.add(ruta.getChofer());
                                            listatRANSPORTE.add(ruta.getVehiculo());
                                            contadorTotal2+=1;
                                        }
                                    }
                                }
                            }
                            rutasDelMesCH.setText(String.valueOf(contadorTotal2));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    layoutCH1.setVisibility(View.VISIBLE);
                    layoutCH2.setVisibility(View.VISIBLE);
                    layoutCH3.setVisibility(View.VISIBLE);
                }else{
                    layoutadm1.setVisibility(View.VISIBLE);
                    layoutadm2.setVisibility(View.VISIBLE);
                    layoutadm3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void rutasDelDia(){
        DatabaseReference rutas = FirebaseDatabase.getInstance().getReference().child("Ruta");
        Query rutasDelDia = rutas.orderByChild("fecha").equalTo(strDate);
        rutasDelDia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int contadorFinalizadas = 0;
                int contadorPendientes = 0;
                int contadorEnCurso = 0;
                int contadorLlegada = 0;

                for(DataSnapshot ds: snapshot.getChildren()){
                    Ruta ruta = ds.getValue(Ruta.class);
                    if(ruta.getEstado().equals("P")){
                        contadorPendientes+=1;
                    }else if(ruta.getEstado().equals("A")){
                        contadorFinalizadas+=1;
                    }else if(ruta.getEstado().equals("I")){
                        contadorEnCurso+=1;
                    }else if (ruta.getEstado().equals("S")) {
                        contadorLlegada+=1;
                    }
                }
                rutasPendientes.setText(String.valueOf(contadorPendientes));
                rutasFinalizadas.setText(String.valueOf(contadorFinalizadas));
                vehiculoTop.setText(String.valueOf(contadorEnCurso));
                choferTop.setText(String.valueOf(contadorLlegada));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void rutaDelMes(){

        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaInicio = fechaActual.withDayOfMonth(1);
        LocalDate fechaFin = fechaActual.withDayOfMonth(fechaActual.lengthOfMonth());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateadaI = fechaInicio.format(formatter);
        String fechaFormateadaF = fechaFin.format(formatter);
        String añoActual = fechaFormateadaI.substring(6, 10);
        String mesActual = fechaFormateadaI.substring(3, 5);
        String diaIActual = fechaFormateadaI.substring(0, 2);
        String diaFActual = fechaFormateadaF.substring(0, 2);

        DatabaseReference rutas = FirebaseDatabase.getInstance().getReference().child("Ruta");
        rutas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int contadorTotal = 0;

                List<String> lista = new ArrayList<>();
                List<String> listatRANSPORTE = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Ruta ruta = ds.getValue(Ruta.class);
                    String añoActualF = ruta.getFecha().substring(6, 10);
                    String mesActualF = ruta.getFecha().substring(3, 5);
                    String diaIActualF = ruta.getFecha().substring(0, 2);
                    if (añoActualF.equals(añoActual)) {
                        if (mesActualF.equals(mesActual)) {
                            if (Integer.parseInt(diaIActualF) >= Integer.parseInt(diaIActual) && Integer.parseInt(diaIActualF) <= Integer.parseInt(diaFActual)) {
                                lista.add(ruta.getChofer());
                                listatRANSPORTE.add(ruta.getVehiculo());
                                contadorTotal += 1;
                            }
                        }
                    }
                }

                Map<String, Integer> recuento = new HashMap<>();
                Map<String, Integer> recuento2 = new HashMap<>();

                String elementoMasRepetido = "";
                int maxRecuento = 0;
                String elementoMasRepetido2 = "";
                int maxRecuento2 = 0;

                for(String elemento : lista){
                    recuento.put(elemento, recuento.getOrDefault(elemento, 0)+1);
                }
                for(String elemento2 : listatRANSPORTE){
                    recuento2.put(elemento2, recuento2.getOrDefault(elemento2,0)+1);
                }

                for(Map.Entry<String, Integer> entry : recuento.entrySet()){
                    if(entry.getValue() > maxRecuento){
                        maxRecuento = entry.getValue();
                        elementoMasRepetido = entry.getKey();
                    }
                }

                for(Map.Entry<String, Integer> entry : recuento2.entrySet()){
                    if(entry.getValue() > maxRecuento2){
                        maxRecuento2 = entry.getValue();
                        elementoMasRepetido2 = entry.getKey();
                    }
                }

//                DatabaseReference chofer = FirebaseDatabase.getInstance().getReference().child("Choferes").child(elementoMasRepetido);
//                chofer.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Choferes chofer = snapshot.getValue(Choferes.class);
//                        choferTop.setText(chofer.getNombres());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

//                DatabaseReference vehiculo = FirebaseDatabase.getInstance().getReference().child("Vehiculos").child(elementoMasRepetido2);
//                vehiculo.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Vehiculos vehiculo = snapshot.getValue(Vehiculos.class);
//                        vehiculoTop.setText(vehiculo.getMarca()+" - "+vehiculo.getPlaca());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                rutasDelMes.setText(String.valueOf(contadorTotal));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}