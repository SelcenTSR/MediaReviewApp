package com.example.proje;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    Uri imageData;
    String myUrl;
    Bitmap selectedImage;
   ImageView imageView,close;
   TextView paylas;
   EditText inceleme;
   RadioButton film,dizi,kitap;
   StorageReference kitapReference,diziReference,filmReference;
   StorageTask yuklemeGorevi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
   imageView=findViewById(R.id.image_added);
   inceleme=findViewById(R.id.inceleme);
   close=findViewById(R.id.close);
   paylas=findViewById(R.id.post);
        film=findViewById(R.id.film);
        dizi=findViewById(R.id.dizi);
        kitap=findViewById(R.id.kitap);
        kitapReference= FirebaseStorage.getInstance().getReference("kitaplar");
        diziReference = FirebaseStorage.getInstance().getReference("diziler");
        filmReference = FirebaseStorage.getInstance().getReference("filmler");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(PostActivity.this,MainActivity.class));
            }
        });

        paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

    }
    private String dosyaUzantisi(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void resimYukle() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Gönderiliyor");
        pd.show();

        if (imageData != null){
            if (film.isChecked()){
                StorageReference storageReference = filmReference.child(System.currentTimeMillis()
                        + "." + dosyaUzantisi(imageData));
                yuklemeGorevi = storageReference.putFile(imageData);
                yuklemeGorevi.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri indirmeUri = task.getResult();
                            myUrl = indirmeUri.toString();
                            DatabaseReference veriYolu= FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Filmler");
                            String gonderiId = veriYolu.push().getKey();
                            HashMap<String,Object>hashMap = new HashMap<>();

                            hashMap.put("gonderiid", gonderiId);
                            hashMap.put("gonderiimage", myUrl);
                            hashMap.put("inceleme", inceleme.getText().toString());
                            hashMap.put("paylasan", FirebaseAuth.getInstance().getCurrentUser().getUid());


                            veriYolu.child(gonderiId).setValue(hashMap);

                            pd.dismiss();

                            startActivity(new Intent(PostActivity.this,MainActivity.class));

                        }
                        else {
                            Toast.makeText(PostActivity.this, "Gönderi başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (dizi.isChecked()){
                StorageReference storageReference = diziReference.child(System.currentTimeMillis()
                        + "." + dosyaUzantisi(imageData));
                yuklemeGorevi = storageReference.putFile(imageData);
                yuklemeGorevi.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri indirmeUri = task.getResult();
                            myUrl = indirmeUri.toString();
                            DatabaseReference veriYolu= FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Diziler");
                            String gonderiId = veriYolu.push().getKey();
                            HashMap<String,Object>hashMap = new HashMap<>();

                            hashMap.put("gonderiid", gonderiId);
                            hashMap.put("gonderiimage", myUrl);
                            hashMap.put("inceleme", inceleme.getText().toString());
                            hashMap.put("paylasan", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            veriYolu.child(gonderiId).setValue(hashMap);

                            pd.dismiss();

                            startActivity(new Intent(PostActivity.this,MainActivity.class));

                        }
                        else {
                            Toast.makeText(PostActivity.this, "Gönderi başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            if (kitap.isChecked()){
                StorageReference storageReference = kitapReference.child(System.currentTimeMillis()
                        + "." + dosyaUzantisi(imageData));
                yuklemeGorevi = storageReference.putFile(imageData);
                yuklemeGorevi.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri indirmeUri = task.getResult();
                            myUrl = indirmeUri.toString();
                            DatabaseReference veriYolu= FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Kitaplar");
                            String gonderiId = veriYolu.push().getKey();
                            HashMap<String,Object>hashMap = new HashMap<>();

                            hashMap.put("gonderiid", gonderiId);
                            hashMap.put("gonderiimage", myUrl);
                            hashMap.put("inceleme", inceleme.getText().toString());
                            hashMap.put("paylasan", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            veriYolu.child(gonderiId).setValue(hashMap);

                            pd.dismiss();

                            startActivity(new Intent(PostActivity.this,MainActivity.class));

                        }
                        else {
                            Toast.makeText(PostActivity.this, "Gönderi başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

    }


    public void selectImage(View view){
if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
}else
{
    Intent intentGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intentGallery,2);
}
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2 && resultCode == RESULT_OK && data != null  ){
           imageData = data.getData();
            try {
                if(Build.VERSION.SDK_INT >=30){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}


