package com.example.data_protection_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.data_protection_android.R;
import com.example.data_protection_android.logic.haffman.CompressQualifier;
import com.example.data_protection_android.logic.haffman.HuffmanCompressor;
import com.example.data_protection_android.logic.haffman.Node;
import com.example.data_protection_android.logic.lzw.LzwCoder;
import com.example.data_protection_android.util.Method;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EncodeFragment extends Fragment {

    @BindView(R.id.tv_progress)
    TextView progressTv;

    @BindView(R.id.tv_method)
    TextView methodTv;

    private static final String FILE_PATH_KEY = "FILE_PATH_KEY";
    private static final String METHOD_KEY = "METHOD_KEY";

    public static EncodeFragment newInstance(String path, Method method) {
        EncodeFragment fragment = new EncodeFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH_KEY, path);
        args.putSerializable(METHOD_KEY, method);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);
        ButterKnife.bind(this, view);

        String file = null;
        Method method = null;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            file = bundle.getString(FILE_PATH_KEY);
            method = (Method) bundle.getSerializable(METHOD_KEY);
        }

        if (file != null && method != null) {
            setData(file, method);
            try {
                start(file, method);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private void setData(String file, Method method) {
        methodTv.setText(method.name());
    }

    private void start(String file, Method method) throws IOException {
        switch (method) {
            case HAFFMAN:
                Node tree;

                String compressedTextFilename = file + ".haffman";
                String decodedTextFilename = file.substring(file.indexOf('.')) + "_decoded.txt";

                addProgressText("Кодирование файла: " + file + "\n");
                tree = HuffmanCompressor.compress(file, compressedTextFilename);

                addProgressText("Архив создан: " + compressedTextFilename);
                addProgressText("Раскодирование файла\n");

                HuffmanCompressor.decompress(compressedTextFilename, decodedTextFilename, tree);

                addProgressText("% компрессии = " + CompressQualifier.compressPercent(
                        new File(file),
                        new File(compressedTextFilename)) + "\n");
                addProgressText("Файлы идентичны = " + CompressQualifier.isUncompressedEqualsSource(
                        new File(file),
                        new File(decodedTextFilename)) + "\n");

                break;
            case LZW:
                LzwCoder coder = new LzwCoder();
                addProgressText("Архивация LZW");
                String archive = coder.archive(file);
                addProgressText("Архив создан: " + archive);

                addProgressText("Дерхивация LZW");
                String newFile = coder.unzip(file, archive);

                addProgressText("Разархивированный файл: " + newFile);
                break;
        }
    }

    private void addProgressText(String text) {
        progressTv.setText(progressTv.getText() + "\n" + text + "\n");
    }
}
