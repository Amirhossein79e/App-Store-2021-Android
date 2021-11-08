package com.amirhosseinemadi.appstore.view.fragment

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.activity.MainActivity
import com.amirhosseinemadi.appstore.view.callback.Callback

class ErrorFragment(private val callback:Callback) : Fragment() {

    private lateinit var view1: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view1 = inflater.inflate(R.layout.fragment_error, container, false)
        initView()
        return view1
    }


    private fun initView()
    {
        view1.findViewById<AppCompatButton>(R.id.btn).setOnClickListener {
            callback.notify()
        }

        Utilities.onBackPressed(view1,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                requireActivity().finish()
            }
        })
    }

}