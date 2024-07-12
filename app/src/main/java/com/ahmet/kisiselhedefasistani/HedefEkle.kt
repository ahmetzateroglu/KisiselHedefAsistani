package com.ahmet.kisiselhedefasistani

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.ahmet.kisiselhedefasistani.databinding.ActivityHedefEkleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.DateFormat
import java.util.Calendar

class HedefEkle : AppCompatActivity() {

    lateinit var binding: ActivityHedefEkleBinding
    private lateinit var auth: FirebaseAuth
    var imageUrl: String?=null
    var uri:Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHedefEkleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth

        val activityResultLauncher= registerForActivityResult<Intent,ActivityResult>(
            ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode== RESULT_OK){
                val data = result.data
                uri = data!!.data
                binding.iwhedefresim.setImageURI(uri)
            }else{
                    Toast.makeText(this@HedefEkle,"Resim Seçilmedi",Toast.LENGTH_LONG).show()
            }
        }
        binding.iwhedefresim.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type="image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.buttonkaydet.setOnClickListener {
            saveData()
        }

    }

    private fun saveData(){

        val storageReference = FirebaseStorage.getInstance().reference.child("Android Images")
            .child(uri!!.lastPathSegment!!)
        val builder = AlertDialog.Builder(this@HedefEkle)
        builder.setCancelable(false)
        builder.setView(R.layout.progressbar_layout)
        val dialog = builder.create()
        dialog.show()
        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage= uriTask.result
            imageUrl=urlImage.toString()
            uploadData()
            dialog.dismiss()
        }.addOnFailureListener {
            dialog.dismiss()
        }
    }

    private fun uploadData(){
        val hedefismi = binding.ethedefismi.text.toString()
        val hedefoncelik = binding.ethedefoncelik.text.toString()
        val hedefkategori = binding.ethedefkategori.text.toString()
        val hedefaciklama = binding.ethedefaciklama.text.toString()

        val dataClass = DataClass(hedefismi, hedefoncelik, hedefkategori, hedefaciklama, imageUrl)
        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        FirebaseDatabase.getInstance().getReference("Kullanicilar").child(auth.currentUser?.uid.toString()).
        child(currentDate)
            .setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@HedefEkle, "Kayıt Eklendi.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@HedefEkle, "Kayıt Eklenemedi.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@HedefEkle, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


}
