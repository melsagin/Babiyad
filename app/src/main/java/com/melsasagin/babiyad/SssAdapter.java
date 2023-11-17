package com.melsasagin.babiyad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class SssAdapter extends BaseExpandableListAdapter {
    //Bu kodlar bir ExpandableListView'ın temel veri kaynağı olan bir BaseExpandableListAdapter sınıfını tanımlamaktadır.
    //Bu sınıf, verileri gruplar halinde ve bunların altında yer alan alt elemanlarla birlikte görüntülemek için kullanılır.
    //SSS sayfası için yazılan adapter sınıfı

    Context context;
    List<String> listGroup;//Grupların başlıklarını tutan liste yani soruların bulunduğu kısım
    HashMap<String,List<String>> listItem;//Her bir grup altında yer alan alt elemanların listesini tutan bir Hashmap.

    public SssAdapter(Context context, List<String> listGroup, HashMap<String, List<String>> listItem)
    {
        this.context = context;
        this.listGroup = listGroup;
        this.listItem = listItem;
    }

    //Aşağıdaki metodlar BaseExpandableListAdapter metodlarıdır.
    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //Belirtilen grup altındaki alt elemanların sayısını döndürür.
        return this.listItem.get(this.listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //Verilen groupPosition indisindeki ana öğeyi döndürür.
        return this.listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //Verilen groupPosition indisindeki ana öğenin childPosition indisindeki alt öğesini döndürür.
        return this.listItem.get(this.listGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        //Belirtilen grup için benzersiz bir ID döndürür.
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //Belirtilen alt eleman için benzersiz bir ID döndürür.
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        //ID'lerin sabit kalıp kalmayacağını belirler.
        //Eğer bu metot true değerini döndürürse, ExpandableListView tarafından önbelleğe alınabilir
        //böylece kullanıcı etkileşimlerinin öğelerin kimliği üzerinde herhangi bir etkisi olmaz.
        //Bu metot, özellikle veri seti sık sık güncelleniyorsa, bir öğenin konumunda veya diğer özelliklerinde
        //değişiklikler yapılması durumunda bu özellik sayesinde listview'ın verilerin doğru yerde görüntülenmesi sağlamak amacıyla kullanılır
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Bu yöntem, verilen grup pozisyonundaki grup öğesi için bir görünüm döndürür
        //convertView parametresi, aynı anda görüntülenen öğelerin sayısını azaltmak için görünüm önbelleğini temsil eder.
        //isExpanded parametresi, ana öğe görünümünün genişletilmiş olup olmadığını belirtir
        String group = (String) getGroup(groupPosition);//getGroup() metoduyla belirtilen pozisyondaki grup öğesinin alınır ve metin dizesine dönüştürülür
        if(convertView == null)//convertView, yeniden kullanılacak görünümü belirtir (eğer varsa)
        {//daha önce bu pozisyonda oluşturulmuş bir görünüm yoksa list_group inflate edilir
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group,null);
        }
        TextView textView = convertView.findViewById(R.id.list_parent);
        textView.setText(group);//view içindeki TextView bileşenine grup öğesi atanır
        return convertView;
        //convertView değişkeninin null olup olmadığını kontrol eder.
        //Eğer null ise, LayoutInflater kullanarak görünüm bileşenlerini oluşturur.
        //TextView nesnesi, belirtilen grup veya alt eleman öğesinin içeriğini görüntülemek için kullanılır ve convertView nesnesi geri döndürülür
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Alt eleman görünümünü oluşturur ve döndürür.
        String child = (String) getChild(groupPosition,childPosition);
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item,null);
        }

        TextView textView = convertView.findViewById(R.id.list_child);
        textView.setText(child);
        return convertView;
        //convertView değişkeninin null olup olmadığını kontrol eder.
        // Eğer null ise, LayoutInflater kullanarak görünüm bileşenlerini oluşturur.
        //TextView nesnesi, belirtilen grup veya alt eleman öğesinin adını görüntülemek için kullanılır ve convertView nesnesi geri döndürülür
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        //Alt elemanların seçilebilir olup olmayacağını belirler.
        return true;
    }
}