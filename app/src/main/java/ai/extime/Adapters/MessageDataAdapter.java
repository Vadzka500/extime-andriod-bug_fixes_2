package ai.extime.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.BuildConfig;
import com.extime.R;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.MessagePartBody;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ai.extime.Activity.MainActivity;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.MessageData;

public class MessageDataAdapter extends RecyclerView.Adapter<MessageDataAdapter.MessageDataViewHolder> {

    private View mainView;

    private ArrayList<MessageData> list;

    private Context context;

    private EmailMessage emailM;

    public MessageDataAdapter(Context context, ArrayList<MessageData> list, EmailMessage message) {
        this.list = list;
        this.context = context;
        this.emailM = message;
    }

    @NonNull
    @Override
    public MessageDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_data_card, viewGroup, false);
        return new MessageDataAdapter.MessageDataViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDataViewHolder holder, int i) {
        MessageData messageData = list.get(i);
        holder.size.setVisibility(View.GONE);
        if (messageData.getClipboard() == null) {
            if (messageData.getFilename().contains("."))
                holder.value.setText(messageData.getFilename().substring(0, messageData.getFilename().indexOf(".")));
            else
                holder.value.setText(messageData.getFilename());

            int resourceId = context.getResources().
                    getIdentifier(String.valueOf(messageData.getFileEnums().getId()), "drawable", context.getPackageName());
            holder.imageView.setImageDrawable(context.getResources().getDrawable(resourceId));

            long s = messageData.getSizeFile();
            if (s > 1023) {
                s = messageData.getSizeFile() / 1024;
                if (s > 1023) {
                    s /= 1024;
                    holder.size.setText(s + " Mb");
                } else holder.size.setText(s + " Kb");

            } else holder.size.setText(s + " B");

            holder.size.setVisibility(View.VISIBLE);

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "File downloading...", Toast.LENGTH_SHORT).show();
                                }
                            });

                            try {

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
                            }

                       /* Storage storage = new Storage(context);
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                        storage.createFile(path + "/Extime/ExtimeContacts/"+messageData.getFilename(),messageData.getFileByteArray());*/

                            String path = context.getFilesDir() + "/mesFiles";
                            File folder = new File(path);
                            if (!folder.exists()) {
                                folder.mkdir();
                            }

                            try {

                                File f = new File(path, messageData.getFilename());

                                boolean isAllowedToWrite;
                                try {
                                    isAllowedToWrite = f.canWrite();
                                }
                                catch (Exception exception) {
                                    isAllowedToWrite = false;
                                    exception.printStackTrace();
                                }

                                if(isAllowedToWrite) {
                                    FileOutputStream fileOutFile = new FileOutputStream(f);
                                    fileOutFile.write(messageData.getFileByteArray());
                                    fileOutFile.close();
                                }


                                Uri contentUri = FileProvider.getUriForFile(context, "com.extime.fileprovider", f.getAbsoluteFile());

                                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                Intent newIntent = new Intent(Intent.ACTION_VIEW);

                                String type = null;
                                String extension = MimeTypeMap.getFileExtensionFromUrl(f.getAbsolutePath());
                                if (extension != null) {
                                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                }

                       /* String mimeType = myMime.getMimeTypeFromExtension(fileExt(f.getName()).substring(1));
                        //newIntent.setDataAndType(Uri.fromFile(f),mimeType);

                        newIntent.setData(Uri.fromFile(f));
                        newIntent.setType("application/msword");
                        newIntent.setAction(Intent.ACTION_VIEW);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/

                                newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                newIntent.setAction(Intent.ACTION_VIEW);
                                Uri uri = Uri.parse("content://" + f.getAbsolutePath());
                                newIntent.setDataAndType(contentUri, type);


                                try {
                                    //context.startActivity(Intent.createChooser(newIntent, "Открыть с помощью приложения:"));
                                    context.startActivity(newIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                                }

                       /* MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String mimeType =
                                myMime.getMimeTypeFromExtension(f.getName());
                        if(android.os.Build.VERSION.SDK_INT >=24) {
                            Uri fileURI = FileProvider.getUriForFile(context,
                                    BuildConfig.APPLICATION_ID,
                                    f);
                            intent.setDataAndType(fileURI, mimeType);

                        }else {
                            intent.setDataAndType(Uri.fromFile(f), mimeType);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            context.startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            Toast.makeText(context, "No Application found to open this type of file.", Toast.LENGTH_LONG).show();

                        }*/


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();


                }
            });
        } else {
            holder.value.setText(messageData.getClipboard().getValueCopy());
            holder.imageView.setImageURI(Uri.parse(messageData.getClipboard().getImageTypeClipboard()));
        }
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

    public ArrayList<MessageData> getList() {
        return list;
    }

    public void setList(ArrayList<MessageData> list) {
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
