package com.melsasagin.babiyad;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditTextPassword;

    private Button resetPasswordButton;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditTextPassword = (EditText) findViewById(R.id.editTextEmail);
        resetPasswordButton = (Button) findViewById(R.id.resetButton);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }

            private void resetPassword() {
                /*
                Kullanıcının e-posta adresini alır ve doğruluğunu kontrol eder. E-posta doğru formatta değilse, kullanıcıya uyarı verilir.
                E-posta doğru ise, şifre sıfırlama e-postası gönderilir ve gönderme işlemi tamamlandıysa kullanıcıya mesaj verilir.
                 */
                String emailPassword = emailEditTextPassword.getText().toString().trim();

                if(emailPassword.isEmpty())
                {
                    emailEditTextPassword.setError("E-posta zorunlu bir alandır !");
                    emailEditTextPassword.requestFocus();//e-posta alanına geçersiz bir e-posta girdisi yapıldığında odaklanmasını ve kullanıcıya tekrar e-posta girdisi yapması için fırsat vermesini sağlar.
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(emailPassword).matches())//girilen e-posta adresinin geçerli bir e-posta adresi formatına uyup uymadığını kontrol eder.
                {
                    /*
                     Patterns.EMAIL_ADDRESS Android platformunda tanımlanmış bir java.util.regex.Pattern nesnesidir ve geçerli bir e-posta adresi formatını tanımlar.
                     matcher() metodu, bir e-posta adresi ile belirtilen java.util.regex.Pattern nesnesi arasında eşleşme arar.
                     Eğer eşleşme varsa, matches() metodu true döndürür. Aksi takdirde false döndürür.
                     */
                    emailEditTextPassword.setError("Lütfen Geçerli E-posta sağlayın !");
                    emailEditTextPassword.requestFocus();
                    return;
                }

                auth.sendPasswordResetEmail(emailPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ForgotPassword.this,"Şifreyi sıfırlamak için e-postanızı kontrol edin",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ForgotPassword.this,"Tekrar Deneyin !",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
