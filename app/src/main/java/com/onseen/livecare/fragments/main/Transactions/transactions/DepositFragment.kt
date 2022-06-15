package com.onseen.livecare.fragments.main.Transactions.transactions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.onseen.livecare.activities.SignatureActivity
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Transactions.ViewModel.DepositViewModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Transaction.TransactionManager
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.android.synthetic.main.fragment_deposit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import android.view.ViewGroup
import com.onseen.livecare.R
import com.onseen.livecare.adapters.EnumGripRowType
import com.onseen.livecare.adapters.GridImageAdapter
import com.onseen.livecare.adapters.GridItem
import com.onseen.livecare.interfaces.RowItemClickListener
import de.mrapp.android.bottomsheet.BottomSheet
import java.io.IOException


class DepositFragment : BaseMainHomeFragment(), AdapterView.OnItemSelectedListener {

    var vmDeposit : DepositViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deposit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle("Deposit", false)

        if(this.vmDeposit != null && this.vmDeposit!!.modelConsumer != null)
            txtBtnSignatureConsumer.text = "Electronic Signature of " + this.vmDeposit?.modelConsumer?.szName
        else
            txtBtnSignatureConsumer.text = "Electronic Signature of Fred Jones"

        btnSignatureConsumer.setOnClickListener {
            hideKeyboard()
            showSignatureActivity(true)
        }
        btnSignatureCaregiver.setOnClickListener {
            hideKeyboard()
            showSignatureActivity(false)
        }

        edtAmount.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amount: Double = UtilsString.parseDouble(edtAmount.text.toString(), 0.0)
                txtTotalAmount.setText(String.format("$%.02f", amount))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        refreshFields()
    }

    override fun viewAppeared() {
        super.viewAppeared()
    }

    fun refreshFields() {
        if(this.vmDeposit == null) {
            this.vmDeposit = DepositViewModel()
            this.vmDeposit!!.date = Date()
        }

        // Current Date
        edtDate.setText(UtilsDate.getStringFromDateTimeWithFormat(this.vmDeposit!!.date, EnumDateTimeFormat.EEEEMMMMdyyyy.value, null))

        // Consumer
        val arrayConsumers = ConsumerManager.sharedInstance().arrayConsumers
        val arrayConsumerNames: ArrayList<String> = ArrayList()
        arrayConsumerNames.add(getString(R.string.select_consumer))

        for(consumer in arrayConsumers) {
            val name = consumer.szName
            arrayConsumerNames.add(name)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayConsumerNames)
        edtConsumer.setAdapter(adapter)
        edtConsumer.setOnItemSelectedListener(this)

        var indexConsumer = -1
        if(this.vmDeposit!!.modelConsumer != null) {
            // pre-select the consumer
            var i = 0
            for(c in arrayConsumers) {
                if(this.vmDeposit!!.modelConsumer!!.id == c.id) {
                    indexConsumer = i
                    break
                }
                i += 1
            }
        }
        if(indexConsumer != -1)
            edtConsumer.setSelection(indexConsumer + 1)
        else
            edtConsumer.setSelection(0)

        refreshSignatureButtons()

        setupImageGripAdapter()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position == 0) return

        refreshAccountList(position - 1)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun refreshAccountList(indexConsumer: Int) {

        if(indexConsumer == -1) {
            val array: ArrayList<String> = ArrayList()
            array.add(getString(R.string.select_account))
            val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, array)
            edtAccount.adapter = adapter
            return
        }

        val consumer = ConsumerManager.sharedInstance().arrayConsumers[indexConsumer]

        showProgressHUD()
        FinancialAccountManager.sharedInstance().requestGetAccountsForConsumer(consumer, false, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        if(consumer.arrayAccounts != null) {
                            val arrayAccountName: ArrayList<String> = ArrayList()
                            arrayAccountName.add(getString(R.string.select_account))
                            for(account in consumer.arrayAccounts!!) {
                                val name = account.szName
                                arrayAccountName.add(name)
                            }

                            val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayAccountName)
                            edtAccount.adapter = adapter

                            // pre-select the account in callback (to run after accounts-list is retrieved from server)
                            var indexAccount = -1
                            if(vmDeposit!!.modelConsumer != null && vmDeposit!!.modelAccount != null && vmDeposit!!.modelConsumer!!.arrayAccounts != null) {
                                var i = 0
                                for(a in vmDeposit!!.modelConsumer!!.arrayAccounts!!) {
                                    if(vmDeposit!!.modelAccount!!.id.equals(a.id)) {
                                        indexAccount = i
                                        break
                                    }
                                    i += 1
                                }
                            }
                            if(indexAccount != -1)
                                edtAccount.setSelection(indexAccount + 1)
                            else {
                                edtAccount.setSelection(0)
                            }
                        }
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                        val array: ArrayList<String> = ArrayList()
                        array.add(getString(R.string.select_account))
                        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, array)
                        edtAccount.adapter = adapter
                    }
                }
            }
        })
    }

    private fun refreshSignatureButtons() {
        if(this.vmDeposit == null) return

        if(this.vmDeposit!!.imageConsumerSignature != null) {
            btnSignatureConsumer.background = ContextCompat.getDrawable(context!!, R.drawable.background_signatured_button)
        } else {
            btnSignatureConsumer.background = ContextCompat.getDrawable(context!!, R.drawable.background_unsignature_button)
        }

        if(this.vmDeposit!!.imageCaregiverSignature != null) {
            btnSignatureCaregiver.background = ContextCompat.getDrawable(context!!, R.drawable.background_signatured_button)
        } else {
            btnSignatureCaregiver.background = ContextCompat.getDrawable(context!!, R.drawable.background_unsignature_button)
        }

    }

    private fun setupImageGripAdapter() {
        val arrayGrid: ArrayList<GridItem> = ArrayList()
        for(i in this.vmDeposit!!.arrayPhotos) {
            val item = GridItem()
            item.imgFile = i
            item.type = EnumGripRowType.ROW_IMAGE
            arrayGrid.add(item)
        }
        val newImageItem = GridItem()
        newImageItem.imgFile = null
        newImageItem.type = EnumGripRowType.ROW_ADD_IMAGE
        arrayGrid.add(newImageItem)

        val gridImageAdapter = GridImageAdapter(activity, arrayGrid)
        gridImageAdapter.itemClickListener = object: RowItemClickListener<GridItem> {
            override fun onClickedRowItem(obj: GridItem, position: Int) {
                if(obj.type == EnumGripRowType.ROW_IMAGE) {
                    showDeleteDialog()
                } else {
                    showTakePhotoDialog()
                }
            }
        }

        gridView.apply {
            adapter = gridImageAdapter
//            setNestedScrollingEnabled(false)
        }

    }

    private fun showDeleteDialog() {
        val builder = BottomSheet.Builder(activity!!)
        builder.addItem(0, R.string.button_remove)
        builder.addItem(1, R.string.button_cancel)
        builder.setOnItemClickListener { parent, view, position, id ->
            if(position == 0) {
                removePhotoAtIndex(position)
            } else if(position == 1) {

            }
        }
        val bottomSheet = builder.create()
        bottomSheet.show()
    }

    private fun removePhotoAtIndex(index: Int) {
        if(this.vmDeposit == null) return

        if(index < this.vmDeposit!!.arrayPhotos.size) {
            this.vmDeposit!!.arrayPhotos.removeAt(index)
        }

        GlobalScope.launch(Dispatchers.Main) {
            setupImageGripAdapter()
        }
    }

    private fun showTakePhotoDialog() {
        val builder = BottomSheet.Builder(activity!!)
        builder.setTitle("Choose One")
        builder.addItem(0, R.string.button_use_camera)
        builder.addItem(1, R.string.button_use_photo)
        builder.setOnItemClickListener { parent, view, position, id ->
            if(position == 0) {
                takePhotoFromCamera()
            } else if(position == 1) {
                choosePhotoFromGallary()
            }
        }
        val bottomSheet = builder.create()
        bottomSheet.show()
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

    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 100
    private val GALLERY_CODE = 1001
    private val CAMERA_CODE = 1002

    private fun setupPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), READ_EXTERNAL_STORAGE_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }

    // Business Login
    private fun validateFields(): Boolean {
        if(this.vmDeposit == null) return false

        // Consumer
        if(edtConsumer.selectedItemPosition <= 0) {
            showToast("Please select consumer.")
            return false
        }
        val indexConsumer = max(edtConsumer.selectedItemPosition, 1)
        val consumer = ConsumerManager.sharedInstance().arrayConsumers[indexConsumer - 1]

        // Account
        if(edtAccount.selectedItemPosition < 0) {
            showToast("Please select account.")
            return false
        }
        val indexAccount = max(edtAccount.selectedItemPosition, 1)
        val account = consumer.arrayAccounts!![indexAccount - 1]

        // Amount
        val amount = UtilsString.parseDouble(edtAmount.text.toString(), 0.0)

        // Description
        val desc = edtDescription.text.toString()

        // Photos & Signatures ---? Already set
        if(this.vmDeposit!!.arrayPhotos.size == 0) {
            showToast("Please add photos.")
            return false
        }

        /*
        if(this.vmDeposit!!.hasConsumerSigned() == false) {
            showToast("Please ask consumer to sign.")
            return false
        }

        if(this.vmDeposit!!.hasCaregiverSigned() == false) {
            showToast("Please sign.")
            return false
        }*/

        this.vmDeposit!!.modelConsumer = consumer
        this.vmDeposit!!.modelAccount = account
        this.vmDeposit!!.fAmount = amount
        this.vmDeposit!!.szDescription = desc

        return true
    }

    private fun prepareDeposit() {
        if(this.validateFields() == false) return

        showProgressHUD()
        this.vmDeposit!!.toDataModel { transaction, message ->
            GlobalScope.launch(Dispatchers.Main) {
                hideProgressHUD()
                if(transaction == null) {
                    showToast(message)
                } else {
                    requestDeposit(transaction)
                }
            }
        }
    }

    private fun requestDeposit(transaction: TransactionDataModel) {
        showProgressHUD()
        TransactionManager.sharedInstance().requestDeposit(transaction, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        requestFinancialAccount(transaction.modelConsumer!!)
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun requestFinancialAccount(consumer: ConsumerDataModel) {
        showProgressHUD()
        FinancialAccountManager.sharedInstance().requestGetAccountsForConsumer(consumer, true, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        gotoBack()
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun gotoBack() {
        mainFragment.onBackPressed()
    }

    private fun showSignatureActivity(forUser: Boolean) {
        val requestCode: Int = if (forUser) SignatureActivity.consumerSignatureRequestCode else SignatureActivity.caregiverSignatureRequestCode

        val intent = Intent(activity, SignatureActivity::class.java)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return

        when (requestCode) {
            SignatureActivity.consumerSignatureRequestCode -> {
                val bitmapByte = data.getByteArrayExtra("signature")
                val signBitMap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.size)
                val file = UtilsBaseFunction.saveImageToInternalStorage(context!!, signBitMap, null)
                this.vmDeposit!!.imageConsumerSignature = file
                refreshSignatureButtons()
            }
            SignatureActivity.caregiverSignatureRequestCode -> {
                val bitmapByte = data.getByteArrayExtra("signature")
                val signBitMap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.size)
                val file = UtilsBaseFunction.saveImageToInternalStorage(context!!, signBitMap, null)
                this.vmDeposit!!.imageCaregiverSignature = file
                refreshSignatureButtons()
            }
            GALLERY_CODE -> {

                val contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                    val fileImage = UtilsBaseFunction.saveImageToInternalStorage(context!!, bitmap, contentURI)
                    this.vmDeposit!!.arrayPhotos.add(fileImage)
                    GlobalScope.launch(Dispatchers.Main) {
                        setupImageGripAdapter()
                    }
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Failed!")
                }
            }
            CAMERA_CODE -> {

                val thumbnail = data.extras!!.get("data") as Bitmap
                val fileImage = UtilsBaseFunction.saveImageToInternalStorage(context!!, thumbnail, null)
                this.vmDeposit!!.arrayPhotos.add(fileImage)
                GlobalScope.launch(Dispatchers.Main) {
                    setupImageGripAdapter()
                }
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
            showBackButton()
        } else {
            setHasOptionsMenu(false)
            hideBackButton()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemNext = menu.findItem(R.id.next)
        itemNext.isVisible = false
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.save) {
            hideKeyboard()
            prepareDeposit()
            return true
        } else if(item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(deposit: DepositViewModel?) =
            DepositFragment().apply {
                arguments = Bundle().apply {
                    vmDeposit = deposit
                }
            }
    }
}
