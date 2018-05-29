package com.jessy_barthelemy.pictothemo.interfaces;

public interface IAsyncApiObjectResponse extends IAsyncResponse{
    void asyncTaskSuccess(Object response);
    void asyncTaskFail(String errorMessage);
}
