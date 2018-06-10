package siva.com.weengineers.Remote;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MANIKANDAN on 16-11-2017.
 */

public class RetrofitClient {

    private static Retrofit retrofit=null;
    public static Retrofit getClient(String baseUrl){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}

