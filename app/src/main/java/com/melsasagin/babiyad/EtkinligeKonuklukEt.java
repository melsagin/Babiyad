package com.melsasagin.babiyad;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EtkinligeKonuklukEt extends AppCompatActivity {

    //AdapterView dinleyicisinin implement edilme sebebi burada spinner üzerindeki değişikliklerin izlenmek istenmesidir
    private String sonBagisTuru;
    protected EditText tarihSec, zamanSec, aciklamaSec, sehirSec;
    private TimePickerDialog timePickerDialog;
    private Calendar calendarTime;
    private int mevcutSaat, mevcutDakika;
    private String amPm;
    protected String sonSehir, sonTarih, sonZaman, sonAciklama;
    protected String mesaj;
    String userencodedmail;
    protected FirebaseAuth mAuth;
    private DatabaseReference reference_event;
    FirebaseUser user;

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinligekonukluket);

        Spinner typeSpinner = findViewById(R.id.donationTypes);

        ArrayAdapter<String> type_adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.bagisTuru));
        // android.R.layout.simple_spinner_item, Spinner'ın her öğesi için kullanılacak görünüm düzenini belirtir. Platform tarafından tanımlanan standart düzendir

        type_adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//bağış seçenekleri listesini görüntülemek için yine androidin standart düzeni kullanılır
        typeSpinner.setAdapter(type_adaptor);

        tarihSec = findViewById(R.id.donationDateEdit);
        zamanSec = findViewById(R.id.donationTimeEdit);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String userkey = user.getEmail();
        userencodedmail = EncodeString(userkey);

        mAuth = FirebaseAuth.getInstance();

        reference_event = FirebaseDatabase.getInstance().getReference("Events");

        tarihSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EtkinligeKonuklukEt.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int secilen_yil, int secilen_ay, int secilen_gun) {
                        secilen_ay = secilen_ay + 1;//index olayından dolayı 0 dan başladığı için 1 arttırılması gerekiyor
                        sonTarih = secilen_gun + "." + secilen_ay + "." + secilen_yil;
                        tarihSec.setText(sonTarih);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        zamanSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarTime = Calendar.getInstance();
                mevcutSaat = calendarTime.get(Calendar.HOUR_OF_DAY);
                mevcutDakika = calendarTime.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(EtkinligeKonuklukEt.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int secilen_saat, int secilen_dakika) {
                        /*if(secilen_saat >= 12)
                            amPm = "ÖS";
                        else
                            amPm = "ÖÖ";
                        sonZaman = String.format("%02d:%02d",secilen_saat,secilen_dakika)+" " + amPm;*/
                        sonZaman = String.format("%02d:%02d",secilen_saat,secilen_dakika);
                        zamanSec.setText(sonZaman);
                    }
                },mevcutSaat,mevcutDakika,true);
                timePickerDialog.show();
            }
        });



        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //parent, seçili öğeyi içeren AdapterView öğesini temsil eder
                //view, seçilen öğenin görünümüdür
                //position, seçilen öğenin konumunu temsil eder
                String text = parent.getItemAtPosition(position).toString();
                //Yukarıdaki yöntem, seçilen öğenin metnini (text), parent'ın getItemAtPosition() metodu kullanılarak alır
                if(text.equals("Sma") || text.equals("Kanser") || text.equals("Çoklu"))
                {
                    sonBagisTuru = text;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void hostEvent(View view) {
        aciklamaSec = findViewById(R.id.donationDescriptionEdit);
        sehirSec = findViewById(R.id.donationCityEdit);

        sonSehir = sehirSec.getText().toString();
        sonAciklama = aciklamaSec.getText().toString();

        mesaj = "Bağış Türü : " +sonBagisTuru+ "\n\nŞehir : " +sonSehir+ "\n\nTarih : " +sonTarih+ "\n\nZaman : " +sonZaman+ "\n\nAçıklama : " +sonAciklama;

        Etkinlikler etkinlikler1 = new Etkinlikler();
        etkinlikler1.setEvent_Details(mesaj);
        etkinlikler1.setDate(sonTarih);
        etkinlikler1.setTime(sonZaman);
        etkinlikler1.setCity(sonSehir);
        etkinlikler1.setType(sonBagisTuru);
        reference_event.child(userencodedmail).setValue(etkinlikler1);
        Toast.makeText(this, "Etkinlik Oluşturuldu", Toast.LENGTH_SHORT).show();
    }
}
