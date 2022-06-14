package com.example.googlesearchschool2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize the views.
        TextView schoolsTitle = findViewById(R.id.titleDetail);
        TextView schoolsInfo = findViewById(R.id.subTitleDetail);
        ImageView schoolsImage = findViewById(R.id.sportsImageDetail);

        // Set the text from the Intent extra.
        schoolsTitle.setText(getIntent().getStringExtra("title"));
        schoolsInfo.setText(getIntent().getStringExtra("Info"));
        Glide.with(this).load(getIntent().getIntExtra("Image",0))
                .into(schoolsImage);

        // Load the image using the Glide library and the Intent extra.
        /*Glide.with(this).load(getIntent().getIntExtra("image_resource",0))
                .into(schoolsImage);*/
    }
}
