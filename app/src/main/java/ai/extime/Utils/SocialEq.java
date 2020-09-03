package ai.extime.Utils;

import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;

public class SocialEq {

    public static String getSub(String web_c) {

        /*if (web_c.contains("www.")) {
            web_c = web_c.replace("www.", "");
        }

        if (web_c.contains("https://")) {
            web_c = web_c.replace("https://", "");
        }

        if (web_c.contains("http://")) {
            web_c = web_c.replace("http://", "");
        }*/

        if (web_c.contains("twitter.com") && web_c.contains("?")) {
            int ind = web_c.indexOf('?');
            if (ind != -1)
                web_c = web_c.substring(0, ind);

        }

        if ((web_c.contains("youtu.be") || web_c.contains("youtube")) && web_c.contains("?") && !web_c.contains("watch?")) {
            int ind = web_c.indexOf('?');
            if (ind != -1)
                web_c = web_c.substring(0, ind);

        }

        if ((web_c.contains("facebook.com") || web_c.contains("fb.com")) && web_c.contains("?") && !web_c.contains("?id=") && !web_c.contains("?v=") && !web_c.contains("?story")) {
            int ind = web_c.indexOf("?");
            if (ind != -1)
                web_c = web_c.substring(0, ind);

        }

        if ((web_c.contains("facebook.com") || web_c.contains("fb.com")) && web_c.contains("&__")) {
            int ind = web_c.indexOf("&__");
            if (ind != -1)
                web_c = web_c.substring(0, ind);
        }


        if (web_c.contains(".com/"))
            web_c = web_c.substring(web_c.indexOf(".com/") + 5);

        if (web_c.length() > 2 && web_c.charAt(web_c.length() - 1) == '/') {
            web_c = web_c.substring(0, web_c.length() - 1);
        }



        return web_c;
    }

    public static boolean checkSocial(String str, Contact contact) {
        String web_c = getSub(str);

        if (checkFacebook(web_c, contact)) return true;
        if (checkInsta(web_c, contact)) return true;
        if (checkLinkedIn(web_c, contact)) return true;
        if (checkMedium(web_c, contact)) return true;
        if (checkTwitter(web_c, contact)) return true;
        if (checkVk(web_c, contact)) return true;
        if (checkYoutube(web_c, contact)) return true;

        return false;
    }



    public static boolean checkFacebook(String str, Contact contact) {

        if(checkFacebookSocial(str, contact)) return true;

        if(checkFacebookList(str, contact)) return true;

        return false;
    }

    public static boolean checkInsta(String str, Contact contact) {

        if(checkInstaSocial(str, contact)) return true;

        if(checkInstaList(str, contact)) return true;

        return false;
    }

    public static boolean checkLinkedIn(String str, Contact contact) {

        if(checkLinkedInSocial(str, contact)) return true;

        if(checkLinkedInList(str, contact)) return true;

        return false;
    }

    public static boolean checkVk(String str, Contact contact) {

        if(checkVkSocial(str, contact)) return true;

        if(checkVkList(str, contact)) return true;

        return false;
    }

    public static boolean checkYoutube(String str, Contact contact) {

        if(checkYoutubeSocial(str, contact)) return true;

        if(checkYoutubeList(str, contact)) return true;

        return false;
    }

    public static boolean checkMedium(String str, Contact contact) {

        if(checkMediumSocail(str, contact)) return true;

        if(checkMediumList(str, contact)) return true;

        return false;
    }

    public static boolean checkTwitter(String str, Contact contact) {

        if(checkTwitterSocial(str, contact)) return true;

        if(checkTwitterList(str, contact)) return true;

        return false;
    }

    public static boolean checkFacebookSocial(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getFacebookLink().trim())))
                    return true;
            }
        }


        return false;
    }

    public static boolean checkInstaSocial(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getInstagramLink().trim())))
                    return true;
            }
        }

        return false;
    }

    public static boolean checkLinkedInSocial(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getLinkedInLink().trim())))
                    return true;
            }
        }

        return false;
    }

    public static boolean checkVkSocial(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getVkLink().trim())))
                    return true;
            }
        }

        return false;
    }

    public static boolean checkYoutubeSocial(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getYoutubeLink().trim())))
                    return true;
            }
        }

        return false;
    }

    public static boolean checkMediumSocail(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getMediumLink().trim())))
                    return true;
            }
        }

        return false;
    }

    public static boolean checkTwitterSocial(String str, Contact contact) {

        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().trim().isEmpty()) {
                if (str.equalsIgnoreCase(getSub(contact.getSocialModel().getTwitterLink().trim())))
                    return true;
            }
        }


        return false;
    }


    public static boolean checkFacebookList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isFacebook(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkInstaList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isInsta(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkLinkedInList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isLinkedIn(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkVkList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isVk(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkYoutubeList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isYoutube(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkMediumList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isMedium(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkTwitterList(String str, Contact contact) {

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            for(ContactInfo ci : contact.getListOfContactInfo()){
                try {
                    if (ClipboardType.isTwitter(ci.value)) {
                        if (str.equalsIgnoreCase(getSub(ci.value.trim().trim()))) return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean checkStrSocials(String str1, String str2){
        return getSub(str1).equalsIgnoreCase(getSub(str2));
    }

    public static boolean isSocial(String str){
        if(ClipboardType.isFacebook(str)) return true;
        if(ClipboardType.isInsta(str)) return true;
        if(ClipboardType.isLinkedIn(str)) return true;
        if(ClipboardType.isYoutube(str)) return true;
        if(ClipboardType.isTwitter(str)) return true;
        if(ClipboardType.isMedium(str)) return true;
        if(ClipboardType.isVk(str)) return true;
        if(ClipboardType.is_Angel(str)) return true;
        if(ClipboardType.is_Tumblr(str)) return true;
        if(ClipboardType.isGitHub(str)) return true;



        return false;
    }



}
