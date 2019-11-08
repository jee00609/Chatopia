package com.kakao.sdk.newtone.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginDB extends AsyncTask<Void, Integer, Void>
{
    String id,pw;
    String data = "";
    boolean check;
    @Override
    protected Void doInBackground(Void... unused) {

        /* 인풋 파라메터값 생성 */
        String param = "ID=" + id+"&"+ "PW=" + pw + "";
        try {
            /* 서버연결 */
            URL url = new URL(
                    "http://cslin.skuniv.ac.kr/~kimmije1009/chatopia/chatopia_login.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();

            /* 안드로이드 -> 서버 파라메터값 전달 */
            OutputStream outs = conn.getOutputStream();
            outs.write(param.getBytes("UTF-8"));
            outs.flush();
            outs.close();

            /* 서버 -> 안드로이드 파라메터값 전달 */
            InputStream is = null;
            BufferedReader in = null;


            is = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            String line = null;
            StringBuffer buff = new StringBuffer();
            while ( ( line = in.readLine() ) != null )
            {
                buff.append(line + "\n");
            }
            data = buff.toString().trim();
            Log.e("RECV DATA",data);





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//
//        if(data.equals("1"))
//        {
//
//        }
//        else if(data.equals("0"))
//        {
//
//        }
//        else
//        {
//
//        }
//    }



    public void getInfo(String ID,String PW)
    {
        this.id=ID;
        this.pw=PW;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(data.equals("1"))
        {
            check=true;
        }
        else if(data.equals("0"))
        {
            check=false;
        }
        else
        {
            check=false;
        }
    }



}
