package ai.extime.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ai.extime.Enums.ExtractEnums;
import ai.extime.Models.Extrator;
import com.extime.R;

public class ExtratorAdapter extends RecyclerView.Adapter<ExtratorAdapter.ExtratorViewHolder> {

    private View mainView;

    private Context context;

    private ArrayList<Extrator> listExtract;

    public ExtratorAdapter(ArrayList<Extrator> list, Context context){
        this.listExtract = list;
        this.context = context;
    }

    public void updateList(ArrayList<Extrator> list){
        this.listExtract = list;
        notifyDataSetChanged();
    }

    public int getSizeList(){
        return listExtract.size();
    }

    @Override
    public ExtratorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_extrat, parent, false);
        return new ExtratorViewHolder(mainView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ExtratorViewHolder holder, int position) {
        Extrator extrator = listExtract.get(position);

        holder.textView.setText(extrator.getText());

        if(extrator.getType() != null && !extrator.getType().equals(ExtractEnums.NAME)) {
            holder.textView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            if(extrator.getType().equals(ExtractEnums.FACEBOOK)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_facebook));
            if(extrator.getType().equals(ExtractEnums.INSTAGRAM)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_instagram));
            if(extrator.getType().equals(ExtractEnums.LINKEDIN)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_linkedin));
            if(extrator.getType().equals(ExtractEnums.VK)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_vk));
            if(extrator.getType().equals(ExtractEnums.TWITTER)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_twitter_48));
            if(extrator.getType().equals(ExtractEnums.YOUTUBE)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_youtube_48));
            if(extrator.getType().equals(ExtractEnums.MEDIUM)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.medium_size_64));
            if(extrator.getType().equals(ExtractEnums.WEB)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_popup_web_blue));
            if(extrator.getType().equals(ExtractEnums.GITHUB)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.github_logo_64));

            if(extrator.getType().equals(ExtractEnums.ANGEL)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.angel));
            if(extrator.getType().equals(ExtractEnums.TUMBL)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tumblr));
            if(extrator.getType().equals(ExtractEnums.G_DOC)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.google_docs));
            if(extrator.getType().equals(ExtractEnums.G_SHEET)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.google_sheets));


            //if(extrator.getType().equals(ExtractEnums.GITHUB)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.github_logo_64));
            //if(extrator.getType().equals(ExtractEnums.GITHUB)) holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.github_logo_64));



        }else{
            holder.textView.setTextColor(context.getResources().getColor(R.color.gray));
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.profile_blue));

        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    extrator.setCheck(true);
                }else
                    extrator.setCheck(false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listExtract.size();
    }

    public ArrayList<Extrator> getListExtract() {
        return listExtract;
    }

    static class ExtratorViewHolder extends RecyclerView.ViewHolder {

        View card;
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;

        ExtratorViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.imageTypeExtract);
            textView = (TextView) itemView.findViewById(R.id.extractValue);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkExtract);

        }
    }
}
