package com.wganizo.weather.ui.forecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.wganizo.weather.R
import com.wganizo.weather.constants.Constants
import com.wganizo.weather.sqlite.PreferencesDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherForecastAdapter(private val context: Context, private val weatherList: List<Weather>) : BaseAdapter() {
    private val dbHelper = PreferencesDatabaseHelper(context)
    private val unitSign = dbHelper.getUnitSign()
    override fun getCount(): Int = weatherList.size

    override fun getItem(position: Int): Any = weatherList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private val constants = Constants()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val weather = weatherList[position]
        holder.dayTextView.text = dateToDay(weather.date)
        holder.tempTextView.text =
            "Max: ${weather.tempMax}$unitSign\nMin: ${weather.tempMin}$unitSign\nAvg: ${weather.temp}$unitSign"
        holder.descriptionTextView.text = weather.description
        println("${constants.baseImageUrl}${weather.icon}${constants.fileFormat}")
        Glide.with(context)
            .load("${constants.baseImageUrl}${weather.icon}${constants.fileFormat}")
            .into(holder.weatherImageView)
        return view
    }

    private class ViewHolder(view: View) {
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val tempTextView: TextView = view.findViewById(R.id.tempTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.description)
        val weatherImageView: ImageView = view.findViewById(R.id.weatherImageView)
    }

    private fun dateToDay(dateString: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date: Date? = dateFormat.parse(dateString)

            if (date == null) {
                "Invalid Date"
            } else {
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                dayFormat.format(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error"
        }
    }
}
