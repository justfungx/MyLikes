package tw.org.iii.mylikes;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager mgr;
    private String data;
    private TextView mesg;
    private StringBuffer sb;
    private UIHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new UIHandler();
        mesg = (TextView)findViewById(R.id.mesg);

        mgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()){
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                while (ifs.hasMoreElements()){
                    NetworkInterface ip = ifs.nextElement();
                    Enumeration<InetAddress> ips = ip.getInetAddresses();
                    while (ips.hasMoreElements()){
                        InetAddress ia = ips.nextElement();
                        Log.d("brad", ia.getHostAddress());
                    }
                }


            } catch (SocketException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("brad", "NOT Connect");
        }


    }

    public void test1(View v){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://www.google.com");
//                    HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
//                    conn.connect();
//                    InputStream in = conn.getInputStream();
//                    int c; StringBuffer sb = new StringBuffer();
//                    while ( (c = in.read()) != -1){
//                        sb.append((char)c);
//                    }
//                    in.close();
//                    Log.d("brad", sb.toString());
//                }catch(Exception ee){
//                    Log.d("brad", ee.toString());
//                }
//            }
//        }.start();

        mesg.setText("");

        MyTread mt1 = new MyTread();
        mt1.start();
    }

    private class MyTread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL("http://ybjson01.youbike.com.tw:1002/gwjs.json");
                HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));
                data = reader.readLine();
                reader.close();
                parseJSON();
            }catch(Exception ee){
                Log.d("brad", ee.toString());
            }
        }
    }

    private void parseJSON(){
        sb = new StringBuffer();
        try {
            JSONArray root = new JSONArray(data);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                String name = row.getString("sna");
                String addr = row.getString("sbi");
                Log.d("brad", name + " -> " + addr);
                sb.append(name + " -> " + addr + "\n");
            }
            handler.sendEmptyMessage(0);
        }catch(Exception ee){
            Log.d("brad", ee.toString());
        }
    }

    public void test2(View v){

    }

    public void test3(View v){

    }

    private class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mesg.setText(sb);


        }
    }

}
