package com.example.proje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
EditText kullaniciadi,adsoyad,email,sifre;
Button kaydol;
TextView txt_giris;
FirebaseAuth auth;
DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        kullaniciadi=findViewById(R.id.kullanıcıadi);
        adsoyad=findViewById(R.id.ad);
        email=findViewById(R.id.email);
        sifre=findViewById(R.id.sifre);
        kaydol=findViewById(R.id.kaydol);
        txt_giris=findViewById(R.id.txt_giris);


        auth = FirebaseAuth.getInstance();

        txt_giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

      kaydol.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              pd=new ProgressDialog(RegisterActivity.this);
              pd.setMessage("Lütfen bekleyiniz");
              pd.show();

              String str_kullaniciadi = kullaniciadi.getText().toString();
              String str_adsoyad = adsoyad.getText().toString();
              String str_email = email.getText().toString();
              String str_sifre = sifre.getText().toString();

              if(TextUtils.isEmpty(str_kullaniciadi)||TextUtils.isEmpty(str_adsoyad)||
                      TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_sifre)){
                  Toast.makeText(RegisterActivity.this,"Bütün alanlar doldurulmalıdır",Toast.LENGTH_SHORT).show();
              } else if (str_sifre.length()<6){
                  Toast.makeText(RegisterActivity.this,"Şifreniz 6 karakterden uzun olmalıdır",Toast.LENGTH_SHORT).show();
              } else{
                 kaydolma(str_kullaniciadi,str_adsoyad,str_email,str_sifre);
              }
          }
      });
    }

    private void kaydolma(String kullaniciadi,String adsoyad, String email, String sifre){
        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String kullaniciId = firebaseUser.getUid();

                    reference= FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(kullaniciId);

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("id",kullaniciId);
                    hashMap.put("kullaniciadi",kullaniciadi);
                    hashMap.put("AdSoyad",adsoyad);
                    hashMap.put("bio","");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/proje-b6ed3.appspot.com/o/profile-placeholder.png?alt=media&token=cb28e0d4-856e-4e86-8292-aee7223b8d82");


                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });

                }else{
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this,"Bu email ya da şifre ile kayıt olamazsınız.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}