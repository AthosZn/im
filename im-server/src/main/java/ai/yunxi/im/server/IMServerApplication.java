package ai.yunxi.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

import ai.yunxi.im.server.zk.RegisterToZK;

/**
 *
 * @author Athos
 * @createTime 2019年2月26日 下午2:59:36
 *
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.myu.console.biz*.**.mapper","com.dmc.mapper"})
public class IMServerApplication implements CommandLineRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(IMServerApplication.class, args);
		LOGGER.info("启动 Service 服务成功");
	}

	/**
	 * 启动后，将节点注册在Zookeeper
	 **/
	@Override
	public void run(String... args) throws Exception {
		try {
			Thread thread = new Thread(new RegisterToZK());
			thread.setName("im-server-register-thread");
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
