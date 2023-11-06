package com.ilender.transportesforilender.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.databinding.ActivityMainBinding;
import com.ilender.transportesforilender.model.Usuarios;
import com.ilender.transportesforilender.providers.AuthProvider;
import com.ilender.transportesforilender.providers.TokkenProvider;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    AuthProvider mAuthProvider;

    private NavigationView navigationView;

    private View header;

    private DatabaseReference mDatabaseRef;


    private String usuario;

    private String tipo;
    TokkenProvider mTokenProvider;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        notificaciones();
    }

    @Override
    protected void onStop(){
        super.onStop();
        notificaciones();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario);
        mAuthProvider = new AuthProvider();
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        setSupportActionBar(binding.appBarMain.toolbar);
        mTokenProvider = new TokkenProvider();

        createToken();
        notificaciones();
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chofer, R.id.nav_cliente, R.id.nav_ruta, R.id.nav_vehiculo, R.id.nav_reporte, R.id.nav_rutas_chofer)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios usuarioObtenido = snapshot.getValue(Usuarios.class);
                tipo = usuarioObtenido.getTipo();
                Menu nav_Menu = navigationView.getMenu();
                TextView nombres = header.findViewById(R.id.txtNombresUsuario);
                TextView correo = header.findViewById(R.id.txtCorreoUsuario);
                TextView tipoUsuario = header.findViewById(R.id.txtTipoUsuario);
                nombres.setText(usuarioObtenido.getNombres());
                correo.setText(usuarioObtenido.getCorreo());

                if(tipo.equals("transporte")){
                    nav_Menu.findItem(R.id.nav_chofer).setVisible(false);
                    nav_Menu.findItem(R.id.nav_cliente).setVisible(false);
                    nav_Menu.findItem(R.id.nav_ruta).setVisible(false);
                    nav_Menu.findItem(R.id.nav_vehiculo).setVisible(false);
                    nav_Menu.findItem(R.id.nav_reporte).setVisible(false);
                    tipoUsuario.setText("Tipo: Transporte");
                }else if(tipo.equals("auxiliar")){
                    nav_Menu.findItem(R.id.nav_rutas_chofer).setVisible(false);
                    nav_Menu.findItem(R.id.nav_cliente).setVisible(false);
                    nav_Menu.findItem(R.id.nav_ruta).setVisible(false);
                    nav_Menu.findItem(R.id.nav_reporte).setVisible(false);
                    tipoUsuario.setText("Tipo: Auxiliar de Almacen");
                }else if(tipo.equals("administrador")){
                    nav_Menu.findItem(R.id.nav_cliente).setVisible(false);
                    nav_Menu.findItem(R.id.nav_rutas_chofer).setVisible(false);
                    tipoUsuario.setText("Tipo: Asistente");
                } else if (tipo.equals("supervisor")) {
                    nav_Menu.findItem(R.id.nav_rutas_chofer).setVisible(false);
                    tipoUsuario.setText("Tipo: Supervisor");
                } else if (tipo.equals("jefe")) {
                    nav_Menu.findItem(R.id.nav_rutas_chofer).setVisible(false);
                    tipoUsuario.setText("Tipo: Jefe de Almacen");
                } else{
                    nav_Menu.findItem(R.id.nav_rutas_chofer).setVisible(false);
                    tipoUsuario.setText("Tipo: Sistemas");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createToken(){
        mTokenProvider.create(mAuthProvider.getUid());
    }

    private void notificaciones() {
//////////////////
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                mAuthProvider.logout();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}