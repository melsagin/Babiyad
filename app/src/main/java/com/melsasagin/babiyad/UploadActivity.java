package com.melsasagin.babiyad;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {
    protected Button browse, upload;
    private ImageView imageView;
    Uri filepath;//seçilen resmin dosya yolunu saklar
    Bitmap bmap;//görüntünün piksel verilerini tutar ve işlemek, görüntülemek veya depolamak için kullanılır
    DatabaseReference mDatabase;
    protected FirebaseUser user;
    protected DatabaseReference mDataBase_user;
    FirebaseStorage firebaseStorage;//storage için oluşturuldu
    StorageReference databaseReference;//mevcut kullanıcının yükleme işlemlerinde kullanılacak depolama konumunu temsil eder
    private String email, fileName="";
    Intent resultintent, typeIntent;
    private int imageCount=0, filetype=0, docsCount=0, imgdone=0, docdone=0, flag=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        browse = findViewById(R.id.browse_btn);
        upload = findViewById(R.id.upload_btn);
        imageView =  findViewById(R.id.image_view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Organisation");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDataBase_user = FirebaseDatabase.getInstance().getReference();
        email = EncodeString(user.getEmail());
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseStorage.getReference().child(email);
        resultintent = new Intent();
        typeIntent = getIntent(); //önceki aktiviteden (ProfileActivity) gelen Intent nesnesi alınır
        if(typeIntent == null){//önceki aktiviteden gelen bir Intent nesnesi yoksa hata ayıklama amaçlı bir log kaydı oluşturulur
            Log.d("NULL","Intent alinamadi");
        }
        else{
            Log.d("NULL","Intent alindi");
        }
        filetype = typeIntent.getIntExtra("Type",0);//Type adındaki ekstra verinin değerini alarak filetype değişkenine atar (Eğer Type adında bir ekstra veri yoksa, 0 değeri döndürülür)
        Log.d("RUN", "FILETYPE " + filetype);
        /*
        19 ve öncesi sürümleri kullananlardan izin alınması gerekmediği için hangi sürümü kullandığını bilmek için yazılıyor.
        Dexter.withActivity(UploadActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ile read_external_storage izni var mı onu kontrol ediyouz
        izin varsa galeriye gidilecek izin yoksa istenecek veya yoksa zorunlu olarak kendisine açıklama yapılarak istenir.
        */
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dexter kütüphanesi kullanılarak UploadActivity için harici depolama izni istenir
                Dexter.withActivity(UploadActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)//izin isteği yapılacak iznin belirtildiği yer parametredir
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {//izin verilmişse
                                Intent intent = new Intent(Intent.ACTION_PICK);//galeriye gidilecek ACTION_PICK -görseli almaya yarayan bir aksiyondur- ile bir görsel alınacak.
                                intent.setType("image/*");//Intent nesnesinin tipi belirlenir. Burada, sadece resim dosyalarının seçilmesi sağlanır.
                                startActivityForResult(intent.createChooser(intent,"Bir uygulama seçin"),1);//fotoğraf tutan uygulamlar arasında seçimi sağlar
                                //startActivityForResult() metodu kullanılarak galeriye gidilir ve bir görsel seçilir.
                                // createChooser() metodu, birden fazla uygulama (downloads,camera) varsa hangi uygulamayı seçeceğine dair bir seçim penceresi sağlar.
                                // Seçim yapılırken gösterilecek metin "Bir resim seçin" olarak belirlenir.
                                // 1 sayısı, sonuç kodu olarak belirlenir.
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {}

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                //Kullanıcıya harici depolama okuma izni istenirken, neden iznin gerekli olduğunu açıklayan bir açıklama gösterir
                                //kullanıcının izin vermesini sağlamak için bir izin isteği gösterir
                                token.continuePermissionRequest();//kullanıcının izin isteğini onaylaması sağlanır
                            }
                        }).check();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);//yükleme işleminin kullanıcıya gösterilmesi sağlanır
        progressDialog.setTitle("Resim Yükleniyor");
        progressDialog.show();

        databaseReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d("RUN", "Flag1 " + String.valueOf(flag));
                for(int i = 0; i < listResult.getItems().size(); i++) {
                    StorageReference item = listResult.getItems().get(i);//listenin belirtilen indeksindeki öğeyi temsil eden bir StorageReference nesnesi oluşturulur
                    Log.d("RUN", "listresult miktari: " + listResult.getItems().size());
                    String name = item.getName();// bu nesne (item) üzerinden öğeye erişilir
                    if (name.startsWith("Ima")) {
                        imageCount += 1;
                        Log.d("RUN", "imagecount " + imageCount);
                    } else {
                        docsCount += 1;
                        Log.d("RUN", "docscount " + docsCount);
                    }
                }
                if (filetype == 1) {
                    fileName = "/Image" + String.valueOf(imageCount);
                    flag = 1;
                } else if (filetype == 2) {
                    fileName = "/Doc" + String.valueOf(docsCount);
                }
                StorageReference databaseReference1 = firebaseStorage.getReference().child(email + fileName);//Yüklenen dosyanın Storage'da kaydedileceği yerin referansını oluşturur
                databaseReference1.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//Seçilen dosyayı yüklemek için putFile() kullanılır
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String name = taskSnapshot.getMetadata().getName();//Yüklenen dosyanın adını name değişkenine atar. bir task anlık nesnesinin meta verilerini alır ve bu meta verilerden dosyanın adını elde eder.
                        final String[] url = new String[1];
                        databaseReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {//URI adres belirten bir metrik. Klsörün yerini söylemek gibi düşünülebilir.
                                url[0] = uri.toString();
                                if (name.startsWith("Ima")) {
                                    UploadInfo uploadInfo = new UploadInfo(name, url[0]);
                                    mDatabase.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                        //addListenerForSingleValueEvent veri değişikliklerini takip etmez ve yalnızca mevcut verileri bir kez alır
                                        //bunu kullanma sebebim db deki veriyi bağışçıya göstereceğim için her an güncellenmemesi gerekir sadece sayfaya bir sonraki girişinde güncellenmeli
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                //DB ye images verisini eklediğim kısım
                                                mDatabase.child(email).child("images").push().setValue(uploadInfo);//veritabanında benzersiz bir kimlik oluşturarak yeni bir alt düğüm oluşturur
                                                Log.d("RUN", "Snapshot" + snapshot);
                                                Log.d("RUN", String.valueOf(snapshot.child("images").getValue(UploadInfo.class)));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                }
                            }
                        });
                        Log.d("RUN", "Flag3 " + String.valueOf(flag));
                        progressDialog.dismiss();//İşlem tamamlandığında, yükleme işlemi için açılan ilerleme çubuğunu kapatır
                        Toast.makeText(getApplicationContext(), "Dosya başarıyla yüklendi", Toast.LENGTH_SHORT).show();
                        if (filetype == 1) {
                            if(imageCount == 0){//bu kontrolü yaparak imagenin sayısını arttırıyorum çünkü yukarıda for içine girmiyor henüz görsel kayıt edilmediği için
                                imageCount++;
                                Log.d("RUN", "imagecount2 " + imageCount);
                            }
                            if (imageCount > 0) {
                                imgdone = 1;
                            }
                            resultintent.putExtra("RES", imgdone);
                            setResult(RESULT_OK, resultintent);//bu Intent'i çağıran etkinliğe sonucu ve ilgili Intent'i iletiyor
                        } else {
                            if(docsCount == 0){
                                docsCount++;
                                Log.d("RUN", "docscount2 " + docsCount);
                            }
                            if (docsCount > 0) {
                                docdone = 1;
                            }
                            resultintent.putExtra("RES", docdone);
                            setResult(RESULT_OK, resultintent);
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {//Yükleme işleminin ilerlemesini takip etmek için bir OnProgressListener dinleyicisi eklenir
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float tamamlanan_yuzdelik = (snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Yükleniyor " + (int) tamamlanan_yuzdelik + " %");
                    }
                });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //istek kodu "1" olarak belirlenir ve geri döndürülen sonuç kodu "RESULT_OK" olduğunda (seçilen resim başarıyla geri döndürüldüğünde)
            filepath = data.getData();
            //getData, geri döndürülen verilerden bir URI nesnesi alır ve bu URI nesnesi, seçilen resmin dosya yolu bilgisini içerir.
            try {
                InputStream ir = getContentResolver().openInputStream(filepath);
                //InputStream, dosyalardan veri okumak için kullanılan soyut bir sınıftır
                //ir, seçilen resmin dosya yolu bilgisine göre resim verisine erişmeye izin verir
                bmap = BitmapFactory.decodeStream(ir);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bmap);
                //seçilen resmin verisini alma, bir "Bitmap" nesnesine dönüştürme ve "ImageView" nesnesinde görüntülemesini sağlar
            } catch (Exception e) {
                Toast.makeText(UploadActivity.this, "UPLOAD ACTIVITY HATA MESAJI: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

}



