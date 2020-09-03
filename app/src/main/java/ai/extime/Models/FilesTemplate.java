package ai.extime.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import ai.extime.Enums.FileEnums;

public class FilesTemplate implements Serializable, Parcelable {

    private String url;

    private String finalUrl = null;

    private FileEnums fileEnums = null;

    private String filename = null;

    private String sizeFile = null;

    public FilesTemplate(Uri url) {
        this.url = url.toString();
    }

    protected FilesTemplate(Parcel in) {
        url = in.readParcelable(Uri.class.getClassLoader());
        finalUrl = in.readString();
        filename = in.readString();
        sizeFile = in.readString();
    }

    public static final Creator<FilesTemplate> CREATOR = new Creator<FilesTemplate>() {
        @Override
        public FilesTemplate createFromParcel(Parcel in) {
            return new FilesTemplate(in);
        }

        @Override
        public FilesTemplate[] newArray(int size) {
            return new FilesTemplate[size];
        }
    };

    public String getFinalUrl() {
        return finalUrl;
    }

    public void setFinalUrl(String finalUrl) {
        this.finalUrl = finalUrl;
    }

    public Uri getUrl() {
        return Uri.parse(url);
    }

    public void setUrl(Uri url) {
        this.url = url.toString();
    }

    public FileEnums getFileEnums() {
        return fileEnums;
    }

    public void setFileEnums(FileEnums fileEnums) {
        this.fileEnums = fileEnums;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(String sizeFile) {
        this.sizeFile = sizeFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(finalUrl);
        dest.writeString(filename);
        dest.writeString(sizeFile);
    }
}
