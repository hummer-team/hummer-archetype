package ${package}.domain.exception;

import com.hummer.common.exceptions.AppException;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/11 16:34
 **/
public class SecretKeyAuthException extends AppException {
    private static final long serialVersionUID = -1949751485467215720L;
    private int errorCode;
    private String message;

    public SecretKeyAuthException(int errorCode, String message) {
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
