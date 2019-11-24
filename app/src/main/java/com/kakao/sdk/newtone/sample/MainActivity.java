package com.kakao.sdk.newtone.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity
{
    EditText editId, editPW;
    String ID,PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //안드로이드 3.0 부터는 mainactivity에서 인터넷 연결시
        //꼭 필요한 코드 부분이다!!
        //여기부터~
        int SDK_INT = android.os.Build.VERSION.SDK_INT;

        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //여기까지~

        //로그인 버튼
        Button Login = (Button) findViewById(R.id.btn_Login);

        //회원가입 버튼
        Button New_Register=(Button)findViewById(R.id.New_Register);



        //입력한 ID 값을 ID 변수에 저장한다.
        editId = (EditText)findViewById(R.id.ID);  // 에디트 텍스트 변수 선언
        ID = editId.getText().toString();           // 에디트 텍스트에 입력된 값(id == msg) 가져오기

        //입력한 PW값을  PW 변수에 저장한다.
        editPW=(EditText)findViewById(R.id.PW);
        PW=editPW.getText().toString();

        //로그인 버튼 눌렀을 때
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ID = editId.getText().toString();
                PW = editPW.getText().toString();

                System.out.println("Click!");
                System.out.println("This is login in ID="+ID);
                System.out.println("This is login in PW="+PW);
                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            System.out.println("boolean success = "+success);

                            Toast.makeText(getApplicationContext(), "success"+success, Toast.LENGTH_SHORT).show();

                            //서버에서 보내준 값이 true이면?
                            if(success){

                                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                String ID = jsonResponse.getString("ID");
                                String PW = jsonResponse.getString("PW");
                                System.out.println("ID = "+ID);
                                System.out.println("PW = "+PW);

                                Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();

                                //로그인에 성공했으므로 MainActivity로 넘어감
                                Intent intent=new Intent(getApplicationContext(),MyClient.class);
                                intent.putExtra("id", ID);
                                intent.putExtra("PW", PW);
                                startActivity(intent);

                            }else{//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Login failed")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();


                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(ID, PW, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });


        //회원가입 버튼 눌렀을 때
        New_Register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //회원가입 창 띄우기
                Intent registermember=new Intent(getApplicationContext(),RegisterMember.class);
                startActivity(registermember);

            }
        });

    }

    public void Warning_Message()
    {
        Looper.prepare();
        Toast.makeText(getApplicationContext(),"ID/PW 확인해주세요",Toast.LENGTH_LONG).show();
        Looper.loop();
    }




}