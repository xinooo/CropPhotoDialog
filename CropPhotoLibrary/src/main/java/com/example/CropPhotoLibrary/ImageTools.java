package com.example.CropPhotoLibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ImageTools {
    public static String setPhotoPath() {
        //儲存路徑
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/CropPhoto/");
        if(!file.exists()){
            file.mkdirs();
        }
        String dir = file.toString();
        //檔名(以時間命名)
        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());
        return dir + "/" + fileName + ".png";
    }

    //儲存圖片
    public static void SaveImage(Bitmap bitmap, String path) {
        //獲取內部儲存狀態
        String state = Environment.getExternalStorageState();
        //如果狀態不是mounted，無法讀寫
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return;
        }
        FileOutputStream out = null;
        File file = new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException ignore) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public static int getBitmapOritation(String path) {
        int degree = 0;
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException ignore) {
        }
        return degree;
    }

    /**
     * 根據圖片的Uri獲取圖片的絕對路徑。 @uri 圖片的uri
     * @return 如果Uri對應的圖片存在,那麼返回該圖片的絕對路徑,否則返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context, uri);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context, uri);
        }
        // SDK > 19
        return getRealPathFromUri_AboveApi19(context, uri);//沒用到
    }

    //針對圖片URI格式為Uri:: file:///storage/emulated/0/DCIM/Camera/IMG_20170613_132837.jpg
    private static String getRealPathFromUri_Byfile(Context context,Uri uri){
        String uri2Str = uri.toString();
        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);
        return filePath;
    }

    /**
     * 適配api11-api18,根據uri獲取圖片的絕對路徑。
     * 針對圖片URI格式為Uri:: content://media/external/images/media/1028
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 適配api19以上,根據uri獲取圖片的絕對路徑
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = null;

        wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
