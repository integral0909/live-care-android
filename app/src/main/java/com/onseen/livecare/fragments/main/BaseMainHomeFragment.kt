package com.onseen.livecare.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

open class BaseMainHomeFragment : BaseFragment() {

    protected lateinit var mainFragment: MainHomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainFragment = parentFragment as MainHomeFragment
    }

    override fun viewAppeared() {

    }

    protected fun hideKeyboard(editText: EditText?) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        imm.hideSoftInputFromWindow(editText?.windowToken, 0)
        editText?.isFocusable = false
        editText?.isFocusableInTouchMode = true
    }
}
