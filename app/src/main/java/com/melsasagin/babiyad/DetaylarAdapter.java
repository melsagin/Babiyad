package com.melsasagin.babiyad;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DetaylarAdapter extends RecyclerView.Adapter<DetaylarAdapter.Detailsviewholder>{

    private ArrayList<Bagis_Veri_Stk> donationholder;
    protected int flag;

    public DetaylarAdapter(ArrayList<Bagis_Veri_Stk> donationholder, int flag){
        this.donationholder = donationholder;
        this.flag = flag;
    }

    @NonNull
    @Override
    public Detailsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//her bir öğe için görünümün oluşturulmasından sorumludur
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rlbagislar, parent, false);
        /*
        Bu satır, RecyclerView'da her bir öğe için görünümün (view) oluşturulmasında kullanılır.
        Bu, LayoutInflater sınıfının from() yöntemi kullanılarak yapılır.
        İlk olarak, öğenin üst öğesine (parent) erişmek için getContext() yöntemi çağrılır.
        Ardından, inflate edilecek görünümün düzen dosyası belirtilir (R.layout.rlbagislar).
        Son olarak, oluşturulan görünümü RecyclerView'daki diğer öğelerden ayırmak için false olarak belirtilen üçüncü bir parametre ile birlikte inflate edilir.
         */
        Detailsviewholder detailsviewholder = new Detailsviewholder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bagis_Veri_Stk stk = donationholder.get(detailsviewholder.getAdapterPosition());//bu eski bir metod old. için uyarı veriyor
                Bagis_Veri_Stk stk = donationholder.get(detailsviewholder.getBindingAdapterPosition());//tıklanan görünümün pozisyonu stk objesine atanır
                String key = stk.getKey();
                Intent intent = new Intent(view.getContext(), BagisAyrintilariStk.class);//rating işlemi BagisAyrintilariStk içinde olacak.
                intent.putExtra("Key", key);//BagisAyrintilariStk'ye key bilgisi intent aracılığıyla gönderilir
                view.getContext().startActivity(intent);
            }
        });
        return detailsviewholder;
    }


    @Override
    public void onBindViewHolder(@NonNull Detailsviewholder holder, int position) {//verileri görünüm öğelerine bağlamaktan sorumlu metdod

        holder.name.setText(donationholder.get(position).getName());//name bilgisi ekranda göterilir
        holder.email.setText((donationholder.get(position).getEmail()).replace(",", "."));
        if (flag == 1){//BekleyenBagislar'da adapter içinde flag 1 olarak gönderilmiştir
            holder.date.setText("Bağışlama Tarihi ");//başlık bu şekilde yazar
            holder.date_val.setText(donationholder.get(position).getDate_donated());
        }
        else{//OnaylananBagislar'da ise adapter içinde flag 0 olarak gönderilmiştir
            holder.date.setText("Onay Tarihi ");
            holder.date_val.setText(donationholder.get(position).getDate_received());
        }
        Bagis_Veri_Stk item = donationholder.get(position);
        holder.bind(item);//burada tutucu item verisine bağlanır ve öğenin görünümü güncellenir (bind metodu DetailsViewholder içinde)
    }

    @Override
    public int getItemCount() {
        return donationholder.size();
    }

    public class Detailsviewholder extends RecyclerView.ViewHolder{

        protected Bagis_Veri_Stk mitem;
        TextView name, email, date, date_val;//rlbagislar.xml içindeki verileri alacak
        public Detailsviewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            date = itemView.findViewById(R.id.date);
            date_val = itemView.findViewById(R.id.date_val);
        }

        public void bind(Bagis_Veri_Stk item){
            //ViewHolder verileri görünüm bileşenlerine yerleştirirken, bind metodu veri öğesini ViewHolder'a bağlamak için kullanılır.
            //Bu metot, ViewHolder'ın bir öğeyi temsil etmesini sağlar ve öğeye ait verilerin görünüme atanmasını sağlar
            mitem = item;
        }
    }
}
