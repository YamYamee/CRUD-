package com.example.backend.common.exception.handler;

import com.example.backend.common.exception.GeneralException;
import com.example.backend.common.response.BaseErrorCode;

public class CommentHandler extends GeneralException {
    public CommentHandler(BaseErrorCode code) {
        super(code);
    }
}
