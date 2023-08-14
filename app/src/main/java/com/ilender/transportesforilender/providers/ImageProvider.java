package com.ilender.transportesforilender.providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ilender.transportesforilender.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        mStorage = FirebaseStorage.getInstance().getReference().child("Guias");

    }

    public UploadTask save(Context context, File file, int count, String id){
        String contador = String.valueOf(count);
        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);
        StorageReference storage = mStorage.child(id).child("Final").child(new Date().toString()+"-"+ contador +".jpg");
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public UploadTask saveInicio(Context context,File file,int count,String id){
        String contador = String.valueOf(count);
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500,500);
        StorageReference storage = mStorage.child(id).child("Inicio").child(new Date().toString() +"-"+ contador +".jpg");
        UploadTask task = storage.putBytes(imageByte);
        return task;

    }
    public StorageReference getStorage(){
        return mStorage;
    }
}
