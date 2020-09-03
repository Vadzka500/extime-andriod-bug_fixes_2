package ai.extime.Events;

public class RemoveCompanyFromListAdapter {

    private long contact_id;

    public RemoveCompanyFromListAdapter(long contact_id){
        this.contact_id = contact_id;
    }

    public long getContact(){
        return contact_id;
    }
}
