package com.onseen.livecare.fragments.main.Patients

import android.Manifest.permission.*
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onseen.livecare.R
import com.onseen.livecare.activities.MainActivity
import com.onseen.livecare.fragments.main.Patients.ViewModel.AuditViewModel
import com.onseen.livecare.interfaces.AuditFragmentListner
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import kotlinx.android.synthetic.main.modal_audit_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*

class AuditTakePhotoFragment : Fragment() {

    var listener: AuditFragmentListner? = null
    var vmAudit: AuditViewModel? = null

    private val GALLERY_CODE = 1
    private val CAMERA_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_audit_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancel.setOnClickListener {
            listener?.onPressedCancel()
        }
        btnNext.setOnClickListener {
            requestAudit()
        }
        imageView.setOnClickListener {
            showPictureDialog()
        }
    }

    //Business Logic

    fun requestAudit() {
        if(this.vmAudit == null) return

        if(this.vmAudit!!.imagePhoto == null) {
            (activity as MainActivity).showToast("Please take a photo of the cash")
            return
        }

        if(this.vmAudit!!.modelConsumer == null || this.vmAudit!!.modelAccount == null) {
            (activity as MainActivity).showToast("Something went wrong.")
            return
        }

        (activity as MainActivity).showProgressHUD()
        this.vmAudit!!.toDataModel { audit, message ->
            if(audit != null) {
                FinancialAccountManager.sharedInstance().requestAuditForAccount(audit, this.vmAudit!!.modelConsumer!!, this.vmAudit!!.modelAccount!!, object : NetworkManagerResponse {
                    override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                        GlobalScope.launch(Dispatchers.Main) {
                            (activity as MainActivity).hideProgressHUD()
                            if(responseDataModel.isSuccess()) {
                                listener?.onPressedCancel()
                            } else {
                                gotoWarningFragment()
                            }
                        }
                    }
                })
            } else {
                (activity as MainActivity).hideProgressHUD()
                (activity as MainActivity).showToast(message)
            }
        }
    }

    fun gotoWarningFragment() {
        listener?.onPressedNextWithPhoto()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Choose One")
        val pictureDialogItems = arrayOf("Use Photo", "Use Camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        if(!setupPermissions()) return

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_CODE)
    }

    private fun takePhotoFromCamera() {
        if(!setupPermissions()) return

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_CODE)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return

        if (requestCode == GALLERY_CODE)
        {
            val contentURI = data.data
            try
            {
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                val fileImage = UtilsBaseFunction.saveImageToInternalStorage(context!!, bitmap, contentURI)
                imageView!!.setImageBitmap(BitmapFactory.decodeFile(fileImage.getAbsolutePath()))
                this.vmAudit!!.imagePhoto = fileImage
            }
            catch (e: IOException) {
                e.printStackTrace()
                (activity as MainActivity).showToast("Failed!")
            }

        }
        else if (requestCode == CAMERA_CODE)
        {
            val thumbnail = data.extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(thumbnail)
            val fileImage = UtilsBaseFunction.saveImageToInternalStorage(context!!, thumbnail, null)
            this.vmAudit!!.imagePhoto = fileImage
        }
    }

    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 100

    private fun setupPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(context!!, READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA), READ_EXTERNAL_STORAGE_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }

}
