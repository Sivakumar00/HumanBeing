package siva.com.weengineers;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by MANIKANDAN on 18-11-2017.
 */

public class NewsAPP extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }

}
