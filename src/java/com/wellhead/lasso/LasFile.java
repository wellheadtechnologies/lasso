package com.wellhead.lasso;
import java.util.List;

public interface LasFile {
    String getName();
    void setName(String name);

    List<Curve> getCurves();
    void setCurves(List<Curve> curves);

    List<Header> getHeaders();
    void setHeaders(List<Header> headers);
    
    Curve getIndex();
    void setIndex(Curve curve);

    Curve getCurve(String name);

    VersionHeader getVersionHeader();
    void setVersionHeader(VersionHeader vh);

    WellHeader getWellHeader();
    void setWellHeader(WellHeader wh);

    CurveHeader getCurveHeader();
    void setCurveHeader(CurveHeader ch);

    ParameterHeader getParameterHeader();
    void setParameterHeader(ParameterHeader ph);

    Boolean contentEquals(LasFile that);

    void replaceCurve(Curve old, Curve _new);
}