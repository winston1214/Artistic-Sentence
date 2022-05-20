package org.techtown.capston_sample_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    int STYLE_NUM = 3;

    ViewPager pagerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        pagerInfo = findViewById(R.id.pagerInfo);
        pagerInfo.setOffscreenPageLimit(STYLE_NUM);

        MyPagerAdapter adapterInfo = new MyPagerAdapter(getSupportFragmentManager());

        StyleInfoFragment1 fragment1 = new StyleInfoFragment1();
        adapterInfo.addItem(fragment1);

        StyleInfoFragment2 fragment2 = new StyleInfoFragment2();
        adapterInfo.addItem(fragment2);

        StyleInfoFragment3 fragment3 = new StyleInfoFragment3();
        adapterInfo.addItem(fragment3);

        pagerInfo.setAdapter(adapterInfo);

        Button buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
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
