package com.wellhead.lasso;

public abstract class CurveHeader implements Header {
    public final String getType() {
	return "CurveHeader";
    }
    public final String getPrefix() { 
	return "~C";
    }
}