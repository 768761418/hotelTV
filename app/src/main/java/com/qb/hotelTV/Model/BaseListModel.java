package com.qb.hotelTV.Model;

import java.util.ArrayList;
import java.util.List;

public class BaseListModel<T> {
    private ArrayList<T> list;
    private int total;

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
