package com.jessy_barthelemy.pictothemo.interfaces;

public interface IAsyncApiObjectResponse {
    void asyncTaskSuccess(Object response);
    void asyncTaskFail(String errorMessage);
}
