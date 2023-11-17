package com.melsasagin.babiyad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SSS extends AppCompatActivity {

    ExpandableListView expandableListView;//ana bir liste öğesi seçildiğinde alt liste öğelerinin görüntülendiği genişleyebilen bir liste sunar
    List<String> listGroup;//Grupların başlıklarını tutan liste yani soruların bulunduğu kısım
    HashMap<String,List<String>> listItem;//Her bir grup altında yer alan alt elemanların listesini tutan bir Hashmap.
    //HashMap veri yapısı, anahtar(başlık/soru)-değer(alt başlık/cevap) çiftleri kullanarak verileri depolar
    SssAdapter adapter;// Bu sınıf, genişleyebilen liste için özel bir görünüm sağlar.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sss);

        expandableListView = findViewById(R.id.expandableView);
        listGroup = new ArrayList<>();
        listItem = new HashMap<>();

        adapter = new SssAdapter(this,listGroup,listItem);
        expandableListView.setAdapter(adapter);

        initListData();

    }

    private void initListData() {
        //Bu metod, listGroup ve listItem nesnelerine veriler ekler ve adapter.notifyDataSetChanged() metoduyla adapte edilir.

        listGroup.add(getString(R.string.Q1));
        listGroup.add(getString(R.string.Q2));
        listGroup.add(getString(R.string.Q3));
        listGroup.add(getString(R.string.Q4));
        listGroup.add(getString(R.string.Q5));
        listGroup.add(getString(R.string.Q6));
        listGroup.add(getString(R.string.Q7));
        listGroup.add(getString(R.string.Q8));
        listGroup.add(getString(R.string.Q9));
        listGroup.add(getString(R.string.Q10));
        listGroup.add(getString(R.string.Q11));

        String[] array;

        List<String> list1 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q1);//Alt öğeler getResources().getStringArray() kullanılarak strings.xml dosyasından alınır.
        for(String item:array)
        {//array dizisindeki elemanlar sırayla item değişkenine atanır ve bu elemanları list1 ArrayList'e ekler
            //for dögüsü her alt öğeyi ilgili listeye ekler
            list1.add(item);
        }

        List<String> list2 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q2);
        for(String item:array)
        {
            list2.add(item);
        }

        List<String> list3 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q3);
        for(String item:array)
        {
            list3.add(item);
        }

        List<String> list4 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q4);
        for(String item:array)
        {
            list4.add(item);
        }

        List<String> list5 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q5);
        for(String item:array)
        {
            list5.add(item);
        }

        List<String> list6 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q6);
        for(String item:array)
        {
            list6.add(item);
        }

        List<String> list7 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q7);
        for(String item:array)
        {
            list7.add(item);
        }

        List<String> list8 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q8);
        for(String item:array)
        {
            list8.add(item);
        }

        List<String> list9 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q9);
        for(String item:array)
        {
            list9.add(item);
        }

        List<String> list10 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q10);
        for(String item:array)
        {
            list10.add(item);
        }

        List<String> list11 = new ArrayList<>();
        array = getResources().getStringArray(R.array.Q11);
        for(String item:array)
        {
            list11.add(item);
        }


        listItem.put(listGroup.get(0),list1);//listItem güncellenir
        listItem.put(listGroup.get(1),list2);//listGroup.get(1) ifadesi, listGroup nesnesinin ikinci grup başlığına karşılık gelen bir String döndürür.
        // Bu String değeri, list2 listesini listItem HashMap'ine eklemek için kullanılır
        listItem.put(listGroup.get(2),list3);
        listItem.put(listGroup.get(3),list4);
        listItem.put(listGroup.get(4),list5);
        listItem.put(listGroup.get(5),list6);
        listItem.put(listGroup.get(6),list7);
        listItem.put(listGroup.get(7),list8);
        listItem.put(listGroup.get(8),list9);
        listItem.put(listGroup.get(9),list10);
        listItem.put(listGroup.get(10),list11);

        adapter.notifyDataSetChanged();//adapter.notifyDataSetChanged() kullanılarak değişiklikler adaptöre bildirilir.

    }
}