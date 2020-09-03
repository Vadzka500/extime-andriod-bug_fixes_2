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
import ai.extime.Interfaces.Postman;

public class FilesConatct extends Fragment {

    private View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.files_contact, viewGroup, false);

        return mainView;
    }




}
