package workflowapp;

import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.redis.RedisProvider;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class ProviderHelper {

    private ProviderHelper() {
    }

    static WorkflowDocumentProvider getProvider() {
        JedisPool jedisPool = getJedisPool();
        return new RedisProvider(jedisPool);
    }

    private static JedisPool getJedisPool() {
        try (InputStream resourceAsStream = ProviderHelper.class.getResourceAsStream("/application.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);

            String host = properties.getProperty("jedis.host");
            String port = properties.getProperty("jedis.port");
            return new JedisPool(host, Integer.parseInt(port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
