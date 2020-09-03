package ai.extime.Models;

import java.io.Serializable;

import io.realm.RealmObject;

public class SocialModel extends RealmObject implements Serializable {


    private String facebookLink;

    private String vkLink;

    private String linkedInLink;

    private String instagramLink;

    private String whatsappLink;

    private String viberLink;

    private String telegramLink = null;

    private String skypeLink;

    private String youtubeLink;

    private String twitterLink;

    private String mediumLink;

    public String getMediumLink() {
        return mediumLink;
    }

    public void setMediumLink(String mediumLink) {
        this.mediumLink = mediumLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getVkLink() {
        return vkLink;
    }

    public void setVkLink(String vkLink) {
        this.vkLink = vkLink;
    }

    public String getLinkedInLink() {
        return linkedInLink;
    }

    public void setLinkedInLink(String linkedInLink) {
        this.linkedInLink = linkedInLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getWhatsappLink() {
        return whatsappLink;
    }

    public void setWhatsappLink(String whatsappLink) {
        this.whatsappLink = whatsappLink;
    }

    public String getViberLink() {
        return viberLink;
    }

    public void setViberLink(String viberLink) {
        this.viberLink = viberLink;
    }

    public String getTelegramLink() {
        return telegramLink;
    }

    public void setTelegramLink(String telegramLink) {
        this.telegramLink = telegramLink;
    }

    public String getSkypeLink() {
        return skypeLink;
    }

    public void setSkypeLink(String skypeLink) {
        this.skypeLink = skypeLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {

       /* if ( (youtubeLink.contains("youtu.be") || youtubeLink.contains("youtube")) && youtubeLink.contains("?uid")) {
            int ind = youtubeLink.indexOf('?');
            if (ind != -1)
                youtubeLink = youtubeLink.substring(0, ind);
        }*/

        this.youtubeLink = youtubeLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }
}
