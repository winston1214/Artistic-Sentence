package org.techtown.capston_sample_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "<<SignupActivity>>";

    String id;
    String pwd;
    String pwd2;
    String age;
    String sex;
    String artist;

    EditText editTextSignupId;
    EditText editTextSignupPwd;
    EditText editTextSignupPwd2;
    EditText editTextSignupAge;
    Spinner spinnerSex;
    Spinner spinnerArtist;

    Button buttonCancel;
    Button buttonSign;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Text2Drawing");

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSign = findViewById(R.id.buttonSignG);

        editTextSignupId = findViewById(R.id.editTextSignupIdG);
        editTextSignupPwd = findViewById(R.id.editTextSignupPwd);
        editTextSignupPwd2 = findViewById(R.id.editTextSignupPwd2);
        editTextSignupAge = findViewById(R.id.editTextSignupAgeG);

        spinnerSex = findViewById(R.id.spinnerSexG);
        ArrayAdapter<CharSequence> spinnerSex_adapter = ArrayAdapter.createFromResource(this, R.array.resourcesSex, android.R.layout.simple_spinner_item);
        spinnerSex_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(spinnerSex_adapter);


        spinnerArtist = findViewById(R.id.spinnerArtistG);
        ArrayAdapter<CharSequence> spinnerArtist_adapter = ArrayAdapter.createFromResource(this, R.array.resourcesArtist, android.R.layout.simple_spinner_item);
        spinnerArtist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(spinnerArtist_adapter);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        spinnerSex.setSelection(0);
        spinnerSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerArtist.setSelection(0);
        spinnerArtist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                artist = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 회원가입
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = editTextSignupId.getText().toString();
                pwd = editTextSignupPwd.getText().toString();
                pwd2 = editTextSignupPwd2.getText().toString();
                age = editTextSignupAge.getText().toString();

                if(id.length() == 0) {
                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
                } else if(pwd.length() < 6) {
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                } else if(pwd2.length() < 6) {
                    Toast.makeText(getApplicationContext(),"비밀번호를 재입력해주세요",Toast.LENGTH_SHORT).show();
                } else if(!pwd.equals(pwd2)) {
                    Toast.makeText(getApplicationContext(),"재입력된 비밀번호가 잘못되었습니다",Toast.LENGTH_SHORT).show();
                } else if(age.length() == 0) {
                    Toast.makeText(getApplicationContext(),"나이를 입력해주세요",Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(id, pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                // 회원가입 성공
                                Log.d(TAG, "회원가입 성공");

                                // 회원정보 생성
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                User user = new User();
                                user.setUid(firebaseUser.getUid());
                                user.setEmailId(firebaseUser.getEmail());
                                user.setPassword(pwd);
                                user.setAge(age);
                                user.setSex(sex);
                                user.setArtist(artist);

                                // 데이터베이스 등록
                                databaseReference.child("User").child(firebaseUser.getUid()).setValue(user);

                                Toast.makeText(getApplicationContext(),"회원가입에 성공하였습니다",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);

                                finish();
                            } else {
                                // 회원가입 실패
                                // 파이어베이스 규칙 : 이메일 중복 또는 비밀번호 5글자 이하 입력 시 회원가입 실패
                                Log.w(TAG, "회원가입 실패");

                                // 회원가입 실패 다이얼로그
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setTitle("회원가입 실패")
                                        .setMessage("이메일 중복")
                                        .setPositiveButton("확인", null);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
        });

    }
}