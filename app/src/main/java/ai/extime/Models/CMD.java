package ai.extime.Models;

import ai.extime.Enums.MagicStringCMDEnum;

/**
 * Created by andrew on 06.12.2017.
 */

public class CMD {

    private MagicStringCMDEnum magicStringCMDEnum;

    private String contactFindName = "";

    private String hashTag = "";

    private String maskFindName = "";

    public CMD(){
        magicStringCMDEnum = MagicStringCMDEnum.NONE;
    }

    public MagicStringCMDEnum getMagicStringCMDEnum() {
        return magicStringCMDEnum;
    }

    public void setMagicStringCMDEnum(MagicStringCMDEnum magicStringCMDEnum) {
        this.magicStringCMDEnum = magicStringCMDEnum;
    }

    public String getContactFindName() {
        return contactFindName;
    }

    public void setContactFindName(String contactFindName) {
        this.contactFindName = contactFindName;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getMaskFindName() {
        return maskFindName;
    }

    public void setMaskFindName(String maskFindName) {
        this.maskFindName = maskFindName;
    }
}
