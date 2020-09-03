package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.extime.R;

import java.util.ArrayList;

import ai.extime.Interfaces.CustomRunnable;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactDataClipboard;
import ai.extime.Models.MessageData;
import ai.extime.Services.ContactCacheService;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageDataExtratorAdapter extends RecyclerView.Adapter<MessageDataExtratorAdapter.MessageDataExtratorViewHolder> {

    private View mainView;

    private ArrayList<MessageData> listOfData;

    private Context context;

    private Activity activity;

    private ArrayList<MessageData> selectMessageData;

    private TextView createTextView, countDataTextView;

    ImageView share, arr, copyImage, downloadImage;

    public MessageDataExtratorAdapter(ArrayList<MessageData> list, Context contex, Activity activity, TextView createTextView, TextView countDataTextView, ImageView share, ImageView arr, ImageView copy, ImageView download) {

        this.listOfData = list;
        this.context = contex;
        this.activity = activity;
        this.selectMessageData = new ArrayList<>();
        this.createTextView = createTextView;
        this.countDataTextView = countDataTextView;
        this.arr = arr;
        this.share = share;

        this.copyImage = copy;
        this.downloadImage = download;
    }

    @NonNull
    @Override
    public MessageDataExtratorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_message_data_extrator, viewGroup, false);
        return new MessageDataExtratorViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MessageDataExtratorViewHolder holder, int i) {
        MessageData data = listOfData.get(i);


        if (selectMessageData.contains(data)) holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);


        if (data.getClipboard() != null) {

            holder.imageType.setImageURI(null);
            holder.imageType.setImageURI(Uri.parse(data.getClipboard().getImageTypeClipboard()));

            holder.value.setText(data.getClipboard().getValueCopy());





            if (data.getClipboard().getListContactsSearch() != null && !data.getClipboard().getListContactsSearch().isEmpty() && data.getClipboard().getListContactsSearch().size() > 0 && (data.getClipboard().getListClipboards() == null || data.getClipboard().getListClipboards().isEmpty())) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            holder.profileIconInitials.setVisibility(View.GONE);
                            holder.companyPhotoIcon.setVisibility(View.GONE);
                            holder.profileIconAvatar.setVisibility(View.VISIBLE);
                            holder.profileIconAvatar.setImageURI(null);

                            if (data.getClipboard().getListContactsSearch().get(0).getPhotoURL() != null)
                                holder.profileIconAvatar.setImageURI(Uri.parse(data.getClipboard().getListContactsSearch().get(0).getPhotoURL()));

                            if (data.getClipboard().getListContactsSearch().get(0).getPhotoURL() == null /*|| (holder.circleImageView.getDrawable()) == null*/) {
                                //if (((BitmapDrawable) holder.circleImageView.getDrawable()).getBitmap() == null) {

                                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                circle.setColor(data.getClipboard().getListContactsSearch().get(0).color);
                                holder.profileIconAvatar.setBackground(circle);
                                holder.profileIconAvatar.setImageDrawable(null);
                                String initials = getInitialsByName(data.getClipboard().getListContactsSearch().get(0).getName());
                                holder.profileIconInitials.setVisibility(View.VISIBLE);
                                holder.profileIconInitials.setText(initials);

                                if (!data.getClipboard().getListContactsSearch().get(0).contact) {

                                    holder.profileIconAvatar.setVisibility(View.GONE);
                                    holder.companyPhotoIcon.setVisibility(View.VISIBLE);
                                    holder.companyPhotoIcon.setBackgroundColor(data.getClipboard().getListContactsSearch().get(0).color);
                                }
                            }
                        } catch (IllegalStateException e) {
                          /*
                            clipboard.getListContactsSearch().remove(0);
                            holder.circleImageView.setVisibility(View.GONE);
                            ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");*/
                        }


                    }
                });
            }


            final ContactDataClipboard[] c = {null};

            CustomRunnable customRunnable = new CustomRunnable() {

                boolean run = true;

                public void stopThread() {
                    run = false;
                }

                @Override
                public void run() {
                    try {


                        if (data.getClipboard().getListContactsSearch() == null || data.getClipboard().getListContactsSearch().isEmpty() || data.getClipboard().getListContactsSearch().size() == 0 && (data.getClipboard().getListClipboards() == null || data.getClipboard().getListClipboards().isEmpty())) {


                            Contact ct = ContactCacheService.find2(data.getClipboard().getValueCopy(), data.getClipboard().getType(), activity);

                            if(ct != null) {
                                boolean com = true;
                                if (ct.listOfContacts != null && !ct.listOfContacts.isEmpty())
                                    com = false;

                                c[0] = new ContactDataClipboard(ct.getId(), ct.getName(), ct.getPhotoURL(), ct.color, com);
                            }else c[0] = null;




                            if (c[0] != null) {

                                data.getClipboard().addContactToListSearch(c[0]);


                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");


                                        holder.profileIconInitials.setVisibility(View.GONE);
                                        holder.companyPhotoIcon.setVisibility(View.GONE);
                                        holder.profileIconAvatar.setVisibility(View.VISIBLE);
                                        holder.profileIconAvatar.setImageURI(null);

                                        if (c[0].getPhotoURL() != null)
                                            holder.profileIconAvatar.setImageURI(Uri.parse(c[0].getPhotoURL()));

                                        if (c[0].getPhotoURL() == null) {

                                            holder.profileIconAvatar.setVisibility(View.VISIBLE);
                                            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                            circle.setColor(c[0].color);
                                            holder.profileIconAvatar.setBackground(circle);
                                            holder.profileIconAvatar.setImageDrawable(null);
                                            String initials = getInitialsByName(c[0].getName());
                                            holder.profileIconInitials.setVisibility(View.VISIBLE);
                                            holder.profileIconInitials.setText(initials);
                                            if (!c[0].contact) {

                                                holder.profileIconAvatar.setVisibility(View.GONE);
                                                holder.companyPhotoIcon.setVisibility(View.VISIBLE);
                                                holder.companyPhotoIcon.setBackgroundColor(c[0].color);
                                            }
                                        }
                                    }
                                });


                                updateCountData();
                            } else {

                            }


                        } else if (data.getClipboard().getListClipboards() == null || data.getClipboard().getListClipboards().isEmpty()) {


                            if (!ContactCacheService.checkExistConatctFromClipboard(listOfData.get(i).getClipboard().getListContactsSearch().get(0).getId(), listOfData.get(i).getClipboard().getValueCopy(), listOfData.get(i).getClipboard().getType(), null)) {


                                Contact ct = ContactCacheService.find2(data.getClipboard().getValueCopy(), data.getClipboard().getType(), activity);

                                if(ct != null) {
                                    boolean com = true;
                                    if (ct.listOfContacts != null && !ct.listOfContacts.isEmpty())
                                        com = false;

                                    c[0] = new ContactDataClipboard(ct.getId(), ct.getName(), ct.getPhotoURL(), ct.color, com);
                                }else c[0] = null;




                                if (c[0] != null) {


                                    data.getClipboard().setFirstClipsToSearch(c[0]);


                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            holder.profileIconInitials.setVisibility(View.GONE);
                                            holder.companyPhotoIcon.setVisibility(View.GONE);
                                            holder.profileIconAvatar.setVisibility(View.VISIBLE);
                                            holder.profileIconAvatar.setImageURI(null);
                                            if (c[0].getPhotoURL() != null)
                                                holder.profileIconAvatar.setImageURI(Uri.parse(c[0].getPhotoURL()));

                                            if (c[0].getPhotoURL() == null) {

                                                holder.profileIconAvatar.setVisibility(View.VISIBLE);
                                                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                                circle.setColor(c[0].color);
                                                holder.profileIconAvatar.setBackground(circle);
                                                holder.profileIconAvatar.setImageDrawable(null);
                                                String initials = getInitialsByName(c[0].getName());
                                                holder.profileIconInitials.setVisibility(View.VISIBLE);
                                                holder.profileIconInitials.setText(initials);
                                                if (!c[0].contact) {

                                                    holder.profileIconAvatar.setVisibility(View.GONE);
                                                    holder.companyPhotoIcon.setVisibility(View.VISIBLE);
                                                    holder.companyPhotoIcon.setBackgroundColor(c[0].color);
                                                }
                                            }
                                        }
                                    });


                                } else {

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //holder.circleImageViewSearch.setVisibility(View.GONE);

                                        /*if (((clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty()) && (clipboard.getListContactsSearch() == null || clipboard.getListContactsSearch().isEmpty())) && (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) ||
                                                clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) ||
                                                clipboard.getType().equals(ClipboardEnum.VK) || clipboard.getType().equals(ClipboardEnum.WEB))) {

                                            boolean checkVisibleSearch = true;
                                            if (listOpen != null) {
                                                for (Clipboard clipboard1 : listOpen) {
                                                    if (clipboard1.getListClipboards().contains(clipboard)) {
                                                        checkVisibleSearch = false;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (checkVisibleSearch) {
                                                holder.frameCircleSearch.setVisibility(View.VISIBLE);
                                                holder.frameSearchNote.setVisibility(View.GONE);
                                                holder.frameSearchSocials.setVisibility(View.VISIBLE);
                                            }

                                        }*/

                                            //holder.imageCompany.setVisibility(View.GONE);
                                        }
                                    });


                               /* try {
                                    data.getClipboard().removeConatctFromListSearchBuId(clipboard.getListContactsSearch().get(0));
                                } catch (Exception e) {
                                    clipboard.getListContactsSearch().remove(0);
                                }*/

                                    c[0] = null;

                                    //==========================
                               /* activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

                                        //holder.frameInageIconClipboard.setVisibility(View.VISIBLE);
                                        holder.imageCompany.setVisibility(View.GONE);
                                        holder.circleImageView.setVisibility(View.GONE);

                                        if (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())
                                            holder.circleImageViewSearch.setVisibility(View.GONE);
                                        else {
                                            holder.circleImageViewSearch.setVisibility(View.GONE);
                                            holder.arrow.setVisibility(View.VISIBLE);
                                            holder.arrow.setImageDrawable(null);
                                            if (listOpen.contains(clipboard)) {
                                                holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_up));
                                                cliclArr[0] = true;
                                            } else {
                                                holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_down));
                                                cliclArr[0] = false;
                                            }
                                        }
                                    }
                                });*/

                               /* if (listOpen != null) {
                                    for (Clipboard clipboard1 : listOpen) {
                                        if (clipboard1.getListClipboards().contains(clipboard)) {
                                        *//*holder.numberGroup.setVisibility(View.VISIBLE);
                                        holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                                        if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1) {
                                            holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                                        }*//*

                                            r.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemChanged(clipboards.indexOf(clipboard1));
                                                }
                                            });


                                            break;
                                        }
                                    }
                                }*/

                                    //==========================
                                }


                                updateCountData();


                                //notifyDataSetChanged();

                            } else {
                             c[0] = data.getClipboard().getListContactsSearch().get(0);

                                updateCountData();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread main = new Thread(customRunnable);
            main.start();


        } else {
            holder.value.setText(data.getFilename().substring(0, data.getFilename().indexOf(".")));
            int resourceId = context.getResources().
                    getIdentifier(String.valueOf(data.getFileEnums().getId()), "drawable", context.getPackageName());
            holder.imageType.setImageDrawable(context.getResources().getDrawable(resourceId));

            holder.frameInageIconClipboard.setVisibility(View.GONE);
        }


        holder.checkFrameClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) holder.checkBox.setChecked(false);
                else holder.checkBox.setChecked(true);

                if (holder.checkBox.isChecked()) {
                    selectMessageData.add(data);
                    createTextView.setTextColor(context.getResources().getColor(R.color.primary));
                } else {
                    selectMessageData.remove(data);
                    createTextView.setTextColor(context.getResources().getColor(R.color.gray));
                }

                if(selectMessageData.size() == 0){
                    share.setVisibility(View.GONE);
                    copyImage.setVisibility(View.GONE);
                    downloadImage.setVisibility(View.GONE);
                    arr.setVisibility(View.VISIBLE);
                }else{
                    share.setVisibility(View.VISIBLE);
                    copyImage.setVisibility(View.VISIBLE);
                    downloadImage.setVisibility(View.VISIBLE);
                    arr.setVisibility(View.GONE);
                }

            }
        });


    }

    public void updateCountData(){
        int count_files = 0;
        int count_contact = 0;

        for (MessageData messageData : listOfData) {
            if (messageData.getClipboard() == null)
                count_files++;
            else {
                if (messageData.getClipboard().getListContactsSearch() == null || messageData.getClipboard().getListContactsSearch().isEmpty()) {

                } else
                    count_contact++;
            }
        }


        String t = listOfData.size() + " items:";
        if (count_contact > 0) {
            if (count_contact == 1)
                t += " 1 contact,";
            else
                t += " " + count_contact + " contacts,";
        }
        if (count_files > 0) {
            if (count_files == 1)
                t += " 1 file";
            else
                t += " " + count_files + " files";
        }

        if (t.charAt(t.length() - 1) == ',')
            t = t.substring(0, t.length() - 1);

        int startI = t.indexOf(":") + 1;
        final SpannableStringBuilder text = new SpannableStringBuilder(t);
        final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#808080"));

        //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        if (startI != -1) {
            text.setSpan(style, startI, text.length(), Spannable.SPAN_COMPOSING);
            //text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
        }
        //holder.hashTagValue.setText(text);

       countDataTextView.setText(text);
    }

    public void uncheck(){
        selectMessageData.clear();
        createTextView.setTextColor(context.getResources().getColor(R.color.gray));

        share.setVisibility(View.GONE);
        copyImage.setVisibility(View.GONE);
        downloadImage.setVisibility(View.GONE);

        arr.setVisibility(View.VISIBLE);

        notifyDataSetChanged();
    }


    public ArrayList<MessageData> getSelectMessageData() {
        return selectMessageData;
    }

    public void setSelectMessageData(ArrayList<MessageData> selectMessageData) {
        this.selectMessageData = selectMessageData;
    }

    @Override
    public int getItemCount() {
        return listOfData.size();
    }

    private String getInitials(Contact contact) {
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    private String getInitialsByName(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String names[] = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    public static class MessageDataExtratorViewHolder extends RecyclerView.ViewHolder {

        ImageView imageType;
        TextView value;

        FrameLayout frameInageIconClipboard;
        ImageView companyPhotoIcon;
        CircleImageView profileIconAvatar;
        TextView profileIconInitials;

        CheckBox checkBox;
        RelativeLayout checkFrameClip;

        public MessageDataExtratorViewHolder(@NonNull View itemView) {
            super(itemView);

            imageType = itemView.findViewById(R.id.imageTypeClipboard);
            value = itemView.findViewById(R.id.clipboardValue);
            frameInageIconClipboard = itemView.findViewById(R.id.frameInageIconClipboard);
            companyPhotoIcon = itemView.findViewById(R.id.companyPhotoIcon);
            profileIconAvatar = itemView.findViewById(R.id.profileIconAvatar);
            profileIconInitials = itemView.findViewById(R.id.profileIconInitials);

            checkBox = itemView.findViewById(R.id.checkClip);
            checkFrameClip = itemView.findViewById(R.id.checkFrameClip);


        }
    }
}
