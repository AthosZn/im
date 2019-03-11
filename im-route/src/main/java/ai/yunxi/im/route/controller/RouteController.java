package ai.yunxi.im.route.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ai.yunxi.im.common.constant.Constant;
import ai.yunxi.im.common.pojo.ChatInfo;
import ai.yunxi.im.common.pojo.ServiceInfo;
import ai.yunxi.im.common.pojo.UserInfo;
import ai.yunxi.im.common.utils.StringUtil;
import ai.yunxi.im.route.service.RouteService;
import ai.yunxi.im.route.zk.ZKUtil;

/**
 * 
 * @author 小五老师-云析学院
 * @createTime 2019年2月27日 下午3:11:53
 * 
 */
@RestController
@RequestMapping("/")
public class RouteController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private ZKUtil zk;
	
	private AtomicLong index = new AtomicLong();
	
	@Autowired
	private RouteService routeService;
	/**
	 * 客户端获得服务端信息
	 **/
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public ServiceInfo login(@RequestBody UserInfo userinfo){
		
		String server="";
		try {
			List<String> all = zk.getAllNode();
			if(all.size()<=0){
				LOGGER.info("no server start...");
				return null;
			}
			Long position = index.incrementAndGet() % all.size();
			if (position < 0) {
			    position = 0L;
			}
			
			server = all.get(position.intValue());
			redisTemplate.opsForValue().set(Constant.ROUTE_PREFIX+userinfo.getId(), server);
			
			LOGGER.info("get server info :"+server);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] serv = server.substring(server.indexOf("-")+1).split(":");
		ServiceInfo serviceInfo = new ServiceInfo(serv[0], Integer.parseInt(serv[1]), Integer.parseInt(serv[2]));
		return serviceInfo;
	}
	
	/**
	 * 分发消息
	 **/
	@RequestMapping(value="/chat", method=RequestMethod.POST)
	public void chat(@RequestBody ChatInfo chatinfo){
		//判断userId是否登录——从缓存取数据 ...
		String islogin = redisTemplate.opsForValue().get(Constant.ROUTE_PREFIX+chatinfo.getUserId());
		if(StringUtil.isEmpty(islogin)){
			LOGGER.info("该用户并未登录["+chatinfo.getUserId()+"]");
			return;
		}
		try {
			//从ZK拿到所有节点，分发消息
			List<String> all = zk.getAllNode();
			for (String server : all) {
				String[] serv = server.substring(server.indexOf("-")+1).split(":");
				String ip = serv[0];
				int httpPort = Integer.parseInt(serv[2]);
				String url = "http://"+ip+":"+httpPort+"/pushMessage";
				
				routeService.sendMessage(url, chatinfo);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 客户端下线
	 **/
	@RequestMapping(value="/logout", method=RequestMethod.POST)
	public void logout(@RequestBody UserInfo userinfo){
		try {
			String server = redisTemplate.opsForValue().get(Constant.ROUTE_PREFIX+userinfo.getId());
			
			//1.从redis缓存删除映射
			redisTemplate.opsForValue().getOperations().delete(Constant.ROUTE_PREFIX+userinfo.getId());
			
			//2.从服务端删除client channel
			String[] serv = server.substring(server.indexOf("-")+1).split(":");
			String ip = serv[0];
			int httpPort = Integer.parseInt(serv[2]);
			String url = "http://"+ip+":"+httpPort+"/clientLogout";
			try {
				//服务端处理客户端下线事件
				routeService.clientLogout(userinfo.getId(), url);
			} catch (ConnectException e) {
				LOGGER.info("服务端已下线...");
			}
			
			LOGGER.info("路由端处理了用户下线逻辑："+userinfo.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
