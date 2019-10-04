package com.example.data_protection_android.activity;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.data_protection_android.R;
import com.example.data_protection_android.util.Action;
import com.example.data_protection_android.util.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_chosen_file)
    TextView chosenFileTv;

    @BindView(R.id.rg_method)
    RadioGroup methodRg;

    @BindView(R.id.rg_action)
    RadioGroup actionRg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_choose_file)
    public void chooseFile() {

    }

    @OnClick(R.id.btn_start)
    public void start() {
        String method = null;
        String action = null;

        switch (methodRg.getCheckedRadioButtonId()) {
            case R.id.rb_haffman:
                method = Method.HAFFMAN;
                break;
            case R.id.rb_lzw:
                method = Method.LZW;
                break;
            case R.id.rb_aes:
                method = Method.AES;
                break;
            case R.id.rb_rsa:
                method = Method.RSA;
                break;
        }

        switch (actionRg.getCheckedRadioButtonId()) {
            case R.id.rb_encrypt:
                action = Action.ENCRYPT;
                break;
            case R.id.rb_decrypt:
                action = Action.DECRYPT;
                break;
        }

        if (method != null && action != null) {

        } else {
            Toast.makeText(this, "choose all info!", Toast.LENGTH_LONG).show();
        }
    }
}
