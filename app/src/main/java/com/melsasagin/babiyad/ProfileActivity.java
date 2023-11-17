package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    protected DatabaseReference reference_user, reference_org;
    private String userKey;
    private String email;
    protected String name;
    private int verify_status = 0,docs_verify = 0,details_verify = 0;
    private Button upload_docs, upload_details;
    private TextView docs_view, verify_view, details_view;
    private int doc_status = 0, docs_update = 0, details_status = 0;

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    protected void displaying_status(DatabaseReference reference, String email){
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    verify_status = snapshot.child("verify").getValue(Integer.class);
                    docs_verify = snapshot.child("docs_verify").getValue(Integer.class);
                    details_verify = snapshot.child("details_verify").getValue(Integer.class);
                    doc_status = snapshot.child("docs_status").getValue(Integer.class);
                    details_status = snapshot.child("details_status").getValue(Integer.class);
                    if(doc_status == 1) {
                        docs_view.setText("Belgeler Yüklendi. Lütfen doğrulama için bekleyin!");
                    }
                    else if (doc_status == 0){
                        docs_view.setText("Onaylanmak için Belgeleri Yükleyin");
                    }
                    if(details_status == 1) {
                        details_view.setText("Ayrıntılar Dolduruldu. Lütfen doğrulama için bekleyin!");
                    }
                    else if (details_status == 0){
                        details_view.setText("Lütfen Ayrıntıları Tamamlayın");
                    }
                    if (details_verify == 1){
                        details_view.setText("Ayrıntılar doğru. Doğrulandı!");
                    }
                    else if (details_verify == 2){
                        details_view.setText("Ayrıntılar eksik. melsasagin@gmail.com adresine e-posta gönderin");
                    }
                    if (docs_verify == 1){
                        docs_view.setText("Belgeler doğru. Doğrulandı!");
                    }
                    else if (docs_verify == 2){
                        docs_view.setText("Bazı Belgeler eksik. melsasagin@gmail.com adresine e-posta gönderin");
                    }
                    if(details_verify == 1 && docs_verify == 1) {
                        verify_status = 1;
                        Stk_Details det = snapshot.getValue(Stk_Details.class);
                        det.setVerify(verify_status);
                        reference_org.child(userKey).setValue(det);//veritabanına eklendi
                        verify_view.setText("Doğrulandınız! :)");
                    }
                    else{
                        verify_view.setText("Onaylanmadınız! Lütfen bekleyin");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Hatalı bir durum var !",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_org);
        upload_details = findViewById(R.id.details_profile_btn);
        upload_docs = findViewById(R.id.docs_profile_btn);
        docs_view = findViewById(R.id.docs_verify);
        details_view = findViewById(R.id.det_verify);
        verify_view = findViewById(R.id.verify_value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference_user = FirebaseDatabase.getInstance().getReference("Users");
        reference_org = FirebaseDatabase.getInstance().getReference("Organisation");
        userKey = user.getEmail();
        userKey = EncodeString(userKey);
        reference_user.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
                email = EncodeString(email);
                name = dataSnapshot.child("fullName").getValue(String.class);
                upload_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ProfileActivity.this, DetailsActivity.class));
                    }
                });
                upload_docs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent typeIntent = new Intent(v.getContext(),UploadActivity.class);//UploadActivitye giden intent oluşturuldu
                        int type = 2;//yüklenen dosyaların tipini belirtmek için Type adlı bir değişken oluşturulur
                        typeIntent.putExtra("Type", type);
                        // Bu veri, UploadActivity activity'sinde doküman yükleme işleminin türünü belirlemek için kullanılır.
                        startActivityForResult(typeIntent, 0);//UploadActivity activity'sini başlatır ve geri dönüş sonucunu kontrol etmek için requestCode değeri olarak 0 kullanır
                        //onActivityResult() yöntemi, startActivityForResult() yöntemi ile başlatılan activity'den geri dönen sonuçları yakalamak için kullanılır
                    }
                });
                displaying_status(reference_org,email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
        //RES UploadActivity içinde docdone ve imagedone bilgisidir
        if(requestCode==0 && resultCode==RESULT_OK){//requestCode'ın 0 olması geri dönüş verilerinin UploadActivity tarafından başlatıldığını belirtir
            if(data.getIntExtra("RES",0) == 1){//Geri dönüş verisinde "RES" adlı bir ekstra değerinin varlığını kontrol eder.
                //Bu ekstra değer, başlatılan Activity tarafından geri dönüş verileri arasında taşınan bir veridir, varsayılan olarak 0 ayarlanmıştır
                docs_update = 1;
                Log.d("RUN", "belge guncellendi" );
                user = FirebaseAuth.getInstance().getCurrentUser();
                reference_org = FirebaseDatabase.getInstance().getReference("Organisation");
                userKey = user.getEmail();
                userKey = EncodeString(userKey);
                reference_org.child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Stk_Details det = dataSnapshot.getValue(Stk_Details.class);
                        det.setDocs_status(docs_update);
                        reference_org.child(userKey).setValue(det);//burada db ye docs_status değerinin güncel halini ekliyorum
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
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
