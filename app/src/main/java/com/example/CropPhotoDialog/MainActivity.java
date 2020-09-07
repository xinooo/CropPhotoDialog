package com.example.CropPhotoDialog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.CropPhotoLibrary.CropPhotoDialog;
import com.example.CropPhotoLibrary.ImageTools;

import java.util.ArrayList;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private ImageView icon,icon2;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        icon = (ImageView)findViewById(R.id.icon);
        icon2 = (ImageView)findViewById(R.id.icon2);
        icon2.setImageResource(R.mipmap.photo);
        showPermission();

        //從相冊獲取圖片裁切
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, null);
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(i, 0);
            }
        });

        //獲取圖片Bitmap裁切
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropPhotoDialog cropPhotoDialog = new CropPhotoDialog(MainActivity.this, ((BitmapDrawable)icon2.getDrawable()).getBitmap(), new CropPhotoDialog.OnCropDialogListener() {
                    @Override
                    public void onCancel(CropPhotoDialog dialog) {

                    }

                    @Override
                    public void onOk(CropPhotoDialog dialog, String path) {
                        bitmap = BitmapFactory.decodeFile(path);
                        icon2.setImageBitmap(bitmap);
                        if(bitmap != null && !bitmap.isRecycled()){
                            bitmap.recycle();
                            bitmap = null;
                        }
                        System.gc();
                    }
                });
                cropPhotoDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == 0){
                try {
                    final Uri imageUri = data.getData();
                    Log.i("imageUri:",imageUri+"");
                    String selectPhoto = ImageTools.getRealPathFromUri(this,imageUri);
                    Log.i("selectPhoto:",selectPhoto);
                    CropPhotoDialog cropPhotoDialog = new CropPhotoDialog(MainActivity.this, selectPhoto, new CropPhotoDialog.OnCropDialogListener() {
                        @Override
                        public void onCancel(CropPhotoDialog dialog) {

                        }

                        @Override
                        public void onOk(CropPhotoDialog dialog, String path) {
                            bitmap = BitmapFactory.decodeFile(path);
                            icon.setImageBitmap(bitmap);
                            if(bitmap != null && !bitmap.isRecycled()){
                                bitmap.recycle();
                                bitmap = null;
                            }
                            System.gc();
                        }
                    });
                    cropPhotoDialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void showPermission() {
        List<String> permissions = new ArrayList<String>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可授權
            } else {
                // 沒有權限
                Toast.makeText(this, "未授權應用使用權限!", Toast.LENGTH_LONG).show();
                showPermission();
            }
        }
    }
}
