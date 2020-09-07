package com.example.CropPhotoLibrary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class CropPhotoDialog implements View.OnClickListener {

    public interface OnCropDialogListener {
        void onCancel(CropPhotoDialog dialog);

        void onOk(CropPhotoDialog dialog, String path);
    }

    private final Activity mactivity;
    private Dialog dialog;
    private ViewGroup root;
    private String mSelectPhotoPath;
    private Bitmap mSelectBitmap;
    private boolean isPath,close = false;
    private ZoomImageView ziv;
    private TextView tvConfirm;
    private TextView tvCancel;
    private OnCropDialogListener onCropDialogListener;

    public CropPhotoDialog(Activity mactivity, Bitmap bitmap, OnCropDialogListener listener){
        this(mactivity, "", bitmap,false, listener);
    }

    public CropPhotoDialog(Activity mactivity,  String selectPhotoPath, OnCropDialogListener listener){
        this(mactivity, selectPhotoPath, null,true, listener);
    }

    public CropPhotoDialog(Activity mactivity, String selectPhotoPath,Bitmap bitmap,boolean isPath, OnCropDialogListener listener) {
        this.mactivity = mactivity;
        this.mSelectPhotoPath = selectPhotoPath;
        this.onCropDialogListener = listener;
        this.mSelectBitmap = bitmap;
        this.isPath = isPath;

        dialog = new Dialog(mactivity, R.style.dialog_no_title);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ViewGroup.LayoutParams contentParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(initView(), contentParam);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = mactivity.getResources().getDisplayMetrics().widthPixels;
            window.setAttributes(params);
            window.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        }
    }

    private View initView(){
        root = (ViewGroup) View.inflate(mactivity, R.layout.activity_crop, null);
        ziv = (ZoomImageView)root.findViewById(R.id.crop_ziv);
        tvConfirm = (TextView)root.findViewById(R.id.crop_confirm);
        tvCancel = (TextView)root.findViewById(R.id.crop_cancel);

        return root;
    }

    private void initViewData(){
        Bitmap bitmap;
        if (isPath){
            if (TextUtils.isEmpty(mSelectPhotoPath)) {
                dialog.dismiss();
                Toast.makeText(mactivity, "Path is null", Toast.LENGTH_LONG).show();
                return;
            }
            bitmap = BitmapFactory.decodeFile(mSelectPhotoPath);
            if (bitmap == null) {
                dialog.dismiss();
                Toast.makeText(mactivity, "Bitmap is null", Toast.LENGTH_LONG).show();
                return;
            }
        }else {
            bitmap = mSelectBitmap;
            if (bitmap == null) {
                dialog.dismiss();
                Toast.makeText(mactivity, "Bitmap is null", Toast.LENGTH_LONG).show();
                return;
            }
        }

        int degree = ImageTools.getBitmapOritation(mSelectPhotoPath);
        if (degree > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        ziv.setImageBitmap(bitmap);

        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    public void show() {
        dialog.show();
        initViewData();
    }

    @Override
    public void onClick(View view) {
        if (view == tvConfirm){
            Bitmap bitmap = ziv.getCropBitmap();
            String newPhotoPath = ImageTools.setPhotoPath();
            ImageTools.SaveImage(bitmap, newPhotoPath);
            CropPhotoDialog.this.onCropDialogListener.onOk(CropPhotoDialog.this,newPhotoPath);
            dialog.dismiss();
        }
        if (view == tvCancel){
            CropPhotoDialog.this.onCropDialogListener.onCancel(CropPhotoDialog.this);
            dialog.dismiss();
        }
    }
}
