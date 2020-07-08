package ai.yunxi.im.route.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import ai.yunxi.im.route.zk.ZKUtil;
import okhttp3.OkHttpClient;

import javax.annotation.PostConstruct;

/**
 *
 * @author Athos
 * @createTime 2019年2月26日 下午10:08:36
 *
 */
@Configuration
public class BeanConfiguration {
	@Autowired
	private InitConfiguration conf;
	@Autowired
	private ZKUtil zkUtil;
	@Autowired
	private ZkClient zk;

	@Bean
	public ZkClient createZKClient(){
		ZkClient zkClient = new ZkClient(conf.getAddr());

		//监听/route节点下子节点的变化，实时更新server list
		zkClient.subscribeChildChanges(conf.getRoot(), new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				zkUtil.setAllNode(currentChilds);
			}
		});
		//获取节点相关数据

		return zkClient;
	}

	@PostConstruct
	public void init(){

		//监听/route节点下子节点的变化，实时更新server list
		zk.subscribeChildChanges(conf.getRoot(), new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				zkUtil.setAllNode(currentChilds);
			}
		});
		//获取节点相关数据
		watchNode(zk);
	}


	//监听服务端的列表信息
	private void watchNode(ZkClient zooKeeper){
		try{
			if(zooKeeper.exists(conf.getRoot())) {
				//获取子节点信息
				List<String> nodeList = zooKeeper.getChildren(conf.getRoot());
				List<String> dataList = new ArrayList<String>();
				for (String node : nodeList) {
//				byte[] bytes = zooKeeper.readData(conf.getRoot() + "/" + node);
					dataList.add(conf.getRoot() + "/" + node);
				}
				zkUtil.setAllNode(dataList);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
     * Redis bean
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * http client
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }
}
