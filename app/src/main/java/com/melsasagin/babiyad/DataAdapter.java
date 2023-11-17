package com.melsasagin.babiyad;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<Data> listdata;
    private String name;
    private String email;


    public DataAdapter(List<Data> listdata) {
        this.listdata = listdata;
    }

    //onCreateViewHolder yönteminin içinde layout inflate edilir (bağlanır)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Data d = listdata.get(viewHolder.getAdapterPosition());//bu eski bir metod old. için uyarı veriyor
                Data d = listdata.get(viewHolder.getBindingAdapterPosition());//nesnenin bağlı old. veri öğesinin güncel konumu alınır
                name = d.getFullName();
                email = d.getEmail();
                //Intent objesi oluşturulur ve view.getContext() ile mevcut görünümün bağlamını alır.
                //Ardından, BagisDetaylariniGoster sınıfının hedef aktivite olarak belirlendiği yeni bir intent oluşturulur.
                Intent intent = new Intent(view.getContext(), BagisDetaylariniGoster.class);
                //Bu ekstra veriler, intent aracılığıyla hedef aktiviteye iletilir ve hedef aktivite bu verilere erişebilir.
                //Örneğin, BagisDetaylariniGoster sınıfında bu ekstra verilere erişmek için getIntent().getStringExtra("STK Name") veya getIntent().getStringExtra("Email id") gibi yöntemler kullanılabilir.
                //Böylece hedef aktivite, bu ekstra verileri kullanarak ilgili bilgileri görüntüleyebilir veya işleyebilir
                intent.putExtra("STK Name",name);
                intent.putExtra("Email id",email);
                view.getContext().startActivity(intent);
            }
        });
        //return new ViewHolder(view);bunu çok fazla veri varsa performans amaçlı kullanabilirim
        //yukarıdaki ifade her bir öğe için ayrı bir görünüm tutucusu oluşturulmasını sağlar ve her bir tutucunun kendi olay dinleyicilerini ve veri bağlamasını yönetmesine olanak tanır
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //veri listesinden belirli bir konumdaki veriyi alır ve bu veriyi ViewHolder'a bağlar
        //listedeki bir öğenin tam adını (fullName) TextView içinde görüntülemek için kullanılır
        Data ld = listdata.get(position);
        holder.fullname.setText(ld.getFullName());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        //ViewHolder, öğelerin görünümünü bulunduran bileşenlere erişmek için kullanılır
        //RecyclerView'da görüntülenen öğelerin görünüm hiyerarşisine erişim sağlar
        private TextView fullname;
        public ViewHolder(View itemView) {
            super(itemView);
            fullname = (TextView)itemView.findViewById(R.id.fullname);
        }
    }
}


