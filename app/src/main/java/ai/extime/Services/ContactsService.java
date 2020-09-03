
package ai.extime.Services;


import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.ContactLoadEvent;
import ai.extime.Events.LoadBarEvent;
import ai.extime.Events.RemoveContact;
import ai.extime.Events.UpdateList;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Contact;
import ai.extime.Models.HashTag;
import ai.extime.Models.SocialModel;
import ai.extime.Models.SynkNewC;
import ai.extime.Utils.ClipboardType;
import io.realm.Realm;
import io.realm.RealmList;
import ai.extime.Models.ContactInfo;

import com.extime.R;

import javax.annotation.Nonnull;

public class ContactsService {

    private ContentResolver contentResolver;

    public static Integer CONTACT_COUNT;

    public static Integer CONTACT_COUNT_OF_DELETE;
    public static boolean checkLoad = false;

    private Activity activity = null;
    public boolean checkFirst = false;

    int number;

    int number_file = 0;

    public static Integer CONTACT_COUNT_FILE;

    private ArrayList<Contact> listOfContacts;

    public enum TYPE_CURSOR {
        EMAIL, CONTACT_NAME, CONTACT_ADRESS, CONTACT_PHONES,
        CONTACT_PREV, ACCOUNT_TYPE, APP_PACKAGE, ORGANIZATION, WEBSITE, PROFILE_USER, NOTE
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 91;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public ContactsService(ContentResolver contentResolver, boolean c, Context context) {
        this.contentResolver = contentResolver;
        CONTACT_COUNT_FILE = getContactFromFile(context);
    }

    public ContactsService(ContentResolver contentResolver, boolean c) {
        this.contentResolver = contentResolver;
    }

    public ContactsService(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    public Cursor getCursorByType(TYPE_CURSOR type_cursor, String contactId) {
        Cursor cursorForReturn = null;
        switch (type_cursor) {
            case EMAIL: {
                try {
                    cursorForReturn = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case PROFILE_USER: {
                cursorForReturn = contentResolver.query(ContactsContract.Profile.CONTENT_URI,
                        new String[]{ContactsContract.Profile._ID, ContactsContract.Profile.DISPLAY_NAME, ContactsContract.Profile.PHOTO_URI}, null, null, ContactsContract.Profile.DISPLAY_NAME + " ASC");
                break;
            }
            case WEBSITE: {
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{contactId,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};
                cursorForReturn = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        null, orgWhere, orgWhereParams, null);
                break;
            }
            case CONTACT_NAME: {
                cursorForReturn = contentResolver.query(
                        ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Profile.DISPLAY_NAME},
                        ContactsContract.Contacts._ID + " = ?",
                        new String[]{contactId}, null);
                break;
            }
            case ORGANIZATION: {
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{contactId,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                cursorForReturn = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        null, orgWhere, orgWhereParams, null);
                break;
            }
            case CONTACT_ADRESS: {
                cursorForReturn = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                                ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE},
                        ContactsContract.Data.CONTACT_ID + "=? AND " +
                                ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE + "=?",
                        new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},
                        null);
                break;
            }
            case CONTACT_PHONES: {
                try {
                    cursorForReturn = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);
                } catch (Exception e) {
                    cursorForReturn = null;
                    e.printStackTrace();
                }
                break;
            }
            case CONTACT_PREV: {
                cursorForReturn = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP, ContactsContract.Contacts.PHOTO_URI}, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
                break;
            }
            case ACCOUNT_TYPE: {
                cursorForReturn = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI,
                        new String[]{ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.RawContacts.ACCOUNT_NAME, ContactsContract.RawContacts.DATA_SET},
                        ContactsContract.RawContacts.CONTACT_ID + " = ?",
                        new String[]{contactId}, null);
                break;
            }
            case APP_PACKAGE: {
                cursorForReturn = contentResolver.query(
                        ContactsContract.RawContacts.CONTENT_URI, null,
                        ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                        new String[]{contactId}, null);
                break;
            }
            case NOTE: {
                String noteWhere = ContactsContract.Data.CONTACT_ID
                        + " = ? AND " + ContactsContract.Data.MIMETYPE
                        + " = ?";
                String[] noteWhereParams = new String[]{
                        contactId,
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                cursorForReturn = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI, null,
                        noteWhere, noteWhereParams, null);
                break;
            }
        }
        return cursorForReturn;
    }

    public ArrayList<String> getContactEmails(String contactId) {
        // System.out.println("getContactEmails click");
        ArrayList<String> emailList = new ArrayList<>();
        Cursor cursorForContactEmails;
        cursorForContactEmails = getCursorByType(TYPE_CURSOR.EMAIL, contactId);
        if (cursorForContactEmails != null) {
            while (cursorForContactEmails.moveToNext()) {
                String email = cursorForContactEmails.getString(cursorForContactEmails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                if (email != null && !email.isEmpty() && !emailList.contains(email)) {
                    emailList.add(email);
                }
            }
            cursorForContactEmails.close();
        }
        return emailList;
    }

    public static void updateFaroviteContact(boolean f, String id, Context context) {
        //System.out.println("UPDATE NAME");


        ContentValues contentValues = new ContentValues();

        if (f == true)
            contentValues.put(ContactsContract.Contacts.STARRED, 1);
        else
            contentValues.put(ContactsContract.Contacts.STARRED, 0);

        context.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, contentValues, ContactsContract.Contacts._ID + "=" + id, null);


    }


    public boolean getContactById(String id, long id_, ArrayList<String> list, SocialModel sc) {
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(id)}, null);
        if (cursor != null && cursor.moveToFirst()) {

            if (list.size() > 0) {

                for (String s : list) {
                    if (checkPhoneOnContact(s, id)) {
                        cursor.close();
                        return true;
                    }
                }
                cursor.close();
                return false;
            } else if (sc != null) {


                if (sc.getFacebookLink() != null) {
                    if (checkNoteOnContact(sc.getFacebookLink(), id)) {
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                }

                if (sc.getLinkedInLink() != null) {
                    if (checkNoteOnContact(sc.getLinkedInLink(), id)) {
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                }

                if (sc.getInstagramLink() != null) {
                    if (checkNoteOnContact(sc.getInstagramLink(), id)) {
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                }

                if (sc.getTwitterLink() != null) {
                    if (checkNoteOnContact(sc.getTwitterLink(), id)) {
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                }

                if (sc.getYoutubeLink() != null) {
                    if (checkNoteOnContact(sc.getYoutubeLink(), id)) {
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                }

                if (sc.getVkLink() != null) {
                    if (checkNoteOnContact(sc.getVkLink(), id)) {
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                }

                cursor.close();
                return true;

            } else {
                cursor.close();
                return true;
            }

        } else {

            return false;
        }

    }

    public boolean checkPhoneOnContact(String phone, String id) {
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
        String[] selectionArgs = new String[]{String.valueOf(phone)};

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));

        Cursor c = contentResolver.query(uri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                //if (c.getString(c.getColumnIndex(ContactsContract.PhoneLookup._ID)).equalsIgnoreCase(id)) {
                if (c.getString(c.getColumnIndex(ContactsContract.PhoneLookup._ID)).equalsIgnoreCase(id)) {
                    return true;
                }

            }
        }

        return false;
    }

    /*public void addAddress(String id, String address){

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


     *//*   cursorForReturn = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE},
                ContactsContract.Data.CONTACT_ID + "=? AND " +
                        ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE + "=?",
                new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},
                null);*//*


     *//*String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);

        if (c.moveToFirst()) {*//*
            System.out.println("MOVE TO FIRST ID");
            //int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                    withValue(ContactsContract.Data.RAW_CONTACT_ID, id).
                    withValue(ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE).
                    withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address)
                    .build());
            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                ops.clear();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
       // }






    }
*/

    public boolean checkNoteOnContact(String note, String id) {

        String noteWhere = ContactsContract.CommonDataKinds.Note.NOTE
                + " = ? AND " + ContactsContract.Data.MIMETYPE
                + " = ?";
        String[] noteWhereParams = new String[]{
                note,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

        String[] projection = new String[]{ContactsContract.CommonDataKinds.Note.CONTACT_ID};


        Cursor c = contentResolver.query(
                ContactsContract.Data.CONTENT_URI, projection,
                noteWhere, noteWhereParams, null);

        if (c != null) {
            while (c.moveToNext()) {

                if (c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Note.CONTACT_ID)).equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String getDisplayName(Context context, String id) {
        String displayName = null;
        // define the columns I want the query to return
        final String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME
        };
        try {
            final Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id + ""}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
                cursor.close();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return displayName;
    }

    public static String getPhotoURI(Context context, String id) {
        String photoUri = null;
        // define the columns I want the query to return
        final String[] projection = new String[]{
                ContactsContract.Contacts.PHOTO_URI
        };
        try {
            final Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,
                    ContactsContract.Contacts._ID + " = ?", new String[]{id}, null);
            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                }
                cursor.close();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return photoUri;
    }


    public void insertPosition(String id, String position, String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {



                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(id)};
                Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                            withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.Organization.TITLE, position)
                            .build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] projections = new String[]{ContactsContract.RawContacts._ID};
                    String selections = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=?";
                    String[] selectionArgss = new String[]{String.valueOf(name)};
                    Cursor c2 = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projections, selections, selectionArgss, null);

                    if (c2.moveToFirst()) {
                        int rawContactId = c2.getInt(c2.getColumnIndex(ContactsContract.RawContacts._ID));
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                                withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                                withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Organization.TITLE, position)
                                .build());
                        try {
                            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
        }).start();
    }

    public void insertCompany(String id, String company, String name) {
       /* new Thread(new Runnable() {
            @Override
            public void run() {*/



        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                    withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                    withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE).
                    withValue(ContactsContract.CommonDataKinds.Organization.DATA, company)
                    .build());
            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        } else {
            String[] projections = new String[]{ContactsContract.RawContacts._ID};
            String selections = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=?";
            String[] selectionArgss = new String[]{String.valueOf(name)};
            Cursor c2 = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projections, selections, selectionArgss, null);

            if (c2.moveToFirst()) {
                int rawContactId = c2.getInt(c2.getColumnIndex(ContactsContract.RawContacts._ID));
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                        withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.Organization.DATA, company)
                        .build());
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }

        }
           /* }
        }).start();*/
    }

    public void addComp(String id, String position, String company) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.RawContacts.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Organization.DATA + "=?";
                String[] orgWhereParams = new String[]{String.valueOf(id),
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, company};
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(orgWhere, orgWhereParams)
                        .withValue(
                                ContactsContract.CommonDataKinds.Organization.TITLE,
                                position);


                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addPostionByPosition(String id, String previusPosition, String position) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.RawContacts.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Organization.TITLE + "=?";
                String[] orgWhereParams = new String[]{String.valueOf(id),
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, previusPosition};
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(orgWhere, orgWhereParams)
                        .withValue(
                                ContactsContract.CommonDataKinds.Organization.TITLE,
                                position);

                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addCompanyToContact(String idContacts, String company) {
        new Thread(new Runnable() {
            @Override
            public void run() {



                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(idContacts)};
                Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                            withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.Organization.DATA, company)
                            .build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void addCompanybyCompany(String id, String previysCompany, String newCompany) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.RawContacts.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Organization.DATA + "=?";
                String[] orgWhereParams = new String[]{String.valueOf(id),
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, previysCompany};
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(orgWhere, orgWhereParams)
                        .withValue(
                                ContactsContract.CommonDataKinds.Organization.DATA,
                                newCompany);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addCompanybyPosition(String id, String position, String newCompany) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.RawContacts.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Organization.TITLE + "=?";
                String[] orgWhereParams = new String[]{String.valueOf(id),
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, position};
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(orgWhere, orgWhereParams)
                        .withValue(
                                ContactsContract.CommonDataKinds.Organization.DATA,
                                newCompany);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public long saveContact(Contact contact) {
        System.out.println("saveContact click");


        boolean checkP = false;
        for (ContactInfo contactInfo : contact.listOfContactInfo) {
            if (contactInfo.type.toString().equals("phone") && !contactInfo.value.equals("+000000000000")) {
                checkP = true;
            }
        }

        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder op;

        if (!checkP) {
            op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED);
        } else {
            op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        }

        contentProviderOperations.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName()); // Name of the person

        contentProviderOperations.add(op.build());

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, contact.getCompany())
                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contact.getCompanyPossition())
                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK);
        contentProviderOperations.add(op.build());

        boolean phone = false;
        for (ContactInfo contactInfo : contact.listOfContactInfo) {
            switch (contactInfo.type.toString()) {
                case "phone": {
                    phone = true;
                    op = ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactInfo.value)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

                    contentProviderOperations.add(op.build());

                    break;
                }
                case "email": {
                    op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, contactInfo.value)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
                    contentProviderOperations.add(op.build());
                    break;
                }
                case "note": {

                    op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                    withValue(ContactsContract.CommonDataKinds.Note.NOTE, contactInfo.value);
                    contentProviderOperations.add(op.build());
                    break;
                }
                case "address": {

                    /*cursorForReturn = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                                    ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                                    ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE},
                            ContactsContract.Data.CONTACT_ID + "=? AND " +
                                    ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE + "=?",
                            new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},
                            null);*/

                    //System.out.println("SAVE NOTE = " + contactInfo.value);
                    op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE).
                                    withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, contactInfo.value);
                    contentProviderOperations.add(op.build());

                    break;
                }
            }
        }
        if (!phone) {

            Realm realm = Realm.getDefaultInstance(); //+
            realm.beginTransaction();

            contact.listOfContactInfo.add(new ContactInfo("phone", "+000000000000", false, true, false, false, false));

            realm.commitTransaction();
            realm.close();

            op = ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "+000000000000")
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

            contentProviderOperations.add(op.build());
        }

        for (HashTag hashTag : contact.getListOfHashtags()) {
            op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.Note.NOTE, hashTag.getHashTagValue().toLowerCase());
            contentProviderOperations.add(op.build());
        }
        if (contact.getSocialModel() != null) {
            if (contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getTelegramLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getWhatsappLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
                System.out.println("SAVE FACEBOOK = " + contact.getSocialModel().getFacebookLink());
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getFacebookLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
                System.out.println("SAVE INSTAGRAM = " + contact.getSocialModel().getInstagramLink());
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getInstagramLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, "Skype: " + contact.getSocialModel().getSkypeLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getVkLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getLinkedInLink());
                contentProviderOperations.add(op.build());

            }
            if (contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getViberLink());
                contentProviderOperations.add(op.build());
            }
            if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
                System.out.println("SAVE TWITTER = " + contact.getSocialModel().getTwitterLink());
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getTwitterLink());
                contentProviderOperations.add(op.build());
            }
            if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getYoutubeLink());
                contentProviderOperations.add(op.build());
            }
            if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getSocialModel().getMediumLink());
                contentProviderOperations.add(op.build());
            }
        }

        try {

            ContentProviderResult[] results = contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);



            //System.out.println("NEW ID CONtACT = "+contactID);
            try {
                final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID};

                final Cursor cursor = contentResolver.query(results[0].uri, projection, null, null, null);

                cursor.moveToNext();

                long contactId = cursor.getLong(0);

                final String[] projection2 = new String[]{ContactsContract.Contacts.LOOKUP_KEY};
                String selection = ContactsContract.Contacts._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(contactId)};
                final Cursor cursor2 = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection2, selection, selectionArgs, null);
                cursor2.moveToNext();
                System.out.println("LOOKUP KEY = " + cursor2.getString(0));
                System.out.println("NEW ID = " + contactId);
                return contactId;
            } catch (Exception e) {
                e.printStackTrace();
                Uri myContactUri = results[0].uri;
                int contactID = Integer.parseInt(myContactUri.getLastPathSegment());
                return contactID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addNoteToContact(String idContacts, String note, String name) {

        if ((note.contains("youtu.be") || note.contains("youtube")) && note.contains("?uid")) {
            int ind = note.indexOf('?');
            if (ind != -1)
                note = note.substring(0, ind);
        }

        final String n = note;

        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("addNoteToContact click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                //idContacts = "12354135";
                System.out.println("ID NOTES = " + idContacts);


                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(idContacts)};
                Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);

                if (c.moveToFirst()) {
                    System.out.println("MOVE TO FIRST ID");
                    int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                            withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.Note.NOTE, n)
                            .build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                        ops.clear();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                } else {


                    String[] projections = new String[]{ContactsContract.RawContacts._ID};
                    String selections = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=?";
                    String[] selectionArgss = new String[]{String.valueOf(name)};
                    Cursor c2 = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projections, selections, selectionArgss, null);

                    if (c2.moveToFirst()) {

                        //System.out.println("phone = "+c2.getString(1));
                        // System.out.println("MOVE TO FIRST");
                        int rawContactId = c2.getInt(c2.getColumnIndex(ContactsContract.RawContacts._ID));
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                                withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                                withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Note.NOTE, n)
                                .build());
                        try {
                            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                            ops.clear();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }
                System.out.println("addNoteToContact click END");

            }
        }).start();
    }

    public void addPhoneToContact(String idContacts, String phone, String name) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("addPhoneToContact click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                //idContacts = "4561651";
                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(idContacts)};
                Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));

                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI);
                    builder.withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT);
                    if (!phone.equalsIgnoreCase("+000000000000")) {
                        ops.add(builder.build());

                    }

                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                            withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.Phone.DATA, phone)
                            .build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] projections = new String[]{ContactsContract.RawContacts._ID};
                    String selections = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=?";
                    String[] selectionArgss = new String[]{String.valueOf(name)};
                    Cursor c2 = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projections, selections, selectionArgss, null);

                    if (c2.moveToFirst()) {
                        int rawContactId = c2.getInt(c2.getColumnIndex(ContactsContract.RawContacts._ID));

                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI);
                        builder.withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT);
                        if (!phone.equalsIgnoreCase("+000000000000"))
                            ops.add(builder.build());

                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                                withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                                withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Phone.DATA, phone)
                                .build());
                        try {
                            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                }
                //System.out.println("addPhoneToContact click END");

            }
        }).start();
    }


    public void deleteContact(String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String[] args = new String[]{id};
                ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    ops.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteEmailContact(String idContacts, String email) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String[] args = new String[]{idContacts, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, email};
                //ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Email.DATA + "=?", args)
                        .build());
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteAddressContact(String idContacts, String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String[] args = new String[]{idContacts, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE, address};
                //ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredPostal.DATA + "=?", args)
                        .build());
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteNoteContact(String idContact, String note) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                //builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?"+ " AND "+ ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + "=?", new String[]{idContacts, previosName});

                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?" + " AND " + ContactsContract.CommonDataKinds.Note.NOTE + "=?";
                String[] orgWhereParams = new String[]{String.valueOf(idContact),
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
                        note.trim()};

                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(orgWhere, orgWhereParams)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE,
                                null);


                ops.add(builder.build());


       /* ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", args)
                .build());*/
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    ops.clear();
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                    System.out.println("Error delete note");
                }
            }
        }).start();
    }


    public String getIdContactByName(String name) {


        String id = null;
        Uri lkup = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
        Cursor idCursor = contentResolver.query(lkup, null, null, null, null);
        while (idCursor.moveToNext()) {
            id = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts._ID));
            String key = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            String name2 = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        }



        return id;
    }


    public void deletePhoneContact(String idContacts, String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String[] args = new String[]{idContacts, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, phone};

                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", args)
                        .build());
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void deleteCompany_Possition(String idContact) {
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/


        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String[] args = new String[]{idContact, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
        //ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ?", args)
                .build());
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
           /* }
        }).start();*/
    }

    public void addMailToContact(String idContacts, String mail, String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("addMailToContact click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(idContacts)};
                Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                            withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.Email.DATA, mail)
                            .build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] projections = new String[]{ContactsContract.RawContacts._ID};
                    String selections = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=?";
                    String[] selectionArgss = new String[]{String.valueOf(name)};
                    Cursor c2 = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projections, selections, selectionArgss, null);

                    if (c2.moveToFirst()) {
                        int rawContactId = c2.getInt(c2.getColumnIndex(ContactsContract.RawContacts._ID));
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                                withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                                withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).
                                withValue(ContactsContract.CommonDataKinds.Email.DATA, mail)
                                .build());
                        try {
                            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }).start();

    }


    public void addAddressToContact(String idContacts, String mail, String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("addMailToContact click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                String[] projection = new String[]{ContactsContract.RawContacts._ID};
                String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(idContacts)};
                Cursor c = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                            withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE).
                            withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, mail)
                            .build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }/* else {
            String[] projections = new String[]{ContactsContract.RawContacts._ID};
            String selections = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=?";
            String[] selectionArgss = new String[]{String.valueOf(name)};
            Cursor c2 = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projections, selections, selectionArgss, null);

            if (c2.moveToFirst()) {
                int rawContactId = c2.getInt(c2.getColumnIndex(ContactsContract.RawContacts._ID));
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId).
                        withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, mail)
                        .build());
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }

        }*/

            }
        }).start();

    }

    public void updateName(String idContacts, String previosName, String newName) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("UPDATE NAME");

                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.RawContacts.Data.MIMETYPE + "=?", new String[]{idContacts, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, null);
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setPhoneIsPrimary(String idContacts, String phone, boolean primary) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{idContacts, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, phone});
        builder.withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, primary);
        builder.withValue(ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY, primary);
        ops.add(builder.build());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMailIsPrimary(String idContacts, String email, boolean primary) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + "=?", new String[]{idContacts, email});
        builder.withValue(ContactsContract.CommonDataKinds.Email.IS_PRIMARY, primary);
        builder.withValue(ContactsContract.CommonDataKinds.Email.IS_SUPER_PRIMARY, primary);
        ops.add(builder.build());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePhone(String idContacts, String previosNumber, String newNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("updatePhone click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ContentProviderOperation.Builder builder2 = ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI);
                builder2.withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT);

                if (previosNumber.equalsIgnoreCase("+000000000000")) {
                    ops.add(builder2.build());

                }

                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{idContacts, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, previosNumber});
                builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateNote(String idContacts, String previosNote, String newNote) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("updatePhone click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);

                builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.RawContacts.Data.MIMETYPE + " =? AND " + ContactsContract.CommonDataKinds.Note.NOTE + "=?", new String[]{idContacts, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE, previosNote});
                builder.withValue(ContactsContract.CommonDataKinds.Note.NOTE, newNote);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateCompany(String idContacts, String previosCompany, String newCompany) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("updateCompany click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{String.valueOf(idContacts),
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(orgWhere, orgWhereParams)
                        .withValue(
                                ContactsContract.CommonDataKinds.Organization.DATA,
                                newCompany);


                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


    public void updateEmail(String idContacts, String previosEmail, String newEmail) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("updateEmail click");
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + "=?", new String[]{idContacts, previosEmail});
                builder.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, newEmail);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateLocation(String idContacts, String previosLocation, String newLocation) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.CommonDataKinds.Organization.TITLE + "=?", new String[]{idContacts, previosLocation});
                builder.withValue(ContactsContract.CommonDataKinds.Organization.TITLE, newLocation);
                ops.add(builder.build());

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean deleteContacts(ArrayList<Contact> listOfContactsForRemove) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        boolean allContactsRemove = true;
        System.out.println(" DELETE " + listOfContactsForRemove);

        for (Contact removeContact : listOfContactsForRemove) {
            String[] args = new String[]{removeContact.getIdContact()};


            new Thread(new Runnable() {
                @Override
                public void run() {
                    ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
                    try {
                        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
        System.out.println(" DELETE END" + listOfContactsForRemove);
        return allContactsRemove;
    }

    public boolean fastUpdateContact(Contact contactForUpdate) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{contactForUpdate.getIdContact(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactForUpdate.getName());
        ops.add(builder.build());

        builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{contactForUpdate.getIdContact(), ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.Organization.DATA, contactForUpdate.getCompany());
        builder.withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contactForUpdate.getCompanyPossition());
        ops.add(builder.build());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


  /*  public boolean fastUpdateContactBiId(Contact contactForUpdate, String id) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?"+ " AND "+ ContactsContract.Data.MIMETYPE + "=?", new String[]{id,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactForUpdate.getName());
        ops.add(builder.build());

        builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?"+ " AND "+ ContactsContract.Data.MIMETYPE + "=?", new String[]{id,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.Organization.DATA, contactForUpdate.getCompany());
        builder.withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contactForUpdate.getCompanyPossition());
        ops.add(builder.build());

        try
        {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public String getContactName(String contactId) {
        String contactName = null;
        Cursor cursorForContactName;
            cursorForContactName = getCursorByType(TYPE_CURSOR.CONTACT_NAME,contactId);
            while (cursorForContactName.moveToNext()) {
                contactName = cursorForContactName.getString(cursorForContactName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (contactName != null && !contactName.isEmpty())
                    cursorForContactName.close();
            }
            return contactName;
    }*/

    public String getContactAddress(String contactId) {
        Cursor cursorForContactAdress;
        String address = null;
        cursorForContactAdress = getCursorByType(TYPE_CURSOR.CONTACT_ADRESS, contactId);
        if (cursorForContactAdress != null)
            if (cursorForContactAdress.moveToFirst()) {
                address = cursorForContactAdress.getString(cursorForContactAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                cursorForContactAdress.close();
            }
        return address;
    }

    public String getWebSite(String contactId) {
        String webSite = "";
        Cursor cursorForContactPhones;
        cursorForContactPhones = getCursorByType(TYPE_CURSOR.WEBSITE, contactId);
        while (cursorForContactPhones.moveToNext()) {
            webSite = cursorForContactPhones.getString(cursorForContactPhones.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
        }
        cursorForContactPhones.close();
        return webSite;
    }

    public ArrayList<String> getContactPhones(String contactId) {
        ArrayList<String> phoneList = new ArrayList<>();
        Cursor cursorForContactPhones;
        cursorForContactPhones = getCursorByType(TYPE_CURSOR.CONTACT_PHONES, contactId);
        try {


            while (cursorForContactPhones != null && cursorForContactPhones.moveToNext()) {
                String phoneNumber = cursorForContactPhones.getString(cursorForContactPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber != null && !phoneNumber.isEmpty() && !phoneList.contains(phoneNumber)) {
                    phoneList.add(phoneNumber);
                }
            }
        } catch (Exception e) {
            System.out.println("ERRO GET PHONE : " + e.fillInStackTrace());
        }
        if (cursorForContactPhones != null)
            cursorForContactPhones.close();

        return phoneList;
    }

    private JSONArray mainJsContact;

    public int getContactFromFile(Context context) {

        Storage storage = new Storage(context);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        String nameBackup = null;
        List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
        if (listBack != null) {
            for (int i = 0; i < listBack.size(); i++) {
                if (listBack.get(i).getName().contains("backup_") || listBack.get(i).getName().equalsIgnoreCase("backup")) {
                    nameBackup = listBack.get(i).getName();
                }
            }
        }

        System.out.println("READ FROM FILE 2");

        String content = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);


        JSONArray js = new JSONArray();

        try {
            js = new JSONArray(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mainJsContact = js;

        return js.length();

    }

    public int getContactsCt() {
        System.out.println("SIZE RUN");
        //    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
        //            new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME}, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");


        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_URI}, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        int contactsCt = 0;

        /*if (contacts != null) {  //old
            System.out.println("CONTACT COUNT = "+contacts.getCount());
            contactsCt = contacts.getCount();
            contacts.close();
        }*/
        //     ArrayList<String> name = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) != null) {
                    contactsCt++; // new

                }

            }
        }

        return contactsCt;
    }

    public int getAllCt() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts.PHOTO_URI}, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        return cursor.getCount();

    }

    public Contact getUserProfile() {
        System.out.println("getUserProfile");

        //Contact c = ContactCacheService.getFirstContact();
        return null;

    }

    public static int prev = 0;

    public ArrayList<Contact> getContactsPrevWithTypes(boolean fromService, Activity activity) {

        checkLoad = true;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if(CONTACT_COUNT == null)
            CONTACT_COUNT = getContactsCt();

        //  System.out.print("fromService = "+fromService);
        int quantityContacts = getAllCt();
        //   System.out.println("quantityContacts = "+quantityContacts);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);


        this.activity = activity;
        int countConpany = 0;
        int countContact = 0;

//        MainActivity.upd = 1;


        CONTACT_COUNT_OF_DELETE = CONTACT_COUNT;

        if (CONTACT_COUNT > 45 && !fromService) quantityContacts = 30;

        listOfContacts = new ArrayList<>();

        Cursor cursorForPrevWithTypes = getCursorByType(TYPE_CURSOR.CONTACT_PREV, null);
        int q = 0;
        // ArrayList<String> name = new ArrayList<>();

        if (cursorForPrevWithTypes != null) {

            //  boolean checkFirst =  sharedPreferences.getBoolean("FIRST", false);
            sharedPreferences.edit().putBoolean("FIRST", true).apply();
            int inds = 0;
            if (prev != 0) {
                inds = prev;
                q = prev;
            }
            for (int indexI = inds; indexI < quantityContacts; indexI++) {
                activity = this.activity;
                prev++;
                //   number = indexI;
                // System.out.println("NUMBER = "+number);
                cursorForPrevWithTypes.moveToNext();

                //   System.out.println("NAME TO GET = "+cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                if (cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) != null) {

                       /* boolean ch = false;
                        for(String n : name){
                            if(cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals(n))
                                ch = true;
                        }
                        if (ch) {

                        //    System.out.println("STOOOOOOOOOOOOOP");
                            continue;
                        }*/

                    Activity finalActivity = activity;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) finalActivity.findViewById(R.id.barFavorite_count)).setText(" " + String.valueOf(CONTACT_COUNT) + "");
                            ((TextView) finalActivity.findViewById(R.id.barCountContacts)).setText("" + ((TextView) finalActivity.findViewById(R.id.barFavorite_count)).getText());
                            ((TextView) finalActivity.findViewById(R.id.barHashtag_count)).setText("0");

                        }
                    });


                    //     name.add(cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    //     System.out.println(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))+"phone numb = "+cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    number = q;
                    q++;

                    String contactId = cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts._ID));
                    String contactName = cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String photoURL = cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    String timestamp = (cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)).toString());
                    //System.out.println("TIMESTAMP = "+timestamp);
                       /* try {
                            Date d = null;
                            String dd = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date(Long.parseLong(timestamp)));
                            Date d2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dd);
                            System.out.println("DATEEEEEEEEEEEEEEEE CONTACT STRING= "+dd);
                            System.out.println("DATEEEEEEEEEEEEEEEE CONTACT = "+d2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/



                    if (contactName == null)
                        contactName = "";

                        /*String dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(timestamp)));
                        Date date = new Date();
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/

                    Date date = new Date();
                    date = new Date(date.getTime() - 40 * 24 * 3600 * 1000L);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    Time time = getRandomDate();
                    time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                    time.setMinutes(cal.get(Calendar.MINUTE));
                    time.setSeconds(cal.get(Calendar.SECOND));


                    Contact contact = new Contact(Integer.valueOf(contactId), contactName, null, false, true, true, time.toString(), null, date);
                    //   contact.setName(contactName);
                    int nameHash = contact.getName().hashCode();
                    contact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                    if (photoURL != null) {
                        contact.setPhotoURL(photoURL);
                    }
                    contact.setIdContact(contactId);

                    contactName = contactName.trim();
                    ArrayList<String> phone = getContactPhones(contactId);
                    if (phone != null && phone.size() > 0) {
                        for (int i = 0; i < phone.size(); i++) {
                            //   phone.get(i).trim();
                            contact.addPhone(phone.get(i));
                            contactName = contactName.trim();
                            String phonee = phone.get(i).trim();
                            phonee = phonee.replaceAll(" ", "");
                            phonee = phonee.replaceAll("-", "");
                            System.out.println("phonee = " + phonee);
                            System.out.println("name = " + contactName);
                            if (contactName.contains(phonee) && contactName.length() > phonee.length()) {
                                contactName = contactName.replace(phonee, "");
                                contactName = contactName.trim();
                            }
                        }
                        contact.setName(contactName.trim());
                    } else {
                        contact.addPhone("+000000000000");
                        addPhoneToContact(contactId, "+000000000000", contactName);
                        contact.setName(contactName.trim());
                    }
                    System.out.println("name end = " + contactName);

                    ArrayList<String> addr = getContactEmails(contactId);
                    for (int i = 0; i < addr.size(); i++) {
                        contact.addEmail(addr.get(i));
                    }


                    RealmList<HashTag> hashList = new RealmList<>();
                    Cursor noteCursor = getCursorByType(TYPE_CURSOR.NOTE, contactId);
                    ArrayList<String> listOfHash = new ArrayList<>();
                    if (noteCursor != null && noteCursor.getCount() > 0) {
                        while (noteCursor.moveToNext()) {
                            String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                            System.out.println("NOTE = " + note);
                            if (note != null && note.length() > 0) {
                                if (note != null && note.length() > 0 && note.charAt(0) == '#' && !listOfHash.contains(note.toLowerCase().trim())) {
                                    HashTag hashtag = new HashTag(note.toLowerCase().trim());
                                    hashList.add(hashtag);

                                    listOfHash.add(note.toLowerCase().trim());

                                } else if (ClipboardType.isInsta(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasInst = true;


                                    String username = note;

                                    if (!username.toLowerCase().contains("instagram.com")) {
                                        username = "https://instagram.com/" + username;
                                    }

                                    //.substring(note.indexOf(".com")+5, note.length());
                                        /*if(username.charAt(username.length()-1) == '/')
                                            username = username.substring(0,username.length()-1);*/

                                    if (username.contains("?utm")) {
                                        int ind = username.indexOf('?');
                                        if (ind != -1)
                                            username = username.substring(0, ind);

                                        updateNote(contactId, note, username);
                                    }


                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setInstagramLink(username);
                                        contact.setSocialModel(socialModel1);
                                    } else {
                                        if (socialModel.getInstagramLink() == null || socialModel.getInstagramLink().isEmpty()) {
                                            socialModel.setInstagramLink(username);
                                            contact.setSocialModel(socialModel);
                                        } else {
                                            if (contact.listOfContactInfo == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }

                                } else if (ClipboardType.isVk(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasVk = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setVkLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {

                                        if (socialModel.getVkLink() == null || socialModel.getVkLink().isEmpty()) {
                                            socialModel.setVkLink(note);
                                            contact.setSocialModel(socialModel);
                                        } else {
                                            if (contact.listOfContactInfo == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }

                                    }
                                } else if (ClipboardType.isFacebook(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasFacebook = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setFacebookLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {

                                        if (socialModel.getFacebookLink() == null || socialModel.getFacebookLink().isEmpty()) {
                                            socialModel.setFacebookLink(note);
                                            contact.setSocialModel(socialModel);
                                        } else {
                                            if (contact.listOfContactInfo == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }
                                } else if (ClipboardType.isLinkedIn(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasLinked = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setLinkedInLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {

                                        if (socialModel.getLinkedInLink() == null || socialModel.getLinkedInLink().isEmpty()) {
                                            socialModel.setLinkedInLink(note);
                                            contact.setSocialModel(socialModel);
                                        } else {
                                            if (contact.listOfContactInfo == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }
                                } else if (note.toString().contains("viber.com") || note.toString().contains("https://www.viber.com")) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasViber = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setViberLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {
                                        socialModel.setViberLink(note);
                                        contact.setSocialModel(socialModel);
                                    }
                                } else if (note.toString().contains("whatsapp.com") || note.toString().contains("https://www.whatsapp.com")) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasWhatsapp = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setWhatsappLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {
                                        socialModel.setWhatsappLink(note);
                                        contact.setSocialModel(socialModel);
                                    }
                                } else if (ClipboardType.isTelegram(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasTelegram = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setTelegramLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {
                                        socialModel.setTelegramLink(note);
                                        contact.setSocialModel(socialModel);
                                    }
                                } else if (note.toString().contains("skype.com") || note.toString().contains("https://www.skype.com")) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasSkype = true;
                                    String username = note.substring(note.indexOf(".com") + 5, note.length());
                                    if (username.charAt(username.length() - 1) == '/')
                                        username = username.substring(0, username.length() - 1);
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setSkypeLink(username);
                                        contact.setSocialModel(socialModel1);
                                    } else {
                                        socialModel.setSkypeLink(username);
                                        contact.setSocialModel(socialModel);
                                    }
                                } else if (ClipboardType.isTwitter(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasTwitter = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setTwitterLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {

                                        if (socialModel.getTwitterLink() == null || socialModel.getTwitterLink().isEmpty()) {
                                            socialModel.setTwitterLink(note);
                                            contact.setSocialModel(socialModel);
                                        } else {
                                            if (contact.listOfContactInfo == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }

                                    }
                                } else if (ClipboardType.isYoutube(note)) {
                                    SocialModel socialModel = contact.getSocialModel();
                                    contact.hasYoutube = true;
                                    if (socialModel == null) {
                                        SocialModel socialModel1 = new SocialModel();
                                        socialModel1.setYoutubeLink(note);
                                        contact.setSocialModel(socialModel1);
                                    } else {

                                        if (socialModel.getYoutubeLink() == null || socialModel.getYoutubeLink().isEmpty()) {
                                            socialModel.setYoutubeLink(note);
                                            contact.setSocialModel(socialModel);
                                        } else {
                                            if (contact.listOfContactInfo == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }

                                    }
                                } else {
                                    try {
                                        Date d = new Date(note);

                                        Calendar cals = Calendar.getInstance();
                                        cal.setTime(d);
                                        Time times = getRandomDate();
                                        time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                                        time.setMinutes(cal.get(Calendar.MINUTE));
                                        time.setSeconds(cal.get(Calendar.SECOND));

                                        //cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);

                                        contact.time = time.toString();

                                        contact.setDateCreate(d);

                                        deleteNoteContact(contactId, note);

                                    } catch (Exception e) {
                                        contact.addNote(note);
                                    }
                                }
                            }
                        }
                    }
                    if (noteCursor != null)
                        noteCursor.close();


                    contact.setListOfHashtags(hashList);


                    Cursor accountCursor = getCursorByType(TYPE_CURSOR.ACCOUNT_TYPE, contactId);

                    if (accountCursor != null && accountCursor.getCount() > 0) {
                        while (accountCursor.moveToNext()) {
                            int accountTypeI = accountCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);



                          /*  int accountDataSetI = accountCursor.getColumnIndex(ContactsContract.RawContacts.DATA_SET);
                            System.out.println(contact.getName());
                            if (accountDataSetI != -1) {
                                String accountData = accountCursor.getString(accountDataSetI);
                                if (accountData != null)
                                    System.out.println("DATA SET = " + accountData);
                            }*/


                            /*ContentResolver cr = getActivity().getContentResolver();
                            // Read Contacts
                            Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.RawContacts.ACCOUNT_TYPE}, ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'telegram' ", null, null);
                            if (c.getCount() <= 0) {
                                Toast.makeText(getActivity(), "No Phone Contact Found..!", Toast.LENGTH_SHORT).show();
                            } else {
                                while (c.moveToNext()) {
                                    String Phone_number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));          //Phone number
                                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));                        //Name of contact
                                }
                            } //get phone by account*/

                            /*Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.Contacts._ID,
                                            ContactsContract.Contacts.DISPLAY_NAME,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.RawContacts.ACCOUNT_TYPE},
                                    ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'google' ",
                                    null, null);*/


                            if (accountTypeI != -1) {
                                String accountType = accountCursor.getString(accountTypeI);
                                if (accountType != null && accountType.contains("telegram") && phone.size() == 0) {

                                } else
                                    contact.addAccountType(accountType);

                                if (accountType != null && accountType.contains("teleg"))
                                    System.out.println("ACC TYPE = " + accountType);


                                if (accountType != null) {

                                    try {
                                        if (accountType.contains("viber")) {
                                            contact.hasViber = true;
                                            SocialModel sc = contact.getSocialModel();
                                            if (sc == null) {
                                                SocialModel sc2 = new SocialModel();
                                                sc2.setViberLink(phone.get(0));
                                                contact.setSocialModel(sc2);
                                            } else {
                                                sc.setViberLink(phone.get(0));
                                                contact.setSocialModel(sc);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (accountType.contains("whatsapp")) {
                                            contact.hasWhatsapp = true;
                                            SocialModel sc = contact.getSocialModel();
                                            if (sc == null) {
                                                SocialModel sc2 = new SocialModel();
                                                sc2.setWhatsappLink(phone.get(0));
                                                contact.setSocialModel(sc2);
                                            } else {
                                                sc.setWhatsappLink(phone.get(0));
                                                contact.setSocialModel(sc);
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (accountType.contains("telegram")) {

                                            System.out.println(contact.getName());



                                            Cursor contactCursor = contentResolver.query(
                                                    ContactsContract.RawContacts.CONTENT_URI,
                                                    new String[]{ContactsContract.RawContacts._ID,
                                                            ContactsContract.RawContacts.CONTACT_ID},
                                                    ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                                                    new String[]{"org.telegram.messenger"},
                                                    null);

                                            ArrayList<String> myWhatsappContacts = new ArrayList<>();

                                            if (contactCursor != null) {
                                                if (contactCursor.getCount() > 0) {
                                                    if (contactCursor.moveToFirst()) {
                                                        do {
                                                            //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                                                            String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));

                                                            if (whatsappContactId != null) {
                                                                //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                                                                Cursor whatsAppContactCursor = contentResolver.query(
                                                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                                        new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                                                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                                                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                                        new String[]{contactId}, null);

                                                                if (whatsAppContactCursor != null) {
                                                                    whatsAppContactCursor.moveToFirst();
                                                                    String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                                                                    String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                                                    String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                                                    whatsAppContactCursor.close();

                                                                    //Add Number to ArrayList
                                                                    myWhatsappContacts.add(number);

                                                              //      showLogI(TAG, " WhatsApp contact id  :  " + id);
                                                              //      showLogI(TAG, " WhatsApp contact name :  " + name);
                                                              //      showLogI(TAG, " WhatsApp contact number :  " + number);
                                                                }
                                                            }
                                                        } while (contactCursor.moveToNext());
                                                        contactCursor.close();
                                                    }
                                                }
                                            }


                                            if (phone.size() != 0/* && !phone.get(0).equals("+000000000000")*/) {
                                                contact.hasTelegram = true;
                                                SocialModel sc = contact.getSocialModel();
                                                if (sc == null) {
                                                    SocialModel sc2 = new SocialModel();

                                                    sc2.setTelegramLink(phone.get(0));
                                                    contact.setSocialModel(sc2);

                                                } else {
                                                    sc.setTelegramLink(phone.get(0));
                                                    contact.setSocialModel(sc);
                                                }
                                            } else {

                                            }

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                                //    }
                            }
                        }
                        accountCursor.close();
                    } else
                        contact.addAccountType("phone");


                    Cursor adressCursor = getCursorByType(TYPE_CURSOR.CONTACT_ADRESS, contactId);
                    while (adressCursor != null && adressCursor.moveToNext()) {
                        String adress = adressCursor.getString(adressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        contact.addAddress(adress);
                    }
                    if (adressCursor != null)
                        adressCursor.close();

                    boolean found = false;

                    //    RealmList<ContactInfo> ctinf = new RealmList<>();
                    boolean checkC = false;

                    boolean checkCompany = true;
                    Cursor organizationCursor = getCursorByType(TYPE_CURSOR.ORGANIZATION, contactId);

                    while (organizationCursor != null && organizationCursor.moveToNext()) {
                        String orgName = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String companyPossition = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        if (companyPossition != null)
                            contact.setCompanyPossition(companyPossition);

                        if (orgName != null && !orgName.isEmpty()) {

                            String oldCompany = null;
                            if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                oldCompany = contact.getCompany();

                            if (contact.getCompany() != null && !contact.getCompany().isEmpty() && contact.getCompany().equals(orgName))
                                continue;

                            contact.setCompany(orgName.trim());

                            for (Contact searchCompanyContact : listOfContacts) {
                                // if (searchCompanyContact.listOfContacts != null) {

                                if (oldCompany != null) {

                                    if (searchCompanyContact.getName().equals(oldCompany) && searchCompanyContact.listOfContacts != null && !searchCompanyContact.listOfContacts.isEmpty()) {
                                        boolean rem = false;
                                        if (searchCompanyContact.listOfContacts.size() == 1) {
                                            EventBus.getDefault().post(new RemoveContact(searchCompanyContact.getId()));

                                            rem = true;
                                        }
                                        found = false;

                                        ContactCacheService.removeContactFromCompany(searchCompanyContact, contact);

                                        if (rem)
                                            listOfContacts.remove(searchCompanyContact);
                                        else {
                                            for (int i = 0; i < searchCompanyContact.listOfContacts.size(); i++) {
                                                if (searchCompanyContact.listOfContacts.get(i).getId() == contact.getId()) {
                                                    searchCompanyContact.listOfContacts.remove(contact);
                                                    break;
                                                }
                                            }
                                        }


                                        break;
                                    }

                                }
                            }

                            for (Contact searchCompanyContact : listOfContacts) {
                                if (searchCompanyContact.getName().equals(orgName) && searchCompanyContact.listOfContacts != null && !searchCompanyContact.listOfContacts.isEmpty()) {

                                    checkC = true;


                                    try {
                                        if (oldCompany == null)
                                            ContactCacheService.justInsertContact(contact);

                                        //   Contact contact1 = ContactCacheService.getContactByName(contact.getName());
                                        //if (!searchCompanyContact.listOfContacts.contains(contact))
                                        searchCompanyContact.listOfContacts.add(contact);

                                        ContactCacheService.updateCompany(searchCompanyContact);

                                        if (oldCompany != null)
                                            ContactCacheService.updateContact(contact, null);

                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }


                                    found = true;
                                    break;
                                }
                                //    }
                            }

                            if (!found) {
                                Date date1 = contact.getDateCreate();
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(date1);
                                Time time1 = getRandomDate();
                                time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                                time1.setMinutes(cal1.get(Calendar.MINUTE));
                                time1.setSeconds(cal1.get(Calendar.SECOND));

                                Contact companyContact = new Contact(0, orgName.trim(), null, false, true, true, time1.toString(), null, date1);
                                // Contact companyContact = new Contact(new Date());
                                //  companyContact.setName(orgName);

                                companyContact.time = time.toString();
                                //     companyContact.time = "";
                                //companyContact.setIdContact(contactId);
                                companyContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                                //  companyContact.listOfContacts = new RealmList<>();
                                //    companyContact.listOfContacts.add(contact);
                                //    listOfContacts.add(companyContact);
                                //      countConpany++;
                                //MainActivity.mainCountCompanuFromLoad++;
                                checkCompany = false;
                                checkC = true;
                                    /*new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            *//*System.out.println("COMP");
                                            ContactCacheService.insertOneContact(contact);
                                            companyContact.listOfContacts.add(contact);
                                            listOfContacts.add(companyContact);
                                            ContactCacheService.updateCompany(companyContact);*//*


                                        }
                                    }).start();*/



                                if (oldCompany == null)
                                    ContactCacheService.justInsertContact(contact);


                                companyContact.listOfContacts.add(contact);

                                listOfContacts.add(companyContact);

                                ContactCacheService.updateCompany(companyContact);


                            }
                        }
                    }

                    if (checkCompany)
                        countContact++;




                    if (organizationCursor != null)
                        organizationCursor.close();

                    listOfContacts.add(contact);

                    if (!checkC) {
                        new Thread(() -> ContactCacheService.justInsertContact(contact)).start();
                    }

                    int finalCountConpany = countConpany;
                    int finalCountContact = countContact;

                    Activity finalActivity1 = activity;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            finalActivity1.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //      int companies = ContactCacheService.getOnlyCompanySize();
                                    //      int concats = ContactCacheService.getOnlyContacts();
                                        /*((TextView) activity.findViewById(R.id.barFavorite_count)).setText(" "+String.valueOf(CONTACT_COUNT)+"");
                                        ((TextView) activity.findViewById(R.id.barCountContacts)).setText(" "+((TextView) activity.findViewById(R.id.barFavorite_count)).getText());*/

                                    ((TextView) finalActivity1.findViewById(R.id.all_hashtag_countContacts)).setText("(" + String.valueOf(MainActivity.mainCountCompanuFromLoad + finalCountContact) + ")");
                                    ((TextView) finalActivity1.findViewById(R.id.total_fav)).setText(" " + String.valueOf(MainActivity.mainCountCompanuFromLoad + finalCountContact) + "");
                                    ((TextView) finalActivity1.findViewById(R.id.people_fav)).setText("(" + String.valueOf(finalCountContact) + ")");
                                    ((TextView) finalActivity1.findViewById(R.id.companies_fav)).setText("(" + String.valueOf(MainActivity.mainCountCompanuFromLoad) + ")");
                                    ((TextView) finalActivity1.findViewById(R.id.popupContactsAll)).setText(" " + String.valueOf("" + ((TextView) finalActivity1.findViewById(R.id.total_fav)).getText() + ""));
                                    ((TextView) finalActivity1.findViewById(R.id.call2mecloud_count)).setText(" " + String.valueOf("" + ((TextView) finalActivity1.findViewById(R.id.total_fav)).getText() + ""));


                                    //   ((TextView) activity.findViewById(R.id.popupContactsAll)).setText("("+String.valueOf(CONTACT_COUNT)+")");
                                    //   ((TextView) activity.findViewById(R.id.total_fav)).setText("("+String.valueOf(CONTACT_COUNT)+")");


                                }
                            });

                        }
                    }).start();


                    if (fromService) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCountLoad();
                            }
                        });
                    }
                } else if (!fromService) indexI--;
            }
            cursorForPrevWithTypes.close();
        }
           /* if(number + 1 != CONTACT_COUNT){
                number = CONTACT_COUNT -1;
                updateCountLoad();
            }*/
        checkLoad = false;


        return listOfContacts;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoadBarEvent event) {

        activity = event.getActivity();
        if (!MainActivity.loadFromFile)
            updateCountLoad();
        else {
            updateCountLoadFile();
        }
    }

    ;

    private void updateCountLoad() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //   System.out.println("CHECKED NUMBER");
                if(CONTACT_COUNT == null) return;
                (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                (activity).findViewById(R.id.loadBar).setVisibility(View.VISIBLE);
                (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                (activity).findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                ((TextView) (activity).findViewById(R.id.quantity_contacts_load)).setText("" + number);
                Integer firstValue = new Integer(number);
                Integer secondValue = new Integer(CONTACT_COUNT);
                ((ProgressBar) (activity).findViewById(R.id.progressBar)).setProgress((firstValue * 100) / secondValue);
                ((TextView) (activity).findViewById(R.id.all_contacts_for_load)).setText("" + CONTACT_COUNT);
                ((TextView) (activity).findViewById(R.id.loadProc)).setVisibility(View.VISIBLE);
                ((TextView) (activity).findViewById(R.id.loadProc)).setText(" Progress - " + (((firstValue * 100) / secondValue) + 1) + "%   ");
                //      System.out.println("NUMBER = "+number);
                //  System.out.println(getContactsCt());
                //    System.out.println(getAllCt());
                if (number + 1 == CONTACT_COUNT) {
                    //System.out.println("SIZE UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU = "+MainActivity.LIST_TO_SAVE_OF_FILE.size());
                    System.out.println("Done success");
                    ((TextView) (activity).findViewById(R.id.loadProc)).setVisibility(View.GONE);
                    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                activity,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    } else {
                        System.out.println("START GONE");
                        (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                        (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                        System.out.println("END GONE");


                        //     storage.createDirectory(path+"/Call2Me/Call2MeFiles");
                        Storage storage = new Storage(activity);
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        storage.createDirectory(path + "/Extime/ExtimeContacts");
                        Gson gson = new Gson();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String jsonContacts = gson.toJson(listOfContacts);
                                    storage.createFile(path + "/Extime/ExtimeContacts/backup_" + MainActivity.versionDebug, jsonContacts);
                                } catch (Exception e) {
                                    System.out.println("ERROR TO CRETAE BACKUP");
                                    e.fillInStackTrace();
                                }
                            }
                        }).start();


                    }
                    ((Postman) activity).setListOfContactsMain(listOfContacts);
                    //MainActivity.LIST_OF_CONTACTS = listOfContacts;

//                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
//                    sharedPreferences.edit().putBoolean("FIRST", true).apply();
                    System.out.println("END Load");
                }
                if (number >= CONTACT_COUNT) {
                    ((TextView) (activity).findViewById(R.id.loadProc)).setVisibility(View.GONE);
                    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                activity,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    } else {
                        System.out.println("START GONE");
                        (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                        (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                        System.out.println("END GONE");
                    }
                }
            }
        });
    }

    public void loadContactFromFile(boolean fromService, Activity act) {


        try {
            this.activity = act;

            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            sharedPreferences.edit().putBoolean("FIRST", true).apply();



            if(CONTACT_COUNT_FILE == null){
                CONTACT_COUNT_FILE = getContactFromFile(activity);
            }

            int quantityContacts = CONTACT_COUNT_FILE;

            CONTACT_COUNT_OF_DELETE = CONTACT_COUNT_FILE;

            if (CONTACT_COUNT_FILE > 45 && !fromService) quantityContacts = 30;


            checkLoad = true;

            JSONArray js = mainJsContact;
            System.out.println("222221223 + " + js.length());
            //int number = 0;

            int coutCont = 0;
            int coutCom = 0;
            ArrayList<Contact> listOfCompanies = new ArrayList<>();

            Gson gson = new Gson();

            //ArrayList<Contact> lll = new ArrayList<>();

            //System.out.println("START ADD NEW");

           /* for (int i = 0; i < quantityContacts; i++) {
                JSONObject o = js.getJSONObject(i);
                Contact c = gson.fromJson(o.toString(), Contact.class);
                lll.add(c);
            }*/

            //System.out.println("END ADD NEW");


            for (int i = 0; i < quantityContacts; i++) {
                JSONObject o = js.getJSONObject(i);
                Contact c = gson.fromJson(o.toString(), Contact.class);

                if (c.listOfContacts == null || c.listOfContacts.isEmpty()) {


                    //   System.out.println("1111111 = " + c.getName());
                    //    if (c.listOfContacts == null || c.listOfContacts.isEmpty())
                    coutCont++;
                    //    else
                    //        coutCom++;
                    final int con = coutCont;
                    //    final int com = coutCom;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) activity.findViewById(R.id.people_fav)).setText("(" + (con) + ")");
                            //      ((TextView) getActivity().findViewById(R.id.companies_fav)).setText("(" + (com) + ")");
                        }
                    });


                    ContactCacheService.updateContact(c, null);


                    number_file++;

                    if (fromService) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCountLoadFile();
                            }
                        });
                    }

                } else
                    listOfCompanies.add(c);


                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) activity.findViewById(R.id.barFavorite_count)).setText(" " + String.valueOf(CONTACT_COUNT_FILE) + "");
                        ((TextView) activity.findViewById(R.id.barCountContacts)).setText("" + ((TextView) activity.findViewById(R.id.barFavorite_count)).getText());
                        //((TextView) activity.findViewById(R.id.barHashtag_count)).setText("0");

                        ((TextView) activity.findViewById(R.id.call2mecloud_count)).setText("(" + (CONTACT_COUNT_FILE) + ")");
                        ((TextView) activity.findViewById(R.id.popupContactsAll)).setText(" " + (CONTACT_COUNT_FILE) + "");
                        ((TextView) activity.findViewById(R.id.total_fav)).setText(" " + (CONTACT_COUNT_FILE) + "");

                    }
                });

            }

            //System.out.println("COMPANI SIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIZE = "+listOfCompanies.size()+", size cont = "+number+", basa size = "+ContactCacheService.getAllContacts(null).size());
            for (int i = 0; i < listOfCompanies.size(); i++) {
                Realm realm = Realm.getDefaultInstance(); //+
                try {

                    for (int j = 0; j < listOfCompanies.get(i).listOfContacts.size(); j++) {
                        System.out.println("NAME TO ADD = " + listOfCompanies.get(i).listOfContacts.get(j).getName());
                        Contact conOfComp = ContactCacheService.getContactByIDContact(realm, listOfCompanies.get(i).listOfContacts.get(j).getIdContact());
                        if (conOfComp != null)
                            listOfCompanies.get(i).listOfContacts.set(j, conOfComp);
                    }

                    ContactCacheService.updateCompany(listOfCompanies.get(i));

                    realm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    realm.close();
                }
                number_file++;
                //   number+=listOfCompanies.get(i).listOfContacts.size();
                //final int num = number;

                coutCom++;
                final int com = coutCom;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ((TextView) getActivity().findViewById(R.id.people_fav)).setText("(" + (con) + ")");
                        ((TextView) activity.findViewById(R.id.companies_fav)).setText("(" + (com) + ")");
                    }
                });

                if (fromService) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCountLoadFile();
                        }
                    });
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) activity.findViewById(R.id.barFavorite_count)).setText(" " + String.valueOf(CONTACT_COUNT_FILE) + "");
                        ((TextView) activity.findViewById(R.id.barCountContacts)).setText("" + ((TextView) activity.findViewById(R.id.barFavorite_count)).getText());
                        //((TextView) activity.findViewById(R.id.barHashtag_count)).setText("0");

                    }
                });

            }





            listOfCompanies.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            checkLoad = false;
        }


    }

    public void updateCountLoadFile() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                   //System.out.println("CHECKED NUMBER");
                if(((MainActivity)activity).selectedFragment.getClass().equals(ContactsFragment.class)) {
                    (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                    (activity).findViewById(R.id.loadBar).setVisibility(View.VISIBLE);
                    (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                    (activity).findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                }else{
                    (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                    (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                }
                ((TextView) (activity).findViewById(R.id.quantity_contacts_load)).setText("" + number_file);
                Integer firstValue = new Integer(number_file);
                Integer secondValue = new Integer(CONTACT_COUNT_FILE);
                ((ProgressBar) (activity).findViewById(R.id.progressBar)).setProgress((firstValue * 100) / secondValue);

                if(((firstValue * 100) / secondValue) < 92 && ((firstValue * 100) / secondValue) % 10 == 0){
                    System.out.println("UPDATE LIST READ");
                    EventBus.getDefault().post(new ContactLoadEvent(listOfContacts));
                }

                ((TextView) (activity).findViewById(R.id.all_contacts_for_load)).setText("" + CONTACT_COUNT_FILE);
                ((TextView) (activity).findViewById(R.id.loadProc)).setVisibility(View.VISIBLE);
                ((TextView) (activity).findViewById(R.id.loadProc)).setText(" Progress - " + (((firstValue * 100) / secondValue) + 1) + "%   ");
                //      System.out.println("NUMBER = "+number);
                //  System.out.println(getContactsCt());
                //    System.out.println(getAllCt());
                if (number_file + 1 == CONTACT_COUNT_FILE) {
                    //System.out.println("SIZE UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU = "+MainActivity.LIST_TO_SAVE_OF_FILE.size());
                    System.out.println("Done success");
                    ((TextView) (activity).findViewById(R.id.loadProc)).setVisibility(View.GONE);
                    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                activity,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    } else {
                        System.out.println("START GONE");
                        (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                        (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                        System.out.println("END GONE");


                    }
                    ((Postman) activity).setListOfContactsMain(listOfContacts);

                    //EventBus.getDefault().post(new SynkNewC());

                    //EventBus.getDefault().post(new UpdateList());

                    //MainActivity.LIST_OF_CONTACTS = listOfContacts;

//                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
//                    sharedPreferences.edit().putBoolean("FIRST", true).apply();
                    System.out.println("END Load 1");
                }
                if (number_file >= CONTACT_COUNT_FILE) {
                    ((TextView) (activity).findViewById(R.id.loadProc)).setVisibility(View.GONE);
                    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                activity,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    } else {
                        System.out.println("START GONE 2");
                        (activity).findViewById(R.id.loadBar).setVisibility(View.GONE);
                        (activity).findViewById(R.id.progressBar).setVisibility(View.GONE);
                        System.out.println("END GONE 2");
                    }
                }
            }
        });
    }

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }
}
