package ai.extime.Enums;

    public enum TypeCardTimeline {
        ROWS(1),
        BOXES(2),
        EXTRA(3),
        TOP(4),
        LIST(5);

        private int id;
        TypeCardTimeline(int id){
            this.id = id;
        }

        public int getId(){
            return id;
        }
    }


