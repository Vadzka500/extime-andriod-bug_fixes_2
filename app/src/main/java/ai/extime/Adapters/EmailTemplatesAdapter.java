package ai.extime.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.extime.Fragments.ReplyFragment;
import ai.extime.Interfaces.IEmails;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.MessageData;
import ai.extime.Models.Template;
import de.hdodenhof.circleimageview.CircleImageView;

public class EmailTemplatesAdapter extends RecyclerView.Adapter<EmailTemplatesAdapter.EmailsTemplatesViewHolder> {

    private View mainView;

    private ArrayList<Template> list;

    private IEmails iEmails;

    private Context context;

    private ReplyFragment replyFragment;

    private String searchString;

    public EmailTemplatesAdapter(ArrayList<Template> list, IEmails iEmails, Context context, ReplyFragment replyFragment) {
        this.list = list;
        this.iEmails = iEmails;
        this.context = context;
        searchString = "";
        this.replyFragment = replyFragment;
    }

    @NonNull
    @Override
    public EmailsTemplatesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_card, viewGroup, false);
        return new EmailsTemplatesViewHolder(mainView);
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

        if (initials.length() == 2) {

            char c_1 = Character.toUpperCase(initials.charAt(0));
            char c_2 = Character.toLowerCase(initials.charAt(1));
            initials = String.valueOf(c_1) + String.valueOf(c_2);
            return initials;
        } else return initials.toUpperCase();

    }

    public void updateList(ArrayList<Template> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateSearch(String search){
        this.searchString = search;
        notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(@NonNull EmailsTemplatesViewHolder holder, int i) {
        Template template = list.get(i);


        if (!template.isTemplateUser()) {
            holder.templateTextCreate.setVisibility(View.VISIBLE);
            holder.templateText.setVisibility(View.GONE);

            holder.templateTitleUser.setVisibility(View.GONE);

            holder.templateTitle.setText(template.getTitle());

            holder.cardTime.setVisibility(View.GONE);

            holder.file_1.setVisibility(View.GONE);
            holder.file_2.setVisibility(View.GONE);
            holder.file_count.setVisibility(View.GONE);

            holder.accountInitials.setText(getInitials(template.getTitle()));
            holder.accountInitials.setVisibility(View.INVISIBLE);

            holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray_light_2));

            holder.accountInitials.setTextColor(context.getResources().getColor(R.color.gray_textView));


            holder.file_1.setVisibility(View.GONE);
            holder.file_2.setVisibility(View.GONE);
            holder.file_count.setVisibility(View.GONE);


            holder.linearLemplate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if(template.getTitle().equalsIgnoreCase("New template")){
                        iEmails.showTemplatePopup(template);
                    }else{
                        iEmails.hideTemplatePopup();
                    }*/

                    if (iEmails != null)
                        iEmails.showTemplatePopup(template);
                    else{
                        showSelectTemplate(template);
                    }

                }
            });

            holder.itemView.findViewById(R.id.closerMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iEmails != null)
                        iEmails.hideTemplatePopup();
                }
            });

        } else {
            holder.file_1.setVisibility(View.INVISIBLE);
            holder.file_2.setVisibility(View.GONE);
            holder.file_count.setVisibility(View.GONE);

            int count_f = 0;
            if (template.getListOfFiles() != null && !template.getListOfFiles().isEmpty()) {
                //ArrayList<FileEnums> files = new ArrayList<>();
                for (FilesTemplate e : template.getListOfFiles()) {
                    if (e.getFileEnums() != null) {

                        if (holder.file_1.getVisibility() == View.INVISIBLE) {
                            holder.file_1.setImageDrawable(context.getResources().getDrawable(e.getFileEnums().getId()));
                            holder.file_1.setVisibility(View.VISIBLE);
                            continue;
                        }

                        if (holder.file_2.getVisibility() == View.GONE) {
                            holder.file_2.setImageDrawable(context.getResources().getDrawable(e.getFileEnums().getId()));
                            holder.file_2.setVisibility(View.VISIBLE);
                            continue;
                        }

                        count_f++;

                        holder.file_count.setVisibility(View.VISIBLE);
                        holder.count_f.setText(String.valueOf(count_f));

                    }

                }

            }

            //====

            holder.cardTime.setVisibility(View.VISIBLE);

            holder.templateTitle.setText(template.getTitle());

            holder.templateTitleUser.setVisibility(View.VISIBLE);
            holder.templateTitleUser.setText("- " + template.getSubject());

            holder.templateTextCreate.setVisibility(View.GONE);
            holder.templateText.setVisibility(View.VISIBLE);

            holder.accountInitials.setVisibility(View.VISIBLE);
            holder.accountInitials.setText(getInitials(template.getTitle()));

            holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            holder.accountInitials.setTextColor(context.getResources().getColor(R.color.primary));

            if (template.getTemplateText() != null) {
                if (template.getTemplateText().toLowerCase().contains("%name")) {
                    int startI = template.getTemplateText().toLowerCase().indexOf("%name");
                    final SpannableStringBuilder text = new SpannableStringBuilder(template.getTemplateText());
                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#87aade"));
                    //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                    text.setSpan(style, startI, startI + 5, Spannable.SPAN_COMPOSING);
                    //text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                    holder.templateText.setText(text);
                } else
                    holder.templateText.setText(template.getTemplateText());
            }


            try {
           /* Calendar cal = Calendar.getInstance();
            cal.setTime(contact.getDateCreate());
            String time =  cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);*/
                Calendar current = Calendar.getInstance();
                Calendar contactDate = Calendar.getInstance();
                current.setTime(new Date());
                contactDate.setTime(template.getDateCreate());
                String timeStr = "";
                if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {

                    timeStr = Time.valueOf(template.time).getHours() + ":";
                    if (Time.valueOf(template.time).getMinutes() < 10) timeStr += "0";
                    timeStr += Time.valueOf(template.time).getMinutes();
                } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {


                    timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
                } else {

                    timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR))/*.substring(2)*/;

                }


                holder.cardTime.setText(/*getUpdTime(contact.getDateCreate(), Time.valueOf(contact.time))*/ timeStr);
            } catch (Exception e) {

            }


            holder.linearLemplate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iEmails != null)
                        iEmails.showTemplatePopup(template);
                    else{
                        showSelectTemplate(template);
                    }

                }
            });

            holder.itemView.findViewById(R.id.closerMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iEmails != null)
                        iEmails.hideTemplatePopup();

                }
            });






            //======================== search




        }


        if (searchString != "" && template.getTitle().toLowerCase().contains(searchString.toLowerCase())) {

            try {
                int startI = template.getTitle().toLowerCase().indexOf(searchString.toLowerCase());
                final SpannableStringBuilder text = new SpannableStringBuilder(template.getTitle());
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                holder.templateTitle.setText(text);
                holder.templateTitleUser.setText(text);
            } catch (Exception e) {
                holder.templateTitle.setText(template.getTitle());
                holder.templateTitleUser.setText(template.getTitle());

            }
        }else{
            holder.templateTitle.setText(template.getTitle());
            holder.templateTitleUser.setText(template.getTitle());
        }


        holder.textFirstType.setVisibility(View.GONE);
        holder.linear_2.setVisibility(View.VISIBLE);
        holder.linearMessage.setVisibility(View.VISIBLE);
        holder.type_image.setVisibility(View.GONE);
        holder.accountInitials.setVisibility(View.VISIBLE);
        //holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray_light_2));

        if(iEmails == null){
            if(!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("New mail")){

                holder.textFirstType.setVisibility(View.VISIBLE);
                holder.linear_2.setVisibility(View.GONE);
                holder.linearMessage.setVisibility(View.GONE);

                holder.textFirstType.setText("New email");

                holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_remind_message));
                holder.type_image.setVisibility(View.VISIBLE);
                holder.accountInitials.setVisibility(View.GONE);
                holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            }else if(!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("Reply")){

                holder.textFirstType.setVisibility(View.VISIBLE);
                holder.linear_2.setVisibility(View.GONE);
                holder.linearMessage.setVisibility(View.GONE);

                holder.textFirstType.setText("Reply");

                holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.reply));
                holder.type_image.setVisibility(View.VISIBLE);
                holder.accountInitials.setVisibility(View.GONE);
                holder.accountAvatar.setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            }
        }

        holder.itemView.setBackgroundColor(Color.TRANSPARENT);


        if(iEmails == null){

            if(!replyFragment.template.isTemplateUser() && !template.isTemplateUser() && template.getTitle().equalsIgnoreCase(replyFragment.template.getTitle())){
                holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
            }else if(replyFragment.template.getId().equals(template.getId())){
                holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
            }
        }

        holder.frameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iEmails != null)
                    iEmails.openTemplateFragment(template);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void showSelectTemplate(Template template){
        String t = template.getTitle()+" has been selected.\nAll previous changes in this email will be lost.";
        int startI = t.indexOf(template.getTitle());
        final SpannableStringBuilder text = new SpannableStringBuilder(t);
        final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        text.setSpan(style, startI, template.getTitle().length(), Spannable.SPAN_COMPOSING);
        text.setSpan(bss, startI, template.getTitle().length(), Spannable.SPAN_COMPOSING);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mainView.getContext());
        alertDialogBuilder.setTitle("Template selection");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    Template template1 = new Template();
                    template1.setId(template.getId());
                    template1.setTitle(template.getTitle());
                    template1.setSubject(template.getSubject());
                    template1.setTemplateUser(template.isTemplateUser());
                    template1.setTemplateText(template.getTemplateText());
                    template1.setDateCreate(template.getDateCreate());

                    template1.setEmailDataTemplate(replyFragment.template.getEmailDataTemplate());
                    template1.setListOfFiles(template.getListOfFiles());

                    replyFragment.template = template1;
                    replyFragment.initViews();

                })
                .setNegativeButton("No", (dialog, id) -> {

                    dialog.cancel();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    static class EmailsTemplatesViewHolder extends RecyclerView.ViewHolder {

        CircleImageView accountAvatar;
        TextView accountInitials, templateTitle, templateTitleUser, count_f;
        TextView templateTextCreate, templateText;

        TextView cardTime;

        ImageView file_1, file_2;
        FrameLayout file_count;

        LinearLayout linearLemplate;

        FrameLayout frameIcon;

        ImageView type_image;
        TextView textFirstType;
        LinearLayout linearMessage, linear_2;

        public EmailsTemplatesViewHolder(@NonNull View itemView) {
            super(itemView);
            accountAvatar = itemView.findViewById(R.id.accountAvatar);
            accountInitials = itemView.findViewById(R.id.accountInitials);
            templateTitle = itemView.findViewById(R.id.templateTitle);
            templateTitleUser = itemView.findViewById(R.id.templateTitleUser);

            templateTextCreate = itemView.findViewById(R.id.templateTextCreate);
            templateText = itemView.findViewById(R.id.templateText);

            cardTime = itemView.findViewById(R.id.cardTime);

            file_1 = itemView.findViewById(R.id.file_first);
            file_2 = itemView.findViewById(R.id.file_second);
            file_count = itemView.findViewById(R.id.files_count);

            linearLemplate = itemView.findViewById(R.id.linearLemplate);

            frameIcon = itemView.findViewById(R.id.frameIcon);

            count_f = itemView.findViewById(R.id.f_count);

            type_image = itemView.findViewById(R.id.type_image);

            textFirstType = itemView.findViewById(R.id.textFirstType);

            linearMessage = itemView.findViewById(R.id.linearMessage);
            linear_2 = itemView.findViewById(R.id.linear_2);

        }
    }
}
