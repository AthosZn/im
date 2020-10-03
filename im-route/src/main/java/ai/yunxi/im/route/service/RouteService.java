package ai.yunxi.im.route.service;

import java.io.IOException;

import ai.yunxi.im.common.pojo.ImMessage;

/**
 *
 * @author Athos
 * @createTime 2019年3月7日 下午8:38:44
 *
 */
public interface RouteService {

	public void sendMessage(String url, ImMessage chat) throws IOException;

	void zkSend(ImMessage chat);

}
