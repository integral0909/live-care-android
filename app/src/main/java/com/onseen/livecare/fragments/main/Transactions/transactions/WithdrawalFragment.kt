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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.onseen.livecare.R
import com.onseen.livecare.activities.SignatureActivity
import com.onseen.livecare.adapters.EnumGripRowType
import com.onseen.livecare.adapters.GridImageAdapter
import com.onseen.livecare.adapters.GridItem
import com.onseen.livecare.fragments.main.Transactions.ViewModel.WithdrawalViewModel
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.interfaces.RowItemClickListener
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
import de.mrapp.android.bottomsheet.BottomSheet
import kotlinx.android.synthetic.main.fragment_withdrawal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.math.max


class WithdrawalFragment : BaseMainHomeFragment(), AdapterView.OnItemSelectedListener {

    var vmWithdrawal: WithdrawalViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_withdrawal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavTitle("Withdrawal", false)

        btnSignatureConsumer.setOnClickListener {
            hideKeyboard()
            showSignatureActivity(true)
        }
        btnSignatureCaregiver.setOnClickListener {
            hideKeyboard()
            showSignatureActivity(false)
        }

        refreshFields()
    }

    override fun viewAppeared() {
        super.viewAppeared()
    }

    private fun refreshFields() {
        if(this.vmWithdrawal == null) {
            this.vmWithdrawal = WithdrawalViewModel()
            this.vmWithdrawal!!.date = Date()
        }

        // Current Date
        edtDate.setText(UtilsDate.getStringFromDateTimeWithFormat(this.vmWithdrawal!!.date, EnumDateTimeFormat.EEEEMMMMdyyyy.value, null))

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
        if(this.vmWithdrawal!!.getModelConsumer() != null) {
            // pre-select the consumer
            var i = 0
            for(c in arrayConsumers) {
                if(this.vmWithdrawal!!.getModelConsumer()!!.id == c.id) {
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

        switchSpending.setOnCheckedChangeListener { buttonView, isChecked ->
            hideKeyboard()
            refreshDiscretionarySpendingSections()
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

        setupImageGripAdapter()
    }

    private fun refreshDiscretionarySpendingSections() {
        if(switchSpending.isChecked) {
//            gridView.visibility = VISIBLE
            btnSignatureConsumer.visibility = VISIBLE
            btnSignatureCaregiver.visibility = VISIBLE
        } else {
//            gridView.visibility = GONE
            btnSignatureConsumer.visibility = GONE
            btnSignatureCaregiver.visibility = GONE
        }
    }

    private fun setupImageGripAdapter() {
        val arrayGrid: ArrayList<GridItem> = ArrayList()
        for(i in this.vmWithdrawal!!.arrayPhotos) {
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
        gridView.adapter = gridImageAdapter
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
        if(this.vmWithdrawal == null) return

        if(index < this.vmWithdrawal!!.arrayPhotos.size) {
            this.vmWithdrawal!!.arrayPhotos.removeAt(index)
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
        if(this.vmWithdrawal == null) return false

        // Consumer
        if(edtConsumer.selectedItemPosition <= 0) {
            showToast("Please select consumer.")
            return false
        }
        val indexConsumer = max(edtConsumer.selectedItemPosition, 1)
        val consumer = ConsumerManager.sharedInstance().arrayConsumers[indexConsumer - 1]

        // Amount
        val amount = UtilsString.parseDouble(edtAmount.text.toString(), 0.0)

        // Description
        val desc = edtDescription.text.toString()

        // Photos & Signatures ---? Already set

        if(this.vmWithdrawal!!.arrayPhotos.size == 0) {
            showToast("Please add photos.")
            return false
        }

        if(switchSpending.isChecked) {

            if(this.vmWithdrawal!!.hasConsumerSigned() == false) {
                showToast("Please ask consumer to sign.")
                return false
            }

            if(this.vmWithdrawal!!.hasCaregiverSigned() == false) {
                showToast("Please sign.")
                return false
            }
        }

        this.vmWithdrawal!!.setModelConsumer(consumer)
        this.vmWithdrawal!!.fAmount = amount
        this.vmWithdrawal!!.szDescription = desc
        this.vmWithdrawal!!.isDiscretionarySpending = switchSpending.isChecked

        return true
    }

    private fun prepareWithdrawal() {
        if(!validateFields()) return

        showProgressHUD()
        this.vmWithdrawal!!.toDataModel { transaction, message ->
            hideProgressHUD()
            if(transaction == null)
                showToast(message)
            else
                requestWithdrawal(transaction)
        }
    }

    private fun requestWithdrawal(transaction: TransactionDataModel) {
        showProgressHUD()
        TransactionManager.sharedInstance().requestWithdrawal(transaction, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    var exceedsMaxSpend = false
                    val newTransaction = responseDataModel.parsedObject as? TransactionDataModel
                    if (newTransaction != null) {
                        exceedsMaxSpend = newTransaction.isExceedsMaxSpendForPeriod
                    }

                    TransactionManager.sharedInstance().requestGetTransactions(object: NetworkManagerResponse {
                        override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                            requestFinancialAccounts(transaction.modelConsumer!!) { success ->
                                GlobalScope.launch(Dispatchers.Main) {
                                    hideProgressHUD()
                                    if(success) gotoBack(exceedsMaxSpend)
                                }
                            }
                        }
                    })
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        hideProgressHUD()
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun requestFinancialAccounts(consumer: ConsumerDataModel, callback: ((success: Boolean) -> Unit)) {
        showProgressHUD()
        FinancialAccountManager.sharedInstance().requestGetAccountsForConsumer(consumer, true, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        callback(true)
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                        callback(false)
                    }
                }
            }
        })
    }

    private fun showSignatureActivity(forUser: Boolean) {
        val requestCode: Int = if (forUser) SignatureActivity.consumerSignatureRequestCode else SignatureActivity.caregiverSignatureRequestCode

        val intent = Intent(activity, SignatureActivity::class.java)
        startActivityForResult(intent, requestCode)
    }

    private fun gotoBack(exceedsMaxSpend: Boolean) {
        if (exceedsMaxSpend) {
            UtilsBaseFunction.showAlert(activity!!, "Warning", "You exceed max spending limitation") {
                mainFragment.onBackPressed()
            }
        } else {
            mainFragment.onBackPressed()
        }
    }
    
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position == 0) return

        val consumer = ConsumerManager.sharedInstance().arrayConsumers[position - 1]
        this.vmWithdrawal!!.setModelConsumer(consumer)
        requestFinancialAccounts(consumer) { success ->

        }
        this.refreshSignatureButtons()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun refreshSignatureButtons() {
        if(this.vmWithdrawal == null) return

        if(this.vmWithdrawal!!.imageConsumerSignature != null) {
            btnSignatureConsumer.background = ContextCompat.getDrawable(context!!, R.drawable.background_signatured_button)
        } else {
            btnSignatureConsumer.background = ContextCompat.getDrawable(context!!, R.drawable.background_unsignature_button)
        }

        if(this.vmWithdrawal!!.imageCaregiverSignature != null) {
            btnSignatureCaregiver.background = ContextCompat.getDrawable(context!!, R.drawable.background_signatured_button)
        } else {
            btnSignatureCaregiver.background = ContextCompat.getDrawable(context!!, R.drawable.background_unsignature_button)
        }

        if(this.vmWithdrawal != null && this.vmWithdrawal!!.getModelConsumer()!= null) {
            txtBtnSignatureConsumer.text = "Electronic Signature of " + this.vmWithdrawal?.getModelConsumer()?.szName
        } else {
            txtBtnSignatureConsumer.text = "Electronic Signature of Consumer"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data == null) return

        when (requestCode) {
            SignatureActivity.consumerSignatureRequestCode -> {
                val bitmapByte = data.getByteArrayExtra("signature")
                val signBitMap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.size)
                val file = UtilsBaseFunction.saveImageToInternalStorage(context!!, signBitMap, null)
                this.vmWithdrawal!!.imageConsumerSignature = file
                refreshSignatureButtons()
            }
            SignatureActivity.caregiverSignatureRequestCode -> {
                val bitmapByte = data.getByteArrayExtra("signature")
                val signBitMap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.size)
                val file = UtilsBaseFunction.saveImageToInternalStorage(context!!, signBitMap, null)
                this.vmWithdrawal!!.imageCaregiverSignature = file
                refreshSignatureButtons()
            }
            GALLERY_CODE -> {

                val contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                    val fileImage = UtilsBaseFunction.saveImageToInternalStorage(context!!, bitmap, contentURI)
                    this.vmWithdrawal!!.arrayPhotos.add(fileImage)
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
                this.vmWithdrawal!!.arrayPhotos.add(fileImage)
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
            prepareWithdrawal()
            return true
        } else if (item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(withdrawal: WithdrawalViewModel?) =
            WithdrawalFragment().apply {
                arguments = Bundle().apply {
                    vmWithdrawal = withdrawal
                }
            }
    }
}
