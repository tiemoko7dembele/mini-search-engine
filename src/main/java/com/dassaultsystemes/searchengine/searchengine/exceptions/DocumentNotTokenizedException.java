package com.dassaultsystemes.searchengine.searchengine.exceptions;

public class DocumentNotTokenizedException extends Exception{
    public DocumentNotTokenizedException(){}

    public DocumentNotTokenizedException(String message){
        super(message);
    }

    public DocumentNotTokenizedException(Throwable cause){
        super(cause);
    }

    public DocumentNotTokenizedException(String message, Throwable cause){
        super(message, cause);
    }
}
