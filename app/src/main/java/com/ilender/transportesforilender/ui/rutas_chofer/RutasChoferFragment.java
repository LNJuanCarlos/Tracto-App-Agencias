package com.ilender.transportesforilender.ui.rutas_chofer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.adapters.RutasActivasAdapter;
import com.ilender.transportesforilender.databinding.FragmentRutasChoferBinding;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Usuarios;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class RutasChoferFragment extends Fragment {


    public RutasChoferFragment() {
        // Required empty public constructor
    }

    private AdView mAdView4;
    private View root;
    private TextView txtFecha;
    private FragmentRutasChoferBinding binding;
    private DatabaseReference mDatabaseRef;
    private String usuario;
    private RecyclerView mRecyclerView;
    private RutasActivasAdapter rutasActivasAdapter;
    LinearLayoutManager lm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRutasChoferBinding.inflate(inflater,container,false);
        root = binding.getRoot();
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario);
        txtFecha = root.findViewById(R.id.txtFechaRutasActivas);
        String strDate = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());
        txtFecha.setText(strDate);
        mRecyclerView = root.findViewById(R.id.rcvRutasActivas);
        lm = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(lm);

        MobileAds.initialize(getContext());

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView4 = root.findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView4.loadAd(adRequest);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios usuarioObtenido = snapshot.getValue(Usuarios.class);
                DatabaseReference mRefRutas = FirebaseDatabase.getInstance().getReference().child("Ruta");

                mRefRutas.orderByChild("fecha").equalTo(strDate).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Ruta> lstRutas = new ArrayList<>();
                        for(DataSnapshot postSnapshot : snapshot.getChildren()){
                            Ruta ruta = postSnapshot.getValue(Ruta.class);
                            if(ruta.getChofer().equals(usuarioObtenido.getChofer())){
                                ruta.setIdRuta(postSnapshot.getKey());
                                lstRutas.add(ruta);
                            }
                        }
                        rutasActivasAdapter = new RutasActivasAdapter(root.getContext(), lstRutas);
                        mRecyclerView.setAdapter(rutasActivasAdapter);
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
        return root;
    }
}