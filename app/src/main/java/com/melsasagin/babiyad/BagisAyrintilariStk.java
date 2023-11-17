package com.melsasagin.babiyad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BagisAyrintilariStk extends AppCompatActivity {
    private DatabaseReference mDatabase_donate, mDatabase_rating;
    private FirebaseUser user;
    private TextView name,email, donated_items, date_received, date_donated;
    private SwitchCompat switch_status;
    private String org_email;
    private boolean complete;
    private RatingBar rating;
    float rating_val = 0.0f;
    private Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagis_ayrintilari);

        name = findViewById(R.id.detailfullname);
        email = findViewById(R.id.detailemailid);
        donated_items = findViewById(R.id.detailsdonated);
        date_received = findViewById(R.id.dater_val);
        date_donated = findViewById(R.id.dated_val);
        switch_status = findViewById(R.id.switchcompat);

        String key = getIntent().getStringExtra("Key");//Key verisi DetaylarAdapter içinden gönderilir ancak oluşturulması BagisDetaylari'nda yapıldı
        user = FirebaseAuth.getInstance().getCurrentUser();
        org_email = user.getEmail();
        org_email = org_email.replace(".", ",");
        mDatabase_donate = FirebaseDatabase.getInstance().getReference().child("OrgDonations");
        mDatabase_rating = FirebaseDatabase.getInstance().getReference().child("Ratings");
        mDatabase_donate.child(org_email).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    name.setText(snapshot.child("name").getValue(String.class));
                    email.setText((snapshot.child("email").getValue(String.class)).replace(",", "."));
                    String items_donate = "\n\n";
                    if (!snapshot.child("bagis").getValue(String.class).equals("")){//bağış miktarının gösterildiği kısım (boş değilse)
                        items_donate += snapshot.child("bagis").getValue(String.class) + "\n";
                    }
                    donated_items.setText(items_donate);
                    date_donated.setText(snapshot.child("date_donated").getValue(String.class));
                    date_received.setText(snapshot.child("date_received").getValue(String.class));
                    complete = snapshot.child("complete").getValue(Boolean.class);
                    if (!complete) {//bağış tamamlanmamışsa (onaylanmamışsa)
                        switch_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                               switch_status.setChecked(isChecked);
                                if (isChecked) {
                                    Bagis_Veri_Stk org_data = snapshot.getValue(Bagis_Veri_Stk.class);
                                    org_data.setComplete(true);
                                    Date date = Calendar.getInstance().getTime();//bağışın alındığı mevcut (anlık) zaman-tarih alınır ve date değişkenine atanır
                                    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());// tarih formatını ayarlamak için kullanılır.
                                    String formattedDate = df.format(date);//formatlanmış tarihi formattedDate değişkenine atar.
                                    org_data.setDate_received(formattedDate);//bağışın alındığı tarihi org_data değişkenine atar.
                                    mDatabase_donate.child(org_email).child(key).setValue(org_data);//güncellenen bağış verilerini Firebase'e kaydeder.
                                    View view = LayoutInflater.from(BagisAyrintilariStk.this).inflate(R.layout.rating_bar, null);
                                    rating = (RatingBar) view.findViewById(R.id.ratingBar);
                                    submit = (Button) view.findViewById(R.id.submitrating);
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(BagisAyrintilariStk.this);
                                    builder.setView(view);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                        @Override
                                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                            rating_val = rating;
                                        }
                                    });
                                    submit.setOnClickListener(new View.OnClickListener() {//ratingbarın submiti dinlenir
                                        @Override
                                        public void onClick(View v) {
                                            Rating rating_db = new Rating(formattedDate, rating_val);
                                            //aşağıdaki push metoduyla benzersiz key değeri oluşturulur
                                            mDatabase_rating.child(snapshot.child("email").getValue(String.class)).child(org_email).push().setValue(rating_db);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else{
                        switch_status.setChecked(true);
                        switch_status.setClickable(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

