package com.addukkanapp.uis.activity_verification_code;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.addukkanapp.R;
import com.addukkanapp.databinding.ActivityVerificationCodeBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.share.Common;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class VerificationCodeActivity extends AppCompatActivity {
    private ActivityVerificationCodeBinding binding;
    private String phone_code = "";
    private String phone = "";
    private boolean canSend = false;
    private CountDownTimer countDownTimer;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String smsCode = "";
    private String lang = "ar";


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_code);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            phone_code = intent.getStringExtra("phone_code");
            phone = intent.getStringExtra("phone");

        }
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        String mPhone = phone_code + phone;
        binding.setPhone(mPhone);
        Paper.init(this);
        lang = Paper.book().read("lang");
        binding.setLang(lang);

        binding.btnConfirm.setOnClickListener(v -> {
            String sms = binding.edtCode.getText().toString().trim();
            if (!sms.isEmpty()) {
                checkValidCode(sms);

            } else {
                binding.edtCode.setError(getString(R.string.inv_code));
            }
        });
        binding.btnResendCode.setOnClickListener(view -> {
            if (canSend) {
                canSend = false;
                resendCode();

            }
        });

        binding.llBack.setOnClickListener(v -> finish());
       // onSuccessCode();
        sendSmsCode();

    }


    public void sendSmsCode() {
        startCounter();

        mAuth.setLanguageCode("en");

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                smsCode = phoneAuthCredential.getSmsCode();
                checkValidCode(smsCode);
            }

            @Override
            public void onCodeSent(@NonNull String verification_id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verification_id, forceResendingToken);
                VerificationCodeActivity.this.verificationId = verification_id;
                Log.e("verification_id", verification_id);
            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e.getMessage() != null) {
                    onCodeFailed(e.getMessage());
                } else {
                    onCodeFailed(getString(R.string.failed));

                }
            }
        };
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phone_code + phone,
                        120,
                        TimeUnit.SECONDS,
                        this,
                        mCallBack

                );
    }

    public void checkValidCode(String code) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.e("vvvvvvvvvv", "vvvvvvvvv");
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        Log.e("cccccccccccc", "ccccccccccccc");
                        dialog.dismiss();
                        onSuccessCode();
                    }).addOnFailureListener(e -> {
                dialog.dismiss();
                if (e.getMessage() != null) {
                    try {
                        onCodeFailed(e.getMessage());

                    } catch (Exception ex) {

                    }
                } else {
                    onCodeFailed(getString(R.string.failed));

                }
            });
        }

    }


    private void startCounter() {
        countDownTimer = new CountDownTimer(120000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) ((millisUntilFinished / 1000) / 60);
                int seconds = (int) ((millisUntilFinished / 1000) % 60);

                String time = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
                onCounterStarted(time);


            }

            @Override
            public void onFinish() {
                onCounterFinished();


            }
        };

        countDownTimer.start();
    }

    public void resendCode() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
        sendSmsCode();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }


    public void onSuccessCode() {
        setResult(RESULT_OK);
        finish();

    }

    public void onCounterStarted(String time) {
        binding.btnResendCode.setText(String.format(Locale.ENGLISH, "%s", time));
        binding.btnResendCode.setTextColor(ContextCompat.getColor(VerificationCodeActivity.this, R.color.gray6));
        binding.btnResendCode.setBackgroundResource(R.color.transparent);
    }

    public void onCounterFinished() {
        canSend = true;
        binding.btnResendCode.setText(R.string.resend2);
        binding.btnResendCode.setTextColor(ContextCompat.getColor(VerificationCodeActivity.this, R.color.colorPrimary));
        binding.btnResendCode.setBackgroundResource(R.color.white);
        binding.tvResend.setText("");
    }

    public void onCodeFailed(String msg) {
        Common.CreateDialogAlert(this, msg);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}