package com.ilender.transportesforilender.ui.reporte;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.model.Choferes;
import com.ilender.transportesforilender.model.Clientes;
import com.ilender.transportesforilender.model.Direccion;
import com.ilender.transportesforilender.model.Ruta;
import com.ilender.transportesforilender.model.Seguimientoruta;
import com.ilender.transportesforilender.model.Transportistas;
import com.ilender.transportesforilender.model.Vehiculos;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReporteFragment extends Fragment {

    private AdView mAdView9;
    private List<Choferes> listChoferes;
    private List<Clientes> listClientes;
    private List<Direccion> listDireccion;
    private List<Transportistas> listTransportistas;
    private List<Vehiculos> listVehiculos;
    private List<Seguimientoruta> lstseguimientorutas;
    private List<Ruta> lstRuta;
    private View root;
    private Button btnGenerarReporte;
    private ProgressBar progressBar;
    private String estadoBoton;
    private static final int STORAGE_PERMISSION_CODE = 101;

    public ReporteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        root = inflater.inflate(R.layout.fragment_reporte, container, false);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        btnGenerarReporte = root.findViewById(R.id.btnGenerarReporte);
        estadoBoton = "NO";
        listChoferes = new ArrayList<>();
        listClientes = new ArrayList<>();
        listDireccion = new ArrayList<>();
        listTransportistas = new ArrayList<>();
        listVehiculos = new ArrayList<>();
        lstseguimientorutas = new ArrayList<>();
        lstRuta = new ArrayList<>();
        obtenerData();
        progressBar = root.findViewById(R.id.progressBar);

        MobileAds.initialize(getContext());

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        /*mAdView9 = root.findViewById(R.id.adView9);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView9.loadAd(adRequest);*/

        btnGenerarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estadoBoton.equals("NO")){
                    generarData();
                    btnGenerarReporte.setText("DESCARGAR DATA");
                    estadoBoton="SI";
                }else{
                    createXLS(lstseguimientorutas);
                    btnGenerarReporte.setText("CARGAR DATA");
                    estadoBoton="NO";
                }
            }
        });

        
        return root;
    }

    private void checkPermission(String permission, int requestCode) {
        if(ContextCompat.checkSelfPermission(root.getContext(),permission) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{permission}, requestCode);
        }else{
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(root.getContext(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(root.getContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void obtenerData(){
        DatabaseReference mRefChofer = FirebaseDatabase.getInstance().getReference().child("Choferes");
        mRefChofer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Choferes chofer = ds.getValue(Choferes.class);
                    chofer.setIdChofer(ds.getKey());
                    listChoferes.add(chofer);
                }

                DatabaseReference mRefClientes = FirebaseDatabase.getInstance().getReference().child("Clientes");
                mRefClientes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Clientes cliente = ds.getValue(Clientes.class);
                            cliente.setIdCliente(ds.getKey());
                            listClientes.add(cliente);
                        }

                        DatabaseReference mRefDireccion = FirebaseDatabase.getInstance().getReference().child("Direccion");
                        mRefDireccion.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    Direccion direccion = ds.getValue(Direccion.class);
                                    direccion.setIdDireccion(ds.getKey());
                                    listDireccion.add(direccion);
                                }

                                DatabaseReference mRefTransportistas = FirebaseDatabase.getInstance().getReference().child("Transportistas");
                                mRefTransportistas.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()){
                                            Transportistas transportistas = ds.getValue(Transportistas.class);
                                            transportistas.setId(ds.getKey());
                                            listTransportistas.add(transportistas);
                                        }

                                        DatabaseReference mRefVehiculos = FirebaseDatabase.getInstance().getReference().child("Vehiculos");
                                        mRefVehiculos.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot ds : snapshot.getChildren()){
                                                    Vehiculos vehiculo = ds.getValue(Vehiculos.class);
                                                    vehiculo.setIdVehiculos(ds.getKey());
                                                    listVehiculos.add(vehiculo);
                                                }
                                                if(progressBar !=null){
                                                    progressBar.setVisibility(View.GONE);
                                                }
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createXLS(List<Seguimientoruta> seguimientos){
        try {
            String strDate = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(new Date());
            File root2 = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FileExcel");
            if(!root2.exists())
                root2.mkdirs();
            File path = new File(root2, "/" + strDate + ".xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(path);

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);

            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short)12);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            XSSFSheet sheet = workbook.createSheet("Reporte de Rutas");
            XSSFRow row = sheet.createRow(0);

            XSSFCell cell = row.createCell(0);
            cell.setCellValue("Cliente");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(1);
            cell.setCellValue("Dirección");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(2);
            cell.setCellValue("Fecha");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(3);
            cell.setCellValue("Transportista");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(4);
            cell.setCellValue("Chofer");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(5);
            cell.setCellValue("Licencia");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(6);
            cell.setCellValue("Vehiculo");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(7);
            cell.setCellValue("Placa");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(8);
            cell.setCellValue("Latitud");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(9);
            cell.setCellValue("Longitud");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(10);
            cell.setCellValue("Estado");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(11);
            cell.setCellValue("Kilometraje");
            cell.setCellStyle(headerStyle);

            String Textocliente = "";
            String Textodireccion = "";
            String Textofecha = "";
            String Textotransportista = "";
            String Textochofer = "";
            String Textolicencia = "";
            String Textovehiculo = "";
            String Textoplaca = "";
            String Textolatitud = "";
            String Textolongitud = "";
            String Textoestado = "";
            String Textokilometraje = "";

            for(int i = 0; i < seguimientos.size(); i++){
                row = sheet.createRow(i + 1);

                Textolatitud = seguimientos.get(i).getLatitud();
                Textolongitud = seguimientos.get(i).getLongitud();
                Textofecha = seguimientos.get(i).getFecha();
                Textokilometraje = seguimientos.get(i).getKilometraje();

                if(seguimientos.get(i).getEstado().equals("F")){
                    Textoestado = "FINALIZADO";
                } else if (seguimientos.get(i).getEstado().equals("A")) {
                    Textoestado = "ATENDIDO";
                } else if (seguimientos.get(i).getEstado().equals("I")) {
                    Textoestado = "INICIADO";
                } else if (seguimientos.get(i).getEstado().equals("P")) {
                    Textoestado = "PENDIENTE";
                }

                for(int j = 0; j < lstRuta.size(); j++){
                    if(lstRuta.get(j).getIdRuta().equals(seguimientos.get(i).getRuta())){
                        for(int h = 0; h < listChoferes.size(); h++){
                            if(lstRuta.get(j).getChofer().equals(listChoferes.get(h).getIdChofer())){
                                Textochofer = listChoferes.get(h).getNombres();
                                Textolicencia = listChoferes.get(h).getLicencia();

                                for(int t = 0; t < listTransportistas.size(); t++){
                                    if(listChoferes.get(h).getTransportista().equals(listTransportistas.get(t).getId())){
                                        Textotransportista = listTransportistas.get(t).getNombre();
                                    }
                                }
                            }
                        }
                        for (int h = 0; h < listVehiculos.size(); h++){
                            if(lstRuta.get(j).getVehiculo().equals(listVehiculos.get(h).getIdVehiculos())){
                                Textovehiculo = listVehiculos.get(h).getMarca();
                                Textoplaca = listVehiculos.get(h).getPlaca();
                            }
                        }
                        for(int h = 0; h < listDireccion.size(); h++){
                            if(lstRuta.get(j).getDireccion().equals(listDireccion.get(h).getIdDireccion())){
                                Textodireccion = listDireccion.get(h).getDistrito() + " - " +listDireccion.get(h).getDescripcion();
                                for(int c = 0; c < listClientes.size(); c++){
                                    if(listDireccion.get(h).getCliente().equals(listClientes.get(c).getIdCliente())){
                                        Textocliente = listClientes.get(c).getNombres();
                                    }
                                }
                            }
                        }
                    }
                }

                cell = row.createCell(0);
                cell.setCellValue(Textocliente);
                sheet.setColumnWidth(0, (Textocliente.length() + 30) * 100);

                cell = row.createCell(1);
                cell.setCellValue(Textodireccion);
                sheet.setColumnWidth(1, (Textodireccion.length() + 30) * 100);

                cell = row.createCell(2);
                cell.setCellValue(Textofecha);
                sheet.setColumnWidth(2, (Textofecha.length() + 30) * 100);

                cell = row.createCell(3);
                cell.setCellValue(Textotransportista);
                sheet.setColumnWidth(3, (Textotransportista.length() + 30) * 100);

                cell = row.createCell(4);
                cell.setCellValue(Textochofer);
                sheet.setColumnWidth(4, (Textochofer.length() + 30) * 100);

                cell = row.createCell(5);
                cell.setCellValue(Textolicencia);
                sheet.setColumnWidth(5, (Textolicencia.length() + 30) * 100);

                cell = row.createCell(6);
                cell.setCellValue(Textovehiculo);
                sheet.setColumnWidth(6, (Textovehiculo.length() + 30) * 100);

                cell = row.createCell(7);
                cell.setCellValue(Textoplaca);
                sheet.setColumnWidth(7, (Textoplaca.length() + 30) * 100);

                cell = row.createCell(8);
                cell.setCellValue(Textolatitud);
                sheet.setColumnWidth(8, (Textolatitud.length() + 30) * 100);

                cell = row.createCell(9);
                cell.setCellValue(Textolongitud);
                sheet.setColumnWidth(9, (Textolongitud.length() + 30) * 100);

                cell = row.createCell(10);
                cell.setCellValue(Textoestado);
                sheet.setColumnWidth(10, (Textoestado.length() + 30) * 100);

                cell = row.createCell(11);
                cell.setCellValue(Textokilometraje);
                sheet.setColumnWidth(11, (Textokilometraje.length() + 30) * 100);

            }

            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(root.getContext(),"Se ha generado el reporte correctamente!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generarData(){
        DatabaseReference mRefRutas = FirebaseDatabase.getInstance().getReference().child("Ruta");
        mRefRutas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Ruta ruta = ds.getValue(Ruta.class);
                    ruta.setIdRuta(ds.getKey());
                    lstRuta.add(ruta);

                    DatabaseReference mRefSeguimientos = FirebaseDatabase.getInstance().getReference().child("Seguimientoruta");
                    Query mRefSeg = mRefSeguimientos.orderByChild("ruta").equalTo(ruta.getIdRuta());
                    mRefSeg.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){
                                Seguimientoruta seguimientoruta = ds.getValue(Seguimientoruta.class);
                                seguimientoruta.setIdSeguimiento(ds.getKey());
                                lstseguimientorutas.add(seguimientoruta);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(progressBar !=null){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}