package org.imc.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

@Component
public class ReadFileService {
    /**
     * 文件转换为字符串
     *
     * @param path  文件路径，绝对路径相对路径读取文件都可,相对路径是相对本模块所在目录
     *              绝对路径："C:\\Users\\小朋友留德\\Desktop\\example.xml"，
     *              相对路径：".\\src\\main\\resources\\parser_example.xml"
     * @return 文件内容
     */
    public static String readFile(String path) {
        File f = new File(path);
        String content = file2String(f, "GBK");
        return content;
    }
    /**
     * 文件转换为字符串
     *
     * @param in            字节流
     * @param charset 文件的字符集
     * @return 文件内容
     */
    private static String stream2String(InputStream in, String charset) {
        StringBuffer sb = new StringBuffer();
        try {
            Reader r = new InputStreamReader(in, charset);
            int length = 0;
            for (char[] c = new char[1024]; (length = r.read(c)) != -1;) {
                sb.append(c, 0, length);
            }
            r.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 文件转换为字符串
     *
     * @param f             文件
     * @param charset 文件的字符集
     * @return 文件内容
     */
    private static String file2String(File f, String charset) {
        String result = null;
        try {
            result = stream2String(new FileInputStream(f), charset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
