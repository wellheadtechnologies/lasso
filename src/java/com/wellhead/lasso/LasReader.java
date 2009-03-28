package com.wellhead.lasso;

public interface LasReader {
    LasFile readLasFile(String path);
    Curve readCurve(String path, String name);
    Boolean canRead(String protocal);
}