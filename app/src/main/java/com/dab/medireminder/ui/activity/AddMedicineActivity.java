package com.dab.medireminder.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.Medicine;
import com.dab.medireminder.ui.fragment.DialogGalleryFragment;
import com.dab.medireminder.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dab.medireminder.constant.Constants.REQUEST_PICK_IMAGE_CAMERA;
import static com.dab.medireminder.constant.Constants.REQUEST_PICK_IMAGE_GALLERY;
import static com.dab.medireminder.ui.activity.MedicineActivity.RESULT_ADD_MEDICINE;

@SuppressLint("NonConstantResourceId")
public class AddMedicineActivity extends BaseActivity implements DialogGalleryFragment.GalleryListener {


    @BindView(R.id.edt_name)
    EditText edtName;

    @BindView(R.id.edit_dose)
    EditText edtDose;

    @BindView(R.id.rl_main)
    View rlMain;

    @BindView(R.id.iv_image)
    ImageView ivIcon;

    @BindView(R.id.btn_image)
    CardView btnImage;

    private DBApp dbApp;
    private Medicine medicine;
    private String imagePath;
    private String actionCurrent;
    private DialogGalleryFragment dialogGalleryFragment;

    @Override
    public int getLayout() {
        return R.layout.activity_add_medicine;
    }

    @Override
    public void initView() {
        hideKeyboard(rlMain);
    }

    @Override
    public void setEvents() {

    }

    @Override
    public void setData() {
        dbApp = new DBApp(this);
        dialogGalleryFragment = new DialogGalleryFragment(this);
    }

    private void addMedicine() {
        String name = edtName.getText().toString();
        String dose = edtDose.getText().toString();

        if (TextUtils.isEmpty(name)) {
            AppUtils.toast(this, "Need input name medicine !");
            return;
        }

        if (TextUtils.isEmpty(dose)) {
            AppUtils.toast(this, "Need input dose !");
            return;
        }

        if (medicine == null) {
            medicine = new Medicine();
        }
        medicine.setName(name);
        medicine.setDose(dose);
        medicine.setImage(imagePath);

        if (!dbApp.addMedicine(medicine)) {
            AppUtils.toast(this, "Oh ! I can't add Medicine !");
            edtName.requestFocus();
        } else {
            AppUtils.toast(this, "Success add new medicine!");
            setResult(RESULT_ADD_MEDICINE);
            finish();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage(String action) {
        if (action.equals(DialogGalleryFragment.GALLERY)) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE_GALLERY);
        } else if (action.equals(DialogGalleryFragment.CAMERA)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    File pictureFile = getPictureFile();
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.dab.medireminder.provider",
                            pictureFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, REQUEST_PICK_IMAGE_CAMERA);
                } catch (IOException ex) {
                    AppUtils.toast(this,
                            "Can't create image");
                }
            } else {
                AppUtils.toast(this,
                        "Can't open your camera.");
            }
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_image, R.id.btn_new})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                setResult(RESULT_ADD_MEDICINE);
                finish();
                break;
            case R.id.btn_image:
                if (dialogGalleryFragment != null && !dialogGalleryFragment.isShow) {
                    dialogGalleryFragment.show(getSupportFragmentManager(), dialogGalleryFragment.getTag());
                }
                break;
            case R.id.btn_new:
                addMedicine();
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void hideKeyboard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                AppUtils.hideSoftKeyboard(v, AddMedicineActivity.this);
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(innerView);
            }
        }
    }

    @Override
    public void onChooseAction(String action) {
        actionCurrent = action;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,};
            if (AppUtils.hasPermissions(this, PERMISSIONS)) {
                selectImage(action);
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
        } else {
            selectImage(action);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_PICK_IMAGE_GALLERY && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imagePath = cursor.getString(columnIndex);
                            Glide.with(this).load(imagePath).into(ivIcon);
                            cursor.close();
                        }
                    }
                } else if (requestCode == REQUEST_PICK_IMAGE_CAMERA) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        ivIcon.setImageURI(Uri.fromFile(imgFile));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("AddMedicineActivity", "onActivityResult: " + e);
        }
    }

    private File getPictureFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("IMG_MEDICINE_".concat(timeStamp), ".jpg", storageDir);
        imagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(actionCurrent);
            } else {
                AppUtils.toast(this, "Need permission to access your camera and photo library");
            }
        }
    }
}
