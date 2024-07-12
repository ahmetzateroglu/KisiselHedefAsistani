package com.ahmet.kisiselhedefasistani

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ahmet.kisiselhedefasistani.databinding.ActivityKayitOlBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class KayitOl : AppCompatActivity() {
    private lateinit var binding:ActivityKayitOlBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKayitOlBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth= Firebase.auth
        firestore= Firebase.firestore
    }

    fun kayitol(view: View) {
        val email = binding.emailhere.text.toString()
        val password = binding.passhere.text.toString()
        val kullaniciAdi = binding.kullaniciadi.text.toString()
        if (email.isBlank() || password.isBlank() || kullaniciAdi.isBlank()) {
            Toast.makeText(
                this,
                "Email, şifre veya kullanıcı adı boş bırakılamaz!",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val yeniKullaniciId = authResult.user?.uid
                val postMap = hashMapOf(
                    "name" to kullaniciAdi,
                    "email" to email
                )
                yeniKullaniciId?.let { userId ->
                    firestore.collection("Kullanicilar").document(userId).set(postMap)
                        .addOnSuccessListener {
                            val intent = Intent(this, GirisYap::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }

    }


}