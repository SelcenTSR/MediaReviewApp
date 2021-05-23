package com.example.proje;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
EditText grs_email,grs_sifre;
Button girisyap;
TextView txt_kaydol;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
grs_email=findViewById(R.id.grs_email);
grs_sifre=findViewById(R.id.grs_sifre);
girisyap=findViewById(R.id.girisyap);
txt_kaydol=findViewById(R.id.txt_kaydol);

auth=FirebaseAuth.getInstance();

txt_kaydol.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }
});

girisyap.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Lütfen Bekleyiniz");
        pd.show();

        String str_grsemail=grs_email.getText().toString();
        String str_grssifre=grs_sifre.getText().toString();

        if (TextUtils.isEmpty(str_grsemail) || TextUtils.isEmpty(str_grssifre)){
            pd.dismiss();
            Toast.makeText(LoginActivity.this,"Email veya şifre yanlış!",Toast.LENGTH_SHORT).show();
        }else {

            auth.signInWithEmailAndPassword(str_grsemail,str_grssifre).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                                .child(auth.getCurrentUser().getUid());

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                pd.dismiss();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this,"Başarısız oldu!",Toast.LENGTH_SHORT).show();

                }

                }
            });
        }
    }
});

    }
}