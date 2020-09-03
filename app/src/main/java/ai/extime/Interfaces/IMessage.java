package ai.extime.Interfaces;

import ai.extime.Models.EmailMessage;
import ai.extime.Models.Template;

public interface IMessage {

    public void clickMessage(EmailMessage message);

    public void openFragmentMessage(EmailMessage message);

    public void openContactList(EmailMessage message);

    void ShowTemplatePopup(Template template);

    void setStarMessage(EmailMessage message, boolean star);

    boolean isOpenPreview();



}
