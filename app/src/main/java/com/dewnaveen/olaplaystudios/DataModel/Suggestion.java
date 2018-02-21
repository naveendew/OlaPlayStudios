package com.dewnaveen.olaplaystudios.DataModel;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by naveendewangan on 16/12/17.
 */

@SuppressLint("ParcelCreator")
public class Suggestion implements SearchSuggestion {

    String suggestion_text;

    public Suggestion(String suggestion_text) {
        this.suggestion_text = suggestion_text;
    }

    public String getSuggestion_text() {
        return suggestion_text;
    }

    public void setSuggestion_text(String suggestion_text) {
        this.suggestion_text = suggestion_text;
    }

    @Override
    public String getBody() {
        return suggestion_text;
    }

    @Override
    public int describeContents() {

        return 0;

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
