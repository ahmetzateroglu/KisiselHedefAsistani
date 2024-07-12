package com.ahmet.kisiselhedefasistani

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.ahmet.kisiselhedefasistani.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var binding: ActivityMainBinding

    private lateinit var firestore: FirebaseFirestore // Firestore ile isim gibi verileri firebaseden çekmek için

    private lateinit var auth: FirebaseAuth // email doğrulamak ve isimle eşleştirmek için currentuser kullanmalıyız bunun içinde authentication kullanmamız lazım

    private lateinit var drawerLayout: DrawerLayout// menu için

    private lateinit var dataList:ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    var databaseReference:DatabaseReference? = null
    var eventListener:ValueEventListener? = null


    private lateinit var GoogleSignInClient: GoogleSignInClient // google bağlantı için

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firestore = Firebase.firestore

        auth= Firebase.auth


        val gridLayoutManager = GridLayoutManager(this@MainActivity,1)
        binding.recyclerView.layoutManager=gridLayoutManager
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progressbar_layout)
        val dialog = builder.create()
        dialog.show()
        dataList = ArrayList()
        adapter= MyAdapter(this@MainActivity, dataList)
        binding.recyclerView.adapter=adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Kullanicilar").
        child(auth.currentUser?.uid.toString())
        dialog.show()


        eventListener=databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children){
                    val dataClass = itemSnapshot.getValue(DataClass::class.java)
                    if (dataClass != null){
                        dataList.add(dataClass)
                        dataClass.key=itemSnapshot.key
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()

            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

        binding.fab.setOnClickListener {
            val intent = Intent(this,HedefEkle::class.java)
            startActivity(intent)
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searcList(newText)
                return true
            }

        })



        // Menü için
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar= findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
            R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home)
        }


        // Google girişi ve hesap bilgilerinin çekilip menüye eklenmesi. Giriş yapılı hesap varsa da onun bilgileri geliyor zaten.

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val headerView = navigationView.getHeaderView(0)
        val isimTextView = headerView.findViewById<TextView>(R.id.headerName)
        val emailTextView = headerView.findViewById<TextView>(R.id.headerEmail)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Kullanicilar").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val name = documentSnapshot.getString("name")
                        val email = documentSnapshot.getString("email")

                        isimTextView.text = name
                        emailTextView.text=email
                    } else {
                        // Belge bulunamadı durumunda yapılacak işlemler
                        Toast.makeText(this, "Kullanıcı bilgisi bulunamadı", Toast.LENGTH_LONG).show()
                        isimTextView.text = ""
                        emailTextView.text=""
                    }
                }
                .addOnFailureListener { exception ->
                    // Hata durumunda yapılacak işlemler
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        } else {
            // Kullanıcı UID'si null ise yapılacak işlemler
            Toast.makeText(this, "Kullanıcı oturumu açık değil", Toast.LENGTH_LONG).show()
            isimTextView.text = ""
            emailTextView.text=""
        }

    }


    fun searcList(text: String){
        val searchList = ArrayList<DataClass>()
        for(dataClass in dataList){
            if(dataClass.ethedefkategori?.lowercase()?.contains(text.lowercase())==true){
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this,Settings::class.java)
        when (item.itemId) {

            R.id.nav_settings -> startActivity(intent)

            R.id.nav_logout -> signOutAndStartSignInActivity()

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
           // onBackPressedDispatcher.onBackPressed()
            super.onBackPressed()
        }
    }



    private fun signOutAndStartSignInActivity() {
        auth.signOut()

        GoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this@MainActivity, GirisYap::class.java)
            startActivity(intent)
            finish()
        }
    }






}