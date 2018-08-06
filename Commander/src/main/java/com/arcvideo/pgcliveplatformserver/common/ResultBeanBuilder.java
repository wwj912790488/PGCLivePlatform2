package com.arcvideo.pgcliveplatformserver.common;

import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Created by slw on 2018/7/6.
 */
@Component
public class ResultBeanBuilder {

    @Autowired
    private MessageSource messageSource;

    public ResultBean ok() {
        return new ResultBean();
    }

    public ResultBean error(String errorMsg) {
        return new ResultBean(ResultBean.FAIL, errorMsg);
    }

    public ResultBean builder(CodeStatus codeStatus, Object... args) {
        int code = codeStatus.getCode();
        String message = messageSource.getMessage(codeStatus.getMessage(), args, null);
        return new ResultBean(code, message);
    }
}
