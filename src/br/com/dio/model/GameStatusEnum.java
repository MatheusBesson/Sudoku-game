package br.com.dio.model;

public enum GameStatusEnum {

    NON_STARTED ("Not started"),
    INCOMPLETE ("Incompleted"),
    COMPLETE ("Completed");

    private String label;

    GameStatusEnum(final String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
