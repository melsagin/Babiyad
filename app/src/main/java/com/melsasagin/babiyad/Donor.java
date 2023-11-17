package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Donor extends AppCompatActivity {

    private Button check;
    private ArrayList<String> listData;
    private int Smaval,Kanserval;
    private String stk;
    DatabaseReference reference_org;
    private int count=0;
    protected ArrayAdapter<String> loc_adaptor;
    Spinner sp;
    BottomNavigationView bottomNavigationView;
    private CheckBox Sma, Kanser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        Sma = findViewById(R.id.smaCheckboxes);
        Kanser = findViewById(R.id.kanserCheckboxes);
        check = (Button) findViewById(R.id.check);

        bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.hostevents);

        sp = (Spinner)findViewById(R.id.stk_spinner1);
        loc_adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.stk_names));
        //android.R.layout.simple_list_item_1, androidde tanımlı basit bir liste öğesi görünümü
        //R.array.stk_names, string içinde tanımladım
        loc_adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//Spinner'a seçenekleri açılır listede göstermek için kullanılan varsayılan bir görünüm şablonu
        sp.setAdapter(loc_adaptor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stk = sp.getSelectedItem().toString();//spinnerda seçilen öğe string olarak alınır
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        check.setOnClickListener(v -> {
            String s_Sma = String.valueOf(Sma.isChecked());
            String s_Kanser = String.valueOf(Kanser.isChecked());
            Log.d("RUN", "Sma" + String.valueOf(Sma.isChecked()));
            Log.d("RUN", "Kanser" + String.valueOf(Kanser.isChecked()));
            if (s_Sma.equals("false")) {
                Smaval = 0;
            } else {
                Smaval = 1;
            }

            if (s_Kanser.equals("false")) {
                Kanserval = 0;
            } else {
                Kanserval = 1;
            }

            reference_org = FirebaseDatabase.getInstance().getReference("Organisation");//firebase'in örneği alınır organisation düğümü oluşturulur
            reference_org.addListenerForSingleValueEvent(new ValueEventListener() {//ValueEventListener ise değişikliği dinler
                //addListenerForSingleValueEvent veri değişikliklerini takip etmez ve yalnızca mevcut verileri bir kez alır
                //bunu kullanma sebebim sonrasında yapacağım stklar listesinin sürekli güncellenmemesi için
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        count = (int) snapshot.getChildrenCount();//Organisation altındaki bütün doğrulanmış stk sayısını aldım
                        listData = new ArrayList<>();
                        int count_org = 0, flag = 0;
                        Log.d("RUN", "Stk " + String.valueOf(stk));//spinnerda seçilen öğedeki stk
                        for (DataSnapshot dbsnapshot : snapshot.getChildren()) {
                            //Burada db deki onaylanmış stklar tek tek gezilir ve bilgiler alınır
                            //örneğin ilk önce krc stksı var bu yüzden if bloğuna girmez
                            //ikinci kısımda ise istediğin org var bu durumda if bloğuna girer
                            //aynı zamanda tüm db deki veriler üzerinden geçilirse bu durumda ikinci if bloğuna girebilir
                            Log.d("RUN", "Count "+ String.valueOf(count_org));
                            count_org += 1;
                            String cur_stk = dbsnapshot.child("stk").getValue(String.class);
                            Log.d("RUN", "Mevcut Stk " + String.valueOf(cur_stk));
                            int sma= dbsnapshot.child("sma").getValue(Integer.class);
                            int kanser = dbsnapshot.child("kanser").getValue(Integer.class);
                            int verify = dbsnapshot.child("verify").getValue(Integer.class);
                            Log.d("run", String.valueOf(sma));
                            Log.d("run", String.valueOf(kanser));
                            Log.d("count", String.valueOf(count_org));
                            int finalCount_org = count_org;
                            //bu if bloğunda eğer seçilen Lösev ise sadece onaylanmış Lösevler flag=1 edilir
                            //Eğer kasder için kayıtlı stk birimi varsa buraya girmez çünkü kullanıcı Lösevi seçti
                            if ((stk.equals(cur_stk)) && ((Smaval == sma && Smaval == 1) || (Kanserval == kanser && Kanserval == 1)) && (verify == 1)) {
                                //db deki stk ile stk değişkeni içindeki stk eşleşiyorsa buraya girecek
                                Log.d("RUN", "Girdi");
                                flag = 1;
                                Log.d("RUN", "Final Count " + String.valueOf(finalCount_org));
                                listData.add(String.valueOf(dbsnapshot.getKey()));//DataSnapshot nesnesinin referans olduğu düğümün anahtarını (key) döndür
                                //mail adresini gösterir
                            }
                            if (finalCount_org == count){//burada bütün onaylanan stklar gezilmiş mi o karar verilir
                                if (flag == 1){//123. satırdaki if bloğuna girdiyse uygun stk var demektir
                                    Intent orgs = new Intent(Donor.this, DisplayOrgs.class);
                                    orgs.putExtra("Name", listData);//stknın mail adresi gönderiliyor
                                    Log.d("RUN", "listdata " + String.valueOf(listData));
                                    startActivity(orgs);
                                }
                                else{
                                    Toast.makeText(Donor.this, "Uygun Sivil Toplum Kuruluşu Yok", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.hostevents){
                startActivity(new Intent(Donor.this, EtkinligeKonuklukEt.class));
                overridePendingTransition(0,0);//etkinlik geçiş animasyonlarını devre dışı bırakmak için kullanılır.
                return true;//başarıyla tıklanma işlemi gerçekleşti
            }
            else if(item.getItemId() == R.id.showevents){
                startActivity(new Intent(Donor.this, EtkinlikGoruntule.class));
                overridePendingTransition(0,0);
                return true;
            }
            else if(item.getItemId() == R.id.about){
                startActivity(new Intent(Donor.this, Hakkimizda.class));
                overridePendingTransition(0,0);
                return true;
            }
            else if(item.getItemId() == R.id.faq){
                startActivity(new Intent(Donor.this, SSS.class));
                overridePendingTransition(0,0);
                return true;
            }

            else if(item.getItemId() == R.id.signout){
                startActivity(new Intent(Donor.this, LoginActivity.class));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }
}
