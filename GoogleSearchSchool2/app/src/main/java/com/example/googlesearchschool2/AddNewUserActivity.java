package com.example.googlesearchschool2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.googlesearchschool2.db.AppDatabase;
import com.example.googlesearchschool2.db.User;

public class AddNewUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner mSpinner;
    private EditText Name;
    private EditText Longitude;
    private EditText Latitude;
    private EditText Info;
    private int selectColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Name = (EditText) findViewById(R.id.Name);
        Longitude = (EditText) findViewById(R.id.Longitude);
        Latitude = (EditText) findViewById(R.id.Latitude);
        Info = (EditText) findViewById(R.id.Info);

        mSpinner = (Spinner) findViewById(R.id.spinner2);
        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.color,
                        android.R.layout.simple_spinner_item);

        //how the spinner will look when it drop downs on click
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to spinne
        mSpinner.setAdapter(adapter);

        Button saveButton = findViewById(R.id.Save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewUser(Name.getText().toString(), Longitude.getText().toString(), Latitude.getText().toString(), Info.getText().toString(), selectColor);
            }
        });

    }


    private void saveNewUser(String Name, String Longitude, String Latitude, String Info, int image) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        User user = new User();
        user.schoolName = Name;
        user.schoolInfo = Info;
        user.schoolLatitude = Double.parseDouble(Latitude);
        user.schoolLongitude = Double.parseDouble(Longitude);
        user.ColorImage = image;

        db.userDao().insertUser(user);

        finish();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         selectColor = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
