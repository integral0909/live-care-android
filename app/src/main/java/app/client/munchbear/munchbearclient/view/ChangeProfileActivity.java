package app.client.munchbear.munchbearclient.view;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.ChangeProfileActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChangeProfileActivity extends BaseActivity {

    @BindView(R.id.avatarImg) ImageView avatarImg;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.avatarLayout) RelativeLayout avatarLayout;
    @BindView(R.id.rootLayout) RelativeLayout rootLayout;
    @BindView(R.id.editName) TextInputLayout editName;
    @BindView(R.id.editPhone) TextInputLayout editPhone;
    @BindView(R.id.editEmail) TextInputLayout editEmail;

    private ChangeProfileActivityViewModel changeProfileActivityViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UserData userData;
    private boolean isGuest;
    private boolean shortForm = false;

    public static final int CHANGE_PROFILE_REQUEST_CODE = 102;
    public static final String SHORT_FORM = "short.form";

    private final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int SELECT_PICTURE_FROM_CAMERA = 1;
    private static final int SELECT_PICTURE_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        ButterKnife.bind(this);

        changeProfileActivityViewModel = ViewModelProviders.of(this).get(ChangeProfileActivityViewModel.class);
        userData = CorePreferencesImpl.getCorePreferences().getUserData();
        isGuest = CorePreferencesImpl.getCorePreferences().getLoginType() == LoginType.GUEST;
        getDataFromIntent();

        initShortForm();
        initAvatar();
        initUserData();

        initObservers();
    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra(SHORT_FORM)) {
            shortForm = getIntent().getBooleanExtra(SHORT_FORM, false);
        }
    }

    private void initShortForm() {
        if (shortForm) {
            avatarLayout.setVisibility(View.GONE);
        } else {
            avatarLayout.setVisibility(isGuest ? View.GONE : View.VISIBLE);
        }

        toolbarTitle.setText(shortForm ? getResources().getString(R.string.change_profile_toolbar_title_contact)
                : getResources().getString(R.string.change_profile_toolbar_title));
        editEmail.setVisibility(shortForm ? View.GONE : View.VISIBLE);
    }

    private void initObservers() {
        changeProfileActivityViewModel.getSaveUserDataLiveData().observe(this, save -> {
            if (save) {
                finish();
            }
        });
    }

    private void initUserData() {
        if (userData != null) {
            editName.getEditText().setText(shortForm ? getIntent().getStringExtra(UserData.KEY_NAME) : userData.getName());
            editPhone.getEditText().setText(shortForm ? getIntent().getStringExtra(UserData.KEY_PHONE) : userData.getPhone());
            editEmail.getEditText().setText(userData.getEmail());
        }
    }

    private void initAvatar() {
        if (!isGuest) {
            avatarImg.setImageBitmap(UserAvatar.getUserAvatar(this));
        }
    }

    private void initChangeAvatarDialog() {
        final String itemTitle[] = {getString(R.string.change_profile_get_photo_from_camera),
                getString(R.string.change_profile_get_photo_from_library),
                getString(R.string.change_profile_get_photo_remove)};

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.change_profile_get_photo_dialog_title));
        alert.setItems(itemTitle, (dialog, which) -> {
            switch (which) {
                case 0:
                    if (checkPermission(Manifest.permission.CAMERA) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        openCameraIntent();
                    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext() != null){
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
                    }
                    break;
                case 1:
                    openGalleryIntent();
                    break;
                case 2:

                    break;
            }
        });
        alert.create().show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCameraIntent();
            }
        }
    }

    private boolean checkPermission(String perm){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), perm) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    private void openGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PICTURE_FROM_GALLERY);
    }

    private void openCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, SELECT_PICTURE_FROM_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE_FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatarImg.setImageBitmap(imageBitmap);
//            UserAvatar.getInstance().setUserAvatarBitmap(imageBitmap);
            UserAvatar.setUserAvatar(imageBitmap);
        }
        if (requestCode == SELECT_PICTURE_FROM_GALLERY && resultCode == RESULT_OK) {
            Single.fromCallable(() -> getBitmapFromGallery(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            avatarImg.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private Bitmap getBitmapFromGallery(Intent data) {
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            UserAvatar.setUserAvatar(selectedImage);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_somethings_went_wrong), Toast.LENGTH_LONG).show();
        }
        return UserAvatar.getUserAvatar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @OnClick(R.id.avatarLayout)
    public void clickChangeAvatar() {
        initChangeAvatarDialog();
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.saveBtn)
    public void saveData(View view) {
        String name = editName.getEditText().getText().toString().trim();
        String email = editEmail.getEditText().getText().toString().trim();
        String phone = editPhone.getEditText().getText().toString().trim();
        if (shortForm) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(UserData.KEY_NAME, name);
            resultIntent.putExtra(UserData.KEY_PHONE, phone);
            setResult(RESULT_OK, resultIntent);
            hideKeyboardFrom(this, view);
            finish();
        } else {
            changeProfileActivityViewModel.saveUserData(userData, name, email, phone);
        }
    }


}
