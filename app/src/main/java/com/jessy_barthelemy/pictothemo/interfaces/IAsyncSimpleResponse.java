package com.jessy_barthelemy.pictothemo.interfaces;

public interface IAsyncSimpleResponse extends IAsyncResponse{
    void asyncTaskSuccess();
    void asyncTaskFail(String errorMessage);
}
