package com.example.learningproject.constant

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {


    companion object {
        private lateinit var sharedPreferences:SharedPreferences
        private const val sharedPrefFile = "LearningProject"


        fun  initLocalDatabase(context:Context){
         sharedPreferences=context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        }

        fun setString(keyName:String,keyValue:String){
           val editor:SharedPreferences.Editor= sharedPreferences.edit()
            editor.putString(keyName,keyValue)
            editor.apply()
            editor.commit()
        }

        fun getString(keyName: String): String {
            return sharedPreferences.getString(keyName, "user").toString()
        }




    }
}


