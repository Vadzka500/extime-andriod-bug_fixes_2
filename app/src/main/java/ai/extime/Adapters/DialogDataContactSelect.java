package ai.extime.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.extime.R;

import java.util.ArrayList;

import ai.extime.Enums.ClipboardEnum;
import ai.extime.Models.ContactInfo;

public class DialogDataContactSelect extends RecyclerView.Adapter<DialogDataContactSelect.DialogViewHolder> {

    private ArrayList<ContactInfo> listOfInfo;
    private View mainView;
    private Context context;
    private View rootView;
    private ArrayList<ContactInfo> selectList;

    RecyclerView recyclerView;

    public DialogDataContactSelect(ArrayList<ContactInfo> list, Context context, View rootView, RecyclerView recyclerView){
        this.listOfInfo = list;
        this.context = context;
        this.rootView = rootView;
        selectList = new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    public ArrayList<ContactInfo> getSeleted(){
        return selectList;
    }



    @NonNull
    @Override
    public DialogDataContactSelect.DialogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_select_contact_data, viewGroup, false);

        return new DialogViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder viewHolder, int i) {

        ContactInfo info = listOfInfo.get(i);


        if (info.isEmail) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_emails);
            viewHolder.selectRadio.setVisibility(View.GONE);
            viewHolder.selectCheck.setVisibility(View.VISIBLE);
            viewHolder.type.setText("email");
        }else if(info.isPhone){
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_phone);
            viewHolder.selectRadio.setVisibility(View.VISIBLE);
            viewHolder.selectCheck.setVisibility(View.GONE);
            viewHolder.type.setText("phone");
        }

        if(info.isPrimary)
            viewHolder.acceptData.setVisibility(View.VISIBLE);
        else
            viewHolder.acceptData.setVisibility(View.GONE);

        if(info.isPhone){
            if(selectList.contains(info)){
                viewHolder.selectRadio.setChecked(true);
            }else{
                viewHolder.selectRadio.setChecked(false);
            }
        }

        viewHolder.value1.setText(info.value);

        viewHolder.selectCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectList.add(info);
                }else{
                    selectList.remove(info);
                }

                if(selectList.size() == 1){
                    rootView.findViewById(R.id.checkExtractInb).setVisibility(View.VISIBLE);
                    if(selectList.get(0).isPrimary){
                        ((CheckBox) rootView.findViewById(R.id.checkExtractInb)).setChecked(true);
                    }else{
                        ((CheckBox) rootView.findViewById(R.id.checkExtractInb)).setChecked(false);
                    }
                }else{
                    rootView.findViewById(R.id.checkExtractInb).setVisibility(View.GONE);
                }

            }
        });


        viewHolder.selectRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectList = new ArrayList<>();
                    selectList.add(info);
                }else{
                    selectList.remove(info);
                }

                if(selectList.size() == 1){
                    rootView.findViewById(R.id.checkExtractInb).setVisibility(View.VISIBLE);
                    if(selectList.get(0).isPrimary){
                        ((CheckBox) rootView.findViewById(R.id.checkExtractInb)).setChecked(true);
                    }else{
                        ((CheckBox) rootView.findViewById(R.id.checkExtractInb)).setChecked(false);
                    }
                }else{
                    rootView.findViewById(R.id.checkExtractInb).setVisibility(View.GONE);
                }


                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });



                //notifyDataSetChanged();
            }
        });

        //viewHolder.itemView.requestLayout();

    }

    @Override
    public int getItemCount() {
        return listOfInfo.size();
    }

    static class DialogViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        TextView value1;
        ImageView contactTypeImage;
        CheckBox selectCheck;
        RadioButton selectRadio;

        ImageView acceptData;

        public DialogViewHolder(@NonNull View itemView) {
            super(itemView);

            type = (TextView) itemView.findViewById(R.id.hashtag_text);
            value1 = (TextView) itemView.findViewById(R.id.value1);
            contactTypeImage = (ImageView) itemView.findViewById(R.id.type_image);
            selectCheck = itemView.findViewById(R.id.selectCheck);
            selectRadio = itemView.findViewById(R.id.selectRadio);
            acceptData = itemView.findViewById(R.id.acceptData);


        }
    }
}
