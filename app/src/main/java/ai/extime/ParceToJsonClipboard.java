package ai.extime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactDataClipboard;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Models.ListAdress;

public class ParceToJsonClipboard {

    private ArrayList<Clipboard> listOfCLipboard;

    public ParceToJsonClipboard(ArrayList<Clipboard> list) {
        this.listOfCLipboard = list;
    }

    public String getJson() {
        JSONArray js = new JSONArray();

        System.out.println("START CONVERT CLIPBOARD");

        for (Clipboard clipboard : listOfCLipboard) {
            try {
                JSONObject object = new JSONObject();

                object.put("valueCopy", clipboard.getValueCopy());
                object.put("time", clipboard.getTime());
                object.put("imageTypeClipboard", clipboard.getImageTypeClipboard());
                object.put("imageSearchCircle", null);
                object.put("type", clipboard.getType());
                object.put("label", clipboard.getLabel());
                object.put("nameFromSocial", clipboard.getNameFromSocial());

                if(clipboard.getListContactsSearch() != null && !clipboard.getListContactsSearch().isEmpty()){
                    JSONArray js_2 = new JSONArray();

                    for(ContactDataClipboard contactDataClipboard : clipboard.getListContactsSearch()){
                        JSONObject object_2 = new JSONObject();

                        object_2.put("id", contactDataClipboard.getId());
                        object_2.put("name", contactDataClipboard.getName());
                        object_2.put("photoURL", contactDataClipboard.getPhotoURL());
                        object_2.put("color", contactDataClipboard.color);
                        object_2.put("contact", contactDataClipboard.contact);

                        js_2.put(object_2);
                    }
                    object.put("listContactsSearch", js_2);
                }

                if(clipboard.getListClipboards() != null && !clipboard.getListClipboards().isEmpty()){
                    JSONArray js_3 = new JSONArray();
                    for(Clipboard clipboard1 : clipboard.getListClipboards()){
                        JSONObject object_3 = new JSONObject();



                        object_3.put("valueCopy", clipboard1.getValueCopy());
                        object_3.put("time", clipboard1.getTime());
                        object_3.put("imageTypeClipboard", clipboard1.getImageTypeClipboard());
                        object_3.put("imageSearchCircle", null);
                        object_3.put("type", clipboard1.getType());
                        object_3.put("label", clipboard1.getLabel());
                        object_3.put("nameFromSocial", clipboard1.getNameFromSocial());

                        if(clipboard1.getListContactsSearch() != null && !clipboard1.getListContactsSearch().isEmpty()){
                            JSONArray js_2 = new JSONArray();

                            for(ContactDataClipboard contactDataClipboard : clipboard1.getListContactsSearch()){
                                JSONObject object_2 = new JSONObject();

                                object_2.put("id", contactDataClipboard.getId());
                                object_2.put("name", contactDataClipboard.getName());
                                object_2.put("photoURL", contactDataClipboard.getPhotoURL());
                                object_2.put("color", contactDataClipboard.color);
                                object_2.put("contact", contactDataClipboard.contact);

                                js_2.put(object_2);
                            }
                            object_3.put("listContactsSearch", js_2);
                        }
                        js_3.put(object_3);
                    }
                    object.put("listClipboards", js_3);
                }


                js.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        listOfCLipboard.clear();

        System.out.println("END CONVERT CLIPBOARD");
        return js.toString();
    }

}
