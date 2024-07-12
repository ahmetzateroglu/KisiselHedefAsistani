package com.ahmet.kisiselhedefasistani

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ahmet.kisiselhedefasistani.databinding.ActivityUpdateBinding
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private var uri: Uri? = null
    private var imageUrl: String? = null
    private var key: String? = null
    private var oldImageURL: String? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    uri = data?.data
                    binding.updateImage.setImageURI(uri)
                } else {
                    Toast.makeText(this@UpdateActivity, "Resim Seçilmedi", Toast.LENGTH_SHORT).show()
                }
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= Firebase.auth

        val bundle = intent.extras
        if (bundle != null) {
            Glide.with(this@UpdateActivity).load(bundle.getString("Image")).into(binding.updateImage)
            binding.updateTitle.setText(bundle.getString("Title"))
            binding.updateDesc.setText(bundle.getString("Description"))
            binding.updatePrior.setText(bundle.getString("Priority"))
            binding.updateCat.setText(bundle.getString("Category"))
            key = bundle.getString("Key")
            oldImageURL = bundle.getString("Image")
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Kullanicilar").
        child(auth.currentUser?.uid.toString()).child(key!!)
        binding.updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.updateButton.setOnClickListener {
            saveData()
            val intent = Intent(this@UpdateActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveData() {

            storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri!!.lastPathSegment!!)

            val builder = AlertDialog.Builder(this@UpdateActivity)
            builder.setCancelable(false)
            builder.setView(R.layout.progressbar_layout)
            val dialog = builder.create()
            dialog.show()
            storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage= uriTask.result
                imageUrl=urlImage.toString()
                updateData()
                dialog.dismiss()
            }.addOnFailureListener {
                println(it.localizedMessage)
                dialog.dismiss()
            }

    }

    private fun updateData() {
        val title = binding.updateTitle.text.toString().trim()
        val desc = binding.updateDesc.text.toString().trim()
        val cat = binding.updateCat.text.toString()
        val pri = binding.updatePrior.text.toString()

        if(uri == null){
            val dataClass = DataClass(title,pri,cat,desc,oldImageURL)
            databaseReference.setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@UpdateActivity, "Güncellendi", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
                .addOnFailureListener { e ->
                    Toast.makeText(this@UpdateActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }else{
            val dataClass = DataClass(title,pri,cat,desc,imageUrl)
            databaseReference.setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reference: StorageReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL!!)
                    reference.delete()
                    Toast.makeText(this@UpdateActivity, "Güncellendi", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
                .addOnFailureListener { e ->
                    Toast.makeText(this@UpdateActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }




    }
}



