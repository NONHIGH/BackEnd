package com.api.portfolio.exceptions.createDirectory;

import java.io.IOException;


public class CreatingDirectoryImageException extends Exception{
    
    public CreatingDirectoryImageException(String message, IOException ex) {
        super(message);
    }    
}
