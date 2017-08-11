package com.jessy_barthelemy.pictothemo.Interfaces;

public interface IVoteResponse {
    void asyncTaskSuccess(boolean positive);
    void asyncTaskFail(String errorMessage);
}
