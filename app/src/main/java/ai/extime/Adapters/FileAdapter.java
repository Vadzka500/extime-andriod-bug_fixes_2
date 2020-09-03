package ai.extime.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.extime.Fragments.FilesFragment;
import ai.extime.Models.Contact;
import ai.extime.Models.FileCall2me;
import com.extime.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder>  {

    private View mainView;

    private Context context;

    private ArrayList<FileCall2me> listOfFileCall2mes;

    private FilesFragment filesFragment;

    public void addNewFile(FileCall2me fileCall2me){
        listOfFileCall2mes.add(fileCall2me);
        notifyDataSetChanged();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {

        ImageView fileType;
        ImageView fileCloud;
        TextView fileName;
        TextView fileHashtags;
        TextView fileTime;
        View card;

        FileViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            fileType = (ImageView) itemView.findViewById(R.id.fileType);
            fileCloud = (ImageView) itemView.findViewById(R.id.fileCloud);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            fileHashtags = (TextView) itemView.findViewById(R.id.fileHashtags);
            fileTime = (TextView) itemView.findViewById(R.id.fileTime);
        }
    }

    private String getInitials(Contact contact){
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names)
                initials += namePart.charAt(0);
        }
        return initials.toUpperCase();
    }



    public FileAdapter(Context context, ArrayList<FileCall2me> listOfFileCall2mes, FilesFragment filesFragment) {
        this.context = context;
        this.listOfFileCall2mes = listOfFileCall2mes;
        this.filesFragment = filesFragment;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_file, viewGroup, false);
        return new FileAdapter.FileViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(final FileAdapter.FileViewHolder holder, int position) {
        //   holder.card.setOnClickListener(v -> {});
        holder.fileName.setText(listOfFileCall2mes.get(position).getName());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filesFragment.closeOtherPopups();
            }
        });
        switch (listOfFileCall2mes.get(position).getType().toLowerCase()){
            case "png":{
                holder.fileType.setImageResource(R.drawable.icn_files_png);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "image/*");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            case "jpg":{
                holder.fileType.setImageResource(R.drawable.icn_files_png);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "image/*");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            case "zip":{
                holder.fileType.setImageResource(R.drawable.icn_files_zip);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "application/zip*");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            case "xls":{
                holder.fileType.setImageResource(R.drawable.icn_files_xls);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "application/vnd.ms-excel");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            case "doc":{
                holder.fileType.setImageResource(R.drawable.icn_files_doc);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "application/msword");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }

                break;
            }
            case "pdf":{
                holder.fileType.setImageResource(R.drawable.icn_files_pdf);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "application/pdf");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            case "ppt":{
                holder.fileType.setImageResource(R.drawable.icn_files_ppt);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "application/vnd.ms-excel");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            case "svg":{
                holder.fileType.setImageResource(R.drawable.icn_files_svg);
//                try {
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            Intent it = new Intent(Intent.ACTION_VIEW);
////                            it.setDataAndType(Uri.parse("file://" + listOfFileCall2mes.get(position).getPath()), "image/*");
////                            context.startActivity(it);
//                        }
//                    });
//                }catch (Exception e){
//                    Toast.makeText(context,"Install app for preview file",Toast.LENGTH_SHORT).show();
//                }
                break;
            }
            default:{
                holder.fileType.setImageResource(R.drawable.icn_files_file);
                break;
            }
        }
        //TIme
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        try {
            Date parsedDate = sdf.parse(listOfFileCall2mes.get(position).getDate());
            cal.setTime(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if(calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR))
//            holder.fileTime.setText(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
//        else if(calendar.get(Calendar.MONTH) != cal.get(Calendar.MONTH) || cal.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH))
        holder.fileTime.setText(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));



    }

    @Override
    public int getItemCount() {
        return listOfFileCall2mes.size();
    }

}

