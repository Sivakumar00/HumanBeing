package siva.com.weengineers.Common;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import siva.com.weengineers.Interface.IconBetterIdeaService;
import siva.com.weengineers.Interface.NewsService;
import siva.com.weengineers.Remote.IconBetterIdeaClient;
import siva.com.weengineers.Remote.RetrofitClient;

/**
 * Created by MANIKANDAN on 16-11-2017.
 */

public class Common {
    
//AIzaSyDUTRxCxU8NL8JESsJuRhXeTBm7Io23sYc
    public static Location mLastLocation=null;
    public static boolean Authenticated=false;
    public static DatabaseReference destination= FirebaseDatabase.getInstance().getReference().child("Poll");
    private static final String BASE_URL="https://newsapi.org/";
    public static final String API_KEY="eaa9279814774ae28493cbc729946a6e";
    public static final String baseURL="https://maps.googleapis.com";

    public static NewsService getNewsService(){
        return RetrofitClient.getClient(BASE_URL).create(NewsService.class);

    }

    public static IconBetterIdeaService getIconService(){
        return IconBetterIdeaClient.getClient().create(IconBetterIdeaService.class);

    }
    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);

    }

    public static String getAPIUrl(String source,String sortBy,String apiKEY){
        StringBuilder apiUrl=new StringBuilder("https://newsapi.org/v1/articles?source=");
        String sample=apiUrl.append(source)
                .append("&sortBy=")
                .append(sortBy)
                .append("&apiKey=")
                .append(apiKEY)
                .toString();
        Log.e("ERADFADFADFAE",sample);
        return sample;
    }

}
