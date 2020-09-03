package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.google.android.gms.common.util.IOUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.MessagePartBody;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.UpdateTemplate;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.MessageData;
import ai.extime.Models.Template;

public class TemplateFileAdapter extends RecyclerView.Adapter<TemplateFileAdapter.MessageDataViewHolder> {

    private View mainView;

    private ArrayList<FilesTemplate> list;

    private Context context;

    private Template emailM;

    private Activity activity;

    public TemplateFileAdapter(Context context, ArrayList<FilesTemplate> list, Template template, Activity activity) {
        this.list = list;
        this.context = context;
        this.emailM = template;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TemplateFileAdapter.MessageDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_data_card, viewGroup, false);
        return new TemplateFileAdapter.MessageDataViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull TemplateFileAdapter.MessageDataViewHolder holder, int i) {
        FilesTemplate filesTemplate = list.get(i);
        holder.size.setVisibility(View.GONE);


        holder.value.setText(filesTemplate.getFilename());


        int resourceId = context.getResources().
                getIdentifier(String.valueOf(filesTemplate.getFileEnums().getId()), "drawable", context.getPackageName());
        holder.imageView.setImageDrawable(context.getResources().getDrawable(resourceId));

        if (filesTemplate.getFilename().contains("."))
            holder.value.setText(filesTemplate.getFilename().substring(0, filesTemplate.getFilename().indexOf(".")));
        else
            holder.value.setText(filesTemplate.getFilename());

        long s = Long.parseLong(filesTemplate.getSizeFile());
        if (s > 1023) {
            s = Long.parseLong(filesTemplate.getSizeFile()) / 1024;
            if (s > 1023) {
                s /= 1024;
                holder.size.setText(s + " Mb");
            } else holder.size.setText(s + " Kb");

        } else holder.size.setText(s + " B");

        holder.size.setVisibility(View.VISIBLE);


        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        activity);
                alertDialogBuilder.setTitle("Do you want to delete file?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            Storage storage = new Storage(activity);
                            //storage.deleteFile(filesTemplate.getUrl().toString());

                            //File file = new File(filesTemplate.getUrl().toString());
                            //file.delete();

                            storage.deleteFile( Environment.getExternalStorageDirectory().getAbsolutePath() + "/Extime/TemplatesFiles/" + filesTemplate.getFilename());

                            list.remove(i);
                            notifyDataSetChanged();

                            if (emailM.getEmailDataTemplate() == null){
                                EventBus.getDefault().post(new UpdateTemplate(emailM));
                            }


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                return true;
            }
        });


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                      /*  ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "File downloading...", Toast.LENGTH_SHORT).show();
                            }
                        });*/

                        /*try {

                            HttpTransport transport = AndroidHttp.newCompatibleTransport();
                            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                            Gmail service = new Gmail.Builder(transport, jsonFactory, emailM.getCredential())
                                    .setApplicationName("Extime")
                                    .build();

                            if (messageData.getFilename() != null && !messageData.getFilename().isEmpty()) {
                                MessagePartBody attachPart = service.users().messages().attachments().
                                        get(emailM.getUser(), emailM.getId(), messageData.getIdFile()).execute();


                                messageData.setFileByteArray(Base64.decodeBase64(attachPart.getData()));


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/


                        String path = context.getFilesDir() + "/templateFiles";
                        File folder = new File(path);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }






                        try {
                            Uri urii = filesTemplate.getUrl();

                          /*  context.getContentResolver().takePersistableUriPermission(urii, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/

                            Storage storage = new Storage(activity);

                            File f2 = storage.getFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Extime/TemplatesFiles/" + filesTemplate.getFilename());

                            InputStream selectedFileInputStream =
                                    context.getContentResolver().openInputStream(Uri.fromFile(f2));

                            byte[] bytes = IOUtils.toByteArray(selectedFileInputStream);

                            File f = new File(path, filesTemplate.getFilename());

                            FileOutputStream fileOutFile = new FileOutputStream(f);
                            fileOutFile.write(bytes);
                            fileOutFile.close();


                            Uri contentUri = FileProvider.getUriForFile(context, "com.extime.fileprovider", f.getAbsoluteFile());

                            filesTemplate.setFinalUrl(f.getAbsolutePath());

                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            Intent newIntent = new Intent(Intent.ACTION_VIEW);

                            String type = null;
                            String extension = MimeTypeMap.getFileExtensionFromUrl(f.getAbsolutePath());
                            if (extension != null) {
                                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            }


                            newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            newIntent.setAction(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse("content://" + f.getAbsolutePath());
                            newIntent.setDataAndType(contentUri, type);


                            try {

                                context.startActivity(newIntent);
                            } catch (ActivityNotFoundException e) {
                                //Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                            }


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "File not found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();


            }
        });

    }




    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<FilesTemplate> getList() {
        return list;
    }

    public void setList(ArrayList<FilesTemplate> list) {
        this.list = list;
    }

    public static class MessageDataViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView value;
        TextView size;
        LinearLayout card;

        public MessageDataViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageMessageData);
            value = itemView.findViewById(R.id.valueMessageData);
            size = itemView.findViewById(R.id.sizeData);
            card = itemView.findViewById(R.id.cardDataOfMessage);
        }
    }

}
