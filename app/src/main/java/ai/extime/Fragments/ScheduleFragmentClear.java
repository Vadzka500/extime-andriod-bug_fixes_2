package ai.extime.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.extime.R;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Interfaces.Postman;

public class ScheduleFragmentClear extends Fragment implements PopupsInter {


    private View mainView;

    private Toolbar toolbarC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.schedule_layout_content, viewGroup, false);

        //toolbarC = ((Postman) getActivity()).getToolbar();

        mainView.findViewById(R.id.imageBlockSheduler).setVisibility(View.GONE);

        //setHasOptionsMenu(true);
        return mainView;
    }

    @Override
    public void closeOtherPopup() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //if(!ContactAdapter.checkMerge)
            //menu.getItem(0).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        //((MainActivity) mainView.getContext()).setContactsToContent();

        //((TextView) toolbarC.findViewById(R.id.toolbar_title)).setText("Schedule");

        //if(!ContactAdapter.checkMerge)
           //((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);

       // toolbarC.findViewById(R.id.toolbar_kanban).setVisibility(View.GONE);

        super.onPrepareOptionsMenu(menu);
    }

}
