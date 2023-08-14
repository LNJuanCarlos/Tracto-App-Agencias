package com.ilender.transportesforilender.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.ilender.transportesforilender.R;
import com.ilender.transportesforilender.providers.AuthProvider;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;

    TextInputEditText mTextInputPassword;

    Button mButtonlogin;

    //
    private CheckBox cbRecordar;
    private SharedPreferences sharedPref;

    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cbRecordar = findViewById(R.id.cbRecordar);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");

        mTextInputEmail.setText(username);
        mTextInputPassword.setText(password);

        mButtonlogin = findViewById(R.id.btnLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuthProvider = new AuthProvider();

        mButtonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = mTextInputEmail.getText().toString();
                String password = mTextInputPassword.getText().toString();

                if (cbRecordar.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();

                }
                if(validar()){
                    login();}

            }

        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuthProvider.getUserSession() !=null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public boolean validar(){
        boolean retorno = true;

        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        if(email.isEmpty()){
            mTextInputEmail.setError("Ingresa Email");
            retorno = false;
        }
        if(password.isEmpty()){
            mTextInputPassword.setError("Ingresa la Contraseña");
            retorno = false;
        }
        return retorno;
    }

    private void login(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        mAuthProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, "El email / o contraseña ingresados , no son correctos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d("campo","email: " +email);
        Log.d("campo","password: " +password);
    }
}
