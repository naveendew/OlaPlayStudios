package com.dewnaveen.olaplaystudios;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dewnaveen.olaplaystudios.Adapter.RecViewSongsAdapter;
import com.dewnaveen.olaplaystudios.Class.Constants;
import com.dewnaveen.olaplaystudios.Class.Utils;
import com.dewnaveen.olaplaystudios.DataModel.DMSong;
import com.dewnaveen.olaplaystudios.DataModel.Suggestion;
import com.dewnaveen.olaplaystudios.Interface.PlayCallback;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongDownloaded;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongFavourite;
import com.dewnaveen.olaplaystudios.Retrofit.ApiUtils;
import com.dewnaveen.olaplaystudios.Retrofit.GETSongList;
import com.dewnaveen.olaplaystudios.Service.NotificationService;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlayCallback {

    private GETSongList getSongList;
    List<DMSong> songList;

    //Suggestion List for Searchview
    List<Suggestion> lstResult;

    TextView tv_test;

    private RecyclerView rv_songs;
    ProgressDialog progress;

    private SimpleExoPlayer exoPlayer;
    LinearLayout media_controller;


    PlayCallback playCallback = this;
    Activity mAct = this;

    ImageView ivCoverImage_;
    TextView tvSongName_, tvArtistsName_;
    ImageButton btnPrev, btnNext;
    ImageView iv_fav;
    int selected_index = 0;
    FloatingSearchView mSearchView;
    String filter_ = "";
    List<DMSong> filtered_songList = new ArrayList<>();
    boolean doubleBackToExitPressedOnce = false;

    ImageView iv_refresh;
    private RelativeLayout rel_seacrh;
    Dialog dialog;
    LinearLayout lin_LoadProgress, lin_loadtrack;
    MediaSession mSession;

    //Buffersize for Streaming with Exoplayer
    private static final int DEFAULT_MIN_BUFFER_MS = 2000;
    private static final int DEFAULT_MAX_BUFFER_MS = 5000;
    private static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;

    Intent serviceIntent = null;

    //Initialize Realm Object
    private Realm realm;

    //BroadcastReceiver to trigger notification click listeners
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equalsIgnoreCase(Constants.ACTION.PLAY_ACTION)) {
                // Play/Pause Song
                setPlayPause(!isPlaying);
                Log.d("BroadcastReceiver", "PLAY_ACTION");
            } else if (action.equalsIgnoreCase(Constants.ACTION.NEXT_ACTION)) {
                // Next Song
                Log.d("BroadcastReceiver", "NEXT_ACTION");
                if (selected_index < songList.size())
                    StartPlaySong(selected_index + 1);
            } else if (action.equalsIgnoreCase(Constants.ACTION.PREV_ACTION)) {
                // Prev Song
                Log.d("BroadcastReceiver", "PREV_ACTION");
                if (selected_index < songList.size())
                    StartPlaySong(selected_index - 1);
            } else if (action.equalsIgnoreCase(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                // Exit Notification
                if (exoPlayer != null)
                    exoPlayer.release();

                if (serviceIntent != null)
                    stopService(serviceIntent);
            }
        }

    };

    //ExoPlayer Listener
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.i(TAG, "onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG, "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG, "Playback ended!");
                    if (selected_index < songList.size())
                        StartPlaySong(selected_index + 1);
                    //Stop playback and return to start position
//                    setPlayPause(false);
//                    exoPlayer.seekTo(0);
                    break;
                case ExoPlayer.STATE_READY:
                    Log.i(TAG, "ExoPlayer ready! pos: " + exoPlayer.getCurrentPosition()
                            + " max: " + stringForTime((int) exoPlayer.getDuration()));
                    lin_loadtrack.setVisibility(View.GONE);
                    setProgress();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG, "Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i(TAG, "onPlaybackError: " + error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }

/*
        @Override
        public void onPositionDiscontinuity() {
            Log.i(TAG, "onPositionDiscontinuity");
        }
*/
    };

    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageButton btnPlay;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;

    private static final String TAG = "MainActivity.class";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Saving First Run Shared Preference
        Utils.saveSharedSetting(mAct, SplashActivity.PREF_USER_FIRST_TIME, "false");

        initToolbar();
        initViews();
        initDrawer();

        permissioncheck();

        getSongList();

        regReciever();

    }

    private void initToolbar() {
        //Setting Toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    private void permissioncheck() {
        PermissionGen.with(mAct)
                .addRequestCode(100)
                .permissions(
//                        Manifest.permission.READ_CALL_LOG,
//                        Manifest.permission.RECEIVE_SMS,
//                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();

    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        Toasty.success(this, "Permission Granted", Toast.LENGTH_LONG, true).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toasty.error(this, "Permission NOT Granted", Toast.LENGTH_LONG, true).show();
    }


    private void regReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction(Constants.ACTION.NEXT_ACTION);
        intentFilter.addAction(Constants.ACTION.PLAY_ACTION);
        intentFilter.addAction(Constants.ACTION.PREV_ACTION);
        intentFilter.addAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        // register the receiver
        registerReceiver(mReceiver, intentFilter);

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initViews() {
        tv_test = (TextView) findViewById(R.id.tv_test);
        rv_songs = (RecyclerView) findViewById(R.id.rv_songs);

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        rel_seacrh = (RelativeLayout) findViewById(R.id.rel_seacrh);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);

    }

    private void getSongList() {

        //Show Progress Dialog
        setupDialog();
        tv_test.setText("Loading...");

        iv_refresh.setVisibility(View.GONE);
        tv_test.setVisibility(View.GONE);
        rel_seacrh.setVisibility(View.VISIBLE);

        getSongList = ApiUtils.getSongList();
        getSongList.getSongs().enqueue(new Callback<List<DMSong>>() {
            @Override
            public void onResponse(Call<List<DMSong>> call, final Response<List<DMSong>> response) {

                mAct.runOnUiThread(new Runnable() {
                    public void run() {
                        if (response.isSuccessful()) {
                            songList = response.body();
                            Log.d("MainActivity", "posts loaded from API " + songList.size());
                            if (songList != null && songList.size() > 0) {

                                setupAdapter();
                                tv_test.setText(songList.toString());
                                Toasty.success(mAct, "Success! Data Loaded From Server", Toast.LENGTH_SHORT, true).show();

                            } else {
                                setupRefresh();
                                Toasty.error(mAct, "Error Connecntin to Server! Please Try Again", Toast.LENGTH_SHORT, true).show();
                            }
                        } else {
                            int statusCode = response.code();
                            setupRefresh();
                            Toasty.error(mAct, "Error Connecntin to Server! Please Try Again", Toast.LENGTH_SHORT, true).show();
                            // handle request errors depending on status code
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<List<DMSong>> call, Throwable t) {
//                showErrorMessage();
                Toasty.error(mAct, "Error Connecntin to Server! Please Try Again ", Toast.LENGTH_SHORT, true).show();
                Log.d("MainActivity", "error loading from API");
                Log.e("MainActivity", t.toString());
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRefresh();
                    }
                });

            }
        });
    }

    private void setupRefresh() {
        hidePDialog();
        iv_refresh.setVisibility(View.VISIBLE);
        tv_test.setText("No Internet Connection ! Try Again ! Click the above icon...");
        tv_test.setVisibility(View.VISIBLE);
        rel_seacrh.setVisibility(View.GONE);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongList();
            }
        });

    }

    private void setupAdapter() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (songList != null) {
                    setUpSearchView();
                    //Setup RecyclerView
                    RecViewSongsAdapter recViewSongsAdapter = new RecViewSongsAdapter(songList, mAct, playCallback);
                    rv_songs.setLayoutManager(new LinearLayoutManager(mAct));
                    rv_songs.setAdapter(recViewSongsAdapter);
                } else {
                    Toasty.error(mAct, "No Internet Connection Available to Start App", Toast.LENGTH_LONG).show();
                    setupRefresh();

                }
                hidePDialog();


            }
        });

    }

    private void prepareExoPlayerFromURL(Uri uri) {

        //Setup Exoplayer
        Handler mHandler = new Handler();

        TrackSelector trackSelector = new DefaultTrackSelector();

        LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
                DEFAULT_MIN_BUFFER_MS, DEFAULT_MAX_BUFFER_MS, DEFAULT_BUFFER_FOR_PLAYBACK_MS, DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";


        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);// For Redirected Url

        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                mHandler, null);

/*
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Ola Play Studios"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
*/

        exoPlayer.addListener(eventListener);

        exoPlayer.prepare(audioSource);
        initMediaControls();

        //Start Playing
        setPlayPause(true);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                exitUI();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_songs) {

            // Handle the camera action
        } else if (id == R.id.nav_fav) {

            startActivity(new Intent(mAct, FavouriteActivity.class));

        } else if (id == R.id.nav_offline) {
            startActivity(new Intent(mAct, OfflineActivity.class));

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDialog() {
        //Show Progress Dialog
        progress = new ProgressDialog(mAct);
        progress.setTitle("Loading Please wait ...");
        progress.setCancelable(false);
        progress.show();
    }

    private void hidePDialog() {
        //Hide Progress Dialog
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    private String stringForTime(int timeMs) {
        //Convertor for Playback Seekbar
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    private void initMediaControls() {
        //Initialize Media Controls for Exoplayer
        initPlayButton();
        initControlButtons();
        initSeekBar();
        initTxtTime();
    }


    private void initControlButtons() {

        //Initialize Exoplayer Control Buttons

        tvSongName_ = (TextView) dialog.findViewById(R.id.tvSongName_);
        tvArtistsName_ = (TextView) dialog.findViewById(R.id.tvArtistsName_);
        btnPrev = (ImageButton) dialog.findViewById(R.id.btnPrev);
        btnNext = (ImageButton) dialog.findViewById(R.id.btnNext);
        ivCoverImage_ = (ImageView) dialog.findViewById(R.id.ivCoverImage_);
        iv_fav = (ImageView) dialog.findViewById(R.id.iv_fav);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_index > 0)
                    StartPlaySong(selected_index - 1);
            }
        });

        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFavouriteRealm(selected_index);
                if (iv_fav.getDrawable().getConstantState() == (mAct.getResources().getDrawable(R.drawable.ic_star).getConstantState()))
                    iv_fav.setImageDrawable(ContextCompat.getDrawable(mAct, R.drawable.ic_star_fill));
                else
                    iv_fav.setImageDrawable(ContextCompat.getDrawable(mAct, R.drawable.ic_star));
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_index < songList.size())
                    StartPlaySong(selected_index + 1);
            }
        });

    }

    private void initPlayButton() {
        //Initialize Exoplayer Play Button

        btnPlay = (ImageButton) dialog.findViewById(R.id.btnPlay);
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
    }

    private void initTxtTime() {
        txtCurrentTime = (TextView) dialog.findViewById(R.id.time_current);
        txtEndTime = (TextView) dialog.findViewById(R.id.player_end_time);
    }


    private void setPlayPause(boolean play) {
        //Play/Pause Player

        isPlaying = play;
        exoPlayer.setPlayWhenReady(play);
        if (!isPlaying) {
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        } else {
            setProgress();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void initSeekBar() {
        //Initialize Seekbar
        seekPlayerProgress = (SeekBar) dialog.findViewById(R.id.mediacontroller_progress);
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);

    }

    private void setProgress() {
        //Set Seekbar progress
        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);
        txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {
                    seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }


    @Override
    public void StartPlaySong(int id) {
        //Play Song

        selected_index = id;
        if (id >= 0) {
            if (exoPlayer != null)
                exoPlayer.stop();
            PlayerDialog(id);

            startServicePlay(id);
//            getUrlOk(id);

        }

    }

    private void setupPlayerUI(int id) {
        //Set up Play Dialog
        lin_LoadProgress.setVisibility(View.GONE);

        Glide.with(mAct)
                .load(songList.get(id).getCoverImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.loader_2)
                .error(R.drawable.loader_2)
                .thumbnail(0.5f)
                .crossFade()
                .into(ivCoverImage_);
        tvSongName_.setText(songList.get(id).getSong());
        tvArtistsName_.setText(songList.get(id).getArtists());

    }

    private void setUpSearchView() {
        //Set up Search View

        lstResult = new ArrayList<>();

        if (songList != null) {

            int max = songList.size();
            for (int i = 0; i < max; i++) {

                lstResult.add(new Suggestion(songList.get(i).getSong()));

            }
        }

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                mSearchView.showProgress();

                lstResult = new ArrayList<>();
                try {
                    for (DMSong song : songList) {

                        if (song.getSong().toLowerCase().trim().contains(newQuery.toLowerCase().trim()))
                            lstResult.add(new Suggestion(song.getSong()));


                    }
                } finally {

                }


                mSearchView.swapSuggestions(lstResult);

                mSearchView.hideProgress();

            }
        });


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                mSearchView.setSearchText(searchSuggestion.getBody());
                mSearchView.clearSuggestions();
                mSearchView.clearSearchFocus();
                filter_ = searchSuggestion.getBody();
                searchList();
            }

            @Override
            public void onSearchAction(String query) {
                filter_ = query;
                searchList();
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                mSearchView.swapSuggestions(lstResult);
                mSearchView.setSearchText(filter_);

            }

            @Override
            public void onFocusCleared() {
                filter_ = mSearchView.getQuery();
                mSearchView.setSearchText(filter_);

                searchList();

            }
        });

    }


    private void searchList() {
        //Search Songs

        filtered_songList = new ArrayList<>();
        for (DMSong song : songList) {
            String key_ = song.getSong() + song.getArtists();
            key_ = key_.toLowerCase().trim();

            if (key_.contains(filter_.toLowerCase().trim()))
                filtered_songList.add(song);

        }

        RecViewSongsAdapter songsAdapter = new RecViewSongsAdapter(filtered_songList, mAct, this);
        rv_songs.setLayoutManager(new LinearLayoutManager(mAct));
        rv_songs.setAdapter(songsAdapter);

        if (filtered_songList.size() < 1) {

            Toasty.error(mAct, "No Result Found", Toast.LENGTH_SHORT).show();
            tv_test.setText("No Result Found");
            tv_test.setVisibility(View.VISIBLE);
        } else {
            Toasty.success(mAct, filtered_songList.size() + " Result Found", Toast.LENGTH_SHORT).show();
            tv_test.setVisibility(View.GONE);
        }

    }

    private void exitUI() {
        // Exit App
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void PlayerDialog(int selectedPosition) {

        //Initialize Player Dialog
        if (dialog == null) {

            dialog = new Dialog(mAct);


            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            dialog.setContentView(R.layout.dialog_player);
            lin_loadtrack = (LinearLayout) dialog.findViewById(R.id.lin_loadtrack);
            lin_LoadProgress = (LinearLayout) dialog.findViewById(R.id.lin_LoadProgress);
        }

        lin_LoadProgress.setVisibility(View.VISIBLE);
        lin_loadtrack.setVisibility(View.VISIBLE);

        String song_url = songList.get(selectedPosition).getUrl();

        prepareExoPlayerFromURL(Uri.parse(song_url));

        Realm realm = null;
        RMSongFavourite realmResults = null;
        try {

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            realmResults = realm.where(RMSongFavourite.class)
                    .equalTo("song", songList.get(selectedPosition).getSong())
                    .findFirst();

            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (realmResults == null)
                iv_fav.setImageDrawable(ContextCompat.getDrawable(mAct, R.drawable.ic_star));
            else
                iv_fav.setImageDrawable(ContextCompat.getDrawable(mAct, R.drawable.ic_star_fill));

        }


        setupPlayerUI(selectedPosition);

        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        if (exoPlayer != null)
            exoPlayer.release();
        //Un Register Broadcast Receiver
        unregisterReceiver(mReceiver);
    }


    public void startServicePlay(int id) {
        //Start Notification Service
        serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        serviceIntent.putExtra("Cover", songList.get(id).getCoverImage());
        serviceIntent.putExtra("Track", songList.get(id).getSong());
        serviceIntent.putExtra("Artist", songList.get(id).getArtists());
        startService(serviceIntent);
    }

    // Save Favourite Song Details to Realm DB
    private void saveFavouriteRealm(int selectedPosition) {


        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            RMSongFavourite realmResults = realm.where(RMSongFavourite.class)
                    .equalTo("song", songList.get(selectedPosition).getSong())
                    .findFirst();


            if (realmResults == null) {

                RMSongFavourite rmSongFavourite = new RMSongFavourite();

                rmSongFavourite.setSong(songList.get(selectedPosition).getSong());
                rmSongFavourite.setArtists(songList.get(selectedPosition).getArtists());
                rmSongFavourite.setUrl(songList.get(selectedPosition).getUrl());
                rmSongFavourite.setCoverImage(songList.get(selectedPosition).getCoverImage());

                realm.copyToRealm(rmSongFavourite);

                Toasty.success(mAct, "Added to Favourite List", Toast.LENGTH_LONG).show();
            } else {
                realmResults.deleteFromRealm();
                Toasty.success(mAct, "Deleted from Favourite List", Toast.LENGTH_LONG).show();

            }

            realm.commitTransaction();

        } finally {
            if (realm != null) {
                realm.close();
            }
        }


    }


}
