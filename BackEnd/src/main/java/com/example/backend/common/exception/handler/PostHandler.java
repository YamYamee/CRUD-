package com.example.backend.common.exception.handler;

import com.example.backend.common.exception.GeneralException;
import com.example.backend.common.response.BaseErrorCode;

public class PostHandler extends GeneralException {
    public PostHandler(BaseErrorCode code) {
        super(code);
    }
}
