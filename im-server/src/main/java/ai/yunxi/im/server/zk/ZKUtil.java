package ai.yunxi.im.server.zk;

import java.net.InetAddress;
import java.util.List;

import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.yunxi.im.server.config.InitConfiguration;

@Component
public class ZKUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(ZKUtil.class);

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private InitConfiguration conf ;

    /**
     * 创建父级节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(conf.getRoot());
        if (exists){
            return;
        }
        //创建 root
        zkClient.createPersistent(conf.getRoot()) ;
    }

    /**
     * 写入指定节点 临时目录
     */
    public void createNode(String path) {
        if(!zkClient.exists(path)) {
            zkClient.createEphemeral(path);
        }
    }

    public void stateChangesListener() {
        zkClient.subscribeStateChanges(new IZkStateListener() {
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                if (state == Watcher.Event.KeeperState.Disconnected) {
                    System.out.println("Disconnected");
                } else if (state == Watcher.Event.KeeperState.SyncConnected) {

                    if(conf.isZkSwitch()){
                        String ip = InetAddress.getLocalHost().getHostAddress();
                        String path = conf.getRoot() + "/"+ip+"-"+conf.getNettyPort()+"-"+conf.getHttpPort();
                        createNode(path);
                        LOGGER.info("---服务端注册到ZK成功，Path="+path);
                    }
                    System.out.println("SyncConnected");
                }
                LOGGER.warn("handleStateChanged");
            }
            public void handleSessionEstablishmentError(Throwable error) throws Exception {
//                LOGGER.warn("handleSessionEstablishmentError");
            }
            public void handleNewSession() throws Exception {
//                LOGGER.warn("handleNewSession");
            }
        });
    }



    /**
     * 获取所有注册节点
     * @return
     */
    public List<String> getAllNode(){
        List<String> children = zkClient.getChildren("/route");
        LOGGER.info("查询所有节点成功，节点数："+children.size());
       return children;
    }
}
