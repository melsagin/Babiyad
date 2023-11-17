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


public class OnaylananBagislar extends Fragment {
    //Fragment Activitylerin içinde ekranlarımızı daha verimli yönetmemize olanak sağlayan bir araçtır
    private RecyclerView mlistRecyclerView;
    View view;
    private DetaylarAdapter adapter;
    private FirebaseUser user;
    private String email;
    private ArrayList<Bagis_Veri_Stk> onaylananbagislar;
    private DatabaseReference mDatabase;
    public OnaylananBagislar(){}

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    protected void displaying_status(DatabaseReference reference, String email){
        //Firebase veritabanında kullanıcının geçmiş bağışlarını arar.
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("RUN", "onaylanacaktan öncesi " +String.valueOf(onaylananbagislar.size()));
                if (snapshot.exists()) {//veritabanında bir veri olup olmadığını kontrol eder
                    for (DataSnapshot childsnapshot : snapshot.getChildren()) {//veritabanında her veri gezilir
                    // "snapshot.getChildren()" ifadesinden elde edilen her bir "DataSnapshot" öğesi, "childsnapshot" değişkenine atanır
                        Bagis_Veri_Stk m = childsnapshot.getValue(Bagis_Veri_Stk.class);
                        boolean complete = m.getComplete();//bağışın tamamlanıp tamamlanmadığını kontrol edilir
                        if (complete){//bagis tamalanmışsa onaylananbagislara eklenir
                            onaylananbagislar.add(m);
                        }
                    }
                    if (onaylananbagislar.size() == 0){
                        Toast.makeText(getActivity(), "Onaylanacak Bağış Yok!", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("RUN", "onaylanacaktan sonrası " +String.valueOf(onaylananbagislar.size()));
                    adapter = new DetaylarAdapter(onaylananbagislar, 0);
                    mlistRecyclerView.setAdapter(adapter);//recyclerView'ün bu adapter ile doldurulması sağlanır
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CreateList(){//veritabanına OrgDonations düğümünü atama işlemi
        mDatabase = FirebaseDatabase.getInstance().getReference().child("OrgDonations");
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = EncodeString(user.getEmail());
        displaying_status(mDatabase,email);//bu metodla geçmiş bağışlar listelenir
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {//görünüm ilk çağrıldığında olacaklar
       //Bekleyen bağışları göstermek için tasarlanmış olan fragment'in görünümünü oluşturur ve ilgili view bileşenlerine erişir
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.onaylananbagislar, container, false);//Bekleyen bağışları göstermek için tasarlanmış olan fragment'in görünümünü temsil eder
        mlistRecyclerView = view.findViewById(R.id.recymevcutbagislar);
        mlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//LinearLayoutManager nesnesi, RecyclerView'in düzenleyicisini ayarlar ve dikey olarak bir liste görüntülemek için kullanılır.
        Log.d("RUN", "Onaylanan Başlat");
        onaylananbagislar = new ArrayList<>();//kullanıcının onaylanan veya bekleyen bağışlarını depolamak için kullanılacak boş bir ArrayList oluşturulur
        CreateList();//kullanıcının bekleyen bağışlarını alır ve onları onaylananbagislar ArrayList'ine ekler
        return view;
    }
}
