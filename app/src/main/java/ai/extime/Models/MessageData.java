package ai.extime.Models;

import java.util.ArrayList;

import ai.extime.Enums.FileEnums;

public class MessageData {

    private FileEnums fileEnums = null;

    private String filename = null;

    private long sizeFile;

    private String idFile = null;

    byte[] fileByteArray = null;

    private Clipboard clipboard = null;

    public byte[] getFileByteArray() {
        return fileByteArray;
    }

    public void setFileByteArray(byte[] fileByteArray) {
        this.fileByteArray = fileByteArray;
    }

    public long getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(long sizeFile) {
        this.sizeFile = sizeFile;
    }

    public String getIdFile() {
        return idFile;
    }

    public void setIdFile(String idFile) {
        this.idFile = idFile;
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

    public Clipboard getClipboard() {
        return clipboard;
    }

    public void setClipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }
}
