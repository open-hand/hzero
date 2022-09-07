package org.hzero.admin.domain.vo;

/**
 * @author XCXCXCXCX
 * @date 2020/6/28 10:11 上午
 */
public class InitChainContext {

    private Service service;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public static class Builder {

        private InitChainContext initChainContext = new InitChainContext();

        public static Builder create() {
            return new Builder();
        }

        public Builder service(Service service) {
            initChainContext.setService(service);
            return this;
        }

        public InitChainContext build() {
            return initChainContext;
        }
    }

}
