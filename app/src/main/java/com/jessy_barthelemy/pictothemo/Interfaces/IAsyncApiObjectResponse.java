package com.jessy_barthelemy.pictothemo.Interfaces;

public interface IAsyncApiObjectResponse {
    void asyncTaskSuccess(Object response);
    void asyncTaskFail(String errorMessage);
}
