package com.wellhead.lasso;

public interface Descriptor {
    String getMnemonic();
    String getUnit();
    String getData();
    String getDescription();

    void setMnemonic(String s);
    void setUnit(String s);
    void setData(String s);
    void setDescription(String s);
}

