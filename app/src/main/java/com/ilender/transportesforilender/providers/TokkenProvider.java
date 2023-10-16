package com.ilender.transportesforilender.providers;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ilender.transportesforilender.model.Tokken;

public class TokkenProvider {

    DatabaseReference mCollection;

    public TokkenProvider() {
        mCollection = FirebaseDatabase.getInstance().getReference("Tokens");
    }

    public void create(String idUser) {
        if (idUser == null) {
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Tokken token = new Tokken(s);
                // Utiliza push() para generar una clave única para cada token
                mCollection.child(idUser).push().setValue(token);
            }
        });
    }

    public DatabaseReference getTokenReference(String idUser) {
        return mCollection.child(idUser);
    }
}