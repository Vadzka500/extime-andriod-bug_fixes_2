package ai.extime.Events;

import ai.extime.Models.Contact;

public class PopupCompanyInProfile {

    private long company;

    public PopupCompanyInProfile(long c){
        this.company = c;
    }

    public long getCompany() {
        return company;
    }

    public void setCompany(long company) {
        this.company = company;
    }
}
