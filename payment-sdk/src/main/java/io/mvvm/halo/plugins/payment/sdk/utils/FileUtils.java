package io.mvvm.halo.plugins.payment.sdk.utils;


import io.micrometer.core.instrument.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * FileUtils.
 *
 * @author: pan
 **/
public class FileUtils {

    public static String readFileToString(String path) {
        try {
            File file = new File(path);
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException("读取文件内容失败");
        }
    }

    public static String readFileToString(final File file) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return IOUtils.toString(in, Charset.defaultCharset());
        }
    }

    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }
}
