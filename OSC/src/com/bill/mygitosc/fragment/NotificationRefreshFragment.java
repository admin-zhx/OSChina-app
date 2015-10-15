package com.bill.mygitosc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bill.mygitosc.R;

/**
 * Created by liaobb on 2015/7/29.
 */
public class NotificationRefreshFragment extends Fragment {
    private static String USER_ID = "user_id";
    private int userID;

    public static NotificationRefreshFragment newInstance(int userID) {
        NotificationRefreshFragment fragment = new NotificationRefreshFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.nofication_content_layout, container, false);
        return view;
    }
}
