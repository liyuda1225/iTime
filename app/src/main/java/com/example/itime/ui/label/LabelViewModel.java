package com.example.itime.ui.label;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LabelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LabelViewModel() {
        mText = new MutableLiveData<> ( );
        mText.setValue ("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}