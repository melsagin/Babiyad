package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText head_org, address_org, uye_org, desc_org, phone_org, city_org;
    private CheckBox sma_org, kanser_org;
    protected Button save, upload_pic;
    private Spinner sp;//c
    protected FirebaseUser user;
    protected DatabaseReference mDataBase_org;
    private String userKey;
    private Map<String, UploadInfo> images = null;//Map sınıfı, anahtar-değer çiftlerini depolamak ve yönetmek için kullanılan bir veri yapısıdır
    private ArrayAdapter<String> loc_adaptor;
    int enAzBiriniSec = 0, details_filled=0,docs_uploaded=0, docs_status=0, image_status=0, details_status=0;


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    //Yalnızca onResume() metodunun kullanılması, aktivite her ön plana çıktığında herhangi bir kaynak yüklemesi yapmadan direkt son bilinen durumuna geçmesini sağlayacaktır.
    //Bu, uygulamanın daha hızlı açılmasını sağlar.
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_details);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Organisation");
        head_org = (EditText) findViewById(R.id.head_view);
        city_org = (EditText) findViewById(R.id.city_view);
        address_org = (EditText) findViewById(R.id.address_view);
        uye_org = (EditText) findViewById(R.id.uye_view);
        desc_org = (EditText) findViewById(R.id.desc_view);
        phone_org = (EditText)findViewById(R.id.phone_view) ;
        sma_org = (CheckBox)findViewById(R.id.sma_cb);
        kanser_org = (CheckBox)findViewById(R.id.kanser_cb);
        sp = (Spinner)findViewById(R.id.stk_spinner);
        loc_adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.stk_names));
        //android.R.layout.simple_list_item_1, androidde tanımlı basit bir liste öğesi görünümü
        //R.array.stk_names, string içinde tanımladım
        loc_adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//Spinner'a seçenekleri açılır listede göstermek için kullanılan varsayılan bir görünüm şablonu
        sp.setAdapter(loc_adaptor);
        save = (Button)findViewById(R.id.save_btn);
        upload_pic = (Button)findViewById(R.id.photo_btn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDataBase_org = FirebaseDatabase.getInstance().getReference("Organisation");
        userKey = EncodeString(user.getEmail());

        mDataBase_org.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    head_org.setText(dataSnapshot.child("head").getValue(String.class));
                    address_org.setText(dataSnapshot.child("address").getValue(String.class));
                    uye_org.setText(String.valueOf(dataSnapshot.child("uye").getValue(Integer.class)));
                    desc_org.setText(dataSnapshot.child("desc").getValue(String.class));
                    phone_org.setText(dataSnapshot.child("phone").getValue(String.class));
                    city_org.setText(dataSnapshot.child("city").getValue(String.class));
                    if (dataSnapshot.child("sma").getValue(Integer.class) == 1){
                        sma_org.setChecked(true);
                    }
                    if (dataSnapshot.child("kanser").getValue(Integer.class) == 1){
                        kanser_org.setChecked(true);
                    }
                    String stk = dataSnapshot.child("stk").getValue(String.class);//c
                    sp.setSelection(loc_adaptor.getPosition(stk));
                    image_status  = dataSnapshot.child("image_status").getValue(Integer.class);
                    docs_uploaded = dataSnapshot.child("docs_status").getValue(Integer.class);
                    details_filled = dataSnapshot.child("details_status").getValue(Integer.class);
                    docs_status = dataSnapshot.child("docs_verify").getValue(Integer.class);
                    details_status = dataSnapshot.child("details_verify").getValue(Integer.class);
                    images = (Map<String, UploadInfo>)dataSnapshot.child("images").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent typeIntent = new Intent(DetailsActivity.this,UploadActivity.class);//UploadActivitye giden intent oluşturuldu
                int type = 1;//yüklenen dosyaların tipini belirtmek için Type adlı bir değişken oluşturulur
                typeIntent.putExtra("Type", type);// Bu veri, UploadActivity activity'sinde doküman yükleme işleminin türünü belirlemek için kullanılır.
                startActivityForResult(typeIntent,0);//UploadActivity activity'sini başlatır ve geri dönüş sonucunu kontrol etmek için requestCode değeri olarak 0 kullanır
                //onActivityResult() yöntemi, startActivityForResult() yöntemi ile başlatılan activity'den geri dönen sonuçları yakalamak için kullanılır
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sma=0, kanser=0;
                String stk = sp.getSelectedItem().toString();
                String phone = phone_org.getText().toString();
                String head = head_org.getText().toString();
                String city = city_org.getText().toString();
                int uye=0;
                String address = address_org.getText().toString();
                String uye_str = uye_org.getText().toString();
                if(uye_str.length() != 0){
                    try {
                        uye = Integer.parseInt(uye_org.getText().toString());
                    }catch (NumberFormatException e){
                        Toast.makeText(DetailsActivity.this,"Stk üye sayısı nümerik bir alandır!", Toast.LENGTH_LONG).show();
                    }
                }
                String desc = desc_org.getText().toString();
                if(sma_org.isChecked()){
                    enAzBiriniSec  = 1;
                    sma = 1;
                }
                if(kanser_org.isChecked()){
                    enAzBiriniSec  = 1;
                    kanser = 1;
                }

                if(head.length()==0 || address.length()==0 || uye_str.length()==0 || desc.length()==0 || phone.length() == 0 || enAzBiriniSec ==0 || image_status==0){
                    Toast.makeText(DetailsActivity.this,"Profiliniz eksik güncellendi!", Toast.LENGTH_SHORT).show();
                    details_filled = 0;
                }
                else{
                    details_filled = 1;
                }
                //images = mDatabase.child(userKey).child("images")
                Log.d("RUN", "Images details "+String.valueOf(images));
                Stk_Details details = new Stk_Details(head,address,city,uye,details_status,docs_status, sma, kanser, phone, desc,stk, images, docs_uploaded, image_status, details_filled);
                mDatabase.child(userKey).setValue(details);//veritabanına eklendi
                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(DetailsActivity.this,"Ayrıntılar Güncellendi!", Toast.LENGTH_LONG).show();
                    }
                }, 2000);

            }

        });
    }
    /*
    onActivityResult yöntemi, startActivityForResult den sonuç almak için kullanılır
    Örneğin, kullanıcının bir fotoğraf çekip kaydetmesi veya bir dosya seçmesi gibi durumlarda seçimlerini işlemek için kullanılabilir
    requestCode: Bir activity'nin başka bir activity'yi başlatırken, başlatılan activity'den geri dönüş almak için kullanılır
    resultCode: Başlatılan aktivitenin sonucunu ifade eder. (RESULT_OK / RESULT_CANCELED)
    data: startActivityForResult() yöntemi ile başlatılan bir activity'den geri dönen verileri içeren bir Intent nesnesidir
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK){
            if(data.getIntExtra("RES",0) == 1){//Geri dönüş verisinde "RES" adlı bir ekstra değerinin varlığını kontrol eder.
                Log.d("RUN", "image_status artik 1 oldu" );
                image_status = 1;
                mDatabase.child(userKey).child("image_status").setValue(image_status);//veritabanına eklendi
            }
        }
    }
    /*
    startActivityForResult(), bir aktiviteyi başlatır ve geri dönüş sonuçlarını elde etmek için bir istek kodu sağlar
    ActivityForResult()  ise, geri dönüş sonuçlarını işlemek için kullanılan yöntemdir.
    startActivityForResult() yöntemi, bir aktiviteyi başlatır ve geri dönüş sonuçlarını elde etmek için bir istek kodu (request code) sağlar.
    Başlatılan aktivitede işlem tamamlandığında, geri dönüş sonuçları onActivityResult() yöntemi kullanılarak ana aktiviteye gönderilir.
    Bu yöntem, kullanıcı tarafından yapılan bir işlem sonucunda bir değişiklik olması gereken durumlarda kullanılır.
    Örneğin, bir kullanıcının bir resim seçmesi veya bir dosya yüklemesi gerektiğinde kullanılabilir.
    ActivityForResult() yöntemi ise, bir aktivitenin geri dönüş sonuçlarını işlemek için kullanılır.
    Bu yöntem, startActivityForResult() yöntemi ile başlatılan aktivitelerin geri dönüş sonuçlarını işlemek için kullanılan yöntemdir.
    Ana aktiviteye geri döndüğünde, geri dönüş sonuçları onActivityResult() yöntemi kullanılarak alınır.
    Bu yöntem, bir aktiviteden geri dönüş sonuçlarının alınması gereken durumlarda kullanılır.
    */
}
