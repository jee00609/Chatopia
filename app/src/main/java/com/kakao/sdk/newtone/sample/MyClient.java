
package com.kakao.sdk.newtone.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizerActivity;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;

import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyClient extends Activity implements TextToSpeechListener {
    // 데이터베이스 사용
    DBHelper myHelper;
    SQLiteDatabase sqlDB;

    // 시간 가져오기 위한 변수
    String d_date, d_time;
    SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss");

    ArrayList<String> results;
    ArrayAdapter<String> adapter;

    protected SpeechRecognizerClient client;
    private TextToSpeechClient ttsClient;           //tts 객체
    private TextToSpeechClient ttsClient2;          //tts 객체
    private Socket socket;                          //소켓생성
    BufferedReader in = null;                       //서버로부터 온 데이터를 읽는다.
    PrintWriter out = null;                         //서버에 데이터를 전송한다.
    EditText input;                                 //화면구성
    Button button, ui_button;                       //화면구성
    ListView chat_text;                             //화면구성
    Intent intent;
    String id, data, intro, intro_temp, serviceType, fromothers, temp_id, temp_msg, sql;
    ScrollView scrollView;                          // 스크롤바
    Thread thread;
    int touch_count = 0, DB_index = 0;
    Boolean touch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        // 데이터베이스 클래스 인터페이스 생성
        myHelper = new DBHelper(this);
        //SQLiteDatabase mydatabase = openOrCreateDatabase("chatDB", MODE_PRIVATE,null);

        Log.v("working : ", "this is working");

        // adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setBackgroundColor(R.drawable.chat_bubble);
                return view;
            }
        };

        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());


        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(this)
                .build();

        ttsClient2 = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(0.5)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(this)
                .build();


        intent = getIntent();                      // 인텐드를 받는다(메인 액티비티에서 넘긴 id)
        id = intent.getStringExtra("id");   // 인텐트로 받은 아이디 저장
        System.out.println("id : " + id);

        //start
        input = (EditText) findViewById(R.id.sendtext);             // 글자입력칸(에디트텍스트)을 찾는다.
        button = (Button) findViewById(R.id.button1);               // 버튼을 찾는다.
        ui_button = (Button) findViewById(R.id.uibutton);
        chat_text = (ListView) findViewById(R.id.chat_text);        //채팅 상황 보여주는 창
        scrollView = (ScrollView) findViewById(R.id.scrollView);    //자동 스크롤 위한 객체

        // 리스트뷰에 어탭터 추가
        chat_text.setAdapter(adapter);

        // 버튼 누를수 있게끔 설정
        setButtonsStatus(true);

        // 지금은 자동 스크롤이며 음성인식 결과로 얻어진 것을 전송하기 위해서
        // 5번 연속 터치하는 것이랑 겹치치 않게 하기 위해서
        // 자동 스크롤만 되게끔 설정함
        chat_text.setEnabled(true);

        // 전송 완료 버튼 누른 경우
        // 순수하게 전송완료 기능만 한다
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // data 변수에 우리가 입력한 메세지를 저장한다.
                // message.add(input.getText().toString());
                data = input.getText().toString(); //글자입력칸에 있는 글자를 String 형태로 받아서 data에 저장
                data.trim();
                if (data != null) {
                    try {
                        //우리가 만든 자바 서버에 메세지 전송
                        out.println(data);

                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else if (data == "quit" || data == "Quit" || data == "QUIT") {
                    MyClient.this.finish();//앱 실행 중단
                }
            }
        });

        // 음성인식 버튼 누를 때
        //ui button -->Listener
        ui_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //음성인식 시작하는 함수 호출하기
                Start_Stt();
            }
        });


        // 소켓 설정 & 스트림 설정 스레드 실행구문
        // 아이디 입력하고 나서 바로 실행됨
        thread = new Thread() {
            public void run() {
                try {
                    // 소켓을 생성&서버와의 연결 시도
                    socket = new Socket("117.17.142.133", 3001);

                    // 입출력 스트림 설정
                    // 서버에서 메세지 읽어올 때  or  서버에 메세지 보낼 때 필요
                    out = new PrintWriter(socket.getOutputStream(), true); //데이터를 전송시 stream 형태로 변환하여                                                                                                                       //전송한다.
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //데이터 수신시 stream을 받아들인다.

                    // 서버한테 아이디 전송
                    out.println(id);

                    //맨 처음에 입장 안내문 출력
                    Intro();

                } catch (IOException e)//예외 처리d
                {
                    e.printStackTrace();
                }

                while (true) {
                    try {
                        temp_id = in.readLine();    //id 수신
                        temp_msg = in.readLine();   //msg 수신

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (temp_id.equals("IntroMessage")) {
                                    adapter.add(temp_msg);
                                    adapter.notifyDataSetChanged();

                                    // 서버로 부터 받은 메세지 전체를 읽어준다.
                                    ttsClient.play(temp_msg);
                                }
                                else if (temp_id.equals("QuitMessage")) {
                                    adapter.add(temp_msg);
                                    adapter.notifyDataSetChanged();

                                    // 서버로 부터 받은 메세지 전체를 읽어준다.
                                    ttsClient.play(temp_msg);
                                }
                                else {
                                    if (temp_id != null && temp_msg != null) {
                                        //완전체로 합치기
                                        fromothers = temp_id + ">>" + temp_msg;

                                        // 서버에서 받아온 메시지 출력
                                        adapter.add(fromothers);
                                        adapter.notifyDataSetChanged();

                                        // 날짜 가져오기
                                        Date date = new Date();
                                        d_date = date_format.format(date);
                                        d_time = time_format.format(date);

                                        // 데이터베이스에 서버에서 받아온 id와 메세지를 저장한다.
                                        sqlDB = myHelper.getWritableDatabase();
                                        sql = String.format("INSERT INTO chatDB VALUES('" + DB_index + "', '" + temp_id + "', '" + temp_msg + "', '" + d_date + "', '" + d_time + "');");
                                        sqlDB.execSQL(sql);
                                        sqlDB.close();
                                        DB_index++;
                                        // Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_LONG).show();

                                        // 서버로 부터 받은 메세지 전체를 읽어준다.
                                        ttsClient.play(temp_id + "  님께서   " + temp_msg + "   라고 보냈습니다");
                                    }
                                }
                            }

                        });
                        // 밑으로 스크롤해준다.
                        //chat_text.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //스레드 시작
        thread.start();
    }

    //버튼의 enable or disable 설정하기
    protected void setButtonsStatus(boolean enabled) {
        findViewById(R.id.uibutton).setEnabled(enabled);    //음성인식 버튼
        findViewById(R.id.button1).setEnabled(enabled);     //메세지 전송 버튼
    }


    //그냥 맨 처음에 입장 안내문 읽어오기 때문에 intro로 이름 지음
    public void Intro() {
        try {

            intro_temp = in.readLine(); // 서버로부터 IntroMessage 읽어온다.
            intro = in.readLine();              //서버로 부터 안내문 읽어서 스트링 변수에 저장

            // 받아온 인트로를 어댑트에 추가
            adapter.add(intro);

            // 변경된 값으로 리스트뷰 갱신
            adapter.notifyDataSetChanged();

            ttsClient.play(intro);              //안내문 음성 출력 해 주기(소리로 들린다)
        } catch (Exception e) {
            e.getMessage();
        }
    }


    //음성인식 시작하는 함수
    public void Start_Stt() {
        serviceType = SpeechRecognizerClient.SERVICE_TYPE_WORD;
        Intent i = new Intent(getApplicationContext(), VoiceRecoActivity.class);

        if (serviceType.equals(SpeechRecognizerClient.SERVICE_TYPE_WORD)) {
            //EditText words = (EditText) findViewById(R.id.words_edit);
            //String wordList = words.getText().toString();

            // Log.i("SpeechSampleActivity", "word list : " + wordList.replace('\n', ','));

            //i.putExtra(SpeechRecognizerActivity.EXTRA_KEY_USER_DICTIONARY, wordList);
        }

        i.putExtra(SpeechRecognizerActivity.EXTRA_KEY_SERVICE_TYPE, serviceType);

        startActivityForResult(i, 0);
    }


    @Override
    //앱 종료시 실행되는 함수
    protected void onStop() {
        super.onStop();
        try {
            //socket.close(); //소켓을 닫는다.
            thread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // API를 더이상 사용하지 않을 때 finalizeLibrary()를 호출한다.
        SpeechRecognizerManager.getInstance().finalizeLibrary();
        TextToSpeechManager.getInstance().finalizeLibrary();
    }

    // 음성인식 결과를 alert창을 통해 보여준다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] temp = new String[20];//음성인식 결과 하나씩 추출할때 쓰는 스트링 임시 변수
        String question = "  를 전송하시겠습니까?  메세지 전송을 원하시면 세번 연속으로 터치해 주세요";
        int count = 0, i = 0;

        System.out.println(data);

        if (resultCode == RESULT_OK) {
            results = data.getStringArrayListExtra(VoiceRecoActivity.EXTRA_KEY_RESULT_ARRAY);

            final StringBuilder builder = new StringBuilder();


            for (String result : results) {
                builder.append(result);
                builder.append("\n");
                temp[i] = result;
            }


            //temp에 저장
            for (int k = 0; k < results.size(); k++) {
                temp[k] = results.get(k);
            }

            //temp에 담긴거 하나씩 읽어주기
            //for문에 넣고 play함수를 연속적으로는 호출하지 못하여
            //문자열 합성 방식을 이용합니다.
            ttsClient2.play("인식률이 가장 높은 내용을 말씀드리겠습니다.  "
                    + temp[0] + question);

            //다른 버튼 오작동을 막기 위해서 false값 설정
            setButtonsStatus(false);

            touch = true;

            // Yes 제스쳐-->5번 연속 터치
            chat_text.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (touch) {
                        touch_count++;    //한번에 2씩 증가한다.(이유: 나도 모르겠다)
                        System.out.println("touch_count : " + touch_count);

                        // 3번 터치한 경우
                        if (touch_count == 6) {
                            ttsClient2.play("메세지를 전송합니다");
                            out.println(results.get(0));

                            //버튼 다시 enable 상태로 변환
                            setButtonsStatus(true);
                            touch_count = 0;//카운트 초기화
                            touch = false;
                        }
                        return true;
                    } else return false;
                }
            });
        }

        //에러가 났을 경우에 뜨는 오류창
        else if (requestCode == RESULT_CANCELED) {
            // 음성인식의 오류 등이 아니라 activity의 취소가 발생했을 때.
            if (data == null) {
                return;
            }

            int errorCode = data.getIntExtra(VoiceRecoActivity.EXTRA_KEY_ERROR_CODE, -1);
            String errorMsg = data.getStringExtra(VoiceRecoActivity.EXTRA_KEY_ERROR_MESSAGE);

            //에러 메세지 출력
            if (errorCode != -1 && !TextUtils.isEmpty(errorMsg)) {
                new AlertDialog.Builder(this).
                        setMessage(errorMsg).
                        setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        show();
            }
        }
    }


    //@Override
    public void onReady() {
        //TODO implement interface DaumSpeechRecognizeListener method
    }

    // @Override
    public void onBeginningOfSpeech() {
        //TODO implement interface DaumSpeechRecognizeListener method
    }

    // @Override
    public void onEndOfSpeech() {
        //TODO implement interface DaumSpeechRecognizeListener method
    }

    // @Override
    public void onError(int errorCode, String errorMsg) {
        //TODO implement interface DaumSpeechRecognizeListener method
        Log.e("SpeechSampleActivity", "onError");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // setButtonsStatus(true);
                Toast.makeText(MyClient.this, "Error 1", Toast.LENGTH_SHORT).show();
            }
        });

        client = null;
    }

    // @Override
    public void onPartialResult(String text) {
        //TODO implement interface DaumSpeechRecognizeListener method
    }

    //@Override
    public void onResults(Bundle results) {
        final StringBuilder builder = new StringBuilder();
        Log.i("SpeechSampleActivity", "onResults");

        ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);

        for (int i = 0; i < texts.size(); i++) {
            builder.append(texts.get(i));
            builder.append(" (");
            builder.append(confs.get(i).intValue());
            builder.append(")\n");
        }


        final Activity activity = MyClient.this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // finishing일때는 처리하지 않는다.
                if (activity.isFinishing()) return;

                AlertDialog.Builder dialog = new AlertDialog.Builder(activity).
                        setMessage(builder.toString()).
                        setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();

                setButtonsStatus(true);

                Toast.makeText(MyClient.this, builder, Toast.LENGTH_SHORT).show();

            }
        });

        client = null;
    }

    // @Override
    public void onAudioLevel(float v) {
        //TODO implement interface DaumSpeechRecognizeListener method
    }

    // @Override
    public void onFinished() {
        Log.i("SpeechSampleActivity", "onFinished");
    }

    // SOLite 사용하기
    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "chatDB", null, 1);
        }

        // 테이블을 생성한다 - DB 처음 만들때 호출됨.
        @Override
        public void onCreate(SQLiteDatabase db) {
            // 테이블 생성
            db.execSQL("CREATE TABLE IF NOT EXISTS chatDB( NUM INTEGER PRIMARY KEY, ID char(20), MSG TEXT, DATE char(20), TIME char(20) );");
        }

        // 테이블을 삭제한 후 다시 생성한다
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS chatDB");
            onCreate(db);
        }
    }
}



