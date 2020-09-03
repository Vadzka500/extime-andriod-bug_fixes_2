package ai.extime.Interfaces;

import java.util.ArrayList;

import ai.extime.Models.Contact;

/**
 * Created by patal on 23.09.2017.
 */

public interface HashtagAddInterface {

    void addHashtagsToSelectedContacts(String hashtag);

    void addNewTagToContact(String hashtag, Contact contact);

    void deleteHashTagsFromUser(String hashtag,Contact contact);

    void updatedUserHashtags(ArrayList<String> updatedHashtags);
}
