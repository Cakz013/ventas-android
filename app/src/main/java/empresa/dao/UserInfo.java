package empresa.dao;

/**
 * Created by Cesar on 7/10/2017.
 */

public class UserInfo {

    private int u;
    private String p;
    public UserInfo(int idusuario, String userPasswordHash) {
        this.u = idusuario;
        this.p = userPasswordHash;
    }
    public int getU() {
        return u;
    }
    public void setU(int u) {
        this.u = u;
    }
    public String getP() {
        return p;
    }
    public void setP(String p) {
        this.p = p;
    }

}
