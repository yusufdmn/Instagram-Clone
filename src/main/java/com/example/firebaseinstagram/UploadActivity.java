package com.example.firebaseinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.ls.LSOutput;

import java.security.Permission;
import java.util.HashMap;
import java.util.UUID;

import io.grpc.internal.JsonUtil;

public class UploadActivity extends AppCompatActivity {

    ImageView imageView;
    EditText commentEditText;
    Bitmap selectedImage;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imageData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageView = findViewById(R.id.selectImageId);
        commentEditText = findViewById(R.id.commentId);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        ////alerts
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.getEmail().matches("dunya@gmail.com")){
            AlertDialog.Builder alert = new AlertDialog.Builder(UploadActivity.this);
            alert.setTitle("Best");
            alert.setMessage("Hello again melek :PPP You are best of best <3");
            alert.setNegativeButton("Evet ben best", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.setIcon(R.drawable.kalp);
            alert.show();
        }

    }

    public void uploadF(View view){


        if (imageData != null){

            UUID uuid = UUID.randomUUID();
            final String idName = "image/" + uuid + ".jpg";

            storageReference.child(idName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {   ////STORAGEYE YÜKLÜYORUZ FOTOYU
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newReference = FirebaseStorage.getInstance().getReference(idName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {       //// BAŞARILI OLURSA YENİ POSTU CLOUD VERİTABANINA YÜKKLÜYORUZ.
                            String downloadUrl = uri.toString();
                            System.out.println(downloadUrl);

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String email = firebaseUser.getEmail();
                            String comment = commentEditText.getText().toString();

                            HashMap<String , Object> postData = new HashMap<>();   //// HASHMAP OLUŞTURUYORUZ ÇÜNKÜ AŞAĞIDA COLLECTİONA HASHMAP GİRİYORUZ.
                            postData.put("userMail" , email);
                            postData.put("comment" , comment);
                            postData.put("downloadUrl" , downloadUrl);
                            postData.put("time" , FieldValue.serverTimestamp());  //// O anki zamanı söylüyor.

                            firebaseFirestore.collection("posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Intent intent = new Intent(UploadActivity.this , FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(UploadActivity.this , e.getLocalizedMessage().toString() , Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, "Başarısız...", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "Başarısız...", Toast.LENGTH_SHORT).show();
                }
            });

        }

        else {
            Toast.makeText(UploadActivity.this, "Başarısız...", Toast.LENGTH_SHORT).show();
        }

    }

    public void selectImageF(View view){

        if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(UploadActivity.this , new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
        }
        else {

            Intent intentToGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery , 2);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {   /// İZİN VERDİKTEN HEMEN SONRA

        if (requestCode==1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery , 2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {    //// İZİN ZATEN VERİLİYSE DAHA SONRA NE OLACAK
        System.out.println(1);

        if (requestCode == 2 && resultCode==RESULT_OK && data !=null){

            imageData = data.getData();
            System.out.println(1);

            try {
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver() , imageData);
                    System.out.println(2);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    System.out.println(3);
                    imageView.setImageBitmap(selectedImage);
                    System.out.println(4);
                }
                else {
                    System.out.println(5);
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver() , imageData);
                    System.out.println(6);
                    imageView.setImageBitmap(selectedImage);
                    System.out.println(7);
                }
            }catch (Exception e){
                System.out.println(e);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}