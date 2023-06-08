package vn.hitu.ntb.http.exception;

import androidx.annotation.NonNull;

import com.hjq.http.exception.HttpException;

import vn.hitu.ntb.http.model.HttpData;

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
public final class ResultException extends HttpException {

    private final HttpData<?> mData;

    public ResultException(String message, HttpData<?> data) {
        super(message);
        mData = data;
    }

    public ResultException(String message, Throwable cause, HttpData<?> data) {
        super(message, cause);
        mData = data;
    }

    @NonNull
    public HttpData<?> getHttpData() {
        return mData;
    }
}