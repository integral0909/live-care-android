package com.onseen.livecare.fragments.main.Transactions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE

import com.onseen.livecare.R
import com.onseen.livecare.adapters.EnumGripRowType
import com.onseen.livecare.adapters.GridImageAdapter
import com.onseen.livecare.adapters.GridItem
import com.onseen.livecare.adapters.PurchaseReceiptsAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Transactions.ViewModel.PurchaseViewModel
import com.onseen.livecare.fragments.main.Transactions.ViewModel.TransactionDetailsViewModel
import com.onseen.livecare.interfaces.PurchaseReceiptsListener
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.Transaction.TransactionManager
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import de.mrapp.android.bottomsheet.BottomSheet
import kotlinx.android.synthetic.main.fragment_purchase.*
import kotlinx.android.synthetic.main.fragment_rider_recurring_trip.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class PurchaseFragment : BaseMainHomeFragment(), PurchaseReceiptsListener {

    private var vmPurchase: PurchaseViewModel? = null
    private var arrayPhotos: ArrayList<File> = ArrayList()

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var receiptsAdapter: PurchaseReceiptsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_purchase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle("Purchase", false)
        refreshFields()

        switchSpending.setOnCheckedChangeListener { buttonView, isChecked ->
            hideKeyboard()

            if(this.vmPurchase != null) {
                this.vmPurchase!!.hasReceiptPhotos = isChecked
                refreshReceiptsPanel()
            }
        }

        btnAdd.setOnClickListener {
            hideKeyboard()

            if(this.vmPurchase != null) {
                this.vmPurchase!!.arrayTransactionDetails.add(TransactionDetailsViewModel())
                refreshListView()
            }
        }
    }

    override fun viewAppeared() {

    }

    private fun refreshFields() {
        if(this.vmPurchase == null) {
            this.vmPurchase = PurchaseViewModel()
            this.vmPurchase!!.date = Date()
        }

        edtDate.setText(UtilsDate.getStringFromDateTimeWithFormat(this.vmPurchase!!.date, EnumDateTimeFormat.EEEEMMMMdyyyy.value, null))

        setupImageGripAdapter()

        // refresh view
        setupRecyclerView()
    }

    private fun setupImageGripAdapter() {
        val arrayGrid: ArrayList<GridItem> = ArrayList()
        for(i in arrayPhotos) {
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
        if(this.vmPurchase == null) return

        if(index < this.vmPurchase!!.arrayReceiptPhotos.size) {
            this.vmPurchase!!.arrayReceiptPhotos.removeAt(index)
            this.arrayPhotos.removeAt(index)
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

    // Refresh Methods

    fun refreshReceiptsPanel() {
        if(this.vmPurchase == null) return

        if(this.vmPurchase!!.hasReceiptPhotos) {
            gridView.visibility = VISIBLE
        } else {
            gridView.visibility = GONE
        }
    }

    fun refreshTotalAmount() {
        if(this.vmPurchase == null) return

        var total = 0.0
        for(transaction in this.vmPurchase!!.arrayTransactionDetails) {
            total = total + transaction.fAmount
        }
        this.txtTotalAmount.setText(String.format("$%.02f", total))
    }

    // MARK: Business Logic
    private fun validateFields(): Boolean {
        if(this.vmPurchase == null) return false

        val merchant = edtMerchant.text.toString()
        val desc = edtDescription.text.toString()
        val hasReceiptPhotos = switchSpending.isChecked

        if(merchant.isEmpty()) {
            showToast("Please enter merchant name.")
            return false
        }

        if(desc.isEmpty()) {
            showToast("Please enter description.")
            return false
        }

        if(hasReceiptPhotos) {
            if(arrayPhotos.size == 0) {
                showToast("Please add receipt photos.")
                return false
            }
        }

        this.vmPurchase!!.szMerchant = merchant
        this.vmPurchase!!.szDescription = desc
        this.vmPurchase!!.hasReceiptPhotos = hasReceiptPhotos
        this.vmPurchase!!.arrayReceiptPhotos.clear()
        this.vmPurchase!!.arrayReceiptPhotos.addAll(arrayPhotos)

        if(this.vmPurchase!!.arrayTransactionDetails.size == 0) {
            showToast("Please add at least one transaction.")
            return false
        }

        for(transaction in this.vmPurchase!!.arrayTransactionDetails) {
            if(transaction.modelConsumer == null) {
                showToast("Please select consumers.")
                return false
            }
            if(transaction.getModelAccount() == null) {
                showToast("Please select accounts.")
                return false
            }
            if(!transaction.hasValidAmount()) {
                showToast("Please enter amounts.")
                return false
            }
        }

        return true
    }

    private fun requestReloadTransactions() {
        // Reload Transactions

        showProgressHUD()
        TransactionManager.sharedInstance().requestGetTransactions(object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    checkAvailableAmount()
                }
            }
        })
    }

    private fun checkAvailableAmount() {
        val vmPurchase = this.vmPurchase ?: return

        for(transaction in vmPurchase.arrayTransactionDetails) {
            val account = transaction.getModelAccount() ?: continue
            if (account.enumType == EnumFinancialAccountType.CASH) {
                // For Cash, we check against pending amount. To make purchase with CASH, user needs to withdraw first.
                val amountPending = transaction.getPendingAmount()
                if (amountPending < transaction.fAmount) {
                    showToast("No sufficient pending amount is available for " + account.szName + ".")
                    return
                }
            } else {
                // For Food Stamp or Gift Cards, we check balance
                val amountBalance = account.fBalance
                if (transaction.fAmount > amountBalance) {
                    showToast("No sufficient amount is available for " + account.szName + ".")
                    return
                }

            }
        }

        if(vmPurchase.hasReceiptPhotos) {
            val cashTransactions: ArrayList<TransactionDetailsViewModel> = vmPurchase.getTransactionDetailsForCashAccount()
            if(cashTransactions.size > 0) {
                gotoDepositRemainingFragment()
            } else {
                gotoSummaryFragment()
            }
        } else {
            gotoReceiptDetailsFragment()
        }
    }

    private fun gotoSummaryFragment() {
        mainFragment.addFragment(PurchaseSummaryFragment.newInstance(this.vmPurchase))
    }

    // MARK Navigations

    private fun gotoReceiptDetailsFragment() {
        mainFragment.addFragment(ReceiptDetailsFragment.newInstance(this.vmPurchase))
    }

    private fun gotoDepositRemainingFragment() {
        mainFragment.addFragment(DepositRemainingCashFragment.newInstance(this.vmPurchase))
    }

    private fun refreshListView() {
        if(this.vmPurchase == null || recyclerView.layoutManager == null) return

        setupRecyclerView()
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
        itemNext.isVisible = true
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.next) {
            hideKeyboard()

            if(!validateFields())
                return true
            requestReloadTransactions()
        } else if(item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return true
    }

    private fun setupRecyclerView() {
        receiptsAdapter = PurchaseReceiptsAdapter(activity!!, this.vmPurchase!!.arrayTransactionDetails)
        receiptsAdapter.itemClickListener = this
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
//            setNestedScrollingEnabled(true)
            layoutManager = viewManager
            adapter = receiptsAdapter
        }
    }

    override fun didTransactionDetailsConsumerSelected(indexConsumer: Int, indexRow: Int) {
        if(this.vmPurchase == null) return

        val consumer = ConsumerManager.sharedInstance().arrayConsumers[indexConsumer]
        val transaction = this.vmPurchase!!.arrayTransactionDetails[indexRow]

        transaction.modelConsumer = consumer
    }

    override fun didTransactionDetailsAccountSelected(indexAccount: Int, indexRow: Int) {
        if(this.vmPurchase == null) return

        val transaction = this.vmPurchase!!.arrayTransactionDetails[indexRow]
        if(transaction.modelConsumer != null && transaction.modelConsumer!!.arrayAccounts != null) {
            transaction.setModelAccount(transaction.modelConsumer!!.arrayAccounts!!.get(indexAccount))
        }
    }

    override fun didTransactionDetailsAmountChanged(amount: Double, indexRow: Int) {
        if(this.vmPurchase == null) return

        val transaction = this.vmPurchase!!.arrayTransactionDetails[indexRow]
        transaction.fAmount = amount
        refreshTotalAmount()
    }

    override fun didTransactionDetailsDeleteClick(indexRow: Int) {
        if(this.vmPurchase == null) return

        this.vmPurchase!!.arrayTransactionDetails.removeAt(indexRow)
        setupRecyclerView()
        refreshTotalAmount()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return

        when (requestCode) {
            GALLERY_CODE -> {

                val contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                    val fileImage = UtilsBaseFunction.saveImageToInternalStorage(context!!, bitmap, contentURI)
                    arrayPhotos.add(fileImage)
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
                arrayPhotos.add(fileImage)
                GlobalScope.launch(Dispatchers.Main) {
                    setupImageGripAdapter()
                }
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(purchaseModel: PurchaseViewModel?) =
            PurchaseFragment().apply {
                arguments = Bundle().apply {
                    vmPurchase = purchaseModel
                }
            }
    }
}
