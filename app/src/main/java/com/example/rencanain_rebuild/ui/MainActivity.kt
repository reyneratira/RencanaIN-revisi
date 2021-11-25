package com.example.rencanain_rebuild.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codeathome.todo.model.Todo
import com.codeathome.todo.network.ApiConfig
import com.example.rencanain_rebuild.R
import com.example.rencanain_rebuild.adapter.AdapterCard
import com.example.rencanain_rebuild.adapter.AdapterItemCard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var adapter: AdapterCard? = null
    private lateinit var rvParent: RecyclerView
    private lateinit var swPlayout: SwipeRefreshLayout
    private lateinit var btnAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swPlayout = findViewById(R.id.swpRef)
        swPlayout.setOnRefreshListener(this)

        getAllTodos()
    }

    fun displayView(){
        rvParent = findViewById(R.id.rv_parent)
        btnAdd = findViewById((R.id.btn_fload_add))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        rvParent.adapter = adapter
        rvParent.layoutManager = layoutManager
    }

    fun getAllTodos() {
        ApiConfig.instanceRetrofit.getAllTodo().enqueue(object : Callback<Todo>{
            override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                if (response.isSuccessful){
                    adapter= AdapterCard(this@MainActivity, response.body()?.data.orEmpty())

                    displayView()
                    postTodo()

                    swPlayout.isRefreshing = false

                } else {

                }
            }

            override fun onFailure(call: Call<Todo>, t: Throwable) {
                Log.d("TAG", "Gagal Get data Todo")
            }

        })
    }

    fun postTodo(){
        btnAdd.setOnClickListener {
            val mDialog =
                LayoutInflater.from(this).inflate(R.layout.dialog_newtodo, null)

            val edtDialog = mDialog.findViewById<EditText>(R.id.edt_simpan_todo)
            val btnSimpan = mDialog.findViewById<Button>(R.id.btn_simpantodo)

            val mBuild = AlertDialog.Builder(this)
                .setView(mDialog)
                .setTitle("Create New Todo")

            val mAlertDialog = mBuild.show()

            btnSimpan.setOnClickListener {
                val name = edtDialog.text.toString()

                ApiConfig.instanceRetrofit.postTodo(name).enqueue(object : Callback<Todo>{
                    override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                        if (response.isSuccessful){
                            Toast.makeText(this@MainActivity, "Todo Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                            getAllTodos()
                        } else{
                            Toast.makeText(this@MainActivity, "Todo Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Todo>, t: Throwable) {
                        getAllTodos()
                    }

                })
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onRefresh() {
        getAllTodos()
    }
}