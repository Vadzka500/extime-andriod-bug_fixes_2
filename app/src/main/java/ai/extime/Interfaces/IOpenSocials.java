package ai.extime.Interfaces;

import ai.extime.Models.Contact;

public interface IOpenSocials {

    void openFacebook(Contact contact);

    void openTwitter(Contact contact);

    void openLinkedIn(Contact contact);

    void openInstagram(Contact contact);

    void openVk(Contact contact);

    void openMedium(Contact contact);

    void openYoutube(Contact contact);

    void intentEmail(String str);

    void intentPhone(String str);
}
