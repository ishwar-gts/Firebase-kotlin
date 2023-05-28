package com.example.learningproject.util

import android.content.Context
import android.widget.Toast

class Constant {

    companion object{
        fun showMsg(msg:String,context:Context){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}