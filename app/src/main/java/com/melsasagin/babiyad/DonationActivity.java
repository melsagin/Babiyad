package com.melsasagin.babiyad;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class DonationActivity extends AppCompatActivity {
    TabLayout tabLayout;
    TabItem prev_item, curr_item;
    ViewPager vpg;
    PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstancestate) {
        Log.d("RUN", "DonationActivity Create");
        super.onCreate(savedInstancestate);
        setContentView(R.layout.donation_activity);
        tabLayout = (TabLayout)findViewById(R.id.tablayout1);
        prev_item = (TabItem)findViewById(R.id.item1);
        curr_item = (TabItem)findViewById(R.id.item2);
        vpg = (ViewPager)findViewById(R.id.fragmentcontainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        /*
        PageAdaptor sınıfını kullanarak Fragment'leri yönetmek üzere bir sayfa adaptörü oluşturulur.
        Bu sayfa adaptörü, getSupportFragmentManager() yöntemini kullanarak Fragment'lere bağlanır.
        Tab sayısını ve tipini değişkenlerden alır.
         */
        vpg.setAdapter(pageAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {//bir sekme seçildiğinde ne olacak
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpg.setCurrentItem(tab.getPosition());//Seçilen sekme numarasına göre Viewpager'da görüntülenecek sayfa ayarlanır

                if(tab.getPosition() == 0 || tab.getPosition() == 1){
                    pageAdapter.notifyDataSetChanged();//İlgili sayfa adaptörünün verileri değiştirildiğinde Viewpager'da güncellenmesi gerektiğini bildirir
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {//bunlar otomatik gelen metodlar ama ilgilenmiyorum

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpg.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //Viewpager'a bir sayfa değişikliği dinleyicisi ekler ve TabLayout'la birlikte çalışması için TabLayout.TabLayoutOnPageChangeListener yöntemini kullanır.
        //Bu, TabLayout'un seçili sekmesine göre görüntülenecek sayfayı değiştirir
    }
}
