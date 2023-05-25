package com.xuecheng.base.exception;

public class XueChengPlusException extends  RuntimeException{
    private String errMessage;

    public XueChengPlusException(){
        super();
    }
    public XueChengPlusException(String message){

        super(message);
        this.errMessage = message;
    }

    public String getErrMessage() {
        return errMessage;
    }
    public static void cast(String message){
        throw new XueChengPlusException(message);
    }

    public static void cast(CommonError error){
        throw new XueChengPlusException(error.getErrMessage());
    }
}
