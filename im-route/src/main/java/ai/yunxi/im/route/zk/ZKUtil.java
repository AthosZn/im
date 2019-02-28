package ai.yunxi.im.route.zk;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import ai.yunxi.im.route.config.AppConfiguration;


/**
 * Function: Zookeeper 工具
 *
 * @author crossoverJie
 *         Date: 2018/8/19 00:33
 * @since JDK 1.8
 */
@Component
public class ZKUtil {

    private static Logger logger = LoggerFactory.getLogger(ZKUtil.class);


    @Autowired
    private ZkClient zkClient;

    @Autowired
    private AppConfiguration appConfiguration ;

    /**
     * 创建父级节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(appConfiguration.getZkRoot());
        if (exists){
            return;
        }

        //创建 root
        zkClient.createPersistent(appConfiguration.getZkRoot()) ;
    }

    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }

    /**
     * 获取所有注册节点
     * @return
     */
    public List<String> getAllNode(){
        List<String> children = zkClient.getChildren("/route");
        logger.info("查询所有节点成功=【{}】", JSON.toJSONString(children));
       return children;
    }
}
