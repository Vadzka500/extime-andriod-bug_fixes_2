package ai.extime.Interfaces;

import ai.extime.Models.Contact;

public interface IKanbanList {

    void openPreview(Contact contact, int number);

    void openProfile(Contact contact);

    void shareContact(Contact contact);

    void closeOtherPopups();

    void setSelectId(long id);

    boolean checkSelectId(long id);

    boolean isSelectEmpty();

    boolean priviewIsVisible();
}
