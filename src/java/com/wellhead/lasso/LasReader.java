package com.wellhead.lasso;

public interface LasReader {
    LasFile readLasFile(String path);
    Curve readCurve(String path);
    Boolean canRead(String protocal);
}