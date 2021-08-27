package me.arwazkhan.foodrunner.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.adapter.HomeRecyclerAdapter
import me.arwazkhan.foodrunner.model.Restaurant
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var radioButtonView: View
    private lateinit var etSearch: EditText
    private lateinit var rLNotFound: RelativeLayout
    private val foodInfoList = arrayListOf<Restaurant>()
    private var ratingComparator = Comparator<Restaurant> { food1, food2 ->

        if (food1.foodRating.compareTo(food2.foodRating, true) == 0) {
            food1.foodName.compareTo(food2.foodName, true)
        } else {
            food1.foodRating.compareTo(food2.foodRating, true)
        }
    }
    private var costComparator = Comparator<Restaurant> { food1, food2 ->

        food1.foodPrice.compareTo(food2.foodPrice, true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        layoutManager = LinearLayoutManager(activity)
        recyclerHome = view.findViewById(R.id.recyclerHome)

        //progress bar-----------------------------------------
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE


        // search function------------------------------------------
        etSearch = view.findViewById(R.id.etSearch)
        rLNotFound = view.findViewById(R.id.rLNotFound)
        etSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(searchText: Editable?) {
                searchRestaurantByName(searchText.toString())
            }

            override fun beforeTextChanged(
                searchText: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                searchText: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        })
        return view
    }

    private fun fetchData() {
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    // Here we will handle the response
                    try {
                        progressLayout.visibility = View.GONE
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            val data = jsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = Restaurant(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("rating"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("image_url")
                                )

                                foodInfoList.add(foodObject)

                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, foodInfoList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred !!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    // Here we will handle the errors
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } else {
            checkInternet()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                radioButtonView = View.inflate(
                    context,
                    R.layout.sort_radio_button,
                    null
                )
                //radiobutton view for sorting display
                AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { _, _ ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(foodInfoList, costComparator)
                            foodInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(foodInfoList, costComparator)
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(foodInfoList, ratingComparator)
                            foodInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { _, _ ->

                    }
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun searchRestaurantByName(searchText: String) {
        val filteredList = arrayListOf<Restaurant>()

        for (item in foodInfoList) {
            if (item.foodName.lowercase().contains(searchText.lowercase())
            ) {
                filteredList.add(item)
            }
        }
        if (filteredList.size == 0) {
            rLNotFound.visibility = View.VISIBLE
        } else {
            rLNotFound.visibility = View.INVISIBLE
        }
        recyclerAdapter =
            HomeRecyclerAdapter(activity as Context, foodInfoList)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager
        recyclerAdapter.filterList(filteredList)
    }

    override fun onResume() {
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            if (foodInfoList.isEmpty())
                fetchData()
        } else {
            checkInternet()
        }
        super.onResume()
    }

    private fun checkInternet() {
        val dialog = AlertDialog.Builder(activity as Context)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(activity as Activity)
        }
        dialog.create()
        dialog.show()
    }

}


