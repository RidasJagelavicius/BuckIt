package com.example.buckit.ui.achievementWall;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AchievementWallViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AchievementWallViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the acheivement wall fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}