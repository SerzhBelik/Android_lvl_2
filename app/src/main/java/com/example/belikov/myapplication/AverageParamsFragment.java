package com.example.belikov.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class AverageParamsFragment extends Fragment {
    private static final String YEARS = "years";

    public static AverageParamsFragment newInstance(Integer years) {
        AverageParamsFragment myFragment = new AverageParamsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(YEARS, years);
        myFragment.setArguments(bundle);
        return myFragment;
    }
}