package org.techtown.capston_sample_1;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

public class ResultActivity extends AppCompatActivity {

    boolean login = false;
    String id = "";
    String text = "";
    String style = "";
    String quality = "100";
    String translated = ""; // 영어로 번역된 글자

    String text1;
    String text2;
    String text3;
    String text4;
    String finalText;

    String imageName;
    Bitmap resultImage;

    TextView tv_rating;
    RatingBar ratingBar;
    Button btn_ratingRegister;
    Button buttonRetry;
    Button buttonSaveImage;
    Button buttonRecommend;
    ImageView iv_result;
    Button btn_back;

    ProgressDialog dialog;

    // 서버 통신
    String serverIp = "192.168.0.8";
    int serverPort = 2031;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        processIntent(intent);

        imageName = text + ".jpg";

        dialog = new ProgressDialog(ResultActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("이미지를 만드는 중입니다.");
        dialog.setCancelable(false);

        //dialog.show();

        //dialog.dismiss();

        iv_result = findViewById(R.id.iv_result);
        tv_rating = findViewById(R.id.tv_rating);
        ratingBar = findViewById(R.id.ratingBar);
        btn_ratingRegister = findViewById(R.id.btn_ratingRegister);
        buttonSaveImage = findViewById(R.id.btn_saveImage);
        buttonRecommend = findViewById(R.id.btn_recommend);
        btn_back = findViewById(R.id.btn_back);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Text2Drawing");

        btn_back.setOnClickListener(v -> {
            Intent backIntent = new Intent();
            setResult(RESULT_OK, backIntent);
            finish();
        });

        // loading 화면
        loading();

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String strRating = "평점을 입력해 주세요 (" + rating + "/5.0)";
            ratingBar.setRating(rating);
            tv_rating.setText(strRating);
        });

        // 서버로부터 결과 이미지 수신
        ImageRequester imageRequester = new ImageRequester(serverIp, serverPort, imageCallback);
        imageRequester.requestImage("true", translated, style, quality);
        Log.d("<<ResultActivity>>", translated);

        // 서버에 이미지 다시 요청
        buttonRetry = findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(v -> {
            loading();
            imageRequester.requestImage("true", translated, style, quality);
        });

        // 이미지 저장
        buttonSaveImage.setOnClickListener(v -> {
            if(saveImage())
                Toast.makeText(this, "이미지 저장에 성공하였습니다", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "이미지 저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
        });

        buttonRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecommendActivity.class);
                intent.putExtra("text1", text1);
                intent.putExtra("text2", text2);
                intent.putExtra("text3", text3);
                intent.putExtra("text4", text4);
                intent.putExtra("finalText", finalText);
                startActivityForResult(intent, 600);
            }
        });

        btn_ratingRegister.setOnClickListener(v -> {
            if(!login) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("비회원은 이용할 수 없습니다")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            } else {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                Rating rating = new Rating();
                rating.setId(firebaseUser.getEmail());
                rating.setInputText(translated);
                rating.setStyle(style);
                rating.setRating(String.valueOf(ratingBar.getRating()));
                databaseReference.child("Rating").child(firebaseUser.getUid()).setValue(rating);

                Toast.makeText(this, "등록되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loading() {
        Glide.with(this)
                .load(R.raw.loading)
                .override(300, 300)
                .into(iv_result);
    }

    private boolean saveImage() {
        boolean saveSuccess = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentResolver contentResolver = getContentResolver();
            Uri imageItem = contentResolver.insert(imageCollection, contentValues);

            try {
                ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(imageItem, "w", null);

                if(pfd != null) {
                    byte[] bitmapToBytes = bitmapToByteArray(resultImage);
                    FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                    fos.write(bitmapToBytes);
                    fos.close();

                    contentValues.clear();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(imageItem, contentValues, null, null);
                }

                saveSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("안드로이드 10 이상부터 이미지 저장 가능")
                    .setPositiveButton("확인", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return saveSuccess;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] byteArray = bos.toByteArray();

        return byteArray;
    }

    private void processIntent(Intent intent){

        if(intent != null){
            Bundle bundle = intent.getExtras();
            login = bundle.getBoolean("login");
            id = bundle.getString("id");
            text = bundle.getString("text");
            style = bundle.getString("style");
            quality = bundle.getString("quality");
            translated = bundle.getString("translatedInput");
        }
    }

    // 결과 이미지 콜백
    ImageCallback imageCallback = new ImageCallback() {
        @Override
        public void onResult(ResultData resultData) {
            StringTokenizer stringTokenizer = new StringTokenizer(resultData.getText(), "|");
            text1 = stringTokenizer.nextToken();
            Log.d("<<ResultActivity>>", text1);
            text2 = stringTokenizer.nextToken();
            Log.d("<<ResultActivity>>", text2);
            text3 = stringTokenizer.nextToken();
            Log.d("<<ResultActivity>>", text3);
            text4 = stringTokenizer.nextToken();
            Log.d("<<ResultActivity>>", text4);
            finalText = stringTokenizer.nextToken();
            Log.d("<<ResultActivity>>", finalText);
            iv_result.setImageBitmap(resultData.getBitmap());
            resultImage = resultData.getBitmap();
        }
    };

}
