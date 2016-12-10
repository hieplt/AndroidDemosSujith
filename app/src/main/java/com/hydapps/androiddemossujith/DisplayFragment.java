package com.hydapps.androiddemossujith;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sujit on 08-12-2016.
 */

public class DisplayFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_frag_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = (TextView) view.findViewById(R.id.tv_display_frag_text);
        Bundle b = getArguments();
        int color = Color.BLACK;
        if (b != null) {
            int tab = b.getInt("tab");
            switch (tab) {
                case 0:
                    color = Color.BLACK;
                    break;
                case 1:
                    color = Color.RED;
                    break;
                case 2:
                    color = Color.GREEN;
                    break;
            }
        }
        tv.setTextColor(color);
    }
}
