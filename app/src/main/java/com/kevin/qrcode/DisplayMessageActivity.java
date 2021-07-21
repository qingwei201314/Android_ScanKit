package com.kevin.qrcode;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 以下内容为添加的
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textone);
        textView.setText(message);

        //显示二维码图片
        int type = HmsScan.QRCODE_SCAN_TYPE;
        int width = 400;
        int height = 400;

        HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator()
                .setBitmapBackgroundColor(Color.WHITE)
                .setBitmapColor(Color.GREEN)
                .setBitmapMargin(3).create();

        try {
            // 如果未设置HmsBuildBitmapOption对象，生成二维码参数options置null。
            Bitmap qrBitmap = ScanUtil.buildBitmap(message, type, width, height, options);

            // 跳转至显示二维码
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            Log.w("buildBitmap", e);
        }
    }
}