package com.melsasagin.babiyad;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EtkinliklerAdapter extends Adapter<EtkinliklerAdapter.ViewHolder> {

    private final List<Etkinlikler> listdata;//RecyclerView'da görüntülenecek olayların listesi
    private String date;
    private String time, city, type, fulldetail;

    public EtkinliklerAdapter(List<Etkinlikler> listdata) {
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //onCreateViewHolder, RecyclerView'daki her bir öğe için bir ViewHolder oluşturur.
        //LayoutInflater, bir XML dosyasını View sınıfına dönüştürmek için kullanılır.
        //Bu yöntem, onCreateViewHolder'a geçirilen View nesnesi olarak döndürülür.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_etkinlikler,parent,false);//list_events görünümünde olacak rw öğeleri
        //Eğer attachToRoot değeri true olarak ayarlanırsa, inflate edilen düzen dosyası doğrudan belirtilen kök düğümünün alt öğesi olarak eklenir ve geri döndürülür.
        //Bu durumda, inflate() işlevi, şişirilen düzen dosyasının kök düğümünü döndürür.
        //Eğer attachToRoot değeri false olarak ayarlanırsa, inflate edilen düzen dosyası belirtilen kök düğümüne bağlanmaz.
        //Bunun yerine, inflate edilen düzen dosyasının kök düğümü ile bir View nesnesi oluşturulur ve döndürülür.
        //Bu durumda, inflate() işlevi, inflate edilen düzen dosyasının kök düğümünü içeren View nesnesini döndürür.
        ViewHolder viewHolder = new ViewHolder(view);//holder view'a bağlanır rw'nin her bir rw öğesi için bir görünüm sağlanır
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Etkinlikler d = listdata.get(viewHolder.getBindingAdapterPosition());//tıklanan öğenin konumunu/position alır
                //Etkinlikler d = listdata.get(viewHolder.getAdapterPosition());//getAdapterPosition() kullanım dışı
                date = d.getDate();
                time = d.getTime();
                type = d.getType();
                city = d.getCity();
                fulldetail = d.getEvent_Details();

                long baslangicZamani = 0;
                //try-catch bloğu, date değişkenindeki tarihi "dd.MM.yyyy" biçiminde ayrıştırır ve baslangicZamani değişkenine milisaniye cinsinden atar.
                try {
                    Date startdate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(date);
                    //Date startdate = new SimpleDateFormat("dd.MM.yyyy").parse(date);//buna uyarı veriyordu o yüzden yukarıdakini kullandım
                    baslangicZamani = startdate.getTime();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                Calendar calendarEvent = Calendar.getInstance();
                //intent iki farklı bileşen arasında iletişimi sağlamak amacıyla kullanılır
                //Intent.ACTION_EDIT kullanılarak Android takvim uygulaması açılır ve önceden oluşturulan ayrıntılar, takvim etkinliğine yerleştirilir.
                Intent i = new Intent(Intent.ACTION_EDIT);
                i.setType("vnd.android.cursor.item/event");//Intent'in etkinlik tipi olduğunu belirtmek amacıyla yazılır
                //putExta ile intente yeni veriler eklenir
                i.putExtra("beginTime",baslangicZamani);
                i.putExtra("allDay", true);//bütün gün takvimde olacak
                //i.putExtra("rule", "FREQ=ONCE");//sadece bir sefer olacak yani tekrarlayan bir sıklığı olmayacak, kullanılmak zorunda değil
                i.putExtra("description",fulldetail);
                //i.putExtra("hasalarm","true");//alarm olsun mu
                i.putExtra("location",city);
                i.putExtra("title", type + " Bağışı için Etkinlik");

                view.getContext().startActivity(i);//görünüm başlatıldı
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //onBindViewHolder, belirli bir pozisyondaki öğe için görünümün içeriğini değiştirir.
        //Pozisyona göre öğenin içeriğini ViewHolder içindeki görünüme yükler.
        Etkinlikler ld = listdata.get(position);
        //ld değişkeni, verilerin depolandığı veri kaynağına ait bir öğeyi temsil eder
        //getEvent_Details() yöntemi, o öğenin ayrıntılarını alır ve TextView'e yazdırır
        holder.txtdet.setText(ld.getEvent_Details());
    }

    @Override
    public int getItemCount() {
        //listedeki öğe sayısı döndürülür
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //ViewHolder, RecyclerView tarafından yönetilen öğelerin her biri için önceden hazırlanmış bir şablon olarak kullanılır.
        //Bu ViewHolder sınıfı, her bir öğenin detaylarının tutulduğu görünüme sahiptir
        private TextView txtdet;
        public ViewHolder(View itemView) {
            super(itemView);
            txtdet = (TextView) itemView.findViewById(R.id.etkinlikdet);
        }
    }
}