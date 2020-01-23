package com.example.data_protection_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.data_protection_android.R;
import com.example.data_protection_android.logic.aes.AES;
import com.example.data_protection_android.logic.haffman.HuffmanCompressor;
import com.example.data_protection_android.logic.haffman.Node;
import com.example.data_protection_android.logic.lzw.LzwCoder;
import com.example.data_protection_android.logic.rsa.RSA;
import com.example.data_protection_android.util.Action;
import com.example.data_protection_android.util.Method;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EncodeFragment extends Fragment implements EncodeListener {

    @BindView(R.id.tv_progress)
    TextView progressTv;

    @BindView(R.id.tv_method)
    TextView methodTv;

    @BindView(R.id.input_section)
    LinearLayout inputSection;

    @BindView(R.id.key_section)
    LinearLayout keySection;

    @BindView(R.id.et_key)
    EditText keyEt;

    @BindView(R.id.et_text)
    EditText textEt;

    private static final String FILE_PATH_KEY = "FILE_PATH_KEY";
    private static final String METHOD_KEY = "METHOD_KEY";
    private static final String ACTION_KEY = "ACTION_KEY";

    private Method method;
    private Action action;

    public static EncodeFragment newInstance(String path, Method method, Action action) {
        EncodeFragment fragment = new EncodeFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH_KEY, path);
        args.putSerializable(METHOD_KEY, method);
        args.putSerializable(ACTION_KEY, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);
        ButterKnife.bind(this, view);

        String file = null;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            file = bundle.getString(FILE_PATH_KEY);
            method = (Method) bundle.getSerializable(METHOD_KEY);
            action = (Action) bundle.getSerializable(ACTION_KEY);
        }

        if (file != null && method != null && action != null) {
            setData(method, action);
            try {
                switch (action) {
                    case DEMO:
                        start(file, method);
                        break;
                    case ENCRYPT:
                        encrypt(file, method);
                        break;
                    case DECRYPT:
                        decrypt(file, method);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    private void setData(Method method, Action action) {
        methodTv.setText(method.name() + " " + action.name());
    }

    private void encrypt(String file, Method method) throws IOException {
        switch (method) {
            case HAFFMAN:
                Toast.makeText(getContext(), "SORRY, ONLY DEMO", Toast.LENGTH_LONG).show();
                break;
            case LZW:
                LzwCoder coder = new LzwCoder();
                addProgressText("Архивация LZW");
                String archive = coder.archive(file, this);
                addProgressText("Архив создан: " + archive);
                break;
            case RSA:
                keySection.setVisibility(View.GONE);
            case AES:
                inputSection.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void decrypt(String file, Method method) throws IOException {
        switch (method) {
            case HAFFMAN:
                Toast.makeText(getContext(), "SORRY, ONLY DEMO", Toast.LENGTH_LONG).show();
                break;
            case LZW:
                LzwCoder coder = new LzwCoder();
                addProgressText("Дерхивация LZW");
                String newFile = coder.unzip(null, file);
                addProgressText("Разархивированный файл: " + newFile);
                break;
            case RSA:
                keySection.setVisibility(View.GONE);
            case AES:
                inputSection.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void start(String file, Method method) throws IOException {
        switch (method) {
            case HAFFMAN:
                Node tree;

                String compressedTextFilename = file + ".haffman";

                String[] split = file.split("/");
                String lastFile = split[split.length - 1];
                String decodedTextFilename = file.replace(lastFile, "decoded_" + lastFile);

                addProgressText("Кодирование файла: " + file + "\n");
                tree = HuffmanCompressor.compress(file, compressedTextFilename, this);

                addProgressText("Архив создан: " + compressedTextFilename);
                addProgressText("Раскодирование файла\n");

                HuffmanCompressor.decompress(compressedTextFilename, decodedTextFilename, tree);

                addProgressText("Разархивированный файл: " + decodedTextFilename);
                addProgressText("% компрессии = " + HuffmanCompressor.compressPercent(
                        new File(file),
                        new File(compressedTextFilename)) + "\n");

                break;
            case LZW:
                LzwCoder coder = new LzwCoder();
                addProgressText("Архивация LZW");
                String archive = coder.archive(file, this);
                addProgressText("Архив создан: " + archive);

                addProgressText("Дерхивация LZW");
                String newFile = coder.unzip(file, archive);

                addProgressText("Разархивированный файл: " + newFile);
                break;
            case RSA:
                keySection.setVisibility(View.GONE);
            case AES:
                inputSection.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.btn_enter)
    public void enterKey() {
        clearProgress();
        String key = keyEt.getText().toString();
        String text = textEt.getText().toString();

        if (method == Method.AES) {
            testAES(key, text);
        } else {
            try {
                testRSA(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void testAES(String key, String text) {
        String encrypt = AES.encrypt(text, key);
        String decrypt = AES.decrypt(encrypt, key);

        addProgressText("\nЗаданый ключ:");
        addProgressText(key);
        addProgressText("\nИсходный текст:");
        addProgressText(text);
        addProgressText("\nЗашифрованный текст:");
        addProgressText(encrypt);
        addProgressText("\nРасшифрованный текст:");
        addProgressText(decrypt);
    }

    private void testRSA(String text) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        addProgressText("\nИсходный текст:");
        addProgressText(text);

        byte[] cipherTextArray = RSA.encrypt(text, publicKey);
        String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
        addProgressText("\nЗашифрованный текст:");
        addProgressText(encryptedText);

        String decryptedText = RSA.decrypt(cipherTextArray, privateKey);
        addProgressText("\nРасшифрованный текст:");
        addProgressText(decryptedText);
    }

    private void addProgressText(String text) {
        progressTv.setText(progressTv.getText() + text + "\n");
    }

    private void clearProgress() {
        progressTv.setText("");
    }

    @Override
    public void displayInfo(String text) {
        addProgressText(text);
    }
}
