package ai.extime.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Patterns;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.extime.Enums.ClipboardEnum;
import ai.extime.Models.Clipboard;
import ai.extime.Services.ContactCacheService;

import com.extime.R;

public class ClipboardType {

    private static SimpleDateFormat dateFormant = new SimpleDateFormat("HH:mm:ss MMM d yyyy", Locale.ENGLISH);
    private static Pattern FACEBOOK_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(web.facebook.com|facebook.com|m.facebook.com|fb.com)\\/(?:(?:\\w)*#!\\/)?(?:pages\\/)?(?:[?\\w\\-\\/]*\\/)?(?:[?\\w\\.]*)?(?:profile.php\\?id=(?=\\d.*))?([\\w\\-\\&\\%\\=\\.]*)?", Pattern.CASE_INSENSITIVE);
    //private static Pattern INSTAGRAM_REGEX = Pattern.compile("(https:\\/\\/|http:\\/\\/|())?(instagram.com|www.instagram.com)\\/[a-zA-Z0-9(\\.\\_\\?)?]+", Pattern.CASE_INSENSITIVE);
    private static Pattern INSTAGRAM_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(instagram.com|www.instagram.com)\\/?(p\\/)?[a-zA-Z0-9(\\.\\_\\/\\=\\?)?]+", Pattern.CASE_INSENSITIVE);
    //private static Pattern TELEGRAM_REGEX = Pattern.compile("^(https:\\/\\/|http:\\/\\/|())?(t|telegram).me\\/[a-zA-Z0-9(\\.\\_\\-\\/\\?)?]*(\\/[a-zA-Z0-9(\\.\\_\\/\\-\\?)?]+)*$", Pattern.CASE_INSENSITIVE);
    private static Pattern TELEGRAM_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(t|telegram).me\\/(@)?[a-zA-Z0-9(\\.\\_\\-\\/\\?)?]*(\\/[a-zA-Z0-9(\\.\\_\\/\\-\\?)?]+)*", Pattern.CASE_INSENSITIVE);
    private static Pattern TWITTER_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(mobile.twitter|twitter)\\.com\\/[A-z0-9_@]+\\/?", Pattern.CASE_INSENSITIVE);
    private static Pattern VK_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(vk.com|m.vk.com)\\/[a-zA-Z0-9_(\\.\\?)?]+", Pattern.CASE_INSENSITIVE);
    private static Pattern LINK_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(uk.linkedin|linkedin)\\.com\\/([a-zA-Zа-яА-Я0-9-(\\.\\/\\-\\%\\?)]{2,60})\\/?", Pattern.CASE_INSENSITIVE);
    private static Pattern GOOGLE_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?plus.google.com\\/[a-zA-Z0-9(\\.\\?)?]+$", Pattern.CASE_INSENSITIVE);
    private static String PHONE_NUMBER_GARBAGE_REGEX = "[()\\s-]+";
    private static String PHONE_NUMBER_REGEX = "(\\+[\\s]*[0-9]+[\\- \\.]*)?(\\([0-9]+\\)[\\- \\.]*)?([0-9][0-9\\- \\.]+[0-9])";
    private static Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);
    private static Pattern MAIL_REGEX = Pattern.compile("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
    //private static Pattern YOUTUBE_REGEX = Pattern.compile("^(http(s)?:\\/\\/)?((w){3}.)?(youtu|m.youtu)(be|.be)?(\\.com)?\\/.+", Pattern.CASE_INSENSITIVE);
    private static Pattern YOUTUBE_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(youtu|m.youtu)(be|.be)?(\\.com)?\\/[a-zA-Z0-9(\\.\\=\\_\\&\\-\\/\\?)?]+", Pattern.CASE_INSENSITIVE);

    private static Pattern GITHUB_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(github.com|www.github.com)\\/[a-zA-Z0-9(\\.\\_\\/\\?)?]*(\\/[a-zA-Z0-9(\\.\\_\\/\\?)?]+)*", Pattern.CASE_INSENSITIVE);
    private static Pattern MEDIUM_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(link.medium.com|medium.com|www.medium.com|onezero.medium.com)\\/(@)?[a-zA-Z0-9(\\.\\_\\-\\/\\?)?]*(\\/[a-zA-Z0-9(\\.\\_\\/\\-\\?)?]+)*", Pattern.CASE_INSENSITIVE);

    private static Pattern GOOGLE_DOCS_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?docs.google.com\\/[a-zA-Z0-9(\\.\\-\\/\\=\\_\\#\\?)?]+", Pattern.CASE_INSENSITIVE);
    private static Pattern GOOGLE_SHEETS_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?docs.google.com\\/spreadsheets\\/?(d\\/)?[a-zA-Z0-9(\\.\\-\\=\\_\\?)?]+(\\/edit)?", Pattern.CASE_INSENSITIVE);

    private static Pattern TUMBLR_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?([a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+)?(.)?(tumblr)(\\.com)?\\/[a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+", Pattern.CASE_INSENSITIVE);

    private static Pattern ANGEL_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?angel.co?\\/?(p\\/)?[a-zA-Z0-9(\\.\\-\\/\\_\\?)?]+", Pattern.CASE_INSENSITIVE);

    private static Pattern WHATSAPP_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?([a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+)?(.)?(whatsapp)(\\.com)?\\/[a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+", Pattern.CASE_INSENSITIVE);

    private static Pattern VIBER_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?([a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+)?(.)?(viber)(\\.com)?\\/[a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+", Pattern.CASE_INSENSITIVE);

    private static Pattern SKYPE_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?([a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+)?(.)?(skype)(\\.com)?\\/[a-zA-Z0-9(\\.\\=\\_\\-\\/\\?)?]+", Pattern.CASE_INSENSITIVE);

    private static Pattern DATE_OF_DIRTH_REGEX = Pattern.compile("[0-9]{2}[.][0-9]{2}[.][0-9]{2,4}", Pattern.CASE_INSENSITIVE);

    private static Pattern ZOOM_REGEX = Pattern.compile("(http(s)?:\\/\\/)?(?:www.)?(zoom.us|us04web.zoom.us)\\/[a-zA-Z0-9(\\.\\=\\_\\&\\-\\/\\?)?]+", Pattern.CASE_INSENSITIVE);


    //private static Pattern WEB_URL = Pattern.compile("^(https?|ftp|file):\\/\\/[-a-zA-Z0-9\\+\\&\\@\\#\\/\\%\\?\\=\\~\\_\\|\\!\\:\\,\\.\\;]*[-a-zA-Z0-9\\+\\&\\@\\#\\/\\%\\=\\~\\_\\|]", Pattern.CASE_INSENSITIVE);


    public static Matcher matcher;

    public static boolean isEmail(String text) {


        return Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    public static boolean isPhone(String text) {
        return Patterns.PHONE.matcher(text).matches();
    }

    public static boolean isInsta(String text) {
        return INSTAGRAM_REGEX.matcher(text).matches();
    }

    public static boolean isFacebook(String text) {
        return FACEBOOK_REGEX.matcher(text).matches();
    }

    public static boolean isVk(String text) {
        return VK_REGEX.matcher(text).matches();
    }

    public static boolean isLinkedIn(String text) {
        return LINK_REGEX.matcher(text).matches();
    }

    public static boolean isYoutube(String text) {
        return YOUTUBE_REGEX.matcher(text).matches();
    }

    public static boolean isTwitter(String text) {
        return TWITTER_REGEX.matcher(text).matches();
    }

    public static boolean isWeb(String text) {
        return Patterns.WEB_URL.matcher(text).matches();
    }


    public static boolean isTelegram(String text) {
        return TELEGRAM_REGEX.matcher(text).matches();
    }

    public static boolean isGitHub(String text) {
        return GITHUB_REGEX.matcher(text).matches();
    }

    public static boolean isMedium(String text) {
        return MEDIUM_REGEX.matcher(text).matches();
    }

    public static boolean isG_Doc(String text) {
        return GOOGLE_DOCS_REGEX.matcher(text).matches();
    }

    public static boolean isZoom(String text) {
        return ZOOM_REGEX.matcher(text).matches();
    }

    public static boolean isBirth(String text) {
        return DATE_OF_DIRTH_REGEX.matcher(text).matches();
    }

    public static boolean isG_Sheet(String text) {
        return GOOGLE_SHEETS_REGEX.matcher(text).matches();
    }

    public static boolean is_Tumblr(String text) {
        return TUMBLR_REGEX.matcher(text).matches();
    }

    public static boolean is_Angel(String text) {
        return ANGEL_REGEX.matcher(text).matches();
    }

    public static boolean is_Viber(String text) {
        return VIBER_REGEX.matcher(text).matches();
    }

    public static boolean is_Whatsapp(String text) {
        return WHATSAPP_REGEX.matcher(text).matches();
    }

    public static boolean is_Skype(String text) {
        return SKYPE_REGEX.matcher(text).matches();
    }

    public static String getEmail(String str){
        Matcher m2 = Patterns.EMAIL_ADDRESS.matcher(str);

        while (m2.find()) {
            String m = m2.group();
            if (m.contains("%3a")) {
                m = m.substring(m.indexOf("%3a") + 3);
            }
            return m;
        }
        return null;
    }

    private static ArrayList<Clipboard> getListBirth(String text, Context context) {
        ArrayList<Clipboard> listOfBirth = new ArrayList<>();
        Matcher matcher = DATE_OF_DIRTH_REGEX.matcher(text);



        while (matcher.find()) {



            listOfBirth.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.gift_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.gift_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.gift_blue), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.gift_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.gift_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.gift_blue), dateFormant.format(new Date()), ClipboardEnum.DATEOFBIRTH));
        }
        return listOfBirth;
    }

    private static ArrayList<Clipboard> getListEmails(String text, Context context) {
        ArrayList<Clipboard> listOfEmails = new ArrayList<>();
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(text);



        while (matcher.find()) {
            String m = matcher.group();
            if (m.contains("%3a")) {
                m = m.substring(m.indexOf("%3a") + 3);
            }
            listOfEmails.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails), m, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails), dateFormant.format(new Date()), ClipboardEnum.EMAIL));
        }
        return listOfEmails;
    }

    private static ArrayList<Clipboard> getListYoutube(String text, Context context) {
        ArrayList<Clipboard> listOfYoutube = new ArrayList<>();



        Matcher matcher = YOUTUBE_REGEX.matcher(text.trim());



        while (matcher.find()) {

            listOfYoutube.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48), dateFormant.format(new Date()), ClipboardEnum.YOUTUBE));
        }
        return listOfYoutube;
    }

    private static ArrayList<Clipboard> getListInstagram(String text, Context context) {
        ArrayList<Clipboard> listOfInstagram = new ArrayList<>();
        Matcher matcher = INSTAGRAM_REGEX.matcher(text);

        while (matcher.find()) {


            String str = text.substring(matcher.start(), text.length());


            String[] mach = str.split(" ");
            String main = mach[0];

            if (main.charAt(main.length() - 1) == '/') {
                main = main.substring(0, main.length() - 1);
            }




            String way = main;
            String name = null;
            int ind = way.indexOf("https://");

            if (ind == -1)
                ind = way.indexOf("http://");

            if (ind == -1)
                way = "https://" + way;
            try {

                name = Jsoup.connect(way).timeout(3000).get().title();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }


            if (name != null) {

                int index = name.length();

                index = name.indexOf('•');

                if (index == -1)
                    index = name.length();


          /*  CreateFragment.nameContactExtractFromSocial = name.substring(0, index - 1);
            CreateFragment.socialEnums = socialEnums;*/

                name = name.substring(0, index - 1);
            }

            listOfInstagram.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_insta) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_insta) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_insta), main, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_insta) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_insta) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_insta), dateFormant.format(new Date()), ClipboardEnum.INSTAGRAM, name));
        }
        return listOfInstagram;
    }

    private static ArrayList<Clipboard> getListSites(String text, Context context) {
        ArrayList<Clipboard> listOfSites = new ArrayList<>();
        Matcher matcher = Patterns.WEB_URL.matcher(text);
        while (matcher.find()) {
            listOfSites.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web), dateFormant.format(new Date()), ClipboardEnum.WEB));
        }
        return listOfSites;
    }

    private static ArrayList<Clipboard> getListFacebook(String text, Context context) {
        ArrayList<Clipboard> listOfFacebook = new ArrayList<>();
        //text = text.substring(1, text.length()-1);
        Matcher matcher = FACEBOOK_REGEX.matcher(text);
        while (matcher.find()) {

            String str = text.substring(matcher.start(), text.length());
            String[] mach = str.split(" ");

            String main = mach[0];


            if (main.charAt(main.length() - 1) == '/') {
                main = main.substring(0, main.length() - 1);
            }

            String way = main;
            String name = null;
            int ind = way.indexOf("https://");

            if (ind == -1)
                ind = way.indexOf("http://");

            if (ind == -1)
                way = "https://" + way;
            try {

                name = Jsoup.connect(way).timeout(3000).get().title();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }


            if (name != null) {

                int index = name.length();

                index = name.indexOf('|');

                if (index == -1)
                    index = name.length();






          /*  CreateFragment.nameContactExtractFromSocial = name.substring(0, index - 1);
            CreateFragment.socialEnums = socialEnums;*/

                name = name.substring(0, index - 1);
            }


            //ContactCacheService.findContactByLink(text, ClipboardEnum.FACEBOOK);
            listOfFacebook.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_faceb) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_faceb) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_faceb), main,
                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_faceb) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_faceb) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_faceb), dateFormant.format(new Date()), ClipboardEnum.FACEBOOK, name));


        }

        return listOfFacebook;
    }

    private static ArrayList<Clipboard> getListGoogleDocs(String text, Context context) {
        ArrayList<Clipboard> listOfDocs = new ArrayList<>();
        Matcher matcher = GOOGLE_DOCS_REGEX.matcher(text);
        while (matcher.find()) {
            listOfDocs.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs), dateFormant.format(new Date()), ClipboardEnum.G_DOC));
        }
        return listOfDocs;
    }

    private static ArrayList<Clipboard> getListGoogleSheets(String text, Context context) {
        ArrayList<Clipboard> listOfSheets = new ArrayList<>();
        Matcher matcher = GOOGLE_SHEETS_REGEX.matcher(text);
        while (matcher.find()) {
            listOfSheets.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets), dateFormant.format(new Date()), ClipboardEnum.G_SHEET));
        }
        return listOfSheets;
    }

    private static ArrayList<Clipboard> getListTwitter(String text, Context context) {
        ArrayList<Clipboard> listOfTwitter = new ArrayList<>();
        Matcher matcher = TWITTER_REGEX.matcher(text);
        while (matcher.find()) {
            listOfTwitter.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48), dateFormant.format(new Date()), ClipboardEnum.TWITTER));
        }
        return listOfTwitter;
    }

    private static ArrayList<Clipboard> getListTumblr(String text, Context context) {
        ArrayList<Clipboard> listOfTumblr = new ArrayList<>();
        Matcher matcher = TUMBLR_REGEX.matcher(text);
        while (matcher.find()) {
            listOfTumblr.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr), dateFormant.format(new Date()), ClipboardEnum.TUMBLR));
        }
        return listOfTumblr;
    }

    private static ArrayList<Clipboard> getListAngel(String text, Context context) {
        ArrayList<Clipboard> listOfAngel = new ArrayList<>();
        Matcher matcher = ANGEL_REGEX.matcher(text);
        while (matcher.find()) {
            listOfAngel.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel), dateFormant.format(new Date()), ClipboardEnum.ANGEL));
        }
        return listOfAngel;
    }

    private static ArrayList<Clipboard> getListZoom(String text, Context context) {
        ArrayList<Clipboard> listOfAngel = new ArrayList<>();
        Matcher matcher = ZOOM_REGEX.matcher(text);
        while (matcher.find()) {
            listOfAngel.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.zoom_logo) + '/' + context.getResources().getResourceTypeName(R.drawable.zoom_logo) + '/' + context.getResources().getResourceEntryName(R.drawable.zoom_logo), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.zoom_logo) + '/' + context.getResources().getResourceTypeName(R.drawable.zoom_logo) + '/' + context.getResources().getResourceEntryName(R.drawable.zoom_logo), dateFormant.format(new Date()), ClipboardEnum.ZOOM));
        }
        return listOfAngel;
    }

    private static ArrayList<Clipboard> getListViber(String text, Context context) {
        ArrayList<Clipboard> listOfAngel = new ArrayList<>();
        Matcher matcher = VIBER_REGEX.matcher(text);
        while (matcher.find()) {
            listOfAngel.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_viber), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_viber), dateFormant.format(new Date()), ClipboardEnum.VIBER));
        }
        return listOfAngel;
    }

    private static ArrayList<Clipboard> getListWhatsapp(String text, Context context) {
        ArrayList<Clipboard> listOfAngel = new ArrayList<>();
        Matcher matcher = WHATSAPP_REGEX.matcher(text);
        while (matcher.find()) {
            listOfAngel.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_whatsapp), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_whatsapp), dateFormant.format(new Date()), ClipboardEnum.WHATSAPP));
        }
        return listOfAngel;
    }

    private static ArrayList<Clipboard> getListSkype(String text, Context context) {
        ArrayList<Clipboard> listOfAngel = new ArrayList<>();
        Matcher matcher = SKYPE_REGEX.matcher(text);
        while (matcher.find()) {
            listOfAngel.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_skype), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_skype), dateFormant.format(new Date()), ClipboardEnum.SKYPE));
        }
        return listOfAngel;
    }

    private static ArrayList<Clipboard> getListGitHub(String text, Context context) {
        ArrayList<Clipboard> listOfGits = new ArrayList<>();
        Matcher matcher = GITHUB_REGEX.matcher(text);
        while (matcher.find()) {
            listOfGits.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceTypeName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceEntryName(R.drawable.github_logo_32), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceTypeName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceEntryName(R.drawable.github_logo_32), dateFormant.format(new Date()), ClipboardEnum.GITHUB));
        }
        return listOfGits;
    }

    private static ArrayList<Clipboard> getListMedium(String text, Context context) {
        ArrayList<Clipboard> listOfGits = new ArrayList<>();
        Matcher matcher = MEDIUM_REGEX.matcher(text);
        while (matcher.find()) {
            listOfGits.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceTypeName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceEntryName(R.drawable.medium_size_32), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceTypeName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceEntryName(R.drawable.medium_size_32), dateFormant.format(new Date()), ClipboardEnum.MEDIUM));
        }
        return listOfGits;
    }

    private static ArrayList<Clipboard> getListTelegram(String text, Context context) {
        ArrayList<Clipboard> listOfTelegram = new ArrayList<>();
        Matcher matcher = TELEGRAM_REGEX.matcher(text);
        while (matcher.find()) {
            listOfTelegram.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_telegram), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_telegram), dateFormant.format(new Date()), ClipboardEnum.TELEGRAM));
        }
        return listOfTelegram;
    }

   /* private static ArrayList<Clipboard> getListGoogle(String text) {
        ArrayList<Clipboard> listOfGoogle = new ArrayList<>();
        matcher = GOOGLE_REGEX.matcher(text);
        while (matcher.find()) {
            listOfGoogle.add(new Clipboard(R.drawable.icn_fab_google, text, R.drawable.icn_fab_google,dateFormant.format(new Date())));
        }
        return listOfGoogle;
    }*/

    private static ArrayList<Clipboard> getListVk(String text, Context context) {
        ArrayList<Clipboard> listOfVk = new ArrayList<>();

        Matcher matcher = VK_REGEX.matcher(text);
        while (matcher.find()) {


            String str = text.substring(matcher.start(), text.length());
            String[] mach = str.split(" ");
            String main = mach[0];

            if (main.charAt(main.length() - 1) == '/') {
                main = main.substring(0, main.length() - 1);
            }



            String way = main;
            String name = null;
            int ind = way.indexOf("https://");

            if (ind == -1)
                ind = way.indexOf("http://");

            if (ind == -1)
                way = "https://" + way;
            try {

                name = Jsoup.connect(way).timeout(3000).get().title();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }


            if (name != null) {

                int index = name.length();

                index = name.indexOf('|');

                if (index == -1)
                    index = name.length();



          /*  CreateFragment.nameContactExtractFromSocial = name.substring(0, index - 1);
            CreateFragment.socialEnums = socialEnums;*/

                name = name.substring(0, index - 1);
            }

            listOfVk.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_vk) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_vk) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_vk), main, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_vk) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_vk) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_vk), dateFormant.format(new Date()), ClipboardEnum.VK, name));
        }
        return listOfVk;
    }

    private static ArrayList<Clipboard> getListPhoneNumbers(String text, Context context) {
        ArrayList<Clipboard> listOfPhoneNumbers = new ArrayList<>();
        //matcher = Patterns.PHONE.matcher(text);
        //if(text != null && text.length() > 2 && text.indexOf(" ") == 1 && text.indexOf("+") == 0) text = text.replace(" ","");
        Matcher matcher = PHONE_NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            if (matcher.group().replaceAll("[\\s\\-\\+\\(\\)]", "").length() > 7)
                listOfPhoneNumbers.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), matcher.group(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), dateFormant.format(new Date()), ClipboardEnum.PHONE));
        }
        return listOfPhoneNumbers;
    }

    private static ArrayList<Clipboard> getListLink(String text, Context context) {

        ArrayList<Clipboard> listOfLinks = new ArrayList<>();
        Matcher matcher = LINK_REGEX.matcher(text);
        while (matcher.find()) {

            String main = matcher.group();

            if (main.charAt(main.length() - 1) == '/') {
                main = main.substring(0, main.length() - 1);
            }


            listOfLinks.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link), main, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link), dateFormant.format(new Date()), ClipboardEnum.LINKEDIN));
        }
        return listOfLinks;
    }

    public static String convertArr(String[] matcher) {
        String res = "";
        for (int i = 0; i < matcher.length; i++) {
            res += matcher[i] + " ";
        }
        return res;
    }

    public static Clipboard getNameType(String name, Context context) {
        return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), name, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME);
    }

    public static Clipboard getNoteType(String text, Context context) {
        return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_notes) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_notes), text, null, dateFormant.format(new Date()), ClipboardEnum.NOTE);
    }

    public static void updateClipboardType(Clipboard clipboard, Context context) {

        if (clipboard.getType().equals(ClipboardEnum.GROUP)) return;


        if (isEmail(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.EMAIL);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails));

            return;
        }

        if (isPhone(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.PHONE);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone));
            return;
        }

        if (isFacebook(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.FACEBOOK);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_faceb) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_faceb) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_faceb));
            return;
        }

        if (isInsta(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.INSTAGRAM);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_insta) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_insta) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_insta));
            return;
        }
        if (isLinkedIn(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.LINKEDIN);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link));
            return;
        }
        if (isVk(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.VK);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_vk) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_vk) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_vk));
            return;
        }

        if (is_Angel(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.ANGEL);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel));
            return;
        }

        if (isMedium(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.MEDIUM);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceTypeName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceEntryName(R.drawable.medium_size_32));
            return;
        }

        if (isGitHub(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.GITHUB);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceTypeName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceEntryName(R.drawable.github_logo_32));
            return;
        }

        if (is_Tumblr(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.TUMBLR);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr));
            return;
        }

        if (isYoutube(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.YOUTUBE);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48));
            return;
        }

        if (isTwitter(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.TWITTER);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48));
            return;
        }

        if (isG_Sheet(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.G_SHEET);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets));
            return;
        }

        if (isG_Doc(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.G_DOC);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs));
            return;
        }

        if (isWeb(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.WEB);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web));
            return;
        }

        if (ContactCacheService.checkExistName(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.NAME);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue));
        }

        if (ContactCacheService.checkExistCompany(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.COMPANY);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies));
        }

        if (ContactCacheService.checkExistPosition(clipboard.getValueCopy())) {
            clipboard.setType(ClipboardEnum.POSITION);
            clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue));
        }


        clipboard.setType(ClipboardEnum.NOTE);
        clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_notes) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_notes));


    }

    public static Clipboard getClipboardHistory(String str, Context context) {
        if (isEmail(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails), dateFormant.format(new Date()), ClipboardEnum.EMAIL);
        } else if (isPhone(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), dateFormant.format(new Date()), ClipboardEnum.PHONE);
        } else if (isFacebook(str)) {
            return getListFacebook(str, context).get(0);
        } else if (isInsta(str)) {
            return getListInstagram(str, context).get(0);
        } else if (isVk(str)) {
            return getListVk(str, context).get(0);
        } else if (isLinkedIn(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link), dateFormant.format(new Date()), ClipboardEnum.LINKEDIN);
        } else if (isMedium(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceTypeName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceEntryName(R.drawable.medium_size_32), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceTypeName(R.drawable.medium_size_32) + '/' + context.getResources().getResourceEntryName(R.drawable.medium_size_32), dateFormant.format(new Date()), ClipboardEnum.MEDIUM);
        } else if (isYoutube(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48), dateFormant.format(new Date()), ClipboardEnum.YOUTUBE);
        } else if (isTwitter(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48), dateFormant.format(new Date()), ClipboardEnum.TWITTER);
        } else if (isG_Sheet(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets), dateFormant.format(new Date()), ClipboardEnum.G_SHEET);
        } else if (isG_Doc(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs), dateFormant.format(new Date()), ClipboardEnum.G_DOC);
        } else if (is_Tumblr(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr), dateFormant.format(new Date()), ClipboardEnum.TUMBLR);
        } else if (is_Angel(str)) {
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel), dateFormant.format(new Date()), ClipboardEnum.ANGEL);
        }else if(isGitHub(str)){
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceTypeName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceEntryName(R.drawable.github_logo_32), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceTypeName(R.drawable.github_logo_32) + '/' + context.getResources().getResourceEntryName(R.drawable.github_logo_32), dateFormant.format(new Date()), ClipboardEnum.GITHUB);
        }else if(isTelegram(str)){
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_telegram), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_telegram) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_telegram), dateFormant.format(new Date()), ClipboardEnum.TELEGRAM);
        }else if(isWeb(str)){
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web), dateFormant.format(new Date()), ClipboardEnum.WEB);
        }else if(ContactCacheService.checkExistName(str)){
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME);
        }else if(ContactCacheService.checkExistCompany(str)){
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), dateFormant.format(new Date()), ClipboardEnum.COMPANY);
        }else if(ContactCacheService.checkExistPosition(str)){
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), dateFormant.format(new Date()), ClipboardEnum.POSITION);
        }else{
            if (str.length() > 0 && str.contains("#") && str.length() > 1) {
                String[] magicSplit;
                str = str.replaceAll("  ", " ");
                magicSplit = str.split(" ");
                String hashtags = "";
                for (int i = 0; i < magicSplit.length; i++) {

                    if (magicSplit[i].trim().length() > 1 && magicSplit[i].trim().charAt(0) == '#') {

                        hashtags += magicSplit[i].trim() + " ";
                        magicSplit[i] = "";
                    }
                }

                str = convertArr(magicSplit).trim();

                hashtags.trim();

               return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_sort_hashtah), hashtags, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_sort_hashtah), dateFormant.format(new Date()), ClipboardEnum.HASHTAG);
            }
            return new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_notes) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_notes), str, null, dateFormant.format(new Date()), ClipboardEnum.NOTE);
        }
    }


    public static ArrayList<Clipboard> getListDataClipboard(String text, Context context) {

        //String value = clipboard.getValueCopy().replaceAll("[\\t\\n\\r]+", " ");
        ArrayList<Clipboard> listOfClipboardsData = new ArrayList<>();
        ArrayList<Clipboard> result = new ArrayList<>();
        String last = new String(text);
        String textMain = text;

        String[] mag = text.split("\n");
        for (int i = 0; i < mag.length; i++) {


            if (mag[i].length() > 6 && mag[i].substring(0, 5).equals("Name:")) {
                result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), mag[i].substring(5).trim(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME));
                mag[i] = "";
                listOfClipboardsData.addAll(result);
                result.clear();
                continue;
            }
            if (mag[i].length() > 9 && mag[i].substring(0, 8).equals("Company:")) {
                result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), mag[i].substring(8).trim(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), dateFormant.format(new Date()), ClipboardEnum.COMPANY));
                mag[i] = "";
                listOfClipboardsData.addAll(result);
                result.clear();
                continue;
            }
            if (mag[i].length() > 10 && mag[i].substring(0, 9).equals("Position:")) {
                result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), mag[i].substring(9).trim(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), dateFormant.format(new Date()), ClipboardEnum.POSITION));
                mag[i] = "";
                listOfClipboardsData.addAll(result);
                result.clear();
                continue;
            }
            if (mag[i].length() > 7 && mag[i].substring(0, 6).equals("Phone:")) {
                result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), mag[i].substring(6).trim(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), dateFormant.format(new Date()), ClipboardEnum.PHONE));
                mag[i] = "";
                listOfClipboardsData.addAll(result);
                result.clear();
                continue;
            }

        }

        text = convertArr(mag);

        text = text.replaceAll("\n", " ");
        String[] magicSplit = text.split(" ");


        //for (int i = 0; i < magicSplit.length; i++) {

        // }

        for (int q = 0; q < 1; q++) {



            for (int i = 0; i < magicSplit.length; i++) {
                /*if(magicSplit[i].equals("Name:") && i+1 < magicSplit.length && magicSplit[i+1].lastIndexOf(":") != magicSplit.length -1){
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), magicSplit[i+1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME));
                    magicSplit[i] = "";
                    magicSplit[i+1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }*/
                /*if(magicSplit[i].equals("Company:") && i+1 < magicSplit.length && magicSplit[i+1].lastIndexOf(":") != magicSplit.length -1){
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), magicSplit[i+1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), dateFormant.format(new Date()), ClipboardEnum.COMPANY));
                    magicSplit[i] = "";
                    magicSplit[i+1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
                if(magicSplit[i].equals("Phone:") && i+1 < magicSplit.length && !magicSplit[i+1].contains(":")){
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), magicSplit[i+1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_phone), dateFormant.format(new Date()), ClipboardEnum.PHONE));
                    magicSplit[i] = "";
                    magicSplit[i+1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
                if(magicSplit[i].equals("Position:") && i+1 < magicSplit.length && magicSplit[i+1].lastIndexOf(":") != magicSplit.length -1){
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), magicSplit[i+1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), dateFormant.format(new Date()), ClipboardEnum.POSITION));
                    magicSplit[i] = "";
                    magicSplit[i+1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }*/

                if (magicSplit[i].equals("Email:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_emails), dateFormant.format(new Date()), ClipboardEnum.EMAIL));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
                if (magicSplit[i].equals("Web:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_popup_web), dateFormant.format(new Date()), ClipboardEnum.WEB));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
                if (magicSplit[i].equals("Facebook:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.addAll(getListFacebook(magicSplit[i + 1].trim(), context));
                    if (!result.isEmpty()) {
                        /*for (Clipboard clipboard : result) {
                            for (int i = 0; i < magicSplit.length; i++) {
                                if (magicSplit[i].contains(clipboard.getValueCopy())) {
                                    magicSplit[i] = "";
                                }
                            }
                        }*/
                        magicSplit[i] = "";
                        magicSplit[i + 1] = "";
                        listOfClipboardsData.addAll(result);
                        result.clear();
                    }
                }
                if (magicSplit[i].equals("Instagram:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.addAll(getListInstagram(magicSplit[i + 1].trim(), context));
                    if (!result.isEmpty()) {
                        /*for (Clipboard clipboard : result) {
                            for (int i = 0; i < magicSplit.length; i++) {
                                if (magicSplit[i].contains(clipboard.getValueCopy())) {
                                    magicSplit[i] = "";
                                }
                            }
                        }*/
                        magicSplit[i] = "";
                        magicSplit[i + 1] = "";
                        listOfClipboardsData.addAll(result);
                        result.clear();
                    }
                }
                if (magicSplit[i].equals("Vk:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.addAll(getListVk(magicSplit[i + 1].trim(), context));
                    if (!result.isEmpty()) {
                        /*for (Clipboard clipboard : result) {
                            for (int i = 0; i < magicSplit.length; i++) {
                                if (magicSplit[i].contains(clipboard.getValueCopy())) {
                                    magicSplit[i] = "";
                                }
                            }
                        }*/
                        magicSplit[i] = "";
                        magicSplit[i + 1] = "";
                        listOfClipboardsData.addAll(result);
                        result.clear();
                    }
                }
                if (magicSplit[i].equalsIgnoreCase("GitHub:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.addAll(getListGitHub(magicSplit[i + 1].trim(), context));
                    if (!result.isEmpty()) {
                        magicSplit[i] = "";
                        magicSplit[i + 1] = "";
                        listOfClipboardsData.addAll(result);
                        result.clear();
                    }
                }

                if (magicSplit[i].equalsIgnoreCase("Medium:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.addAll(getListMedium(magicSplit[i + 1].trim(), context));
                    if (!result.isEmpty()) {
                        magicSplit[i] = "";
                        magicSplit[i + 1] = "";
                        listOfClipboardsData.addAll(result);
                        result.clear();
                    }
                }

                if (magicSplit[i].equals("Linkedin:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_link) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_link) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_link), dateFormant.format(new Date()), ClipboardEnum.LINKEDIN));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equals("Twitter:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_twitter_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_twitter_48), dateFormant.format(new Date()), ClipboardEnum.TWITTER));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
                if (magicSplit[i].equals("Youtube:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceTypeName(R.drawable.ic_youtube_48) + '/' + context.getResources().getResourceEntryName(R.drawable.ic_youtube_48), dateFormant.format(new Date()), ClipboardEnum.YOUTUBE));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
                if (magicSplit[i].equalsIgnoreCase("Skype:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_skype), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_skype) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_skype), dateFormant.format(new Date()), ClipboardEnum.SKYPE));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equalsIgnoreCase("Google_doc:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_docs) + '/' + context.getResources().getResourceTypeName(R.drawable.google_docs) + '/' + context.getResources().getResourceEntryName(R.drawable.google_docs), dateFormant.format(new Date()), ClipboardEnum.G_DOC));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equalsIgnoreCase("Google_sheet:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.google_sheets) + '/' + context.getResources().getResourceTypeName(R.drawable.google_sheets) + '/' + context.getResources().getResourceEntryName(R.drawable.google_sheets), dateFormant.format(new Date()), ClipboardEnum.G_SHEET));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equalsIgnoreCase("Tumblr:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.tumblr) + '/' + context.getResources().getResourceTypeName(R.drawable.tumblr) + '/' + context.getResources().getResourceEntryName(R.drawable.tumblr), dateFormant.format(new Date()), ClipboardEnum.TUMBLR));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equalsIgnoreCase("Angel:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.angel) + '/' + context.getResources().getResourceTypeName(R.drawable.angel) + '/' + context.getResources().getResourceEntryName(R.drawable.angel), dateFormant.format(new Date()), ClipboardEnum.ANGEL));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equalsIgnoreCase("Viber:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_viber), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_viber) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_viber), dateFormant.format(new Date()), ClipboardEnum.VIBER));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }

                if (magicSplit[i].equalsIgnoreCase("Whatsapp:") && i + 1 < magicSplit.length && magicSplit[i + 1].lastIndexOf(":") != magicSplit.length - 1) {
                    result.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_whatsapp), magicSplit[i + 1], ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_social_whatsapp) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_social_whatsapp), dateFormant.format(new Date()), ClipboardEnum.WHATSAPP));
                    magicSplit[i] = "";
                    magicSplit[i + 1] = "";
                    listOfClipboardsData.addAll(result);
                    result.clear();
                }
            }



            if(magicSplit.length > 0 && magicSplit[0] != null && magicSplit[0].toLowerCase().equals("посмотрите")){
                int in = convertArr(magicSplit).trim().indexOf("(");
                if(in != -1) {
                    String str = convertArr(magicSplit).trim().substring(11, in);
                    Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME);
                    listOfClipboardsData.add(clipboard);
                }

            }

            if(magicSplit.length > 0 && magicSplit[0] != null && magicSplit[0].toLowerCase().equals("take") &&  convertArr(magicSplit).trim().contains("Take a look at")){
                int in = convertArr(magicSplit).trim().indexOf("(");
                if(in != -1) {
                    String str = convertArr(magicSplit).trim().substring(15, in);
                    Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME);
                    listOfClipboardsData.add(clipboard);
                }

            }


            result.addAll(getListFacebook(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListInstagram(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

               /* listOfClipboardsData.addAll(getListTwitter(text));
            if(!listOfClipboardsData.isEmpty()) break;*/


            result.addAll(getListVk(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;
               /* listOfClipboardsData.addAll(getListGoogle(text));
            if(!listOfClipboardsData.isEmpty()) break;*/
            result.addAll(getListLink(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListYoutube(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;


            result.addAll(getListGitHub(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;


            result.addAll(getListMedium(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;




            result.addAll(getListTwitter(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListTelegram(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListEmails(convertArr(magicSplit).trim(), context));
            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            ////======

            result.addAll(getListGoogleSheets(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListGoogleDocs(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;



            result.addAll(getListTumblr(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListAngel(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

           /* if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListZoom(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;*/

            result.addAll(getListWhatsapp(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListViber(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListSkype(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;


            //===

            result.addAll(getListSites(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }
                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListBirth(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].trim().equals(clipboard.getValueCopy().trim())) {
                            magicSplit[i] = "";
                            listOfClipboardsData.add(clipboard);
                        }
                    }
                }

                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;

            result.addAll(getListPhoneNumbers(convertArr(magicSplit).trim(), context));

            if (!result.isEmpty()) {
                for (Clipboard clipboard : result) {
                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].contains(clipboard.getValueCopy())) {
                            magicSplit[i] = "";
                        }
                    }

                    String rr = convertArr(magicSplit);
                    rr = rr.replace(clipboard.getValueCopy(), "");
                    magicSplit = rr.split(" ");


                }
                listOfClipboardsData.addAll(result);
                result.clear();
            }

            if (convertArr(magicSplit).trim().length() == 0) break;


            String str = convertArr(magicSplit).trim();


            if (str.length() > 0 && str.contains("#") && str.length() > 1) {

                str = str.replaceAll("  ", " ");
                magicSplit = str.split(" ");
                String hashtags = "";
                if(str.length() > 0) {
                    for (int i = 0; i < magicSplit.length; i++) {

                        if (magicSplit[i].trim().length() > 0 && magicSplit[i].trim().charAt(0) == '#' && magicSplit[i].trim().length() > 1) {

                            hashtags += magicSplit[i].trim() + " ";
                            magicSplit[i] = "";
                        }
                    }

                    str = convertArr(magicSplit).trim();

                    hashtags.trim();

                    Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_sort_hashtah), hashtags, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_sort_hashtah) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_sort_hashtah), dateFormant.format(new Date()), ClipboardEnum.HASHTAG);
                    listOfClipboardsData.add(clipboard);

                    if (str.trim().length() == 0) break;
                }
            }

            //str = convertArr(magicSplit).trim();


            if (str.length() > 0 && ContactCacheService.checkExistName(str)) {
                Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME);
                listOfClipboardsData.add(clipboard);
            } else if (str.length() > 0 && ContactCacheService.checkExistCompany(str)) {
                Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), dateFormant.format(new Date()), ClipboardEnum.COMPANY);
                listOfClipboardsData.add(clipboard);
            } else if (str.length() > 0 && ContactCacheService.checkExistPosition(str)) {
                Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), dateFormant.format(new Date()), ClipboardEnum.POSITION);
                listOfClipboardsData.add(clipboard);
            } else if (str.length() > 0) {

                if((str.contains("1st") || str.contains("2nd") || str.contains("3rd")) && str.contains(" at ")){

                    try {
                        int indName = str.indexOf("1st");
                        if(indName == -1) indName = str.indexOf("2nd");
                        if(indName == -1) indName = str.indexOf("3rd");

                        if (indName > -1) {
                            Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), str.substring(0, indName).trim(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.profile_blue), dateFormant.format(new Date()), ClipboardEnum.NAME);
                            listOfClipboardsData.add(clipboard);

                            str = str.substring(indName+4);
                        }

                        int indPosition = str.indexOf(" at ");

                        if(indPosition > -1){
                            Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), str.substring(0, indPosition).trim(), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + context.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + context.getResources().getResourceEntryName(R.drawable.position_blue), dateFormant.format(new Date()), ClipboardEnum.POSITION);
                            listOfClipboardsData.add(clipboard);

                            str = str.substring(indPosition+4);
                        }

                        int indCompany = str.indexOf(",") -1;

                        if(indCompany > -1){
                            Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), str.substring(0, indCompany), ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), dateFormant.format(new Date()), ClipboardEnum.COMPANY);
                            listOfClipboardsData.add(clipboard);
                        }else{
                            Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), str, ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_companies), dateFormant.format(new Date()), ClipboardEnum.COMPANY);
                            listOfClipboardsData.add(clipboard);
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {

                    Clipboard clipboard = new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_notes) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_notes), str, null, dateFormant.format(new Date()), ClipboardEnum.NOTE);
                    listOfClipboardsData.add(clipboard);
                }
            }
        }

        if (listOfClipboardsData.size() > 1) {
            Clipboard clipboards = new Clipboard(null, textMain, null, dateFormant.format(new Date()), ClipboardEnum.GROUP);
            listOfClipboardsData.add(new Clipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(R.drawable.icn_notes) + '/' + context.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + context.getResources().getResourceEntryName(R.drawable.icn_notes), last, null, dateFormant.format(new Date()), ClipboardEnum.NOTE));
            clipboards.setListClipboards(listOfClipboardsData);
            listOfClipboardsData.clear();
            listOfClipboardsData.add(clipboards);
        }


        return listOfClipboardsData;
    }

}
