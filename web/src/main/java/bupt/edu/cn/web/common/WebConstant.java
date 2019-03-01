package bupt.edu.cn.web.common;

public enum WebConstant {
    QUERY_ERROR (2,false,"查询失败"),
    QUERY_SUCCESS (1,true,"查询成功")
    ;

    private int status;
    private boolean result;
    private String reason;


    WebConstant(int status, boolean result, String reason){
        this.setStatus(status);
        this.reason = reason;
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getReason() {
        return reason;
    }

    public boolean isResult() {
        return result;
    }
}
