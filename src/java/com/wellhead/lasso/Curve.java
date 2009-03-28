package com.wellhead.lasso;
import java.util.List;

public interface Curve {
    Descriptor getDescriptor();
    List<Double> getLasData();
    Curve getIndex();

    String getMnemonic();
    String getUnit();
    String getData();
    String getDescription();

    void setDescriptor(Descriptor d);
    void setLasData(List<Double> data);
    void setIndex(Curve index);

    void setMnemonic(String s);
    void setUnit(String s);
    void setData(String s);
    void setDescription(String s);
}