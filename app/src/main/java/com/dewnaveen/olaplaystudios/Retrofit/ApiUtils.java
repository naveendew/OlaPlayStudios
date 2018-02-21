package com.dewnaveen.olaplaystudios.Retrofit;

/**
 * Created by naveendewangan on 16/12/17.
 */

public class ApiUtils {
    public static final String BASE_URL = "http://starlord.hackerearth.com/";

    public static GETSongList getSongList() {
        return RetrofitClient.getClient(BASE_URL).create(GETSongList.class);
    }
}
