package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class BagisDetaylariniGoster extends AppCompatActivity {

    private DatabaseReference reference_org;

    private TextView detailfullname,detailemail,detailphone;

    private  TextView add,type_donation,desc,head,uye;
    String encodedemail, items_req;
    ArrayList<UploadInfo> org_images;
    RecyclerView images_view;
    GalleryAdapter adapter;
    private Button donate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagis_detaylarini_goster);

        detailfullname = (TextView) findViewById(R.id.detailfullname);
        detailemail = (TextView) findViewById(R.id.detailemailid);
        add = findViewById(R.id.detailaddress);
        desc = findViewById(R.id.detaildesc);
        head = findViewById(R.id.detailhead);
        type_donation = findViewById(R.id.typedonation);
        uye = findViewById(R.id.detailsuye);
        detailphone = findViewById(R.id.detailphone);
        images_view = findViewById(R.id.rec_view);
        String fullname = getIntent().getStringExtra("STK Name");//DataAdapter'dan alındı
        String emailid = getIntent().getStringExtra("Email id");
        detailfullname.setText(fullname);
        detailemail.setText(emailid);
        donate = findViewById(R.id.Button3);
        encodedemail = EncodeString(emailid);
        reference_org = FirebaseDatabase.getInstance().getReference("Organisation");

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BagisDetaylariniGoster.this, BagisDetaylari.class);
                intent.putExtra("Email id",emailid);
                startActivity(intent);
            }
        });

        reference_org.child(encodedemail).addValueEventListener(new ValueEventListener() {//organisation içindeki stk maili ile db dinlenir
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){//veritabanında bir görüntünün var olup olmadığını kontrol eder, varsa
                    String address = " " + snapshot.child("address").getValue(String.class) + "  " + snapshot.child("city").getValue(String.class);
                    add.setText(address);//organisation düğümündeki bağış yapılacak stk nın adres(semt) ve şehir bu xml deki detailadress'a eklenir
                    items_req = " ";
                    if (snapshot.child("sma").getValue(Integer.class) == 1){
                        items_req += " Sma  ";
                    }
                    if (snapshot.child("kanser").getValue(Integer.class) == 1){
                        items_req += " Kanser ";
                    }
                    type_donation.setText(items_req);
                    detailphone.setText(snapshot.child("phone").getValue().toString());
                    desc.setText(snapshot.child("desc").getValue(String.class));
                    head.setText(snapshot.child("head").getValue(String.class));
                    uye.setText(snapshot.child("uye").getValue().toString() + " kişi");
                    desc.setText(snapshot.child("desc").getValue(String.class));
                }
                else {
                    Toast.makeText(BagisDetaylariniGoster.this, fullname+ " detayları bulunamadı",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        org_images = new ArrayList<>();
        images_view.setHasFixedSize(true);//RecyclerView'ın boyutunu sabitleyerek performansı arttırmak hedeflenir
        images_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));//öğeler yatay -yan yana- olarak tek bir sıra halinde düzenlenir.
        adapter = new GalleryAdapter(this,org_images);
        images_view.setAdapter(adapter);
        //buraya kadar yukarıdakiler sadece boş bir görünüm oluşturur asıl görsel aşağıdaki metodda db den alınır
        getir_images();
    }

    private void getir_images(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Organisation").child(encodedemail);
        Log.d("RUN", "Ref "+ reference);
        reference.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                org_images.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UploadInfo upload = dataSnapshot.getValue(UploadInfo.class);
                    org_images.add(upload);
                    adapter = new GalleryAdapter(BagisDetaylariniGoster.this,org_images);
                    images_view.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private static String EncodeString(String string) {
        return (string.replace(".", ","));
    }
}