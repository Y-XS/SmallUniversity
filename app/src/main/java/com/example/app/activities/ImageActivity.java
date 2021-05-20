package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageActivity extends AppCompatActivity {
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        photoView = (PhotoView) findViewById(R.id.photoView);
        String url = getIntent().getStringExtra("URL");
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        View v = findViewById(R.id.image_back_color);//找到你要设透明背景的layout 的id
//        v.getBackground().setAlpha(50);//0~255透明度值
//        photoView.setImageURI(Uri.parse(url));
        Glide.with(this).load(url).error(R.mipmap.error).into(photoView);
        Log.d("ImageActivity", "onCreate: ==================="+url);
    }
}
