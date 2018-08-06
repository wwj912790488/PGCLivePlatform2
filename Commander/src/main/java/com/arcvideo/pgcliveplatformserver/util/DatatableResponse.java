package com.arcvideo.pgcliveplatformserver.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slw on 2017/2/6.
 */
public class DatatableResponse<T> {
    private Integer sEcho;
    private Integer iTotalDisplayRecords;
    private Integer iTotalRecords;
    private List<T> aaData = new ArrayList<T>();

    public DatatableResponse() {
        super();
    }

    public Integer getsEcho() {
        return sEcho;
    }

    public void setsEcho(Integer sEcho) {
        this.sEcho = sEcho;
    }

    public Integer getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(Integer iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public Integer getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(Integer iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public List<T> getAaData() {
        return aaData;
    }

    public void setAaData(List<T> aaData) {
        this.aaData = aaData;
    }
}
