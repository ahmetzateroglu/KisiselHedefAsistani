package com.ahmet.kisiselhedefasistani

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ahmet.kisiselhedefasistani.databinding.ActivityGirisYapBinding
import com.ahmet.kisiselhedefasistani.databinding.ActivitySettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth

        binding.sifreDegisButton.setOnClickListener {
            SifreDegistir()
        }

    }

    private fun SifreDegistir(){
        if(binding.eskiSifre.text.isNotEmpty() && binding.yeniSifre.text.isNotEmpty() && binding.yeniSifreDogrula.text.isNotEmpty()){
            if (binding.yeniSifre.text.toString().equals(binding.yeniSifreDogrula.text.toString())){
                val user=auth.currentUser
                if (user != null && user.email != null){
                    val credential = EmailAuthProvider.getCredential(user.email!! , binding.eskiSifre.text.toString())
                    user.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful){
                            user?.updatePassword(binding.yeniSifre.text.toString())?.addOnCompleteListener {task ->
                                if (task.isSuccessful){
                                    Toast.makeText(this,"Şifreniz değiştirildi.",Toast.LENGTH_LONG).show()
                                    auth.signOut()
                                    startActivity(Intent(this,GirisYap::class.java))
                                    finish()
                                }
                                else{
                                    Toast.makeText(this,"Şifreniz değiştirilemedi.",Toast.LENGTH_LONG).show()
                                }
                            }
                        }else{
                            Toast.makeText(this,"Şifreniz değiştirilemedi. Yanlış şifre girmediğinize emin misiniz?"
                                ,Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    startActivity(Intent(this,GirisYap::class.java))
                    finish()
                }
            }else{
                Toast.makeText(this,"Şifreleriniz eşleşmedi.",Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Lütfen Tüm Alanları Doldurunuz.",Toast.LENGTH_LONG).show()
        }

    }
}