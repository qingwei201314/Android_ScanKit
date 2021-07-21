package com.kevin.qrcode;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.kevin.qrcode.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    // 存消息的key
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    // 扫码按钮的自定义变量值 CAMERA_REQ_CODE
    static final int CAMERA_REQ_CODE = 3;
    //实现“onRequestPermissionsResult”函数接收校验权限结果
    final int PERMISSIONS_LENGTH = 1;
    public static final int BITMAP = 333;
    public static final int REQUEST_CODE_PHOTO = 444;
    // 存图片的路径
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     *  点“确定”按钮后触发的事件
     */
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.textMain);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /**
     * 点按“拍照”后
     */
    public void dispatchTakePictureIntent(View view) {
        // 相片存外部
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.kevin.qrcode", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * 生成拍照文件
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("扫描结束.");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK ){
            return;
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 存原图和缩略图
            Bitmap bitmap = null;
            ContentResolver resolver = getContentResolver();
            try {
                File photoFile = new File(currentPhotoPath);
                Uri photoURI = FileProvider.getUriForFile(this,"com.kevin.qrcode", photoFile);
//                bitmap = BitmapFactory.decodeStream(resolver.openInputStream(photoURI));

                bitmap = MediaStore.Images.Media.getBitmap(resolver,photoURI);
                //解决横向显示问题
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 调焦距
     */
    public int convertZoomInt(double zoomValue, Camera camera) {
        //计算相机预览缩放值。parameters为相机参数。
        Camera.Parameters parameters = camera.getParameters();
        List<Integer> allZoomRatios = parameters.getZoomRatios();
        float maxZoom = Math.round(allZoomRatios.get(allZoomRatios.size() - 1) / 100f);
        if (zoomValue >= maxZoom) {
            return allZoomRatios.size() - 1;
        }
        for (int i = 1; i < allZoomRatios.size(); i++) {
            if (allZoomRatios.get(i) >= (zoomValue * 100) && allZoomRatios.get(i - 1) <= (zoomValue * 100)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *  跳转到扫码页面按钮
     */
    public void newViewBtnClick(View view) {
        Intent intent  = new Intent(this, ShowScan.class);
        startActivity(intent);
    }
}