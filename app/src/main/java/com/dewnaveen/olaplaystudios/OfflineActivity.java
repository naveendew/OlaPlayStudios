package com.dewnaveen.olaplaystudios;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dewnaveen.olaplaystudios.Adapter.RecViewSongsFavAdapter;
import com.dewnaveen.olaplaystudios.Adapter.RecViewSongsOfflineAdapter;
import com.dewnaveen.olaplaystudios.Interface.PlayCallback;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongDownloaded;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongFavourite;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;

public class OfflineActivity extends AppCompatActivity implements PlayCallback {
    private RecyclerView rv_songs;
    List<RMSongFavourite> songList;
    Activity mAct = this;
    PlayCallback playCallback = this;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);


        initToolbar();
        rv_songs = (RecyclerView) findViewById(R.id.rv_songs);

        Realm realm = null;
        RealmResults<RMSongDownloaded> realmResults = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            realmResults = realm.where(RMSongDownloaded.class)
                    .findAll();

            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (realmResults != null) {

            RecViewSongsOfflineAdapter recViewSongsAdapter = new RecViewSongsOfflineAdapter(realmResults, mAct, playCallback);
            rv_songs.setLayoutManager(new LinearLayoutManager(mAct));
            rv_songs.setAdapter(recViewSongsAdapter);
        }else{
            Toasty.error(mAct,"No Offline Song found", Toast.LENGTH_LONG).show();
        }

    }

    private void initToolbar() {
        //Setting Toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView tv_toolTitle = (TextView) findViewById(R.id.tv_toolTitle);
        tv_toolTitle.setText("Downloaded List");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

    }


    @Override
    public void StartPlaySong(int id) {
        //Play Song

    }

}


