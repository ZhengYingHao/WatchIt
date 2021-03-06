package com.example.zyh.watchit;


import android.accounts.NetworkErrorException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class HttpUtil {
    public static final String TAG = "HttpUtil";
    public static final String WRONG_USER_NAME_OR_PASSWORD_INFO = "false";

    private static final String CHARSET = "utf-8";


    public static void uploadString(String path, String param) {
        int TIME_OUT = 1000 * 5;
        String boundary = UUID.randomUUID().toString();
        String lineEnd = "\r\n", prefix = "--";
        String content_type = "multipart/form-data";

        HttpURLConnection urlConnection = null;
        DataOutputStream dos;

        try {
            URL url = new URL(path);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);
            urlConnection.setRequestProperty("Charset", CHARSET);
            urlConnection.setRequestProperty("connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", content_type + ";boundary=" + boundary);

            dos = new DataOutputStream(urlConnection.getOutputStream());



            //first boundary
            String firstBoundary = prefix + boundary + lineEnd;
            //middle boundary
            String midBoundary = lineEnd + firstBoundary;
            //Encapsulated multipart part
            String idPart = "Content-Disposition: form-data; name=\"userId\""
                    + lineEnd + lineEnd;
            String statePart = "Content-Disposition: form-data; name=\"state\""
                    + lineEnd + lineEnd;
            String aidPart = "Content-Disposition: form-data; name=\"aid\""
                    + lineEnd + lineEnd;
            //last boundary
            String lastBoundary = lineEnd + prefix + boundary + prefix + lineEnd;



            dos.write(firstBoundary.getBytes());
            dos.write(idPart.getBytes());
            dos.write(UserInfo.getUserInfo().getUserId().getBytes());
            dos.write(midBoundary.getBytes());

            dos.write(statePart.getBytes());
            dos.write(UserInfo.getUserInfo().getState().getBytes());
            dos.write(midBoundary.getBytes());

            dos.write(aidPart.getBytes());
            dos.write(param.getBytes());
            dos.write(lastBoundary.getBytes());
            dos.flush();


            /**
             * 好似需要 urlConnection.getResponseCode() 先会连接网站
             */
            int responseCode = urlConnection.getResponseCode();
            switch (responseCode) {
                case 200:
                    Log.i(TAG, "connect success.");
                    break;
                default:
                    Log.i(TAG, "connect failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static byte[] downloadData(String path) {
        int TIME_OUT = 1000 * 60 * 5;

        HttpURLConnection urlConnection = null;
        InputStream in;

        byte[] bytes;

        try {
            URL url = new URL(path);
            Log.i(TAG, "get url");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);

            in = urlConnection.getInputStream();
            Log.i(TAG, "success to open");
            int dataLength = in.available();
            Log.i(TAG, "bytes length: " + dataLength);

            dataLength = 1024 * 1024;
            bytes = new byte[dataLength];
            int readBytes = 0, flag;
            while (true) {
                flag = in.read(bytes, readBytes, dataLength - readBytes);
                if (flag == 0 || flag == -1) {
                    Log.i(TAG, "flag = " + flag);
                    break;
                }
                readBytes += flag;
                Log.i(TAG, "readBytes: " + readBytes);
            }

            byte[] actuallyBytes = new byte[readBytes];
            System.arraycopy(bytes, 0, actuallyBytes, 0, readBytes);
            Log.i(TAG, "sum : " + readBytes);
            return actuallyBytes;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "failed to open : " + e.toString());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static String sendDataToPath(String path, String name, String pd) throws NetworkErrorException{
        int TIME_OUT = 1000 * 60;

        HttpURLConnection urlConnection = null;
        String boundary = UUID.randomUUID().toString();
        String lineEnd = "\r\n", prefix = "--";
        String content_type = "multipart/form-data";

        InputStream in;

        try {
            URL url = new URL(path);
            Log.i(TAG, "get url");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);
            urlConnection.setRequestProperty("Charset", CHARSET);
            urlConnection.setRequestProperty("connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", content_type + ";boundary=" + boundary);

            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());

            //first boundary
            String firstBoundary = prefix + boundary + lineEnd;
            //Encapsulated multipart part
            String namePart = "Content-Disposition: form-data; name=\"name\""
                    + lineEnd + lineEnd;
            //middle boundary
            String midBoundary = lineEnd + firstBoundary;
            //Encapsulated multipart part
            String pdPart = "Content-Disposition: form-data; name=\"password\""
                    + lineEnd + lineEnd;
            //last boundary
            String lastBoundary = lineEnd + prefix + boundary + prefix + lineEnd;



            out.write(firstBoundary.getBytes());
            out.write(namePart.getBytes());
            out.write(name.getBytes());
            out.write(midBoundary.getBytes());
            out.write(pdPart.getBytes());
            out.write(pd.getBytes());
            out.write(lastBoundary.getBytes());
            out.flush();


            in = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }

            int response = urlConnection.getResponseCode();
            if (response == 200) {
                Log.i(TAG, "send name and password success.");
            } else {
                Log.i(TAG, "send name and password failed.");
            }

            return builder.toString();

        } catch (IOException e) {
            Log.i(TAG, "failed to open : " + e.toString());
            throw new NetworkErrorException();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static String isUser(String path, String name, String pd) throws NetworkErrorException{
        return sendDataToPath(path, name, pd);
    }

    public static String register(String path, String name, String pd) throws NetworkErrorException {
        return sendDataToPath(path, name, pd);
    }
}
