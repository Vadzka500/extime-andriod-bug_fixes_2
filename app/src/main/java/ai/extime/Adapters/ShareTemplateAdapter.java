package ai.extime.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.extime.R;

import java.util.ArrayList;

import ai.extime.Activity.MainActivity;
import ai.extime.Fragments.NewTemplateFragment;
import ai.extime.Fragments.ReplyFragment;
import ai.extime.Models.EmailDataTemplate;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.Template;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShareTemplateAdapter extends RecyclerView.Adapter<ShareTemplateAdapter.ShareTemplateViewHolder> {


    private View mainView;

    private ArrayList<Template> list;

    private Activity activity;




    public ShareTemplateAdapter(Activity activity, ArrayList<Template> list){
        this.activity = activity;
        this.list = list;
        //this.shareTemplateMessageReplyI = shareTemplateMessageReplyI;
    }

    @NonNull
    @Override
    public ShareTemplateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_template_card, viewGroup, false);
        return new ShareTemplateAdapter.ShareTemplateViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareTemplateViewHolder holder, int i) {
        Template template = list.get(i);

        holder.accountInitials.setText(getInitials(template.getTitle()));
        holder.name_tamplate.setText(template.getTitle());
        holder.name_tamplate.setTextColor(activity.getResources().getColor(R.color.gray_textView));
        if(!template.isTemplateUser()){
            if(list.get(i).getTitle().equalsIgnoreCase("reply")){
                holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                holder.accountInitials.setTextColor(activity.getResources().getColor(R.color.primary));
                holder.accountInitials.setVisibility(View.GONE);
                holder.iconReply.setImageDrawable(activity.getDrawable(R.drawable.reply));
                holder.iconReply.setVisibility(View.VISIBLE);
                holder.name_tamplate.setTextColor(activity.getResources().getColor(R.color.primary));
            }else if(list.get(i).getTitle().equalsIgnoreCase("New mail")){
                holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                holder.accountInitials.setTextColor(activity.getResources().getColor(R.color.primary));
                holder.accountInitials.setVisibility(View.GONE);
                holder.iconReply.setImageDrawable(activity.getDrawable(R.drawable.icn_remind_message));
                holder.iconReply.setVisibility(View.VISIBLE);
                holder.name_tamplate.setTextColor(activity.getResources().getColor(R.color.primary));
            }else {
                holder.iconReply.setVisibility(View.GONE);
                holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray_light));
                holder.accountInitials.setTextColor(activity.getResources().getColor(R.color.gray_textView));
                holder.accountInitials.setVisibility(View.VISIBLE);
            }
        }else{
            holder.iconReply.setVisibility(View.GONE);
            holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            holder.accountInitials.setTextColor(activity.getResources().getColor(R.color.primary));
            holder.accountInitials.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemplate(template);
            }
        });

    }

    private String getInitials(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String names[] = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }

        if(initials.length() == 2){

            char c_1 = Character.toUpperCase(initials.charAt(0));
            char c_2 = Character.toLowerCase(initials.charAt(1));
            initials = String.valueOf(c_1) + String.valueOf(c_2);
            return initials;
        }else return initials.toUpperCase();

    }

    public void openTemplate(Template template) {

        if(ShareTemplatesMessageReply.sender != null && ShareTemplatesMessageReply.name != null && ShareTemplatesMessageReply.recipient != null){
            EmailDataTemplate emailDataTemplate = new EmailDataTemplate(ShareTemplatesMessageReply.sender, ShareTemplatesMessageReply.name, ShareTemplatesMessageReply.recipient, ShareTemplatesMessageReply.emailMessage);


            Template template1 = new Template();

            template1.setId(template.getId());
            template1.setTitle(template.getTitle());
            template1.setSubject(template.getSubject());
            template1.setTemplateUser(template.isTemplateUser());
            template1.setTemplateText(template.getTemplateText());
            template1.setDateCreate(template.getDateCreate());

            template1.setEmailDataTemplate(emailDataTemplate);
            template1.setListOfFiles(template.getListOfFiles());

            if(activity.findViewById(R.id.clipboardContainer).getVisibility() == View.VISIBLE){
                activity.findViewById(R.id.clipboardPopup).callOnClick();
            }

            if(!ShareTemplatesMessageReply.popup) {
                android.support.v4.app.FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.main_content, ReplyFragment.newInstance(template1, ShareTemplatesMessageReply.emailMessage)).addToBackStack("template").commit();
            }else{
                ShareTemplatesMessageReply.iMessage.ShowTemplatePopup(template1);
            }

            activity.findViewById(R.id.share_template_message).setVisibility(View.GONE);
        }

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ShareTemplateViewHolder extends RecyclerView.ViewHolder {

        CircleImageView accountAvatar;

        TextView accountInitials, name_tamplate;

        ImageView iconReply;

        public ShareTemplateViewHolder(@NonNull View itemView) {
            super(itemView);

            accountAvatar = itemView.findViewById(R.id.accountAvatar);
            accountInitials = itemView.findViewById(R.id.accountInitials);
            name_tamplate = itemView.findViewById(R.id.name_tamplate);
            iconReply = itemView.findViewById(R.id.iconReply);

        }
    }




}
