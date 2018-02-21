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
import com.dewnaveen.olaplaystudios.Interface.PlayCallback;
import com.dewnaveen.olaplaystudios.R;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongDownloaded;
import com.dewnaveen.olaplaystudios.RealmModels.RMSongDownloaded;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RecViewSongsOfflineAdapter extends RecyclerView.Adapter<RecViewSongsOfflineAdapter.MusicViewHolder> {

    private List<RMSongDownloaded> RMSongDownloadeds;
    private Context mContext;
    Bitmap icon;
    int selectedPosition = 0;
    int[] rainbow;
    PlayCallback playCallback;

    public RecViewSongsOfflineAdapter(List<RMSongDownloaded> RMSongDownloadeds, Context mContext, PlayCallback playCallback) {
        this.RMSongDownloadeds = RMSongDownloadeds;
        this.mContext = mContext;
        this.playCallback = playCallback;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_song_offline, null);

        MusicViewHolder contactViewHolder = new MusicViewHolder(view);

        return contactViewHolder;

    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        RMSongDownloaded RMSongDownloaded = RMSongDownloadeds.get(position);

        holder.tvSongName.setText(RMSongDownloaded.getSong());
        holder.tvArtistsName.setText(RMSongDownloaded.getArtists());

        // Handle Redirect Url with Glide
        Glide.with(mContext)
                .load(RMSongDownloaded.getCoverImage())
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
        return RMSongDownloadeds.size();
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



        }
    }




}