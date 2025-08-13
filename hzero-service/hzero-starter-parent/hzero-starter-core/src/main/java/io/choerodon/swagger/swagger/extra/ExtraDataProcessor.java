package io.choerodon.swagger.swagger.extra;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import org.hzero.core.util.RouteValidateUtil;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bojiangzhou
 * @author wuguokai
 */
public class ExtraDataProcessor implements BeanPostProcessor {

    private ExtraData extraData = ExtraDataManager.extraData;

    private List<ChoerodonRouteData> routeDataList = new ArrayList<>();

    private final Environment environment;

    public ExtraDataProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 在继承开发时，多个 @ChoerodonExtraData 标识的bean，外部bean会覆盖当前包的bean.
        ChoerodonExtraData choerodonExtraData = AnnotationUtils.findAnnotation(bean.getClass(), ChoerodonExtraData.class);
        if (choerodonExtraData != null && bean instanceof ExtraDataManager) {
            //DONE 获取类中的数据并加入到swagger json里
            ExtraData newExtraData = ((ExtraDataManager) bean).getData();
            if(newExtraData != null){
                ChoerodonRouteData routeData = (ChoerodonRouteData) newExtraData.get(ExtraData.ZUUL_ROUTE_DATA);
                resolvePlaceHolders(routeData);
                RouteValidateUtil.assertDangerousRouteWhenStarted(routeData.getPath());
                routeDataList.add(routeData);
            }
        }
        return bean;
    }

    private void resolvePlaceHolders(ChoerodonRouteData routeData) {
        Field[] fields = routeData.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                boolean old = field.isAccessible();
                field.setAccessible(true);
                Object value = field.get(routeData);
                if (value instanceof String) {
                    field.set(routeData, environment.resolvePlaceholders((String) value));
                }
                field.setAccessible(old);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("resolve place holder for choerodonRouteData failed.", e);
        }
    }

    public ExtraData getExtraData() {
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, routeDataList);
        return extraData;
    }
}
