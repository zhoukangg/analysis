package bupt.edu.cn.web.util.realtime;

import javax.websocket.Session;
import java.io.Serializable;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @Date 2019/3/1 上午9:08
 */
public class Client implements Serializable {

    private static final long serialVersionUID = 8957107006902627635L;

    private int cockpitId;

    private Session session;

    public int getCockpitId() {
        return cockpitId;
    }

    public void setCockpitId(int cockpitId) {
        this.cockpitId = cockpitId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Client(int cockpitId, Session session) {
        this.cockpitId = cockpitId;
        this.session = session;
    }

    public Client() {
    }
}
