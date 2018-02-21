package com.dewnaveen.olaplaystudios.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dewnaveen.olaplaystudios.DataModel.DMSong;
import com.dewnaveen.olaplaystudios.Interface.PlayCallback;
import com.dewnaveen.olaplaystudios.R;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongDownloaded;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RecViewSongsAdapter extends RecyclerView.Adapter<RecViewSongsAdapter.MusicViewHolder> {

    private List<DMSong> dmSongs;
    private Context mContext;
    Bitmap icon;
    int selectedPosition = 0;
    int[] rainbow;
    PlayCallback playCallback;

    public RecViewSongsAdapter(List<DMSong> dmSongs, Context mContext, PlayCallback playCallback) {
        this.dmSongs = dmSongs;
        this.mContext = mContext;
        this.playCallback = playCallback;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_song, null);

        MusicViewHolder contactViewHolder = new MusicViewHolder(view);

        return contactViewHolder;

    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        DMSong dmSong = dmSongs.get(position);

        holder.tvSongName.setText(dmSong.getSong());
        holder.tvArtistsName.setText(dmSong.getArtists());

        // Handle Redirect Url with Glide
        Glide.with(mContext)
                .load(dmSong.getCoverImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.loader_2)
                .error(R.drawable.loader_2)
                .thumbnail(0.2f)
                .crossFade()
                .into(holder.ivCoverImage);

    }

    @Override
    public int getItemCount() {
        return dmSongs.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvArtistsName;
        ImageView ivCoverImage, iv_download;
        LinearLayout lin_root;


        public MusicViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            tvArtistsName = (TextView) itemView.findViewById(R.id.tvArtistsName);
            ivCoverImage = (ImageView) itemView.findViewById(R.id.ivCoverImage);
            iv_download = (ImageView) itemView.findViewById(R.id.iv_download);

            lin_root = (LinearLayout) itemView.findViewById(R.id.lin_root);

            lin_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = getAdapterPosition();
                    playCallback.StartPlaySong(selectedPosition);
                }
            });

            iv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = getAdapterPosition();
                    Log.d("iv_download ", String.valueOf(selectedPosition));
                    try {
//                        createFolder();
                        showDownloadDialog(selectedPosition, view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


        }
    }


    private void downloadFileAsync(final String downloadUrl, final View view) throws Exception {
        //Download File with Okhttp
//        OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)//to get file from Redirected Url
                .build();

        Request request = new Request.Builder().url(downloadUrl).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }

                File folder = new File(Environment.getExternalStorageDirectory() + "/OlaStudio");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    // Do something on success
                    FileOutputStream fos = new FileOutputStream(folder + "/" + dmSongs.get(selectedPosition).getSong() + ".mp3");
//                FileOutputStream fos = new FileOutputStream("d:/tmp.txt");
                    fos.write(response.body().bytes());
                    fos.close();

                    saveDownloadFileRealm();
                    Snackbar snackbar = Snackbar
                            .make(view, "Download Completed", Snackbar.LENGTH_LONG)
                            .setAction("OPEN", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                                            + "/OlaStudio/");
                                    intent.setDataAndType(uri, "text/csv");
                                    mContext.startActivity(Intent.createChooser(intent, "Open folder"));
                                }
                            });


                    snackbar.show();
/*
                    mContext.runOnUiThread(new Runnable() {
                        public void run() {
                            Toasty.info(mContext, "File Saved on InternalStorage/OlaStudio Directory", Toast.LENGTH_SHORT).show();
                        }});
*/


                }

            }
        });
    }

    // Save Downloaded Song Details to Realm DB
    private void saveDownloadFileRealm() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            RMSongDownloaded rmSongDownloaded = new RMSongDownloaded();

            rmSongDownloaded.setSong(dmSongs.get(selectedPosition).getSong());
            rmSongDownloaded.setArtists(dmSongs.get(selectedPosition).getArtists());
            rmSongDownloaded.setUrl(dmSongs.get(selectedPosition).getUrl());
            rmSongDownloaded.setCoverImage(dmSongs.get(selectedPosition).getCoverImage());

            realm.copyToRealm(rmSongDownloaded);

            realm.commitTransaction();

        } finally {
            if (realm != null) {
                realm.close();
            }
        }


    }

    private void showDownloadDialog(final int selectedPosition, final View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Download " + dmSongs.get(selectedPosition).getSong() + ".mp3 ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Toasty.info(mContext, "Downloading " + dmSongs.get(selectedPosition).getSong() + ".mp3 ...", Toast.LENGTH_LONG).show();
                            downloadFileAsync(dmSongs.get(selectedPosition).getUrl(), view);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}