package ai.yunxi.im.server.handle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

/**
 * 
 * @author 小五老师-云析学院
 * @createTime 2019年3月7日 下午9:12:59
 * 
 */
@Component
public class ChannelMap {

	private final Map<Integer, Channel> CHANNEL_MAP = new ConcurrentHashMap<Integer, Channel>(16);

	public Map<Integer, Channel> getCHANNEL_MAP() {
		return CHANNEL_MAP;
	}

	public void putClient(Integer userId, Channel channel){
		CHANNEL_MAP.put(userId, channel);
	}
	
	public Channel getClient(Integer userId){
		return CHANNEL_MAP.get(userId);
	}
}
