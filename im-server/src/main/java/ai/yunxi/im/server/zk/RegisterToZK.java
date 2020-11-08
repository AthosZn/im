package ai.yunxi.im.server.zk;

import ai.yunxi.im.server.config.InitConfiguration;
import ai.yunxi.im.server.config.SpringBeanFactory;
import ai.yunxi.im.server.util.AddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class RegisterToZK implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(RegisterToZK.class);

	private InitConfiguration conf;
	private ZKUtil zk;

	public RegisterToZK() {
		conf = SpringBeanFactory.getBean(InitConfiguration.class);
		zk = SpringBeanFactory.getBean(ZKUtil.class);
	}

	@Override
	public void run() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			int httpPort = conf.getHttpPort();
			int nettyPort = conf.getNettyPort();
		    ip=AddressUtils.getInstance().getInnetIp();
			LOGGER.info("---服务端注册到Zookeeper. ip:"+ip+"; httpPort:"+httpPort+"; nettyPort:"+nettyPort);

			//创建父节点 持久化节点
			zk.createRootNode();
			//判断是否需要注册到zk
			if(conf.isZkSwitch()){
				String path = conf.getRoot() + "/"+ip+"-"+conf.getNettyPort()+"-"+conf.getHttpPort();
				zk.createNode(path);
				LOGGER.info("---服务端注册到ZK成功，Path="+path);
			}
			zk.stateChangesListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




//	public void process(WatchedEvent watchedEvent) {
//		if (watchedEvent.getState() == Event.KeeperState.Expired) {
//			System.out.println("expired");
//		}
//	}
}
