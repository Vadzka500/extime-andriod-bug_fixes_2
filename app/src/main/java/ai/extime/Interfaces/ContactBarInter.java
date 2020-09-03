package ai.extime.Interfaces;

import ai.extime.Models.Contact;

/**
 * Created by patal on 12.08.2017.
 */

public interface ContactBarInter {

    void sortList();

    void showHashTagPopUp();

    void showAllContactsPopup();

    void showFavoriteContactsPopup();

    void showFastEditPopup(Contact contact);

    void showRemindPopup(Contact contact);

}
