package org.techtown.capston_sample_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class TextFragment extends Fragment {

    EditText editText;
    ImageButton imageButtonMic;
    Intent intentMic;
    SpeechRecognizer mRecognizer;
    Button buttonSampleText1;
    Button buttonSampleText2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        editText = (EditText) view.findViewById(R.id.editText);
        imageButtonMic = (ImageButton) view.findViewById(R.id.imageButtonMic);
        buttonSampleText1 = (Button) view.findViewById(R.id.buttonSampleText1);
        buttonSampleText2 = (Button) view.findViewById(R.id.buttonSampleText2);

        // focus 가 된 순간 Random, Sample 둘 다 아닌 것으로 처리
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                ((MainActivity)getActivity()).isRandom = false;
                ((MainActivity)getActivity()).isSample = false;
                ((MainActivity)getActivity()).isSample1 = false;
                ((MainActivity)getActivity()).isSample2 = false;
            }
        });

        buttonSampleText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).isRandom = false;
                ((MainActivity)getActivity()).isSample = true;
                ((MainActivity)getActivity()).isSample1 = true;
                ((MainActivity)getActivity()).isSample2 = false;
                editText.setText(buttonSampleText1.getText());
            }
        });

        buttonSampleText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).isRandom = false;
                ((MainActivity)getActivity()).isSample = true;
                ((MainActivity)getActivity()).isSample2 = true;
                ((MainActivity)getActivity()).isSample1 = false;
                editText.setText(buttonSampleText2.getText());
            }
        });

        intentMic = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentMic.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        intentMic.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        imageButtonMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intentMic);
            }
        });

        return view;
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getActivity().getApplicationContext(), "음성인식 시작", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "결과를 찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 오류";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류";
                    break;
            }

            Toast.makeText(getActivity().getApplicationContext(), "에러 발생 : " + message,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onResults(Bundle results) {

            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                editText.setText(matches.get(i));
            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };
}