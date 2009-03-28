package com.wellhead.lasso;

public abstract class WellHeader implements Header {
    public final String getType() {
	return "WellHeader";
    }
    public final String getPrefix() {
	return "~W";
    }
}