package com.example.githubuserslist

import android.content.Intent
import android.media.tv.TvContract.Channels.CONTENT_URI
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserslist.Adapter.FavoriteAdapter
import com.example.githubuserslist.Helper.MappingHelper
import com.example.githubuserslist.databinding.ActivityFavoriteUserBinding
import com.example.githubuserslist.entity.FavoriteItems
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteUser : AppCompatActivity() {


//    private lateinit var adapter: NoteAdapter
//
//    private lateinit var binding: ActivityMainBinding
//
//    companion object {
//        private const val EXTRA_STATE = "EXTRA_STATE"
//    }

    private lateinit var adapter: FavoriteAdapter
//    private lateinit var favoriteViewModel: MainViewModel
    private lateinit var binding: ActivityFavoriteUserBinding

    companion object {
        val TAG = FavoriteUser::class.java.simpleName
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        adapter = FavoriteAdapter(this)
        adapter.notifyDataSetChanged()

        binding.recyclerViewFavorite.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFavorite.setHasFixedSize(true)
        binding.recyclerViewFavorite.adapter = adapter

//        favoriteViewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.NewInstanceFactory()
//        ).get(MainViewModel::class.java)
//
//        setList()
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            binding = ActivityMainBinding.inflate(layoutInflater)
//            setContentView(binding.root)
//
//            supportActionBar?.title = "Notes"

//            binding.rvNotes.layoutManager = LinearLayoutManager(this)
//            binding.rvNotes.setHasFixedSize(true)
            adapter = FavoriteAdapter(this)
            binding.recyclerViewFavorite.adapter = adapter

//            binding.fabAdd.setOnClickListener {
//                val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
//                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
//            }

            if (savedInstanceState == null) {
                loadNotesAsync()
            } else {
                val list = savedInstanceState.getParcelableArrayList<FavoriteItems>(EXTRA_STATE)
                if (list != null) {
                    adapter.listFavoriteUser = list
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.language) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressbarFavorite.visibility = View.VISIBLE
        } else {
            binding.progressbarFavorite.visibility = View.GONE
        }
    }


    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            showLoading(true)
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favData = deferredNotes.await()
            showLoading(false)
            if (favData.size > 0) {
                adapter.listFavoriteUser = favData
            } else {
                adapter.listFavoriteUser = ArrayList()
                showSnackbarMessage("Not Found")
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavoriteUser)
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.recyclerViewFavorite, message, Snackbar.LENGTH_SHORT).show()
    }
}