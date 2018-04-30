package superawesome.tv.sanetworktester;

import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import tv.superawesome.lib.sanetwork.file.SAFileDownloader;
import tv.superawesome.lib.sanetwork.file.SAFileDownloaderInterface;
import tv.superawesome.lib.sanetwork.listdownload.SAFileListDownloader;
import tv.superawesome.lib.sanetwork.listdownload.SAFileListDownloaderInterface;
import tv.superawesome.lib.sanetwork.request.SANetwork;
import tv.superawesome.lib.sanetwork.request.SANetworkInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
        JSONObject object = new JSONObject();
//        try {
//            object.put("Content-Type", "application/json");
//            object.put("User-Agent","Mozilla/5.0 (Linux; Android 4.4.4; SD4930UR Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/34.0.0.0 Mobile Safari/537.36");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        SANetwork network = new SANetwork();
        network.sendGET(this, "https://ads.superawesome.tv/v2/ad/35684?test=false&sdkVersion=android_6.1.5&rnd=1385959&bundle=tv.superawesome.demoapp&name=SASDK+-+Android&dauid=251505879&ct=2&lang=en_US&device=phone", null, object, new SANetworkInterface() {
            @Override
            public void saDidGetResponse(int status, String payload, boolean success) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
