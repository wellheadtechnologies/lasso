package com.wellhead.lasso;

public abstract class VersionHeader implements Header {
    public final String getType() {
	return "VersionHeader";
    }
    public final String getPrefix() { 
	return "~V";
    }
}