package com.wellhead.lasso;

public abstract class ParameterHeader implements Header {
    public final String getType() {
	return "ParameterHeader";
    }
    public final String getPrefix() { 
	return "~P";
    }
}