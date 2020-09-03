package ai.extime.Utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.OpenableColumns;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.IOException;
import java.util.ArrayList;

import ai.extime.Enums.FileEnums;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.MessageData;

public class FileType {

    public ArrayList<MessageData> getTypeOFFile(Message message, Gmail service, String user) {
        ArrayList<MessageData> list = new ArrayList<>();

        if (message != null && message.getPayload() != null && message.getPayload().getParts() != null) {
            for (MessagePart part : message.getPayload().getParts()) {
                MessageData data = new MessageData();
                data.setFilename(part.getFilename());
                data.setSizeFile(part.getBody().getSize());
                data.setIdFile(part.getBody().getAttachmentId());

                System.out.println("FIL N = " + part.getFilename());
                System.out.println("FIL T = " + part.getMimeType());

                if (part.getMimeType().equalsIgnoreCase("image/png"))
                    data.setFileEnums(FileEnums.PNG);
                if (part.getMimeType().equalsIgnoreCase("image/jpg") || part.getMimeType().equalsIgnoreCase("image/jpeg"))
                    data.setFileEnums(FileEnums.JPG);
                if (part.getMimeType().equalsIgnoreCase("application/pdf") || part.getMimeType().equalsIgnoreCase("application/octet-stream"))
                    data.setFileEnums(FileEnums.PDF);
                if (part.getMimeType().equalsIgnoreCase("application/vnd.ms-powerpoint"))
                    data.setFileEnums(FileEnums.PPT);
                if (part.getMimeType().equalsIgnoreCase("image/gif"))
                    data.setFileEnums(FileEnums.GIF);
                if (part.getMimeType().equalsIgnoreCase("application/x-pointplus"))
                    data.setFileEnums(FileEnums.CSS);
                if (part.getMimeType().equalsIgnoreCase("image/bmp"))
                    data.setFileEnums(FileEnums.BMP);
                if (part.getMimeType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        || part.getMimeType().equalsIgnoreCase("application/msword"))
                    data.setFileEnums(FileEnums.DOC);
                if (part.getMimeType().equalsIgnoreCase("video/quicktime"))
                    data.setFileEnums(FileEnums.MOV);
                if (part.getMimeType().equalsIgnoreCase("image/tiff"))
                    data.setFileEnums(FileEnums.TIF);
                if (part.getMimeType().equalsIgnoreCase("video/msvideo"))
                    data.setFileEnums(FileEnums.AVI);
                if (part.getMimeType().equalsIgnoreCase("image/psd"))
                    data.setFileEnums(FileEnums.PSD);
                if (part.getMimeType().equalsIgnoreCase("application/x-excel"))
                    data.setFileEnums(FileEnums.XLS);

                if (part.getMimeType().equalsIgnoreCase("application/postscript") && part.getFilename() != null) {
                    if (part.getFilename().contains(".ai")) data.setFileEnums(FileEnums.AI);
                    if (part.getFilename().contains(".eps")) data.setFileEnums(FileEnums.EPS);
                }

                if (part.getMimeType().equalsIgnoreCase("text/plain") && part.getFilename() != null) {
                    if (part.getFilename().contains(".txt")) data.setFileEnums(FileEnums.TXT);
                }

                if (part.getMimeType().equalsIgnoreCase("application/octet-stream") && part.getFilename() != null) {
                    if (part.getFilename().contains(".mp3")) data.setFileEnums(FileEnums.MP3);
                    if (part.getFilename().contains(".cad")) data.setFileEnums(FileEnums.CAD);
                    if (part.getFilename().contains(".xml")) data.setFileEnums(FileEnums.XML);
                    if (part.getFilename().contains(".cdr")) data.setFileEnums(FileEnums.CDR);
                    if (part.getFilename().contains(".sql")) data.setFileEnums(FileEnums.SQL);
                    if (part.getFilename().contains(".svg")) data.setFileEnums(FileEnums.SVG);
                    if (part.getFilename().contains(".raw")) data.setFileEnums(FileEnums.RAW);
                    if (part.getFilename().contains(".php")) data.setFileEnums(FileEnums.PHP);

                }

                if (part.getFilename() != null && !part.getFilename().isEmpty()) {
                    if (part.getFilename().contains(".iso")) data.setFileEnums(FileEnums.ISO);
                    if (part.getFilename().contains(".zip")) data.setFileEnums(FileEnums.ZIP);
                    if (part.getFilename().contains(".js")) data.setFileEnums(FileEnums.JS);

                    if (data.getFileEnums() != null)
                        list.add(data);
                }

                //ISO, ZIP, JS


            }
        }

        return list;
    }

    public static void getNameSize(FilesTemplate filesTemplate, Activity activity) {

        Cursor cursor = activity.getContentResolver()
                .query(filesTemplate.getUrl(), null, null, null, null, null);

        try {

            if (cursor != null && cursor.moveToFirst()) {


                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));


                if (displayName.contains(".png"))
                    filesTemplate.setFileEnums(FileEnums.PNG);
                if (displayName.contains(".jpg") || displayName.contains(".jpeg"))
                    filesTemplate.setFileEnums(FileEnums.JPG);
                if (displayName.contains(".pdf"))
                    filesTemplate.setFileEnums(FileEnums.PDF);
                if (displayName.contains(".ppt"))
                    filesTemplate.setFileEnums(FileEnums.PPT);
                if (displayName.contains(".gif"))
                    filesTemplate.setFileEnums(FileEnums.GIF);
                if (displayName.contains(".css"))
                    filesTemplate.setFileEnums(FileEnums.CSS);
                if (displayName.contains(".bmp"))
                    filesTemplate.setFileEnums(FileEnums.BMP);
                if (displayName.contains(".doc")
                        || displayName.contains(".docx"))
                    filesTemplate.setFileEnums(FileEnums.DOC);
                if (displayName.contains(".mov"))
                    filesTemplate.setFileEnums(FileEnums.MOV);
                if (displayName.contains(".tiff"))
                    filesTemplate.setFileEnums(FileEnums.TIF);
                if (displayName.contains(".avi"))
                    filesTemplate.setFileEnums(FileEnums.AVI);
                if (displayName.contains(".psd"))
                    filesTemplate.setFileEnums(FileEnums.PSD);
                if (displayName.contains(".xls"))
                    filesTemplate.setFileEnums(FileEnums.XLS);
                if (displayName.contains(".mp4"))
                    filesTemplate.setFileEnums(FileEnums.MP4);


                if (displayName.contains(".ai")) filesTemplate.setFileEnums(FileEnums.AI);
                if (displayName.contains(".eps")) filesTemplate.setFileEnums(FileEnums.EPS);
                if (displayName.contains(".txt")) filesTemplate.setFileEnums(FileEnums.TXT);
                if (displayName.contains(".mp3")) filesTemplate.setFileEnums(FileEnums.MP3);
                if (displayName.contains(".cad")) filesTemplate.setFileEnums(FileEnums.CAD);
                if (displayName.contains(".xml")) filesTemplate.setFileEnums(FileEnums.XML);
                if (displayName.contains(".cdr")) filesTemplate.setFileEnums(FileEnums.CDR);
                if (displayName.contains(".sql")) filesTemplate.setFileEnums(FileEnums.SQL);
                if (displayName.contains(".svg")) filesTemplate.setFileEnums(FileEnums.SVG);
                if (displayName.contains(".raw")) filesTemplate.setFileEnums(FileEnums.RAW);


                if (displayName.contains(".iso")) filesTemplate.setFileEnums(FileEnums.ISO);
                if (displayName.contains(".zip")) filesTemplate.setFileEnums(FileEnums.ZIP);
                if (displayName.contains(".js")) filesTemplate.setFileEnums(FileEnums.JS);


                int ind = displayName.indexOf('.');

                filesTemplate.setFilename(displayName);



                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                String size = null;
                if (!cursor.isNull(sizeIndex)) {

                    size = cursor.getString(sizeIndex);
                    filesTemplate.setSizeFile(size.toString());

                } else {
                    size = "Unknown";
                }

            }
        } finally {
            cursor.close();
        }

    }
}
