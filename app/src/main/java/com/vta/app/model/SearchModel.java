package com.vta.app.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModel  implements Searchable {
    private String mTitle;

    public SearchModel(String title) {
        mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SearchModel setTitle(String title) {
        mTitle = title;
        return this;
    }
}
