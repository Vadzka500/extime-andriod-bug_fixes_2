package ai.extime.Events;

import ai.extime.Models.Template;

public class UpdateTemplate {

    private Template template;

    public UpdateTemplate(Template template) {
        this.template = template;
    }

    public Template getTemplate() {
        return template;
    }
}
