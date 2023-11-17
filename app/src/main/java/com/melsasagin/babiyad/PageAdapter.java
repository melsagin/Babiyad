package com.melsasagin.babiyad;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    /*
    Bu kod, bir Android uygulamasında bir ViewPager'a bağlanacak bir FragmentPagerAdapter sınıfı tanımlamaktadır.
    PageAdapter, kullanıcının sayfalar arasında kaydırarak içerikleri görüntüleyebileceği bir görünüm olan ViewPager'ı yönetir.
    Kod, önceki/prev(onaylanan) ve mevcut/curr(bekleyen) bağışlar için iki farklı Fragment döndürür.
    getCount() metodu, ViewPager'ın içindeki sayfa sayısını döndürür.
    getItem() metodu belirli bir konum için ilgili Fragment'ı döndürür.
     */

    int tab_count;

    public PageAdapter(@NonNull FragmentManager fm, int count) {
        super(fm, count);
        tab_count = count;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            Log.d("RUN","PA onaylanan");
            return new OnaylananBagislar();
        }
        else if(position == 1){
            Log.d("RUN", "PA bekleyen");
            return  new BekleyenBagislar();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tab_count;
    }
}

