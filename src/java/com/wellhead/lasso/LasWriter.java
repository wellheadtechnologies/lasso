package com.wellhead.lasso;

public interface LasWriter {
    void writeLasFile(LasFile lf, String path);
    void writeCurve(Curve c, String path);
    Boolean canWrite(String protocol);
}