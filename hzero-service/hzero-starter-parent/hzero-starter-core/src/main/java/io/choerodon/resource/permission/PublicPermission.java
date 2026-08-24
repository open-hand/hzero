package io.choerodon.resource.permission;

import org.springframework.http.HttpMethod;

import java.util.Objects;

/**
 * @author flyleft
 * 2018/4/17
 */
public class PublicPermission {

    public final String path;
    public final HttpMethod method;

    public PublicPermission(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublicPermission that = (PublicPermission) o;
        return Objects.equals(path, that.path) &&
                method == that.method;
    }

    @Override
    public int hashCode() {

        return Objects.hash(path, method);
    }
}
