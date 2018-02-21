
package com.dewnaveen.olaplaystudios.DataModel;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DMSong implements Serializable, Parcelable
{

    @SerializedName("song")
    @Expose
    private String song;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("artists")
    @Expose
    private String artists;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
    public final static Creator<DMSong> CREATOR = new Creator<DMSong>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DMSong createFromParcel(Parcel in) {
            return new DMSong(in);
        }

        public DMSong[] newArray(int size) {
            return (new DMSong[size]);
        }

    }
    ;
    private final static long serialVersionUID = -7895252098509931798L;

    protected DMSong(Parcel in) {
        this.song = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.artists = ((String) in.readValue((String.class.getClassLoader())));
        this.coverImage = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public DMSong() {
    }

    /**
     * 
     * @param artists
     * @param song
     * @param coverImage
     * @param url
     */
    public DMSong(String song, String url, String artists, String coverImage) {
        super();
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(song);
        dest.writeValue(url);
        dest.writeValue(artists);
        dest.writeValue(coverImage);
    }

    public int describeContents() {
        return  0;
    }

}
