# CropPhotoDialog
## Adding it to your project
Step 1. Add the JitPack repository to your build file
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```groovy
dependencies {
    implementation 'com.github.xinooo:CropPhotoDialog:1.0.0'
}
```
## Usage
### Add permissions to manifest
 ```
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 ```
### Crop photo
  ```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == -1) {
        if (requestCode == CROP_PHOTO){
            try {
                final Uri imageUri = data.getData();
                String PhotoPath = ImageTools.getRealPathFromUri(this,imageUri);
                CropPhotoDialog cropPhotoDialog = new CropPhotoDialog(MainActivity.this, PhotoPath, new CropPhotoDialog.OnCropDialogListener() {
                    @Override
                    public void onCancel(CropPhotoDialog dialog) {

                    }

                    @Override
                    public void onOk(CropPhotoDialog dialog, String path) {
                        icon.setImageBitmap(BitmapFactory.decodeFile(path));
                    }
                });
                cropPhotoDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
 ```
 ### Crop Bitmap
 ```java
CropPhotoDialog cropPhotoDialog = new CropPhotoDialog(MainActivity.this, itmap, new CropPhotoDialog.OnCropDialogListener() {
    @Override
    public void onCancel(CropPhotoDialog dialog) {

    }

    @Override
    public void onOk(CropPhotoDialog dialog, String path) {
        icon2.setImageBitmap( BitmapFactory.decodeFile(path));
    }
});
cropPhotoDialog.show();  
 ```
 
