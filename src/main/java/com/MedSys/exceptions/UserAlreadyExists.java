package com.MedSys.exceptions;

public class UserAlreadyExists extends RuntimeException{
    int status;
    public UserAlreadyExists(int status,String msg)
    {
        super(msg);
        this.status =status;

    }

    int getStatus()
    {
        return this.status;
    }
    
}
