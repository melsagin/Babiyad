package com.melsasagin.babiyad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BekleyenBagislar extends Fragment {
    //Fragment Activitylerin içinde ekranlarımızı daha verimli yönetmemize olanak sağlayan bir araçtır
    private RecyclerView mlistRecyclerView;
    View view;
    private DetaylarAdapter adapter;
    protected FirebaseUser user;
    protected String email;
    private ArrayList<Bagis_Veri_Stk> bekleyenbagislar;
    protected DatabaseReference mDatabase;
    public BekleyenBagislar(){}

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    protected void displaying_status(DatabaseReference reference, String email){
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childsnapshot : snapshot.getChildren()) {//tüm veriler alımır ve Bagis_Veri_Stk sınıfına dönüştürülür
                        Bagis_Veri_Stk m = childsnapshot.getValue(Bagis_Veri_Stk.class);
                        boolean complete = m.getComplete();//bağışın tamamlanıp tamamlanmadığını kontrol edilir
                        if (!complete){//bagis tamalanmamışsa bekleyenbagislara eklenir
                            bekleyenbagislar.add(m);
                        }
                    }
                    if (bekleyenbagislar.size() == 0){
                        Log.d("RUN", "sonra " +String.valueOf(bekleyenbagislar.size()));
                        Toast.makeText(getActivity(), "Bekleyen Bağış Yok!", Toast.LENGTH_SHORT).show();
                    }
                    adapter = new DetaylarAdapter(bekleyenbagislar, 1);
                    mlistRecyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CreateList(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("OrgDonations");
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = EncodeString(user.getEmail());
        displaying_status(mDatabase,email);//bu metodla bekleyen bağışlar listelenir
    }


   /*
   Fragment'in kullanıcı arayüzünü oluşturmak ve ilgili View nesnesini döndürmek için onCreateView metodu uygulanır
    */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Bekleyen bağışları göstermek için tasarlanmış olan fragment'in görünümünü oluşturur ve ilgili view bileşenlerine erişir
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.bekleyenbagislar, container, false);//Bekleyen bağışları göstermek için tasarlanmış olan fragment'in görünümünü temsil eder
        mlistRecyclerView = view.findViewById(R.id.recyoncekibagislar);
        mlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//LinearLayoutManager nesnesi, RecyclerView'in düzenleyicisini ayarlar ve dikey olarak bir liste görüntülemek için kullanılır.
        Log.d("RUN", "Bekleyen Başlat");
        bekleyenbagislar = new ArrayList<>();//kullanıcının onaylanan veya bekleyen bağışlarını depolamak için kullanılacak boş bir ArrayList oluşturulur
        CreateList();//kullanıcının bekleyen bağışlarını alır ve onları onaylananbagislar ArrayList'ine ekler
        return view;
    }
}
