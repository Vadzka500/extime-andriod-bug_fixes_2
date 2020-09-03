package ai.extime.Interfaces;

import ai.extime.Models.Template;

public interface IEmails {

    public void showTemplatePopup(Template template);

    public void hideTemplatePopup();

    void openTemplateFragment(Template template);


}
