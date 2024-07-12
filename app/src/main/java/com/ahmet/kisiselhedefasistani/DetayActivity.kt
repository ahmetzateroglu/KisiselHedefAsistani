package com.ahmet.kisiselhedefasistani

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ahmet.kisiselhedefasistani.databinding.ActivityDetayBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DetayActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetayBinding
    private lateinit var auth: FirebaseAuth

    var key: String = ""
    var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= Firebase.auth
        val bundle = intent.extras
        if (bundle != null){
            binding.detailDesc.text=bundle.getString("Description")
            binding.detailTittle.text=bundle.getString("Title")
            binding.detailCat.text=bundle.getString("Category")
            binding.detailPrior.text=bundle.getString("Priority")
            imageUrl=bundle.getString("Image")!!
            key=bundle.getString("Key")!!
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)
        }
        binding.deleteButton.setOnClickListener {

            val reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(auth.currentUser?.uid.toString())
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.getReferenceFromUrl(imageUrl)

            storageReference.delete().addOnSuccessListener {
                reference.child(key).removeValue()
                Toast.makeText(this@DetayActivity, "Deleted", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

        }
        binding.editButton.setOnClickListener {

            val intent = Intent(this@DetayActivity, UpdateActivity::class.java)
                .putExtra("Title", binding.detailTittle.text.toString())
                .putExtra("Description", binding.detailDesc.text.toString())
                .putExtra("Priority", binding.detailPrior.text.toString())
                .putExtra("Category", binding.detailCat.text.toString())
                .putExtra("Image", imageUrl)
                .putExtra("Key", key)

            startActivity(intent)
            finish()
        }

    }
}