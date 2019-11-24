package com.kakao.sdk.newtone.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterMember extends AppCompatActivity {
    EditText ID, PW, Q1, Q2;
    String ID_Answer, PW_Answer, Q1_Answer, Q2_Answer;
    Button btn_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registermember);

        //EditText 위젯에 사용자가 입력한 내용 받아서 스트링 변수에 저장하기

        //ID 값 저장
        ID = (EditText) findViewById(R.id.ID);
        ID_Answer = ID.getText().toString();

        //PW 값 저장
        PW = (EditText) findViewById(R.id.PW);
        PW_Answer = PW.getText().toString();

        Q1 = (EditText) findViewById(R.id.Q1);
        Q1_Answer = Q1.getText().toString();

        Q2 = (EditText) findViewById(R.id.Q2);
        Q2_Answer = Q2.getText().toString();

        btn_Register = (Button) findViewById(R.id.btn_Register);


        //회원 가입 완료 버튼 눌렀을 때
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ID_Answer = ID.getText().toString();
                PW_Answer = PW.getText().toString();
                Q1_Answer = Q1.getText().toString();
                Q2_Answer = Q2.getText().toString();

                System.out.println("Click!");
                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            System.out.println("boolean success = " + success);

                            Toast.makeText(getApplicationContext(), "success" + success, Toast.LENGTH_SHORT).show();

                            //서버에서 보내준 값이 true이면?
                            if (success) {

                                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                                finish();


                            } else {//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterMember.this);
                                builder.setMessage("Try another ID")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(ID_Answer, PW_Answer, Q1_Answer, Q2_Answer, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterMember.this);
                queue.add(registerRequest);
            }
        });


    }
}
