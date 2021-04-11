package com.example.firebaseinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuthFeed;
    FirebaseFirestore firebaseFirestoreFeed;
    FeedRcyclerAdapter feedRcyclerAdapter;

    ArrayList<String> userCommentFromFB, userMailFromFB, userImageFromFB;   /// RCYCLER VİEW DA GÖSTERMEK İÇİN ARRAYLİST yapıyoruz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuthFeed = FirebaseAuth.getInstance();
        firebaseFirestoreFeed = FirebaseFirestore.getInstance();

        userCommentFromFB = new ArrayList<>();
        userImageFromFB = new ArrayList<>();
        userMailFromFB = new ArrayList<>();

        getDataFromFirebase();

        /// Recycler

        feedRcyclerAdapter = new FeedRcyclerAdapter(userMailFromFB , userCommentFromFB , userImageFromFB);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(feedRcyclerAdapter);


    }


    public void getDataFromFirebase(){

        CollectionReference collectionReference = firebaseFirestoreFeed.collection("posts");   ////POSTS COLLECTİONUNDAKİ BİLGİLERİ ALDIK.
        collectionReference.orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {  // orderby yaparak date ye göre sıraladık. DESCENDING DEĞİL DE ASCERDING YAPSAK TAM TERSİ SIRALAR. Where ekleyip filtreleme de yapabilriz.
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {   //// BİLGİLER value NİN İÇİNDE LİSTE ŞEKLİNDE KAYITLI.

                if (error != null){
                    Toast.makeText(FeedActivity.this , error.getLocalizedMessage().toString() , Toast.LENGTH_LONG).show();
                }

                if (value != null){

                    for (DocumentSnapshot documentSnapshot : value){  ////VALUE BİLGİLERİ DOCUMENTSNAPSHOTS OLARAK KAYDETTİĞİ İÇİN ONU KULLANDIK.

                        Map<String,Object> data = documentSnapshot.getData();

                        String mail = (String)  data.get("userMail");
                        String comment = (String) data.get("comment");
                        String url = (String) data.get("downloadUrl");

                        userCommentFromFB.add(comment);
                        userImageFromFB.add(url);
                        userMailFromFB.add(mail);

                        feedRcyclerAdapter.notifyDataSetChanged();

                        System.out.println("comment: " + comment);
                    }

                }

            }
        });

    }







    /////MENÜ

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu , menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addPostId){


            Intent intentToUpload = new Intent(FeedActivity.this , UploadActivity.class);
            startActivity(intentToUpload);

        }else if (item.getItemId() == R.id.signOutId){


                firebaseAuthFeed.signOut();
                Intent intentToEnter = new Intent(FeedActivity.this , EnterPage.class);
                startActivity(intentToEnter);
                finish();
            }

        return super.onOptionsItemSelected(item);
    }
}