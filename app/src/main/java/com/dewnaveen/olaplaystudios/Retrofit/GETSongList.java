package com.dewnaveen.olaplaystudios.Retrofit;

/**
 * Created by naveendewangan on 16/12/17.
 */


import com.dewnaveen.olaplaystudios.DataModel.DMSong;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GETSongList {
    @GET("studio")
    Call<List<DMSong>> getSongs();
}
