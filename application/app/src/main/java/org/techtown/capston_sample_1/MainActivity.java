package org.techtown.capston_sample_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "<<MainActivity>>";
    private static final int RC_RESULT = 500;

    ViewPager pager;

    Button buttonBack;
    Button buttonNext;
    Button buttonRandom;

    String textInputed = "";
    String styleInputed = "";

    Boolean login = false;
    String id = "";
    String pwd = "";

    final int PERMISSION = 1;

    String qualityInputed = "100";

    // 번역
    Translator koToEnTranslator;
    String translatedInput = ""; // 영어로 번역된 글자
    Boolean downloadCheck = false; // 번역 모델 다운로드 확인

    String ranText = "";
    String koreanRanText = "";

    boolean isSample = false;
    boolean isSample1 = false;
    boolean isSample2 = false;
    boolean isRandom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 번역 초기화
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KOREAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        koToEnTranslator = Translation.getClient(options);

        // 번역 모델 다운로드
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        koToEnTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {
            Log.d(TAG, "번역 모델 다운로드 성공");
            downloadCheck = true;
        }).addOnFailureListener(e -> {
            Log.w(TAG, "번역 모델 다운로드 실패");
        });

        buttonBack = findViewById(R.id.buttonBack);
        buttonNext = findViewById(R.id.buttonNext);

        buttonBack.setVisibility(View.GONE);
        buttonNext.setText("Start!");

        buttonRandom = findViewById(R.id.buttonRandom);

        buttonRandom.setVisibility(View.INVISIBLE);
        buttonRandom.setEnabled(false);

        pager = findViewById(R.id.pagerInfo);
        pager.setOffscreenPageLimit(4);


        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        StartFragment startFragment = new StartFragment();
        adapter.addItem(startFragment);

        TextFragment textFragment = new TextFragment();
        adapter.addItem(textFragment);

        StyleFragment styleFragment = new StyleFragment();
        adapter.addItem(styleFragment);

        EndFragment endFragment = new EndFragment();
        adapter.addItem(endFragment);

        pager.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 1){
                    pager.setCurrentItem(0);
                }else if(pager.getCurrentItem() == 2){
                    pager.setCurrentItem(1);
                }else if(pager.getCurrentItem() == 3){
                    pager.setCurrentItem(2);
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 0){
                    pager.setCurrentItem(1);
                }else if(pager.getCurrentItem() == 1){
                    pager.setCurrentItem(2);
                }else if(pager.getCurrentItem() == 2){
                    pager.setCurrentItem(3);
                }else if(pager.getCurrentItem() == 3){
                    if(isRandom) {
                        translatedInput = ranText;

                        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                        intent.putExtra("login",login);
                        intent.putExtra("id", id);
                        intent.putExtra("text", textInputed);
                        intent.putExtra("style", styleInputed);
                        intent.putExtra("quality", qualityInputed);
                        intent.putExtra("translatedInput", translatedInput); // 영어로 번역된 글자
                        startActivityForResult(intent, RC_RESULT);
                    } else if(isSample) {
                        if(isSample1)
                            translatedInput = "waterfall";
                        else if(isSample2)
                            translatedInput = "A picture of a bedroom with a portrait of Van Gogh";

                        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                        intent.putExtra("login",login);
                        intent.putExtra("id", id);
                        intent.putExtra("text", textInputed);
                        intent.putExtra("style", styleInputed);
                        intent.putExtra("quality", qualityInputed);
                        intent.putExtra("translatedInput", translatedInput); // 영어로 번역된 글자
                        startActivityForResult(intent, RC_RESULT);
                    } else {
                        if(downloadCheck) {
                            // 번역
                            koToEnTranslator.translate(textInputed).addOnSuccessListener(s -> {
                                // 번역 성공
                                translatedInput = s;
                                Log.d(TAG, translatedInput);

                                Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                                intent.putExtra("login",login);
                                intent.putExtra("id", id);
                                intent.putExtra("text", textInputed);
                                intent.putExtra("style", styleInputed);
                                intent.putExtra("quality", qualityInputed);
                                intent.putExtra("translatedInput", translatedInput); // 영어로 번역된 글자
                                startActivityForResult(intent, RC_RESULT);
                            }).addOnFailureListener(e -> {
                                // 번역 실패
                            });
                        }
                    }

                }
            }
        });

        buttonRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRandom = true;
                isSample = false;
                isSample1 = false;
                isSample2 = false;

                int ranText1 = random.nextInt(4);
                int ranText2 = random.nextInt(10);
                int ranText3 = random.nextInt(34);
                int ranStyle = random.nextInt(4);
                int ranQul = random.nextInt(9);

                ArrayList<String> text1 = new ArrayList<String>(Arrays.asList("A painting of a", "A pencil art sketch of a", "An illustration of a" ,"A photograph of a"));
                ArrayList<String> text2 = new ArrayList<String>(Arrays.asList("spinning", "dreaming", "watering", "loving" ,"eating", "drinking", "sleeping", "repeating", "surreal", "psychedelic"));
                ArrayList<String> text3 = new ArrayList<String>(Arrays.asList("fish", "egg", "peacock", "watermelon", "pickle", "horse", "dog", "house", "kitchen", "bedroom", "door", "table", "lamp", "dresser", "watch", "logo", "icon", "tree",
                        "grass", "flower", "plant", "shrub" ,"bloom" ,"screwdriver", "spanner", "figurine", "statue", "graveyard", "hotel", "bus", "train", "car", "computer", "monitor"));

                ArrayList<String> koreanText1 = new ArrayList<String>(Arrays.asList("그림", "연필 아트 스케치", "삽화" ,"사진"));
                ArrayList<String> koreanText2 = new ArrayList<String>(Arrays.asList("회전하는", "꿈꾸는", "물 뿌리는", "사랑하는" ,"먹는", "마시는", "잠자는", "반복하는", "초현실적인", "사이키델릭한"));
                ArrayList<String> koreanText3 = new ArrayList<String>(Arrays.asList("물고기의", "계란", "공작", "수박", "피클", "말", "개", "집", "주방", "침실", "문", "식탁", "램프", "옷장", "시계", "상징", "아이콘", "나무",
                        "풀", "꽃", "식물", "관목" ,"꽃" ,"드라이버", "스패너", "작은 조각상", "조각상", "묘지", "호텔", "버스", "기차", "자동차", "컴퓨터", "모니터"));

                ranText = text1.get(ranText1) + " " + text2.get(ranText2) + " " + text3.get(ranText3);
                koreanRanText = koreanText2.get(ranText2) + " " + koreanText3.get(ranText3) + " " + koreanText1.get(ranText1);

                textFragment.editText.setText(koreanRanText);

                switch(ranStyle){
                    case 0:
                        styleFragment.selectedStyle.setName("Picasso");
                        break;
                    case 1:
                        styleFragment.selectedStyle.setName("Monet");
                        break;
                    case 2:
                        styleFragment.selectedStyle.setName("Pop Art");
                        break;
                    case 3:
                        styleFragment.selectedStyle.setName("none");
                        break;
                }

                int randomQuality = 100 + (ranQul * 50);
                endFragment.seekbarQuality.setProgress(randomQuality);
                endFragment.qualityText.setText(String.valueOf(randomQuality));
                qualityInputed = Integer.toString(randomQuality);

                pager.setCurrentItem(3);
            }
        });


        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {

                if (position == 0){
                    buttonBack.setVisibility(View.GONE);
                    buttonNext.setText("Start!");

                    buttonRandom.setVisibility(View.GONE);
                    buttonRandom.setEnabled(false);

                } else if (position == 1){
                    buttonRandom.setVisibility(View.VISIBLE);
                    buttonRandom.setEnabled(true);

                    buttonBack.setVisibility(View.VISIBLE);
                    buttonNext.setText("Next");
                    buttonNext.setEnabled(true);

                } else if (position == 2){

                    buttonRandom.setVisibility(View.GONE);
                    buttonRandom.setEnabled(true);

                    buttonBack.setVisibility(View.VISIBLE);
                    buttonNext.setText("Next");
                    buttonNext.setEnabled(true);

                } else if(position == 3) {

                    buttonNext.setText("Make Image!");

                    buttonRandom.setVisibility(View.INVISIBLE);
                    buttonRandom.setEnabled(false);

                    textInputed = textFragment.editText.getText().toString();
                    styleInputed = styleFragment.selectedStyle.getName();

                    endFragment.textInput.setText(textInputed);
                    endFragment.styleInput.setText(styleInputed);

                    if(textInputed.length() == 0 || styleInputed.length() == 0){
                        buttonNext.setEnabled(false);
                    }
                    if(textInputed.length() == 0){
                        endFragment.textInput.setText("문장을 입력해주세요!");
                    }
                    if(styleInputed.length() == 0){
                        endFragment.styleInput.setText("그림체를 선택해주세요!");
                    }

                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }

    class MyPagerAdapter extends FragmentStatePagerAdapter{
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public MyPagerAdapter(FragmentManager fm){
            super(fm);
        }

        public void addItem(Fragment item){
            items.add(item);
        }

        public Fragment getItem(int position){
            return items.get(position);
        }

        @Override
        public int getCount(){
            return items.size();
        }
    }
}