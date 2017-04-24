package com.example.alexander_topilskii.internetradio.models.database;

import java.util.LinkedList;
import java.util.List;

public class MockModel {
    private List<Station> list = new LinkedList<>();

    public MockModel() {
        this.list.add(new Station(0, "name0", "http://cast.radiogroup.com.ua:8000/europaplus", true));
        this.list.add(new Station(1, "name1", "source1", false));
        this.list.add(new Station(2, "name2", "source2", false));
        this.list.add(new Station(3, "name3", "source3", false));
    }

//    @Override
//    public Cursor getStations() {
//        return list;
//    }
//
//    @Override
//    public void changeCurrentStations(int id) {
//        for (Station station : list) {
//            if (id == station.getId()) station.setCurrent(true);
//            else station.setCurrent(false);
//        }
//    }
//
//    @Override
//    public Station getCurrentStation() {
//        for (Station station : list) {
//            if (station.isCurrent()) return station;
//        }
//        return list.get(0);
//    }
}
