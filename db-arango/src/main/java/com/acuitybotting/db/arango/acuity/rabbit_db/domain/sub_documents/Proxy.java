package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.InheritSubId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Proxy implements InheritSubId{

    private transient String subId;
    private String host;
    private String port;
    private String username;
    private String encryptedPassword;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proxy)) return false;

        Proxy proxy = (Proxy) o;

        if (getHost() != null ? !getHost().equals(proxy.getHost()) : proxy.getHost() != null) return false;
        if (getPort() != null ? !getPort().equals(proxy.getPort()) : proxy.getPort() != null) return false;
        if (getUsername() != null ? !getUsername().equals(proxy.getUsername()) : proxy.getUsername() != null)
            return false;
        return getEncryptedPassword() != null ? getEncryptedPassword().equals(proxy.getEncryptedPassword()) : proxy.getEncryptedPassword() == null;
    }

    @Override
    public int hashCode() {
        int result = getHost() != null ? getHost().hashCode() : 0;
        result = 31 * result + (getPort() != null ? getPort().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getEncryptedPassword() != null ? getEncryptedPassword().hashCode() : 0);
        return result;
    }

    @Override
    public void setParentSubId(String id) {
        this.subId = id;
    }
}
