package org.techtown.capston_sample_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StyleFindActivity extends AppCompatActivity {

    Button buttonBack;
    ImageButton imageButton1;
    ImageButton imageButton2;
    TextView textView1;
    TextView textView2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylefind);

        imageButton1 = findViewById(R.id.imageButton_1);
        imageButton1.setImageResource(R.drawable.sample_icon_1);

        textView1 = findViewById(R.id.textView_1);
        textView1.setText("스타일 1");

        imageButton2= findViewById(R.id.imageButton_2);
        imageButton2.setImageResource(R.drawable.sample_icon_2);

        textView2 = findViewById(R.id.textView_2);
        textView2.setText("스타일 2");

        buttonBack = findViewById(R.id.btn_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}
