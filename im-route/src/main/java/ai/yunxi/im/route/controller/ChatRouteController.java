package ai.yunxi.im.route.controller;

import ai.yunxi.im.common.constant.BasicConstant;
import ai.yunxi.im.common.pojo.ImMessage;
import ai.yunxi.im.common.pojo.ServerInfo;
import ai.yunxi.im.common.pojo.UserInfo;
import ai.yunxi.im.common.utils.StringUtil;
import ai.yunxi.im.route.service.RouteService;
import ai.yunxi.im.route.zk.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Athos
 * @createTime 2019年2月27日 下午3:11:53
 *
 */
@RestController
@RequestMapping("/chat")
public class ChatRouteController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatRouteController.class);
	private AtomicLong index = new AtomicLong();

	@Autowired
	private ZKUtil zk;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private RouteService routeService;

	/**
	 * 分发消息
	 **/
	@RequestMapping(value="/send", method=RequestMethod.POST)
	public void chat(@RequestBody ImMessage chat){
		//判断userId是否登录——从缓存取数据 ...
		String islogin = redisTemplate.opsForValue().get(BasicConstant.ROUTE_PREFIX + chat.getUserId());
		if(StringUtil.isEmpty(islogin)){
			LOGGER.info("该用户并未登录["+chat.getUserId()+"]");
			return;
		}
		try {
			//从ZK拿到所有节点，分发消息
			List<String> all = zk.getAllNode();
			for (String server : all) {
				String[] serv = server.split("-");
				String ip = serv[0];
				int httpPort = Integer.parseInt(serv[2]);
				String url = "http://"+ip+":"+httpPort+"/pushMessage";
				routeService.sendMessage(url, chat);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
