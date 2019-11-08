package com.kakao.sdk.newtone.sample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterDB extends AsyncTask<Void, Integer, Void>
{
    String id,pw,q1,q2,str,receiveMsg;

    @Override
    protected Void doInBackground(Void... unused) {

        /* 인풋 파라메터값 생성 */
        String param = "ID=" + id + "&" + "PW=" + pw + "&" + "Q1=" + q1 + "&" + "Q2=" + q2 + "";
        BufferedReader reader;
        JSONObject json;
        System.out.println(param);
        try {
            /* 서버연결 */
            URL url = new URL(
                    "http://cslin.skuniv.ac.kr/~kimmije1009/chatopia/chatopia_Register.php");
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

            System.out.println("전송 완료");

//            /* 서버 -> 안드로이드 파라메터값 전달 */
            InputStream is = null;
            BufferedReader in = null;
            String data = "";

            is = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            String line = null;
            StringBuffer buff = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buff.append(line + "\n");
            }
            data = buff.toString().trim();
            Log.v("RECV DATA", data);


        } catch (Exception e) {

        }

        return null;
    }

    public void getInfo(String ID,String PW,String Q1,String Q2)
    {
        id=ID;
        pw=PW;
        q1=Q1;
        q2=Q2;
        System.out.println("parameters :"+id+""+pw+""+q1+""+q2);
        Log.v("parameters:",id+pw+q1+q2);
    }

//    public void loadData(String json) throws JSONException
//    {
//        JSONArray array=new JSONArray(json);
//        String []heroes=new String[array.length()];
//
//        JSONObject obj=array.getJSONObject(0);
//        heroes[0]=obj.getString("success");
//
//        System.out.println("Msg from php server : "+heroes[0]);
//
//    }

}
