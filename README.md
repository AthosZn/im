# IM������
һ������Netty�����칤�ߣ������У���ʹ��Zookeeper+Redisʵ�ַ���˷ֲ�ʽ����

## ϵͳ�ܹ�
 ![image](https://github.com/smallFive55/im/raw/master/pic/im.png)
-  `�ͻ���`��`�����`�����ӣ�ͨ��`·�ɶ�`ѡ�����`�����`�ڵ�
-  `�����`��������`Zookeeper`ע���Լ�
-  `�ͻ���`������Ϣͨ��`·�ɶ�`��ѡ���Ӧ��`�����`����������Ϣ
-  `·�ɶ�`��״̬����Ⱥ����ֻ��Ҫһ��`Nginx`������
 
## TODO LIST
* [x] �ͻ��˷��������
* [x] �ͻ��˴����û�������Ϣ
* [x] ��Ϣ���л���Protobuf��
* [x] �����ע��
* [x] ·�ɶ˻�ȡ���÷���˽ڵ�
* [x] ��ϢȺ������
* [x] �ͻ������ߣ�ǿ������/�������ߣ�
* [x] ����˶��ߣ��ͻ�������
* [ ] ����
* [ ] ��Ϣ�ط�
* [ ] �����¼
* [ ] ������Ϣ

## ����
1.����redis��Zookeeper���޸�redis��Zookeeper����

2.���롢���
```
mvn -Dmaven.test.skip=true clean package
```
3.����·��
```
java -jar im-route\target\im-route-0.0.1-SNAPSHOT.jar
```	
4.���������
```
java -jar im-server\target\im-server-0.0.1-SNAPSHOT.jar --server.port=8084 --im.server.port=8090
java -jar im-server\target\im-server-0.0.1-SNAPSHOT.jar --server.port=8085 --im.server.port=8091
```	
5.�����ͻ���
```
java -jar im-client\target\im-client-0.0.1-SNAPSHOT.jar --server.port=8071 --im.user.id=1001 --im.user.userName=Five
java -jar im-client\target\im-client-0.0.1-SNAPSHOT.jar --server.port=8072 --im.user.id=1002 --im.user.userName=Luffy
```	