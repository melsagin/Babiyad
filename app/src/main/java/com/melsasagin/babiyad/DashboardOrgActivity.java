package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class DashboardOrgActivity extends AppCompatActivity {

    private TextView verification_status, showname;
    protected FirebaseUser user;
    protected DatabaseReference reference_user, reference_org;
    protected String userKey;
    String name;
    protected int verify_status, docs_status, details_status;
    ArrayList<SliderData> sliderDataArrayList;
    SliderView sliderView;
    String url1,url2,url3;
    BottomNavigationView bottomNavigationView;

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_org);
        bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.donations);
        showname = (TextView) findViewById(R.id.showname);


        //alt gezinme çubuğundaki öğelerin tıklanma olaylarını dinler.
        bottomNavigationView.setOnItemSelectedListener(item -> {//setOnNavigationSeletedListener kullanımdan kalktı
            if (item.getItemId() == R.id.donations ){//menu_org_navigation.xml den bakılır
                startActivity(new Intent(DashboardOrgActivity.this, DonationActivity.class));
                overridePendingTransition(0,0);//aktiviteye girerken veya çıkarken animasyon olmasın
                return true;//başarıyla tıklanma işlemi gerçekleşti
            }

            else if (item.getItemId() == R.id.events){
                startActivity(new Intent(DashboardOrgActivity.this, EtkinlikGoruntule.class));
                overridePendingTransition(0,0);
                return true;
            }

            else if (item.getItemId() == R.id.profile){
                startActivity(new Intent(DashboardOrgActivity.this, ProfileActivity.class));
                overridePendingTransition(0,0);
                return true;
            }

            else if (item.getItemId() == R.id.about){
                startActivity(new Intent(DashboardOrgActivity.this, Hakkimizda.class));
                overridePendingTransition(0,0);
                return true;
            }

            else if (item.getItemId() == R.id.signout){
                startActivity(new Intent(DashboardOrgActivity.this, LoginActivity.class));
                overridePendingTransition(0,0);
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {//başka bir uygulamaya geçilip tekrar geri gelindiğinde, Activity'nin durumu değişecektir ve onResume() metodu çağrılacaktır
        // onResume, Activity'nin arka plana atıldıktan sonra tekrar ön plana geldiğinde çağrılan bir metod
        super.onResume();
        sliderDataArrayList = new ArrayList<>();

        sliderView = findViewById(R.id.slider);
        url1 = "https://goop-img.com/wp-content/uploads/2020/03/how-to-help-covid-19-shutterstock_1440974273-1.jpg";
        url2 = "https://helios-i.mashable.com/imagery/articles/02dMbqnkHQZbIFpjPGpBQU4/hero-image.fill.size_1200x900.v1611614120.jpg";
        url3 = "https://static.independent.co.uk/s3fs-public/thumbnails/image/2020/03/05/17/istock-1162567225.jpg?quality=75&width=1200&auto=webp";
        sliderDataArrayList.add(new SliderData(url1));
        sliderDataArrayList.add(new SliderData(url2));
        sliderDataArrayList.add(new SliderData(url3));
        SliderAdapter1 adapter = new SliderAdapter1(this, sliderDataArrayList);//SliderAdapter1 sınıfından bir adapter oluşturuluyor. Bu adapter, slider view'da gösterilecek olan resimleri ayarlamak için kullanılacak.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);//Slider'ın otomatik döngüsü yönü ayarlanıyor ve slaytın soldan sağa doğru hareket eder
        sliderView.setSliderAdapter(adapter);//SliderAdapter, Slider view'a bağlanıyor
        sliderView.setScrollTimeInSec(3);//Slider'ın otomatik döngü süresi ayarlanıyor. Bu durumda, her 3 saniyede bir otomatik olarak dönecektir.
        sliderView.setAutoCycle(true);//Slider'ın otomatik döngüsü aktif hale getiriliyor.
        sliderView.startAutoCycle();//Slider'ın otomatik döngüsü başlatılıyor

        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("RUN", String.valueOf(user));
        reference_user = FirebaseDatabase.getInstance().getReference("Users");
        reference_org = FirebaseDatabase.getInstance().getReference("Organisation");
        userKey = user.getEmail();
        Log.d("RUN", userKey);
        userKey = EncodeString(userKey);
        Log.d("RUN", userKey);
        reference_user.child(userKey).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {//userkey kullanılarak kullnıcının fullName bilgisi alınır
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.getValue(String.class);
                showname.setText("Hoşgeldiniz "+ name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        displaying_status(reference_org, userKey);//displaying_status(...) metodu aşağıda tanımlanmıştır
    }

    protected void displaying_status(DatabaseReference reference, String email){
        verification_status = (TextView)findViewById(R.id.verification_status);
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//DataSnapshot ile db nin anlık ekran görüntüsü alınır
                if (snapshot.exists()) { //veritabanında bir görüntünün var olup olmadığını kontrol eder
                    Log.d("RUN", "Girdi");
                    verify_status = snapshot.child("verify").getValue(Integer.class);//db deki değerler atanır
                    docs_status = snapshot.child("docs_verify").getValue(Integer.class);
                    details_status = snapshot.child("details_verify").getValue(Integer.class);
                    if(verify_status == 1) {
                        verification_status.setText("Onaylandınız!");
                    }
                    else{
                        verification_status.setText("Onaylanmadınız! Lütfen Ayrıntıları Tamamlayın");
                    }
                }
                else{
                    verification_status.setText("Ayrıntıları tamamlamak için Profil Bölümüne gidin!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardOrgActivity.this,"Hatalı bir durum var !",Toast.LENGTH_LONG).show();
            }
        });
    }
}
