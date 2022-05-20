package org.techtown.capston_sample_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StartFragment extends Fragment {

    private static final String TAG = "<<StartFragment>>";
    private static final int RC_SIGN_IN_GOOGLE = 100;
    private static final int RC_REGISTER_WITH_GOOGLE = 200;

    Button buttonLogin;
    Button buttonSignup;
    Button buttonSignupG;
    EditText textId;
    EditText textPassword;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    DatabaseReference databaseReference;
    String emailId;
    String password;

    LinearLayout layoutLogin;
    LinearLayout layoutWelcome;
    LinearLayout layoutRegisterAndGooglelogin;

    TextView textViewId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);

        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonSignup = view.findViewById(R.id.buttonSignup);
        buttonSignupG = view.findViewById(R.id.buttonLoginG);
        textId = view.findViewById(R.id.editTextId);
        textPassword = view.findViewById(R.id.editTextPassword);

        layoutLogin = view.findViewById(R.id.layoutLogIn);
        layoutWelcome = view.findViewById(R.id.layoutWelcome);
        layoutRegisterAndGooglelogin = view.findViewById(R.id.layoutRegisterAndGoogleLogin);

        textViewId = view.findViewById(R.id.textViewId);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Text2Drawing");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((MainActivity)getActivity()).login == false) {
                    // 이메일 로그인
                    emailId = textId.getText().toString();
                    password = textPassword.getText().toString();

                    if(emailId.equals("") || password.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("아이디 또는 비밀번호를 입력해주세요!")
                                .setPositiveButton("확인", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    // 이메일 로그인 성공
                                    Log.d(TAG, "이메일 로그인 성공");

                                    Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).id = textId.getText().toString();
                                    ((MainActivity) getActivity()).pwd = textPassword.getText().toString();
                                    ((MainActivity) getActivity()).login = true;
                                    buttonLogin.setText("LogOut");

                                    textViewId.setText(emailId);
                                    layoutLogin.setVisibility(view.INVISIBLE);
                                    layoutWelcome.setVisibility(view.VISIBLE);
                                    layoutRegisterAndGooglelogin.setVisibility(View.INVISIBLE);

                                    ((MainActivity) getActivity()).pager.setCurrentItem(1);

                                } else {
                                    // 이메일 로그인 실패
                                    Log.w(TAG, "이메일 로그인 실패");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("아이디 또는 비밀번호를 다시 입력해주세요!")
                                            .setPositiveButton("확인", null);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            }
                        });
                    }

                } else if(((MainActivity)getActivity()).login == true) {
                    // 로그아웃
                    firebaseAuth.signOut();

                    Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
                    textId.setText("");
                    textPassword.setText("");
                    ((MainActivity) getActivity()).id = "";
                    ((MainActivity) getActivity()).pwd = "";
                    ((MainActivity) getActivity()).login = false;
                    buttonLogin.setText("Log In");

                    layoutWelcome.setVisibility(view.INVISIBLE);
                    layoutLogin.setVisibility(view.VISIBLE);
                    textViewId.setText("");
                    layoutRegisterAndGooglelogin.setVisibility(View.VISIBLE);

                }
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        // 구글 계정 회원가입 및 로그인
        buttonSignupG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
            }
        });

        return view;
    }

    // 구글 계정 확인 후 파이어베이스 인증 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    // 파이어베이스 인증
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "인증 성공");

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            databaseReference.child("User").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    if(user == null) {
                                        // 처음 로그인한 경우이므로 회원가입 화면으로 이동
                                        Intent registerIntent = new Intent(getActivity(), GoogleSignupActivity.class);
                                        registerIntent.putExtra("emailId", firebaseUser.getEmail());
                                        startActivityForResult(registerIntent, RC_REGISTER_WITH_GOOGLE);
                                    } else {
                                        // 회원가입한 계정이므로 로그인
                                        Log.d(TAG, "구글 로그인 성공");

                                        Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                                        ((MainActivity) getActivity()).login = true;
                                        buttonLogin.setText("LogOut");

                                        textViewId.setText(firebaseUser.getEmail());
                                        layoutLogin.setVisibility(View.INVISIBLE);
                                        layoutWelcome.setVisibility(View.VISIBLE);
                                        layoutRegisterAndGooglelogin.setVisibility(View.INVISIBLE);

                                        ((MainActivity) getActivity()).pager.setCurrentItem(1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Log.w(TAG, "인증 실패", task.getException());
                        }
                    }
                });
    }
}