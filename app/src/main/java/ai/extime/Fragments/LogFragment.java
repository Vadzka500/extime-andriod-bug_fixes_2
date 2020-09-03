package ai.extime.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.ContactAdapter;

import ai.extime.Interfaces.PopupsInter;
import ai.extime.Interfaces.Postman;

import com.extime.R;

import java.util.Locale;

/**
 * Created by patal on 21.09.2017.
 */

public class LogFragment extends Fragment implements PopupsInter {

    private View mainView;

    private Toolbar toolbarC;

    private Activity activityApp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.log_layout_content, viewGroup, false);

        toolbarC = ((Postman) getActivity()).getToolbar();

        setHasOptionsMenu(true);

        activityApp.findViewById(R.id.barFlipper).setVisibility(View.GONE);

        initViews();


        return mainView;
    }

    public void initViews(){

        activityApp.findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);
    }

    @Override
    public void closeOtherPopup() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!ContactAdapter.checkMerge)
        menu.getItem(0).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        ((MainActivity) mainView.getContext()).hideViewFlipper();
        ((MainActivity) mainView.getContext()).startDrawer();

        ((TextView) toolbarC.findViewById(R.id.toolbar_title)).setText("Home");

        if(!ContactAdapter.checkMerge)
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);

        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }

}
