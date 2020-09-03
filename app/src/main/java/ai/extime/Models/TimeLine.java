package ai.extime.Models;

import java.util.Date;

/**
 * Created by patal on 23.09.2017.
 */

public class TimeLine {

    private Contact contact;

    private Date date;

    private String typeTimeLine;

    private boolean hasGeo;

    private boolean hasEmail;

    private boolean hasPDF;

    private boolean hasPause;

    public boolean isHasPause() {
        return hasPause;
    }

    public void setHasPause(boolean hasPause) {
        this.hasPause = hasPause;
    }

    private boolean hasRemind;

    private boolean hasFacebook;

    private int count;

    private String bottomValue;

    private Boolean hasArrow;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTypeTimeLine() {
        return typeTimeLine;
    }

    public void setTypeTimeLine(String typeTimeLine) {
        this.typeTimeLine = typeTimeLine;
    }

    public boolean isHasGeo() {
        return hasGeo;
    }

    public void setHasGeo(boolean hasGeo) {
        this.hasGeo = hasGeo;
    }

    public boolean isHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(boolean hasEmail) {
        this.hasEmail = hasEmail;
    }

    public boolean isHasPDF() {
        return hasPDF;
    }

    public void setHasPDF(boolean hasPDF) {
        this.hasPDF = hasPDF;
    }

    public boolean isHasRemind() {
        return hasRemind;
    }

    public void setHasRemind(boolean hasRemind) {
        this.hasRemind = hasRemind;
    }

    public boolean isHasFacebook() {
        return hasFacebook;
    }

    public void setHasFacebook(boolean hasFacebook) {
        this.hasFacebook = hasFacebook;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBottomValue() {
        return bottomValue;
    }

    public void setBottomValue(String bottomValue) {
        this.bottomValue = bottomValue;
    }

    public Boolean getHasArrow() {
        return hasArrow;
    }

    public void setHasArrow(Boolean hasArrow) {
        this.hasArrow = hasArrow;
    }
}
