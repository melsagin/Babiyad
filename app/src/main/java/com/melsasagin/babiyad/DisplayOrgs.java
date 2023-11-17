package com.melsasagin.babiyad;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayOrgs extends AppCompatActivity {
    /*
    Bu sınıfın amacı, kullanıcı arayüzünde Firebase veritabanındaki Uygun Kuruluşlar verilerini listelemektir.
    Bu veriler, Data sınıfına dönüştürülerek RecyclerView üzerinde görüntülenir
     */
    private ArrayList<String> listData;
    private ArrayList<Data> stk_detaylar;
    private RecyclerView rv;
    private DataAdapter adapter;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_orgs);
        rv = (RecyclerView) findViewById(R.id.recycler1);
        rv.setHasFixedSize(true);//RecyclerView'ın boyutunu sabitleyerek performansı arttırmak hedeflenir
        rv.setLayoutManager(new LinearLayoutManager(this));//öğeler dikey olarak tek bir sıra halinde düzenlenir.
        listData = (ArrayList<String>) getIntent().getSerializableExtra("Name");//Donor classından gelen Name ekstra verisi alınır ve listData değişkenine atanır
        /*
        kompleks veri yapılarını (ArrayList) Intent ile iletilmek istendiğinde ve bu veri yapısını getIntentExtra() metoduyla almak istediğimde
        veri yapısının serileştirilebilir olması için Serializable arabirimini uygulamam gerekir getSerializableExtra ile.
        Ancak, basit veri tiplerini veya dönüşüm işlemleri gerektirmeyen verileri doğrudan alabilirim
         */
        Log.d("RUN","SIZE " + String.valueOf(listData.size()));//SIZE 1
        Log.d("RUN","DATA " + String.valueOf(listData));//DATA [stkadi@gmail,com]
        stk_detaylar = new ArrayList<>();
        final DatabaseReference nm = FirebaseDatabase.getInstance().getReference("Users");
        //Aşağıda genel olarak listdata içerisindeki veriler kontrollü bir biçimde stk_detaylara aktarılır
        //ve stk_detaylar ise adapter üzerinden recyclerview üzerinde görüntülenir
        for (int i=0; i<listData.size(); i++) {
            Log.d("RUN", String.valueOf(listData.get(i)));//stkadi@gmail,com
            nm.child(String.valueOf(listData.get(i))).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot npsnapshot) {
                    Log.d("RUN", String.valueOf(npsnapshot));//DataSnapshot { key = stkadi@gmail,com, value = {enable=1, fullName=DenemeStk, userType=STK, email=stkadi@gmail.com} }
                    Data l = npsnapshot.getValue(Data.class);//Veritabanı anlık görüntüsünden bir Data nesnesi alır ve l değişkenine atanır
                    stk_detaylar.add(l);//burada listdata içerisindeki veriler stk_detaylar içerisine eklenir
                    count += 1;//ve count, stk_detaylara her ekleme yapıldığında arttırılır
                    if (count == listData.size()) {// Eğer count değeri (stk_detaylar.size) listData dizisinin boyutuna eşitse, yani tüm veriler alındıysa
                        adapter = new DataAdapter(stk_detaylar);//adapter alınır
                        rv.setAdapter(adapter);//recyclerview da gösterilir
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}

