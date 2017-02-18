package com.jessy_barthelemy.pictothemo.Interfaces;

public interface IAsyncResponse {
    void asyncTaskSuccess();
    void asyncTaskFail(String errorMessage);
}
