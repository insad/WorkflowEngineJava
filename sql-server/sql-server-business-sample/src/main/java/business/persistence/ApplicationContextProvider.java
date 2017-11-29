package business.persistence;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext ctx = null;

    public static <T> T getBean(Class<T> cls) {
        return ctx.getBean(cls);
    }

    static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        ApplicationContextProvider.ctx = ctx;
    }
}