package com.example.firebaseinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EnterPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        emailEditText = findViewById(R.id.emailTextId);
        passwordEditText = findViewById(R.id.passwordId);

        firebaseAuth = FirebaseAuth.getInstance();

        ///////////////DAHA ÖNCEDEN GİRİŞ YAPILDI MI DİYE KONTROL EDİYORUZ.
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(EnterPage.this , FeedActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void signInF (View view){

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(EnterPage.this , "Giriş Başarılı." , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EnterPage.this , FeedActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EnterPage.this , e.getLocalizedMessage().toString() , Toast.LENGTH_LONG).show();
            }
        });

    }




    public void signUpF (View view){

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {  ////EĞER ÜYE OLUŞTURMA BAŞARILI OLURSA:

                Toast.makeText(EnterPage.this , "Üye oluşturuldu." , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EnterPage.this , FeedActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {   ///// ÜYE OLUŞTURURKEN HATA OLURSA:
                Toast.makeText(EnterPage.this , e.getLocalizedMessage().toString() , Toast.LENGTH_LONG).show();
            }
        });

    }
}