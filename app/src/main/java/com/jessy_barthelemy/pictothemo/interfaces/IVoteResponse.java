package com.jessy_barthelemy.pictothemo.interfaces;

public interface IVoteResponse {
    void asyncTaskSuccess(boolean positive);
    void asyncTaskFail(String errorMessage);
}
