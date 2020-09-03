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
import ai.extime.Adapters.MessageAdapter;
import ai.extime.Events.ClickMessageKanban;
import ai.extime.Events.CloseOtherPopups;
import ai.extime.Events.EventSetStarMessage;
import ai.extime.Events.HidePopupMessage;
import ai.extime.Events.OpenMessageKanban;
import ai.extime.Interfaces.IMessage;
import ai.extime.Models.Contact;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.Template;
import ai.extime.Services.ContactCacheService;

public class KanbanRowTimelineFragment extends Fragment implements IMessage {

    private View mainView;

    public MessageAdapter messageAdapter;

    private ArrayList<EmailMessage> list;

    private RecyclerView recyclerView;


    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.kanban_row_timeline, container, false);

        String type = getArguments().getString("list");

        if(type.equals("Inbox")){

            list = TimeLineFragment.listInbox;
        }else if(type.equals("Unread")){
            list = TimeLineFragment.listUnread;
        }else if(type.equals("Favorite")){
            list = TimeLineFragment.listfavourite;
        }else if(type.equals("Sent")){
            list = TimeLineFragment.listSent;
        }else if(type.equals("Important")){
            list = TimeLineFragment.listImportant;
        }else if(type.equals("Drafts")){
            list = TimeLineFragment.listDrafts;
        }else if(type.equals("Spam")){
            list = TimeLineFragment.listSpam;
        }else if(type.equals("Trash")){
            list = TimeLineFragment.listTrash;
        }






        messageAdapter = new MessageAdapter(mainView.getContext(), list, this, 1);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mainView.getContext());
        recyclerView = mainView.findViewById(R.id.contact_kanban_recycler_t);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        mainView.findViewById(R.id.closerKanb_t).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageAdapter.notifyDataSetChanged();

                EventBus.getDefault().post(new HidePopupMessage());

            }
        });

        return mainView;
    }

    public void updateList(ArrayList<EmailMessage> list){
        messageAdapter.updateKanban(list);
    }


    @Override
    public void clickMessage(EmailMessage message) {
        EventBus.getDefault().post(new ClickMessageKanban(message));
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void openFragmentMessage(EmailMessage message) {
        EventBus.getDefault().post(new OpenMessageKanban(message));
    }

    @Override
    public void openContactList(EmailMessage message) {

    }

    @Override
    public void ShowTemplatePopup(Template template) {

    }

    @Override
    public void setStarMessage(EmailMessage message, boolean star) {
        EventBus.getDefault().post(new EventSetStarMessage(star, message));
    }

    @Override
    public boolean isOpenPreview() {
        return false;
    }
}
