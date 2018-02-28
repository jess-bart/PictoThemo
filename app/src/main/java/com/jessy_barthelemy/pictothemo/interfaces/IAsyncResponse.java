package com.jessy_barthelemy.pictothemo.interfaces;

public interface IAsyncResponse {
    void asyncTaskSuccess();
    void asyncTaskFail(String errorMessage);
}
