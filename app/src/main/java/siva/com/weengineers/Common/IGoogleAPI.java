package siva.com.weengineers.Common;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by MANIKANDAN on 01-12-2017.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);
}
