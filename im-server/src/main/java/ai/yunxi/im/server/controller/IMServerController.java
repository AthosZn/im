package ai.yunxi.im.server.controller;

import java.util.Map.Entry;

import ai.yunxi.im.common.pojo.ChatInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ai.yunxi.im.common.constant.MessageConstant;
import ai.yunxi.im.common.pojo.ChatInfo;
import ai.yunxi.im.common.protocol.MessageProto;
import ai.yunxi.im.server.handle.ChannelMap;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 *
 * @author Athos
 * @createTime 2019年2月26日 下午3:10:41
 * 服务端处理器
 */
@RestController
@RequestMapping("/")
public class IMServerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IMServerController.class);
	private ChannelMap CHANNEL_MAP = ChannelMap.newInstance();
	private AttributeKey<Long> userId = AttributeKey.valueOf("userId");
	/**
	 * 服务端接收消息，并推送到指定客户端
	 **/
	@RequestMapping(value="/pushMessage", method=RequestMethod.POST)
	public void pushMessage(@RequestBody ChatInfo chat){
		//1.接收客户端封装好的消息对象
		MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
				.setCommand(chat.getCommand())
				.setTime(chat.getCurrentTime())
				.setUserId(chat.getToId())
				.setContent(chat.getContent()).build();
		//2.根据消息发送给指定客户端（群发）
		//   根据userID，从本地Map集合中得到对应的客户端Channel，发送消息
		if(MessageConstant.CHAT.equals(message.getCommand())||MessageConstant.FRIEND_ASK.equals(message.getCommand())){
			for (Entry<Long, Channel> entry : CHANNEL_MAP.getCHANNEL_MAP().entrySet()) {
				//过滤客户端本身
				if(entry.getKey() != message.getToUserId()){
					LOGGER.info("----服务端向"+entry.getValue().attr(userId).get()+"发送了消息，来自userId="+message.getUserId()+", content="+message.getContent());
					entry.getValue().writeAndFlush(message.toByteArray());
				}
			}
		}
	}

	/**
	 * 服务端处理客户端下线事件
	 **/
	@RequestMapping(value="/clientLogout", method=RequestMethod.POST)
	public void clientLogout(@RequestBody ChatInfo ChatInfo){

		CHANNEL_MAP.getCHANNEL_MAP().remove(ChatInfo.getToId()
		);
		LOGGER.info("---客户端下线["+ChatInfo.getToId()+"]");
	}
}
