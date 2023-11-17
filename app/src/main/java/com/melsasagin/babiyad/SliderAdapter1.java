package com.melsasagin.babiyad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter1 extends SliderViewAdapter<SliderAdapter1.SliderAdapterViewHolder> {

    //IMAGE SLIDER ISLEMI

    // resimlerin URL'lerini depolamak için liste
    private final List<SliderData> mSliderItems;

    // Constructor
    public SliderAdapter1(Context context, ArrayList<SliderData> sliderDataArrayList) {
        this.mSliderItems = sliderDataArrayList;
    }

    // onCreateViewHolder yönteminin içinde Slider_layout inflate edilir (bağlanır)
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        //null, oluşturulan View'ın hedef ViewGrup'a doğrudan bağlanmadığı, bağlama işleminin sonradan gerçekleştirilebileceği anlamına gelir.
        return new SliderAdapterViewHolder(inflate);
    }

    // onBindViewHolder içindeki görüntüleri SliderView öğesine yerleştirmek için kullanılır
    //layout içerisinde hangi verileri göstermek istediğimiz yazılır
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final SliderData sliderItem = mSliderItems.get(position);//herhangi bir yerde değişiklik olmasın diye final ön eki kullandım

        //Glide, imageView da url'den resim yüklemek için kullanılan bir kütüphane
        //Glide.with(context).load(uri).into(holder.imV);
        //load: Glide tarafından yüklenecek resmin URL'sini belirler
        //fitCenter: resmin görüntüleme alanına sığdırılmasını sağlar
        //into: yüklenen resmi imageViewBackground adlı ImageView öğesine yerleştirir.
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImgUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {//layout u tutacak sınıf
        /*
        SliderAdapterViewHolder sınıfının itemView değişkenine,
        üst sınıf olan SliderViewAdapter.ViewHolder'dan gelen referansı atamak
        ve alt sınıfın bu referans üzerinde özel işlemler gerçekleştirebilmesini sağlamak için itemView kullanılır
        Alt sınıfın yapıcısı, üst sınıfın yapıcısını geçersiz kılmalıdır.
        Yani, yapıcı metodun parametresi aynı olmalıdır, aksi takdirde miras alınan üst sınıfın yapıcısı yerine geçerli olmaz
         */
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.myimage);
            this.itemView = itemView;
        }
    }
}

/*RelativeLayout kullanıcının göreceği kadar XML oluşturup scroll edildiğinde tekrar onu kullanıp gösterir. Bu açıdan daha verimlidir.
        Adapter java classımızı (bkz:SliderAdapter1), Layout (bkz:slider_layout.xml) ile kodları birbirine bağlayıp kullanıcıya gösterebilmek için yazarız.
        Oluşturduğumuz SliderAdapter classımıza SliderViewAdapter classındaki özellikleri alabilmek için extend ederek; SliderAdapter classımıza SliderViewAdapter'dan inheritance (kalıtım) alırız.
        SliderViewAdapter sınıfından kalıtım alırken SliderViewAdapter<>, bir tane "<VH>" -> ViewHolder / Görünüm Tutucu sınıfı oluşturmamızı ister.
        Bu nedenle, Adapter classımız içerisinde bir tane de "görünümlerimizi tutması açısından" yardımcı bir Holder (bkz:SliderAdapterViewHolder) sınıfı oluştururuz.
        Aynı şekilde Holder sınıfımıza bu sefer ViewHolder'dan inheritance (kalıtım) alırız.
        (Hatamız devam eder) Alt + Enter diyerek Constructorımızı oluştururuz.
        Holder classımızı oluşturduktan sonra, artık Adapter sınıfımızın miras aldığı SliderViewAdapter<> içerisine Holder sınıfımızı (bkz: SliderAdapterViewHolder) yazabiliriz.
        Devam eden hatayı gidermek için tekrar Alt+Enter diyerek uygulamamız gereken 3 methodu oluşturuyoruz.
        1. onCreateViewHolder() -> XML'imizi (bkz:slider_layout.xml) bağlama işlemini yapacağımız methoddur.
        2. onBindViewHolder() -> Görünümleri tutması için oluşturduğumuz ViewHolder sınıfımız bağlandığında; XML / Layoutumuz içerisinde hangi verileri göstermek istediğimizi söyleyen methoddur.
        3. getItemCount() -> XML'imizin kaç defa oluşturulacağını söylediğimiz methoddur.
        Sonrasında methodları kullanıp sırasıyla; bağlama işlemini, gösterilecek verileri ve kaç defa gösterileceği gibi işlemleri yapabilmek için bir adet XML / Layout (bkz:slider_layout.xml) oluşturup tekrar etmesini istediğimiz layout tasarımımızı yapıyoruz.
        3. methodda (bkz: getItemCount()) XML'imizin kaç defa gösterileceğini söyleyebilmemiz için SliderAdapter classımızdan sliderDataArrayList'e ulaşıp sizeını alırız.
 */