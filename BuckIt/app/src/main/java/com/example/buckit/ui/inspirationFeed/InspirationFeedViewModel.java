package com.example.buckit.ui.inspirationFeed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InspirationFeedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InspirationFeedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the inspiration feed fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}