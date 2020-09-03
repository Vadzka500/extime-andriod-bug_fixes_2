package ai.extime.Services;

import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

import ai.extime.Enums.ExtractEnums;
import ai.extime.Enums.SocialEnums;
import ai.extime.Fragments.ContactsFragment;

public class ParsingClipboard extends AsyncTask<Void, Void, String> {

    private SocialEnums socialEnums;
    private String way;
    private String name = null;

    public ParsingClipboard(String s, SocialEnums socialEnums){
        this.way = s;
        this.socialEnums = socialEnums;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        int ind = way.indexOf("https://");

        if (ind == -1)
            ind = way.indexOf("http://");

        if(ind == -1)
            way = "https://" + way;
        try {

            name = Jsoup.connect(way).timeout(3000).get().title();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e2){
            e2.printStackTrace();
        }



        if(name != null) {

            int index = name.length();
            if (socialEnums.equals(SocialEnums.INSTAGRAM)) {
                index = name.indexOf('|');
                ContactsFragment.extractEnumsToExtractContainer = ExtractEnums.INSTAGRAM;
            } else if (socialEnums.equals(SocialEnums.VK)) {
                index = name.indexOf('|');
                ContactsFragment.extractEnumsToExtractContainer = ExtractEnums.VK;

            } else if (socialEnums.equals(SocialEnums.FACEBOOK)) {
                index = name.indexOf('|');
                ContactsFragment.extractEnumsToExtractContainer = ExtractEnums.FACEBOOK;
            } else if (socialEnums.equals(SocialEnums.LINKEDIN)) {
                ContactsFragment.extractEnumsToExtractContainer = ExtractEnums.LINKEDIN;
            }
            if (index == -1)
                index = name.length();






          /*  CreateFragment.nameContactExtractFromSocial = name.substring(0, index - 1);
            CreateFragment.socialEnums = socialEnums;*/

            return name.substring(0, index - 1);
        }else
            return null;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);

    }
}
