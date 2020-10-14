package ai.yunxi.im.server.handle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

/**
 *
 * @author Athos
 * @createTime 2019年3月7日 下午9:12:59
 *
 */
public class ChannelMap {

	private static ChannelMap instance;
	private final Map<Long, Channel> CHANNEL_MAP = new ConcurrentHashMap<Long, Channel>();

	private ChannelMap() {
	}
	public static ChannelMap newInstance(){
		if(instance == null){
			instance = new ChannelMap();
		}
		return instance;
	}

	public Map<Long, Channel> getCHANNEL_MAP() {
		return CHANNEL_MAP;
	}

	public void putClient(Long userId, Channel channel){
		CHANNEL_MAP.put(userId, channel);
	}

	public Channel getClient(Integer userId){
		return CHANNEL_MAP.get(userId);
	}
}
