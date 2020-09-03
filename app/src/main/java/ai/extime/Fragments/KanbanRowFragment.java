package ai.extime.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ai.extime.Adapters.KanbakContactsAdapter;
import ai.extime.Events.CloseOtherPopups;
import ai.extime.Models.Contact;
import ai.extime.Services.ContactCacheService;

public class KanbanRowFragment extends Fragment {

    private View mainView;

    public KanbakContactsAdapter kanbanContactsAdapter;

    private ArrayList<Contact> list;

    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.kanban_row_adapter, container, false);

        if(getArguments() == null || getArguments().getSerializable("list") == null)
            list = ContactCacheService.getAllContacts(null);
        else {
            list = (ArrayList<Contact>) getArguments().getSerializable("list");

        }


        initRecycler();


        mainView.findViewById(R.id.closerKanb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().findViewById(R.id.profile_popup).getVisibility() == View.VISIBLE)
                getActivity().findViewById(R.id.profile_popup).setVisibility(View.GONE);

                kanbanContactsAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new CloseOtherPopups());
                //closeOtherPopup();
            }
        });

        return mainView;
    }

    public void initRecycler(){
        kanbanContactsAdapter = new KanbakContactsAdapter(list, mainView.getContext(), getActivity());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mainView.getContext());
        recyclerView = mainView.findViewById(R.id.contact_kanban_recycler);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(kanbanContactsAdapter);
    }


}
