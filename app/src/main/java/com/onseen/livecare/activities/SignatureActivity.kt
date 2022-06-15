package com.onseen.livecare.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.onseen.livecare.R
import kotlinx.android.synthetic.main.activity_signature.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*

class SignatureActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        setTitle(R.string.signature)

        btnClear.setOnClickListener {
            signaturePad.clear()
        }

        btnSave.setOnClickListener {
            if(signaturePad.isEmpty) {
                GlobalScope.launch(Dispatchers.Main) {
                    showToast("Please sign")
                }
                return@setOnClickListener
            }

            submitSignature()
        }
    }

    private fun submitSignature() {

        val resultIntent = Intent()
        val stream = ByteArrayOutputStream()
        val finalBitmap = signaturePad.signatureBitmap
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        resultIntent.putExtra("signature", stream.toByteArray())
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        val consumerSignatureRequestCode = 100
        val caregiverSignatureRequestCode = 101
    }
}
