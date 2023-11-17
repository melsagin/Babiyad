package com.melsasagin.babiyad;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EtkinlikGoruntule extends AppCompatActivity {

    private List<Etkinlikler> listData;
    private RecyclerView rv;
    private EtkinliklerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlikgoruntule);

        rv = (RecyclerView)findViewById(R.id.recycler2);
        rv.setHasFixedSize(true);//RecyclerView'ın boyutunu sabitleyerek performansı arttırmak hedeflenir
        rv.setLayoutManager(new LinearLayoutManager(this));//öğeler dikey olarak tek bir sıra halinde düzenlenir.
        listData = new ArrayList<>();

        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events");

        Calendar calendar = Calendar.getInstance();//Calender nesnesi oluşturulur
        Date today = calendar.getTime();//getTime ile yerel zaman dilimindeki tarih ve saat bilgisi alınır

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//db'de veri varsa for çalışır
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        //nesnesinin çocuklarını (yani alt düzey verilerini) alır ve her bir çocuk için ayrı ayrı döngüyü çalıştırır
                        Etkinlikler l = npsnapshot.getValue(Etkinlikler.class);//veritabanından gelen veriler Etkinlikler sınıfına karşılık gelen bir nesneye dönüştürülür
                        listData.add(l);
                    }

                    Comparator<Etkinlikler> tarihKarsilastirici = new Comparator<Etkinlikler>() {
                        @Override
                        public int compare(Etkinlikler o1, Etkinlikler o2) {
                            try {
                                //hem tarih hem de saat alabilmek için SimpleDateFormat sınıfı kullanılır
                                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                                Date etkinlikDateTime1 = dateTimeFormat.parse(o1.getDate() + " " + o1.getTime());
                                Date etkinlikDateTime2 = dateTimeFormat.parse(o2.getDate() + " " + o2.getTime());
                                return etkinlikDateTime1.compareTo(etkinlikDateTime2);//compareTo() ile iki  tarih ve saat karşılaştırılır
                            } catch (Exception e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    };

                    Collections.sort(listData, tarihKarsilastirici); // listeyi tarihe göre sıralar
                    List<Etkinlikler> gelecekEtkinlikler = new ArrayList<>();
                    List<Etkinlikler> gecmisEtkinlikler = new ArrayList<>();
                    for (Etkinlikler event : listData) {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                            Date eventDate = dateFormat.parse(event.getDate() + " " + event.getTime()); // etkinlik tarih ve saati
                            if (eventDate.after(today)) { // tarih bugünden sonraysa, gelecekEtkinlikler listesine ekle
                                gelecekEtkinlikler.add(event);
                            } else if (eventDate.before(today)) { // tarih bugünden önceyse, gemişEtkinlikler listesine ekle
                                gecmisEtkinlikler.add(event);
                            } else { // aynı gün ise saat kontrolü yap
                                Calendar eventCalendar = Calendar.getInstance();//tarih saat alınır
                                eventCalendar.setTime(eventDate);//eventDate içindeki saat eventCalendar değişkenine çekilir
                                int eventHour = eventCalendar.get(Calendar.HOUR_OF_DAY);
                                int eventMinute = eventCalendar.get(Calendar.MINUTE);
                                Calendar todayCalendar = Calendar.getInstance();
                                todayCalendar.setTime(today);//bugünün içindekki saat bilgisi todayCalendara çekilir
                                int todayHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
                                int todayMinute = todayCalendar.get(Calendar.MINUTE);
                                if (eventHour > todayHour || (eventHour == todayHour && eventMinute > todayMinute)) { // etkinlik bugünden sonraysa, gelecekEtkinlikler listesine ekle
                                    gelecekEtkinlikler.add(event);
                                } else { // etkinlik bugünden önceyse, gecmisEtkinlikler listesine ekle
                                    gecmisEtkinlikler.add(event);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    gelecekEtkinlikler.addAll(gecmisEtkinlikler); // gelecekEtkinlikler listesine gecmisEtkinlikler listesini ekle
                    //addAll() metodu, gecmisEtkinliklerin tüm elemanlarını, GelecekEtkinlikler listesinin sonuna ekler
                    listData = gelecekEtkinlikler; // listData listesine futureEvents listesini ata

                    //EtkinliklerAdapter nesnesi oluşturulur ve RecyclerView'e bağlanır
                    //RecyclerView, listData içindeki tüm etkinliklerin listesini, EtkinliklerAdapter kullanarak gösterir.
                    //Bu sayede kullanıcı, tüm etkinlikleri görüntüleyebilir.
                    adapter = new EtkinliklerAdapter(listData);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
