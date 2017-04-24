package com.example.alexander_topilskii.internetradio.models.database;


public class Station {
    private int id;
    private String name;
    private String source;
    private boolean isCurrent;

    public Station(int id, String name, String source, boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.isCurrent = isCurrent;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    void setCurrent(boolean current) {
        isCurrent = current;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
