package com.melsasagin.babiyad;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BagisDetaylari extends AppCompatActivity {

    //Kullanıcı adı ve bağış miktarı girildiği takdirde bağış yapma sürecinin son aşamasını kapsayan aktivitedir.

    private EditText bagisciName,bagisMiktari;
    private String finalBagisciName,finalBagisMiktari;
    String encodedemail, bagisciemail;
    private DatabaseReference mDatabase_donate;
    String emailid;
    FirebaseStorage firebaseStorage;
    StorageReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagis_detaylari);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        bagisciemail = user.getEmail();
        bagisciemail = EncodeString(bagisciemail);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseStorage.getReference().child(bagisciemail);
        emailid = getIntent().getStringExtra("Email id");//bir önceki aktiviteden (BagisDetaylariniGoster) email id alınır ve bu aktivitede kullanılır
        mDatabase_donate = FirebaseDatabase.getInstance().getReference().child("OrgDonations");

        encodedemail= EncodeString(emailid);
    }

    private  static String EncodeString(String emailid) {
        return (emailid.replace(".", ","));
    }


    public void donate(View view) {
        bagisciName =  findViewById(R.id.donorName);
        bagisMiktari = findViewById(R.id.bagisQuantity);

        finalBagisciName = bagisciName.getText().toString();
        finalBagisMiktari = bagisMiktari.getText().toString();

        upload_database();

        Toast.makeText(this, "Bağış Gerçekleştirildi", Toast.LENGTH_SHORT).show();

    }

    private  void upload_database(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = df.format(date);
        boolean complete = false;
        String key = mDatabase_donate.child(encodedemail).push().getKey();//push().getKey() yöntemi çağrılarak rastgele bir anahtar değeri oluşturulur
        String date_received = "";
        Bagis_Veri_Stk org_data = new Bagis_Veri_Stk(key, bagisciemail, finalBagisciName, finalBagisMiktari, formattedDate, date_received, complete);
        mDatabase_donate.child(encodedemail).child(key).setValue(org_data);
    }
}