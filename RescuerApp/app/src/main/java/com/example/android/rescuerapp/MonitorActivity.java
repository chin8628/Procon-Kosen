package com.example.android.rescuerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.rescuerapp.wifihotspotutils.ClientScanResult;
import com.example.android.rescuerapp.wifihotspotutils.FinishScanListener;
import com.example.android.rescuerapp.wifihotspotutils.mWifiApManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorActivity extends AppCompatActivity {

    private mWifiApManager wifiApManager;

    private Button mRefreshBtn;
    private TextView countDevice;

    private int mNumConnectedClient = 0;
    private ArrayList<String> mMacConnectedClient = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        countDevice = (TextView) findViewById(R.id.countDevice);

        wifiApManager = new mWifiApManager(this);

        Timer timer = new Timer();
        timer.schedule(new ScanInterval(), 0, 2000);

        mRefreshBtn = (Button) findViewById(R.id.refreshBtn);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                for (ClientScanResult clientScanResult : clients) {
                    if (!mMacConnectedClient.contains(clientScanResult.getHWAddr()) &&
                            clientScanResult.isReachable()){
                        mMacConnectedClient.add(clientScanResult.getHWAddr());
                        mNumConnectedClient += 1;
                    }
                }

                countDevice.setText(Integer.toString(mNumConnectedClient));

                String log = "";
                for (ClientScanResult clientScanResult : clients) {
                    log = "IpAddr: " + clientScanResult.getIpAddr() + "\n" +
                    "Device: " + clientScanResult.getDevice() + "\n" +
                    "HWAddr: " + clientScanResult.getHWAddr() + "\n" +
                    "isReachable: " + clientScanResult.isReachable() + "\n";
                    Log.v("APLog", log);
                }
            }
        });
    }

    class ScanInterval extends TimerTask {
        public void run() {
            scan();
            Log.v("wow", "wow");
        }
    }

}
