package ai.extime.Events;

public enum TypeCard {
    SHORT(1),
    FULL(2),
    MEDIUM(3),
    ROW(4);



    private int id;
    TypeCard(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
