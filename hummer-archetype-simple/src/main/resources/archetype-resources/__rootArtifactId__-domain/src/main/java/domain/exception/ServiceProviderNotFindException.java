package ${package}.domain.exception;

import com.hummer.common.exceptions.AppException;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/9 13:53
 **/
public class ServiceProviderNotFindException extends AppException {
    private static final long serialVersionUID = 8965410610493387566L;
    private int errorCode;
    private String message;

    public ServiceProviderNotFindException(int errorCode, String message) {
        super(errorCode,message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
