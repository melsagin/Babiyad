package com.melsasagin.babiyad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    protected TextView registerText,registerUser;
    private EditText editTextFullName,editTextEmail,editTextPassword;

    private RadioGroup radioGroup;
    public RadioButton radioButton;

    private String userType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerText = (TextView) findViewById(R.id.back_to_sign_in);
        registerText.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        radioGroup = (RadioGroup) findViewById(R.id.groupRadio);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.back_to_sign_in:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void radioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            //Hangi radyo düğmesinin tıklandığını kontrol eder
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Seçilen Radyo Düğmesini Alınır
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            }
        });
    }



    private void registerUser() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        /*
         Log.d() metodu, DEBUG seviyesindeki log mesajlarını göndermek için ve hata ayıklama amaçlı olarak kullanılır.
         Bu,  uygulamanın nasıl çalıştığını ve hata ayıklama gereksinimlerini görmelerine yardımcı olur.
         */
        Log.d("RUN", email);
        radioButton  = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        userType = radioButton.getText().toString().trim();

        if(fullName.isEmpty())
        {
            editTextFullName.setError("Ad Soyad veya Kurum Adı zorunlu bir alandır !");
            editTextFullName.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            editTextEmail.setError("E-posta zorunlu bir alandır !");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Lütfen Geçerli E-posta sağlayın !");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            editTextPassword.setError("Password zorunlu bir alandır !");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6)
        {
            editTextPassword.setError("Minimum parola uzunluğu 6 karakter olmalıdır !");
            editTextPassword.requestFocus();
            return;
        }

        /*
        Firebase işlemleri, arka planda asenkron olarak gerçekleştirilir.
        Bu nedenle, Firebase işlemlerini çağırdığımda işlemin tamamlanması için belirli bir süre beklemem gerekmez.
        Bunun yerine, işlem tamamlandığında bana bir Task döndürülür
        Ben bu Task üzerinden işlemin tamamlanıp tamamlanmadığını ve başarılı olup olmadığını kontrol edebilirim.
         */
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())//kullanıcı başarıyla kayıt edilmiş ise
                        {
                            User user = new User(fullName,email,userType, 1);
                            String encodedemail = EncodeString(email);
                            Log.d("RUN", encodedemail);
                            //Firebase'de "Users" adlı bir düğümün altında, "encodedemail" adlı bir alt düğüm oluşturarak, "user" adlı bir nesnenin verilerini bu düğüme kaydeder.
                            FirebaseDatabase.getInstance().getReference("Users").child(encodedemail).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(RegisterUser.this,"Kullanıcı başarıyla kaydedildi !",Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(RegisterUser.this,"Kayıt başarısız. Kullanıcı zaten var !",Toast.LENGTH_LONG).show();
                                                }
                                        }
                             });
                        }
                        else
                        {
                            Toast.makeText(RegisterUser.this,"Kayıt başarısız. Kullanıcı zaten var !",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
}
