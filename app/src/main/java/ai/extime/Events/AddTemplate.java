package ai.extime.Events;

import ai.extime.Models.Template;

public class AddTemplate {

    private Template template;

    public AddTemplate(Template template) {
        this.template = template;
    }

    public Template getTemplate() {
        return template;
    }
}
