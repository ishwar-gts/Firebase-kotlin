package com.example.learningproject.util

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.learningproject.R
import com.example.learningproject.databinding.CustomLoaderBinding

class LoadingDialog(mainActivity:  Activity,title:String) {

    private var activity: Activity? =null
    private var binding:CustomLoaderBinding?=null
    var title:String?=null
//    private var dialogBuilder:AlertDialog.Builder?=null
    private var dialog: AlertDialog? = null


    init {
        binding = CustomLoaderBinding.inflate(LayoutInflater.from(mainActivity))
        dialog = AlertDialog.Builder(mainActivity)
            .setView(binding?.root)
            .setCancelable(false)
            .create()
//        activity=mainActivity
//        binding=CustomLoaderBinding.inflate(activity!!.layoutInflater)
        this.title=title
        binding!!.loadingText.text=this.title

    }

    fun startLoadingDialog(){
//        dialogBuilder =AlertDialog.Builder(activity)
//        dialogBuilder!!.setView(binding!!.root)
//        dialogBuilder!!.setCancelable(false)
//        dialogBuilder!!.show().window!!.
//        setLayout(400,320)
        dialog?.show()
        dialog?.window?.setLayout(400, 320)

    }


    fun dismiss() {
        dialog!!.dismiss()
    }

    fun setLoadingText(text: String) {
        binding?.loadingText?.text = text
    }


}


