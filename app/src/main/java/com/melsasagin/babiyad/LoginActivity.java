package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    // register ve forgotPassword değişkenleri, ekranın üzerinde yer alan "Register" ve "Forgot Password" butonlarının referanslarını saklar.
    // editTextEmailMain ve editTextPasswordMain değişkenleri ise, kullanıcının e-posta adresi ve parolasını girdiği EditTextlerin referanslarını saklar.
    // reference_user değişkeni ise, Firebase veritabanında "Users" adındaki veri yapısının referansını saklar.

    private EditText editTextEmailMain,editTextPasswordMain;
    private DatabaseReference reference_user;
    String usertype;
    private int enable;

    private FirebaseAuth myAuth;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /*
        onCreate() metodu çağrıldığında, uygulamanın giriş ekranını göstermek için setContentView() metodu çağrılır ve giriş ekranını gösteren XML dosyası belirtilir.
        register TextView nesnesi ve signIn Button nesnesi tanımlanır ve bu nesnelerin tasarım dosyasındaki karşılıkları bulunur.
        editTextEmailMain ve editTextPasswordMain EditText nesneleri tanımlanır ve bu nesnelerin tasarım dosyasındaki karşılıkları bulunur.
        FirebaseAuth sınıfından bir nesne oluşturulur ve bu nesne myAuth değişkenine atanır. Bu nesne Firebase girişi için kullanılacaktır.
        forgotPassword TextView nesnesi tanımlanır ve bu nesnenin tasarım dosyasındaki karşılığı bulunur.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView register = (TextView) findViewById(R.id.registerTextNew);
        register.setOnClickListener(this);//görüntüye tıklanabilirlik katılır

        Button signIn = (Button) findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        editTextEmailMain = (EditText) findViewById(R.id.emailMain);
        editTextPasswordMain = (EditText) findViewById(R.id.passwordMain);

        myAuth = FirebaseAuth.getInstance();

        TextView forgotPassword = (TextView) findViewById(R.id.forgotPasswordText);
        forgotPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        /*
        onClick() metodu View türünden bir nesne alır ve bu nesne hangi bileşen tarafından tıklandığını belirtir.
        Bir switch yapısı kullanılır ve tıklanan bileşenin ID'sine göre işlemler yapılır. Örneğin, eğer register TextView tıklandıysa, RegisterUser sınıfının bir Intent ile çağrılması için startActivity() metodu çağrılır.
        Eğer signIn Button tıklandıysa, userLogin() metodu çağrılır.
        Eğer forgotPassword TextView tıklandıysa, ForgotPassword sınıfının bir Intent ile çağrılması için startActivity() metodu çağrılır.
         */
        if(v.getId() == R.id.registerTextNew){
            startActivity(new Intent(LoginActivity.this,RegisterUser.class));
        }
        else if(v.getId() == R.id.loginButton){
            userLogin();
        }
        else if(v.getId() == R.id.forgotPasswordText){
            startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
        }
    }
    private void userLogin() {
        String emailMain = editTextEmailMain.getText().toString().trim();//Bu değişkene trim() metodu uygulanarak, bu değişkenin başında ve sonunda bulunan boşluk karakterleri silinir
        String passwordMain = editTextPasswordMain.getText().toString().trim();

        if(emailMain.isEmpty())
        {
            editTextEmailMain.setError("E-posta zorunlu bir alandır !");
            editTextEmailMain.requestFocus();//editTextEmailMain nesnesine odaklanılır.
            return;// İşleme return ile son verilir. Bu sayede, işlemin geri kalanı çalıştırılmaz ve bu hata durumu işlenmiş olur.
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailMain).matches())//kullanıcıdan alınan e-posta adresinin geçerli bir e-posta adresi olup olmadığını kontrol eder
        {
            //Patterns.EMAIL_ADDRESS içinde bulunan matcher() metodu, verilen e-posta adresini EMAIL_ADDRESS ile eşleştirir ve eşleşme olup olmadığını kontrol eder.
            editTextEmailMain.setError("Lütfen Geçerli E-posta sağlayın !");
            editTextEmailMain.requestFocus();
            return;
        }
        if(passwordMain.isEmpty())
        {
            editTextPasswordMain.setError("Şifre girmek zorunludur !");
            editTextPasswordMain.requestFocus();
            return;
        }
        if(passwordMain.length()<6)
        {//FirebaseAuthWeakPasswordException hatası vermemesi adına
            editTextPasswordMain.setError("Minimum parola uzunluğu 6 karakter olmalıdır !");
            editTextPasswordMain.requestFocus();
            return;
        }

        //addOnCompleteListener bitince ne olcağını gösterir
        myAuth.signInWithEmailAndPassword(emailMain,passwordMain).addOnCompleteListener(task -> {//myAuth.signInWithEmailAndPassword(emailMain,passwordMain).addOnCompleteListener(new OnCompleteListener<AuthResult>() şeklinde olunca uyarı veriyor
            //Task nesnesine bir tamamlama dinleyicisi eklenir. Bu dinleyici, Task nesnesinin tamamlanıp tamamlanmadığını kontrol eder ve eğer tamamlanmış ise, işlemin sonucuna göre bir yönlendirme yapar.
            if(task.isSuccessful()) {//görev başarılı olduysa
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//getInstance() metodu, Firebase oturum yöneticisini temsil eden bir FirebaseAuth nesnesi döndürür. Bu nesnenin getCurrentUser() metodu ise, oturum açılmış olan kullanıcının bilgilerini içeren bir FirebaseUser nesnesi döndürür. Bu nesne, oturum açılmış olan kullanıcının e-posta adresi, kullanıcı adı gibi bilgilerini içerir.
                if (user.isEmailVerified()) {
                    reference_user = FirebaseDatabase.getInstance().getReference("Users");
                    String encoded_email = EncodeString(emailMain);
                    reference_user.child(encoded_email).addValueEventListener(new ValueEventListener() {
                        /*
                        reference_user.child(encoded_email) ifadesi, reference_user nesnesinin altında bulunan ve encoded_email değişkeni ile eşleşen bir veri yapısına ulaşır.
                        Bu veri yapısına bir değer dinleyicisi eklenir ve bu dinleyici, veri yapısındaki değerler değiştiğinde çalışacak olan bir metod (onDataChange) içerir.
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){//verinin anlık durum görüntüsü (dataSnapshot) varsa
                                usertype = dataSnapshot.child("userType").getValue(String.class);
                                enable = dataSnapshot.child("enable").getValue(Integer.class);
                                if(usertype.equals("Bağışçı")){
                                    if (enable == 1) {
                                        Intent intent = new Intent(LoginActivity.this, Donor.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Hesap Devre Dışı! Hileli Faaliyetler Tespit Edildi", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    startActivity(new Intent(LoginActivity.this, DashboardOrgActivity.class));
                                }
                            }
                        }
                        @Override
                        /*onCancelled() metodu ise, veritabanı işlemlerinde bir hata oluştuğunda çalışacak olan bir metoddur. Bu metodun içine DatabaseError tipinden bir nesne verilir
                        Bu metodun kullanılma sebebi, veritabanından veri okuma işlemlerinde oluşabilecek hata durumlarının önlenmesidir.
                        Örneğin, internet bağlantısı kesildiğinde veya veritabanı sunucusu çalışmıyor olduğunda bu metod çalışarak, uygulamada bir hata oluşmaması sağlanır.
                        Bu sayede, uygulama düzgün bir şekilde çalışmaya devam eder.
                         */
                        public void onCancelled(@NonNull DatabaseError databaseError) {}

                    });
                }
                else
                {
                    user.sendEmailVerification();
                    Toast.makeText(LoginActivity.this,"Hesabınızı doğrulamak için E-postanızı kontrol edin",Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Giriş yapılamadı ! Lütfen kimlik bilgilerinizi kontrol edin",Toast.LENGTH_LONG).show();
            }
        });
    }
    public static String EncodeString(String string) {
        /*
        Bu kod satırlarında bir metod tanımlanmıştır ve bu metodun görevi verilen bir dizge içerisindeki noktalama işaretlerini virgüllere dönüştürmektir.
        Örneğin, aşağıdaki örnekte görüldüğü gibi:
        String input = "Hello. How are you today?";
        String output = EncodeString(input);
        // output: "Hello, How are you today?"
        Bu metod, verilen dizge içerisinde "." karakterini arar ve bu karakterleri "," ile değiştirir. Bu metodun dönüş değeri olarak, noktalama işaretleri değiştirilmiş dizge döndürülür.
        Bu metodun ne zaman kullanılabileceği konusunda bir fikir vermek gerekirse, veritabanına kaydedilen veriler içerisinde noktalama işaretleri kullanılmaması gereken durumlarda bu metod kullanılabilir.
        Örneğin, veritabanında bir sütun oluşturulmuş ve bu sütun içerisinde yalnızca metin verisi tutulmaktadır.
        Bu sütunda noktalama işaretleri kullanılmaması gereken bir sütun olduğu varsayılırsa, bu metod kullanılarak veritabanına kaydedilmeden önce dizgeler içerisindeki noktalama işaretleri değiştirilebilir.
        Bu sayede veritabanında hata oluşmadan veri kaydedilebilir
         */
        return string.replace(".", ",");
    }

}