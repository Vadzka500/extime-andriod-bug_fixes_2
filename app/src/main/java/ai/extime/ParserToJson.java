package ai.extime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ai.extime.Activity.MainActivity;
import ai.extime.Models.Contact;
import ai.extime.Models.HashTag;
import ai.extime.Models.ListAdress;
import ai.extime.Services.ContactCacheService;
import ai.extime.Models.ContactInfo;
import io.realm.Realm;

public class ParserToJson {

    ArrayList<Contact> listOfContacts;

    private Realm jsonRealm;

    public ParserToJson(){
        jsonRealm = Realm.getDefaultInstance(); //+
        listOfContacts = ContactCacheService.getAllContactsRealm(null, jsonRealm);

        Collections.sort(listOfContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {

                return o2.getDateCreate().compareTo(o1.getDateCreate());

            }
        });
    }

    public String getJson(){
        JSONArray js = new JSONArray();
        System.out.println("START CONVERT JSON");

        for(Contact contact : listOfContacts){
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", contact.getId());
                obj.put("name", contact.getName());
                obj.put("company",contact.getCompany());
                obj.put("companyPossition",contact.getCompanyPossition());
                obj.put("adress",contact.getAdress());
                obj.put("webSite",contact.webSite);
                obj.put("hashtag",contact.hashtag);
                obj.put("call2me",contact.call2me);
                obj.put("isNew",contact.isNew);
                obj.put("isCreate",contact.isCreate);
                obj.put("time",contact.time);
                obj.put("idContact",contact.getIdContact());
                obj.put("photoURL",contact.photoURL);
                obj.put("color",contact.color);
                obj.put("hasViber",contact.hasViber);
                obj.put("hasTelegram",contact.hasTelegram);
                obj.put("hasFacebook",contact.hasFacebook);
                obj.put("hasWhatsapp",contact.hasWhatsapp);
                obj.put("hasSkype",contact.hasSkype);
                obj.put("hasLinked",contact.hasLinked);
                obj.put("hasInst",contact.hasInst);
                obj.put("hasVk",contact.hasVk);
                obj.put("hasTwitter",contact.hasTwitter);
                obj.put("hasYoutube",contact.hasYoutube);
                obj.put("hasMedium",contact.hasYoutube);

                obj.put("isFavorite",contact.isFavorite);
                obj.put("isImportant",contact.isImportant);
                obj.put("isFinished",contact.isFinished);
                obj.put("isPause",contact.isPause);

                obj.put("isCrown",contact.isCrown);
                obj.put("isVip",contact.isVip);
                obj.put("isStartup",contact.isStartup);
                obj.put("isInvestor",contact.isInvestor);


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                //obj.put("dateAddMark",dateFormat.format(contact.dateAddMark));

                obj.put("shortInto",contact.shortInto);
                //obj.put("emailCompany",contact.emailCompany);







                obj.put("dateCreate",dateFormat.format(contact.getDateCreate()));

                if(contact.listOfAdress != null && !contact.listOfAdress.isEmpty()){
                    JSONArray arrayAddress = new JSONArray();
                    for(ListAdress listAdress: contact.listOfAdress){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("address", listAdress.getAddress());
                        arrayAddress.put(jsonObject);
                    }
                    obj.put("listOfAdress", arrayAddress);
                }

                if(contact.getSocialModel() != null){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("facebookLink",contact.getSocialModel().getFacebookLink());
                    jsonObject.put("vkLink",contact.getSocialModel().getVkLink());
                    jsonObject.put("linkedInLink",contact.getSocialModel().getLinkedInLink());
                    jsonObject.put("instagramLink",contact.getSocialModel().getInstagramLink());
                    jsonObject.put("whatsappLink",contact.getSocialModel().getWhatsappLink());
                    jsonObject.put("viberLink",contact.getSocialModel().getViberLink());
                    jsonObject.put("telegramLink",contact.getSocialModel().getTelegramLink());
                    jsonObject.put("skypeLink",contact.getSocialModel().getSkypeLink());
                    jsonObject.put("youtubeLink",contact.getSocialModel().getYoutubeLink());
                    jsonObject.put("twitterLink",contact.getSocialModel().getTwitterLink());
                    jsonObject.put("mediumLink",contact.getSocialModel().getTwitterLink());

                    obj.put("socialModel",jsonObject);
                }

                if(contact.listOfContactInfo != null && !contact.listOfContactInfo.isEmpty()){
                    JSONArray arrayContactInfo= new JSONArray();
                    for(ContactInfo contactInfo : contact.listOfContactInfo) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", contactInfo.id);
                        jsonObject.put("type", contactInfo.type);
                        jsonObject.put("value", contactInfo.value);
                        jsonObject.put("carrier", null);
                        jsonObject.put("region", null);
                        jsonObject.put("newValue", contactInfo.getNewValue());
                        jsonObject.put("typeData", contactInfo.typeData);
                        jsonObject.put("isEmail", contactInfo.isEmail);
                        jsonObject.put("isPhone", contactInfo.isPhone);
                        jsonObject.put("isGeo", contactInfo.isGeo);
                        jsonObject.put("isNote", contactInfo.isNote);
                        jsonObject.put("isConfirmed", contactInfo.isConfirmed);
                        jsonObject.put("titleValue", contactInfo.titleValue);


                        arrayContactInfo.put(jsonObject);
                    }
                    obj.put("listOfContactInfo",arrayContactInfo);
                }

                if(contact.accountTypes != null && !contact.accountTypes.isEmpty()){
                    JSONArray arrayAccountType = new JSONArray();
                    for(ListAdress listAdress : contact.accountTypes){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("address", listAdress.getAddress());
                        arrayAccountType.put(jsonObject);
                    }
                    obj.put("accountTypes",arrayAccountType);
                }

                if(contact.getListOfHashtags() != null && !contact.getListOfHashtags().isEmpty()){
                    JSONArray arrayHashtags = new JSONArray();
                    for(HashTag hashTag : contact.getListOfHashtags()){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("hashTagValue",hashTag.getHashTagValue());
                        jsonObject.put("typeHashtag",hashTag.getTypeHashtag());
                        if(hashTag.getDate() != null)
                            jsonObject.put("date",dateFormat.format(hashTag.getDate()));
                        else
                            jsonObject.put("date",dateFormat.format(new Date()));
                        arrayHashtags.put(jsonObject);
                    }
                    obj.put("listOfHashtags",arrayHashtags);
                }

                if(contact.listOfContacts != null && !contact.listOfContacts.isEmpty()){
                    JSONArray arrayContacts = new JSONArray();
                    for(Contact contact1 : contact.listOfContacts){
                        JSONObject jsonObject = new JSONObject();

                        //==================================================================================================
                        jsonObject.put("id", contact1.getId());
                        jsonObject.put("name", contact1.getName());
                        jsonObject.put("company",contact1.getCompany());
                        jsonObject.put("companyPossition",contact1.getCompanyPossition());
                        jsonObject.put("adress",contact1.getAdress());
                        jsonObject.put("webSite",contact1.webSite);
                        jsonObject.put("hashtag",contact1.hashtag);
                        jsonObject.put("call2me",contact1.call2me);
                        jsonObject.put("isNew",contact1.isNew);
                        jsonObject.put("isCreate",contact1.isCreate);
                        jsonObject.put("time",contact1.time);
                        jsonObject.put("idContact",contact1.getIdContact());
                        jsonObject.put("photoURL",contact1.photoURL);
                        jsonObject.put("color",contact1.color);
                        jsonObject.put("hasViber",contact1.hasViber);
                        jsonObject.put("hasTelegram",contact1.hasTelegram);
                        jsonObject.put("hasFacebook",contact1.hasFacebook);
                        jsonObject.put("hasWhatsapp",contact1.hasWhatsapp);
                        jsonObject.put("hasSkype",contact1.hasSkype);
                        jsonObject.put("hasLinked",contact1.hasLinked);
                        jsonObject.put("hasInst",contact1.hasInst);
                        jsonObject.put("hasVk",contact1.hasVk);
                        jsonObject.put("hasTwitter",contact1.hasTwitter);
                        jsonObject.put("hasYoutube",contact1.hasYoutube);
                        jsonObject.put("hasMedium",contact1.hasYoutube);
                        jsonObject.put("isFavorite",contact1.isFavorite);
                        jsonObject.put("isImportant",contact1.isImportant);
                        jsonObject.put("isFinished",contact1.isFinished);
                        jsonObject.put("isPause",contact1.isPause);

                        jsonObject.put("isCrown",contact.isCrown);
                        jsonObject.put("isVip",contact.isVip);
                        jsonObject.put("isStartup",contact.isStartup);
                        jsonObject.put("isInvestor",contact.isInvestor);

                        //jsonObject.put("dateAddMark",dateFormat.format(contact1.dateAddMark));

                        jsonObject.put("shortInto",contact1.shortInto);
                        //jsonObject.put("emailCompany",contact1.emailCompany);




                        jsonObject.put("dateCreate",dateFormat.format(contact1.getDateCreate()));

                        if(contact1.listOfAdress != null && !contact1.listOfAdress.isEmpty()){
                            JSONArray arrayAddress = new JSONArray();
                            for(ListAdress listAdress: contact1.listOfAdress){
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("address", listAdress.getAddress());
                                arrayAddress.put(jsonObject2);
                            }
                            jsonObject.put("listOfAdress", arrayAddress);
                        }

                        if(contact1.getSocialModel() != null){
                            JSONObject jsonObject2 = new JSONObject();
                            jsonObject2.put("facebookLink",contact1.getSocialModel().getFacebookLink());
                            jsonObject2.put("vkLink",contact1.getSocialModel().getVkLink());
                            jsonObject2.put("linkedInLink",contact1.getSocialModel().getLinkedInLink());
                            jsonObject2.put("instagramLink",contact1.getSocialModel().getInstagramLink());
                            jsonObject2.put("whatsappLink",contact1.getSocialModel().getWhatsappLink());
                            jsonObject2.put("viberLink",contact1.getSocialModel().getViberLink());
                            jsonObject2.put("telegramLink",contact1.getSocialModel().getTelegramLink());
                            jsonObject2.put("skypeLink",contact1.getSocialModel().getSkypeLink());
                            jsonObject2.put("youtubeLink",contact1.getSocialModel().getYoutubeLink());
                            jsonObject2.put("twitterLink",contact1.getSocialModel().getTwitterLink());
                            jsonObject2.put("mediumLink",contact1.getSocialModel().getTwitterLink());

                            jsonObject.put("socialModel",jsonObject2);
                        }

                        if(contact1.listOfContactInfo != null && !contact1.listOfContactInfo.isEmpty()){
                            JSONArray arrayContactInfo= new JSONArray();
                            for(ContactInfo contactInfo : contact.listOfContactInfo) {
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("id", contactInfo.id);
                                jsonObject2.put("type", contactInfo.type);
                                jsonObject2.put("value", contactInfo.value);
                                jsonObject2.put("carrier", null);
                                jsonObject2.put("region", null);
                                jsonObject2.put("newValue", contactInfo.getNewValue());
                                jsonObject2.put("typeData", contactInfo.typeData);
                                jsonObject2.put("isEmail", contactInfo.isEmail);
                                jsonObject2.put("isPhone", contactInfo.isPhone);
                                jsonObject2.put("isGeo", contactInfo.isGeo);
                                jsonObject2.put("isNote", contactInfo.isNote);
                                jsonObject2.put("isConfirmed", contactInfo.isConfirmed);
                                jsonObject2.put("titleValue", contactInfo.titleValue);
                                arrayContactInfo.put(jsonObject2);
                            }
                            jsonObject.put("listOfContactInfo",arrayContactInfo);
                        }

                        if(contact1.accountTypes != null && !contact1.accountTypes.isEmpty()){
                            JSONArray arrayAccountType = new JSONArray();
                            for(ListAdress listAdress : contact1.accountTypes){
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("address", listAdress.getAddress());
                                arrayAccountType.put(jsonObject2);
                            }
                            jsonObject.put("accountTypes",arrayAccountType);
                        }


                        if(contact1.getListOfHashtags() != null && !contact1.getListOfHashtags().isEmpty()){
                            JSONArray arrayHashtags = new JSONArray();
                            for(HashTag hashTag : contact1.getListOfHashtags()){
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("hashTagValue",hashTag.getHashTagValue());
                                jsonObject2.put("typeHashtag",hashTag.getTypeHashtag());
                                if(hashTag.getDate() !=null)
                                    jsonObject2.put("date",dateFormat.format(hashTag.getDate()));
                                else
                                    jsonObject2.put("date",dateFormat.format(new Date()));
                                arrayHashtags.put(jsonObject2);
                            }
                            jsonObject.put("listOfHashtags",arrayHashtags);
                        }

                        jsonObject.put("listOfContacts",new JSONArray());
                        //==================================================================================================
                        arrayContacts.put(jsonObject);
                    }
                    obj.put("listOfContacts",arrayContacts);
                }else {
                    obj.put("listOfContacts",new JSONArray());
                }

                js.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        listOfContacts.clear();
        jsonRealm.close();
        System.out.println("END CONVERT JSON");

        if(MainActivity.destroyApp){
            System.out.println("json Compact = "+Realm.compactRealm(Realm.getDefaultConfiguration()));
        }


        return js.toString();
    }


}
