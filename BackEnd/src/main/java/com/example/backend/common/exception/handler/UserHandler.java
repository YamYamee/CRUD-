package com.example.backend.common.exception.handler;

import com.example.backend.common.exception.GeneralException;
import com.example.backend.common.response.BaseErrorCode;

public class UserHandler extends GeneralException {
    public UserHandler(BaseErrorCode code) {
        super(code);
    }
}
