package org.techtown.capston_sample_1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class EndFragment extends Fragment {

    TextView textInput;
    TextView styleInput;
    TextView qualityText;
    ImageView styleImage;
    SeekBar seekbarQuality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_end, container, false);

        textInput = (TextView) view.findViewById(R.id.textInputText);
        styleInput = (TextView) view.findViewById(R.id.textSelectedStyle);
        styleImage = (ImageView) view.findViewById(R.id.imageStyle);
        qualityText = (TextView) view.findViewById(R.id.textViewQuality);
        seekbarQuality = (SeekBar) view.findViewById(R.id.seekBarQuality);

        seekbarQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (i % 50 == 0) {
                    qualityText.setText(String.valueOf(i));
                } else {
                    seekbarQuality.setProgress((i/ 50) * 50);
                    qualityText.setText(String.valueOf((i / 50) * 50));
                }
                //qualityText.setText(Integer.toString(i));
                ((MainActivity)getActivity()).qualityInputed = Integer.toString(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
}