package superawesome.tv.sanetworktester;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String abc = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/dkopqAGR8eYBV5KNQP7wH9UQniqbG4Ga-low.mp4";
        try {
            URI uri = new URI(abc);
            String[] segments = uri.getPath().split("/");
            String seg = segments[segments.length-1];

            Log.d("SuperAwesome", "" + seg);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }
}
