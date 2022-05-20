package org.techtown.capston_sample_1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class RecommendActivity extends AppCompatActivity {

    private static final String TAG = "<<RecommendActivity>>";

    Button buttonCancel_rec;
    Button btn_text1;
    Button btn_text2;
    Button btn_text3;
    Button btn_text4;
    TextView tv_finalText;
    ImageView iv_recommendImage;

    String text1;
    String text2;
    String text3;
    String text4;
    String finalText;

    // 번역
    Translator enToKoTranslator;

    // 서버 통신
    String serverIp = "192.168.0.8";
    int serverPort = 2031;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        buttonCancel_rec = findViewById(R.id.buttonCancel_rec);
        btn_text1 = findViewById(R.id.btn_text1);
        btn_text2 = findViewById(R.id.btn_text2);
        btn_text3 = findViewById(R.id.btn_text3);
        btn_text4 = findViewById(R.id.btn_text4);
        tv_finalText = findViewById(R.id.textRecStyle);
        iv_recommendImage = findViewById(R.id.iv_recommendImage);

        // 번역 초기화
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.KOREAN)
                .build();
        enToKoTranslator = Translation.getClient(options);

        // 번역 모델 다운로드
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        enToKoTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {
            Log.d(TAG, "추천 번역 모델 다운로드 성공");
        }).addOnFailureListener(e -> {
            Log.w(TAG, "추천 번역 모델 다운로드 실패");
        });

        Intent intent = getIntent();
        text1 = intent.getStringExtra("text1");
        Log.d(TAG, text1);
        text2 = intent.getStringExtra("text2");
        Log.d(TAG, text2);
        text3 = intent.getStringExtra("text3");
        Log.d(TAG, text3);
        text4 = intent.getStringExtra("text4");
        Log.d(TAG, text4);
        finalText = intent.getStringExtra("finalText");
        Log.d(TAG, finalText);

        enToKoTranslator.translate(text1).addOnSuccessListener(s -> btn_text1.setText(s));
        enToKoTranslator.translate(text2).addOnSuccessListener(s -> btn_text2.setText(s));
        enToKoTranslator.translate(text3).addOnSuccessListener(s -> btn_text3.setText(s));
        enToKoTranslator.translate(text4).addOnSuccessListener(s -> btn_text4.setText(s));
        tv_finalText.setText(finalText);

        btn_text1.setOnClickListener(v -> {
            loading();
            ImageRequester imageRequester = new ImageRequester(serverIp, serverPort, imageCallback);
            imageRequester.requestImage("false", text1, "Picasso", "100");
        });

        btn_text2.setOnClickListener(v -> {
            loading();
            ImageRequester imageRequester = new ImageRequester(serverIp, serverPort, imageCallback);
            imageRequester.requestImage("false", text2, "Picasso", "100");
        });

        btn_text3.setOnClickListener(v -> {
            loading();
            ImageRequester imageRequester = new ImageRequester(serverIp, serverPort, imageCallback);
            imageRequester.requestImage("false", text3, "Picasso", "100");
        });

        btn_text4.setOnClickListener(v -> {
            loading();
            ImageRequester imageRequester = new ImageRequester(serverIp, serverPort, imageCallback);
            imageRequester.requestImage("false", text4, "Picasso", "100");
        });

        buttonCancel_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                finish();
            }
        });

    }

    private void loading() {
        Glide.with(this)
                .load(R.raw.loading)
                .into(iv_recommendImage);
    }

    ImageCallback imageCallback = new ImageCallback() {
        @Override
        public void onResult(ResultData resultData) {
            iv_recommendImage.setImageBitmap(resultData.getBitmap());
        }
    };
}