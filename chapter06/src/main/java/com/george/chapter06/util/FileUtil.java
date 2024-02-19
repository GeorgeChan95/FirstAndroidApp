package com.george.chapter06.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    /**
     * 保存文本内容到本地
     * @param path 文件路径
     * @param txt 文本内容
     */
    public static void saveText(String path, String txt) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(path));
            bufferedWriter.write(txt);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从指定路径读取文本内容
     * @param path
     * @return
     */
    public static String openText(String path) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
