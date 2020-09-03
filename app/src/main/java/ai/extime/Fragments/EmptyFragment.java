package ai.extime.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.extime.R;

import ai.extime.Interfaces.CardViewPagerCLicker;
import ai.extime.Models.Contact;

public class EmptyFragment  extends Fragment {

    private View mainView;

    private CardViewPagerCLicker cardViewPagerCLicker;

    private long id;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().getSerializable("selectedContact") != null) {
            id = (long) getArguments().getSerializable("selectedContact");
            cardViewPagerCLicker = (CardViewPagerCLicker) getArguments().getSerializable("clicker");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.files_contact, viewGroup, false);

      /*  mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click qq");
                cardViewPagerCLicker.click(id);
            }
        });*/


        return mainView;
    }


}
