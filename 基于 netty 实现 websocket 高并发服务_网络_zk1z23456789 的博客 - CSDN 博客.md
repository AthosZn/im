> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/zk1z23456789/article/details/90612164

**1.WebScoket 简述**

WebSocket 是一种在单个 [TCP](https://zh.wikipedia.org/wiki/%E4%BC%A0%E8%BE%93%E6%8E%A7%E5%88%B6%E5%8D%8F%E8%AE%AE) 连接上进行[全双工](https://zh.wikipedia.org/wiki/%E5%85%A8%E9%9B%99%E5%B7%A5)通信的协议。 

WebSocket 使得客户端和服务器之间的数据交换变得更加简单，允许服务端主动向客户端推送数据。在 WebSocket API 中，浏览器和服务器只需要完成一次握手，两者之间就直接可以创建持久性的连接，并进行双向数据传输。

websocket 协议本身是构建在 http 协议之上的升级协议，客户端首先向服务器端去建立连接，这个连接本身就是 http 协议只是在头信息中包含了一些 websocket 协议的相关信息，一旦 http 连接建立之后，服务器端读到这些 websocket 协议的相关信息就将此协议升级成 websocket 协议。websocket 协议也可以应用在非浏览器应用，只需要引入相关的 websocket 库就可以了.

Websocket 使用 ws 或 wss 的[统一资源标志符](https://zh.wikipedia.org/wiki/%E7%BB%9F%E4%B8%80%E8%B5%84%E6%BA%90%E6%A0%87%E5%BF%97%E7%AC%A6)，类似于 [HTTPS](https://zh.wikipedia.org/wiki/HTTPS)，其中 wss 表示在 [TLS](https://zh.wikipedia.org/wiki/TLS) 之上的 Websocket.

对于 nginx 配置, 握手升级过程如下图所示:

![](https://img-blog.csdnimg.cn/20190527193953794.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

![](https://img-blog.csdnimg.cn/20190527194011935.png)

connection 必须设置成 Upgrade, 表示客户端希望连接升级.

Upgrade 字段必须设置为 websocket, 表示希望升级到 websocket 协议.

**2. 利用 spring-websocket 实现聊天室**

引入依赖 jar 包:

![](https://img-blog.csdnimg.cn/20190527195126594.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

spring-websocket 详细文档说明详见官方文档:

[https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/websocket.html](https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/websocket.html)

接下来直接上代码解释其实现方式:

![](https://img-blog.csdnimg.cn/20190527200212844.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

将需要处理的 handler 添加到注册中心, 配置 websocket 入口，允许访问的域、注册 Handler、SockJs 支持和拦截器, 当有 websocket 连接进来以后, 就交给我们实现的 handler 去执行业务逻辑.

在这里我们也兼容了对 SockJs 的支持,WebSocket 是一个相对比较新的规范，在 Web 浏览器和应用服务器上没有得到一致的支持。所以我们需要一种 WebSocket 的备选方案。

而这恰恰是 SockJS 所擅长的。SockJS 是 WebSocket 技术的一种模拟，在表面上，它尽可能对应 WebSocket API，但是在底层非常智能。如果 WebSocket 技术不可用的话，就会选择另外的通信方式。

要实现自己的处理逻辑就需要实现 WebSocketHandler 这个接口, 这个接口里面有 5 个方法, 如下图:

![](https://img-blog.csdnimg.cn/20190527200411307.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

```
afterConnectionEstablished:连接成功
handleMessage:消息处理
handleTransportError:异常
afterConnectionClosed:连接关闭
```

我们也可以通过握手拦截器中的 before 或者 after 方法去设置一些属性值, 或者做一下其他的业务操作等等.

业务代码做到这里, 然后 nginx 配置做好处理, 我们整个的 websocket 服务基本已经搭建完成, 就可以提供对外的服务了, 这里我们使用 spring-websoket+nginx+tomcat 就简单的实现了我们的基本任务需求了, 基于此架构的我们就简要的说到这里.

**3. 基于 netty 实现**

3.0 netty 简介

Netty 是什么？

由 JBOSS 提供的基于 Java NIO 的开源框架，Netty 提供异步非阻塞、事件驱动、高性能、高可靠、高可定制性的网络应用程序和工具，可用于开发服务端和客户端。

简单说一下 BIO 和 NIO 的区别

BIO 主要存在以下缺点：  
1. 从线程模型图中可以看到，一连接一线程，由于线程数是有限的，所以这样的模型是非常消耗资源的，  
最终也导致它不能承受高并发连接的需求  
2. 性能低，因为频繁的进行上下文切换，导致 CUP 利用率低  
3. 可靠性差，由于所有的 IO 操作都是同步的，即使是业务线程也如此，所以业务线程的 IO 操作也有可能被阻塞.

1.NIO 采用了 Reactor 线程模型，一个 Reactor 聚合了一个多路复用器 Selector，它可以同时注册、监听和轮询  
成百上千个 Channel，这样一个 IO 线程可以同时处理很多个客户端连接，线程模型优化为 1：N(N < 最大句柄、数)，  
或 M:N(M 通常为 CUP 核数 + 1)  
2. 避免了 IO 线程频繁的上下文切换，提升了 CUP 的效率  
3. 所有的 IO 操作都是异步的，所以业务线程的 IO 操作就不用担心阻塞，系统降低了对网络的实时情况和外部组件  
的处理能力的依赖.  
为什么要使用 netty 框架呢?

使用 JDK 原生 NIO 的不足之处  
1.NIO 的类库和 API 相当复杂，使用它来开发，需要非常熟练地掌握 Selector、ByteBuffer、ServerSocketChannel、SocketChannel 等  
2. 需要很多额外的编程技能来辅助使用 NIO, 例如，因为 NIO 涉及了 Reactor 线程模型，所以必须必须对多线程和网络编程非常熟悉才能写出高质量的 NIO 程序  
3. 想要有高可靠性，工作量和难度都非常的大，因为服务端需要面临客户端频繁的接入和断开、网络闪断、半包读写、失败缓存、网络阻塞的问题，这些将严重影响我们的可靠性，而使用原生 NIO 解决它们的难度相当大。  
4.JDK NIO 中著名的 BUG--epoll 空轮询，当 select 返回 0 时，会导致 Selector 空轮询而导致 CUP100%，官方表示 JDK1.6 之后修复了这个问题，其实只是发生的概率降低了，没有根本上解决。  
那么为什么要用 Netty 呢？  
1.API 使用简单，更容易上手，开发门槛低  
2. 功能强大，预置了多种编解码功能，支持多种主流协议  
3. 定制能力高，可以通过 ChannelHandler 对通信框架进行灵活地拓展  
4. 高性能，与目前多种 NIO 主流框架相比，Netty 综合性能最高  
5. 高稳定性，解决了 JDK NIO 的 BUG  
6. 经历了大规模的商业应用考验，质量和可靠性都有很好的验证  
 

![](https://img-blog.csdnimg.cn/20190527201638512.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

这是一个摘自于 netty 官方的服务启动的 demo, 我们先说一下启动的流程, 然后我们再详细的说一下具体的具体的参数说明.

*   创建 boss 和 work 线程组, bossGroup 负责接收客户端的链接, workerGroup 负责工作线程 (IO 操作, 任务操作等等)
    
*   ServerBootstrap 是一个辅助启动 NIO 服务的类
    
*   设置服务端的 channel 类型, 这里我们使用的 nio 的, 所以是 NioSserverSocketChannel
    
*   设置 childHandler, 具体需要执行的处理器, 这是一个实现 ChannelInitializer 抽象类的内部类, 这个可以帮助使用新建一些自己的 handler, 处理自己的网络程序, 这个抽象类里面有一个 initChannel 方法, 在 websocket 链接进来的时候, 就会初始化调用这个参数.
    
*   设置 tcp 的一些标准参数, 例如 KEEP_ALIVE, 这是开启心跳机制的, 当客户端服务端建立链接处于 ESTABLISHED 状态, 超过 2 个小时未交流, 机制就会被启动, 等等一些 tcp 参数. 
    
*   绑定端口, 启动服务
    

下面我们对启动流程中的个别做一下简要的说明和分析:

3.1 EventLoopGroup

3.1.1 EventLoopGroup, 在这里 new 了 2 个

```
EventLoopGroup bossGroup = new NioEventLoopGroup();NettyRuntime.availableProcessors()
EventLoopGroup workerGroup = new NioEventLoopGroup();
```

一个作为 boss 线程组, 负责客户端接收, 一个负责工作线程的工作 (与客户端的 IO 操作和任务操作等等).

```
private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
 
protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
  super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
}
```

我们创建的时候, 并未设置要创建的 group 数量, 默认是当前 cpu 核数的 2 倍.

为什么需要创建 2 个 EventLoopGroup 呢? 我们就需要提一个 Reactor 模型了, netty 是基于 Reactor 模型实现的.

3.2 Reactor 模型之:

3.2.1. 单线程模型![](https://img-blog.csdnimg.cn/20190527203731989.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

理论上一个 NIO 线程, 既能够接收客户端的链接, 同时也能够处理 IO 操作以及其他任务操作等等, 但是一个线程对 cpu 利用率不高, 并且, 一旦有大量的请求连接, 性能上势必会下降, 甚至无法响应的情况.

3.2.2. 多线程模型

![](https://img-blog.csdnimg.cn/20190527204212672.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

1 个线程负责专门接收客户端的链接, 另一组线程负责处理 IO 操作或者其他的任务操作. 虽然如此，但理论上来说依然有一个地方是单点的；那就是处理客户端连接的线程。

因为大多数服务端应用或多或少在连接时都会处理一些业务，如鉴权之类的，当连接的客户端越来越多时这一个线程依然会存在性能问题。

3.2.3: 主从模式

![](https://img-blog.csdnimg.cn/2019052720423153.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

一个 NIO 线程池处理链接监听, 一个线程池处理 IO 操作, 并且在 netty 官方中, 墙裂推荐使用这种线程模型.

虽然我们当前项目 booGroup 使用了线程组, 但是实际中还是用的单线程的, 具体原因在 bind 的时候再详述.

3.2 bind 过程

```
private ChannelFuture doBind(final SocketAddress localAddress) {
        final ChannelFuture regFuture = this.initAndRegister();
        final Channel channel = regFuture.channel();
        //省略以下代码
    }
```

在调用 bind 的时候会调用到 AbstractBootstrap 中的 doBind() 方法, 上面就是代码的简写, 继续跟踪代码, 在调用完这个以后, 接下来就会打开一个 socket, 就像我们之前使用 ServerSocket 一样, 打开 socket, 等待客户端的链接

```
Class NioServerSocketChannel
 
private static java.nio.channels.ServerSocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openServerSocketChannel();
        } catch (IOException var2) {
            throw new ChannelException("Failed to open a server socket.", var2);
        }
    }
```

 接下来就是 accept 操作, netty 是事件驱动的, 在当前 channel 上设置 accept 事件

```
public NioServerSocketChannel(java.nio.channels.ServerSocketChannel channel) {
        super((Channel)null, channel, 16);
        //16 就是代表着accept事件
        this.config = new NioServerSocketChannel.NioServerSocketChannelConfig(this, this.javaChannel().socket());
    }
```

 接着就是初始化 Pipeline(暂时不说), 以及 netty 底层的 io 操作对象 Unsafe.

```
    final ChannelFuture initAndRegister() {
        Channel channel = null;
 
        try {
            channel = this.channelFactory.newChannel();
            this.init(channel);
        } catch (Throwable var3) {
            //省略
            }
        }
 
        ChannelFuture regFuture = this.config().group().register(channel);
        //省略
 
        return regFuture;
    }
```

创建完这些以后, 继续进行初始化和注册的流程, 创建完 channel 之后有一个 this.init 的方法, 点进去之后就是一些 tcp 参数的初始化, 以及一些 AttributeKey 的属性值设置.

```
p.addLast(new ChannelHandler[]{new ChannelInitializer<Channel>() {
            public void initChannel(final Channel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = ServerBootstrap.this.config.handler();
                if (handler != null) {
                    pipeline.addLast(new ChannelHandler[]{handler});
                }
 
                ch.eventLoop().execute(new Runnable() {
                    public void run() {
                        pipeline.addLast(new ChannelHandler[]{new ServerBootstrap.ServerBootstrapAcceptor(ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs)});
                    }
                });
            }
        }});
```

 这里会把 ServerBootstrapAcceptor 对象放到当前 channel 的处理链中, 同时还把 workerGroup 作为构造函数的参数放入其中, 这里的作用咱们下面再具体分析.

继续调用 initAndRegister 方法, 进入这个方法我们就看到一个 newChannel 的方法, 点进去就会看到是通过反射生成服务端的 channel 对象的, 此处的 this.config().group() 获取到的 EventLoopGroup 就是设置的 bossGroup 线程组, 但奇怪的是, 当前项目启动就只使用了一个线程, 并没有使用线程组的概念, 是因为我们只启动了一个 ServerBootStrap 启动类, 线程组的概念使用于同时启动多个 ServerBootStrap.

继续跟踪代码

```
MultithreadEventLoopGroup
 
public ChannelFuture register(Channel channel) {
        return this.next().register(channel);
    }
```

会调用 MultithreadEventLoopGroup 的 register 方法

```
SingleThreadEventLoop
 
public ChannelFuture register(ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }
```

接着就会调用 AbstractChannel 的 register0 方法, 如下

```
private void register0(ChannelPromise promise) {
            try {
                boolean firstRegistration = this.neverRegistered;
                AbstractChannel.this.doRegister();
                this.neverRegistered = false;
                AbstractChannel.this.registered = true;
                AbstractChannel.this.pipeline.invokeHandlerAddedIfNeeded();
                this.safeSetSuccess(promise);
                AbstractChannel.this.pipeline.fireChannelRegistered();
                //省略
 
        }
```

执行完里面的 doResgister 方法之后, 下面的就是触发一个时间, 顺着 pipeline 链执行.

接下来我们继续看 doRegister 方法, 最终会执行 AbstractNioChannel 里面的 doRgister 方法

```
protected void doRegister() throws Exception {
        boolean selected = false;
 
        while(true) {
            try {
                this.selectionKey = this.javaChannel().register(this.eventLoop().unwrappedSelector(), 0, this);
                return;
            } catch (CancelledKeyException var3) {
                //省略
            }
        }
    }
```

这里呢, 生成一个 selecttionKey 就结束了.

3.3 Selector 选择器

我们就接着 netty 服务启动流程最后一步来继续解释其含义.

Selector 允许一个单一的线程来操作多个 Channel. 如果我们的应用程序中使用了多个 Channel, 那么使用 Selector 很方便的实现这样的目的, 但是因为在一个线程中使用了多个 Channel, 因此也会造成了每个 Channel 传输效率的降低.  
使用 Selector 的图解如下:

![](https://img-blog.csdnimg.cn/20190528100248637.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

为了使用 Selector, 我们首先需要将 Channel 注册到 Selector 中, 随后调用 Selector 的 select() 方法, 这个方法会阻塞, 直到注册在 Selector 中的 Channel 发送可读写事件. 当这个方法返回后, 当前的这个线程就可以处理 Channel 的事件了.

```
NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
        if (selectorProvider == null) {
            throw new NullPointerException("selectorProvider");
        } else if (strategy == null) {
            throw new NullPointerException("selectStrategy");
        } else {
            this.provider = selectorProvider;
            NioEventLoop.SelectorTuple selectorTuple = this.openSelector();
            this.selector = selectorTuple.selector;
            this.unwrappedSelector = selectorTuple.unwrappedSelector;
            this.selectStrategy = strategy;
        }
    }
```

在初始化 NioEventLoopGroup 的时候, 初始化了一个 selector 选择器, 在有 channel 进来的时候, 注册到这个 selector 上面来. 在注册完成以后生成一个 SelectionKey, 这个 key 是什么呢?

SelectionKey 包含如下内容:

*   interest set, 即我们感兴趣的事件集, 即在调用 register 注册 channel 时所设置的 interest set.
    
*   ready set
    
*   channel
    
*   selector
    
*   attached object, 可选的附加对象
    

Selector 大致流程如下:

     1. 通过 Selector.open() 打开一个 Selector. 

     2. 将 Channel 注册到 Selector 中, 并设置需要监听的事件 (interest set)

     3. 不断重复:

          1. 调用 select() 方法

          2. 调用 selector.selectedKeys() 获取 selected keys

          3. 迭代每个 selected key:

          4. 从 selected key 中获取 对应的 Channel 和附加信息 (如果有的话)

              判断是哪些 IO 事件已经就绪了, 然后处理它们. `如果是 OP_ACCEPT 事件, 获取 SocketChannel, 并将它设置为 非阻塞的, 然后将这个 Channel 注册到 Selector 中.`

接下来我们进入到源码里面观察 selector 的操作流程

```
protected void run() {
        while(true) {
            while(true) {
                //省略
        }
    }
```

这里是 2 个死循环, 一直校验是否有新的客户端链接或者新的任务是否需要执行.

而这个 run 的启动是在 SingleThreadEventExecutor 中的 execute 方法中开启的线程.

```
switch(this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks())) {
                    case -2:
                        continue;
                    case -1:
                        this.select(this.wakenUp.getAndSet(false));
                        if (this.wakenUp.get()) {
                            this.selector.wakeup();
                        }
```

```
SingleThreadEventExecutor类里面维护了一个队列

```

```
private final Queue<Runnable> taskQueue;

```

这是一个任务队列, 是在上面的这个类里面执行的 execute 的方法, 把需要执行的 task 添加到队列里面去, 以备在 selector 选择的时候从队列里面取出来执行, 每一个 task 都是事先 Runnable 接口的, 都是一个单独的线程.

```
public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        } else {
            boolean inEventLoop = this.inEventLoop();
            if (inEventLoop) {
                this.addTask(task);
            } else {
                this.startThread();
                this.addTask(task);
                if (this.isShutdown() && this.removeTask(task)) {
                    reject();
                }
            }
 
        }
    }
```

```
switch(this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks())) {
                    case -2:
                        continue;
                    case -1:
                        this.select(this.wakenUp.getAndSet(false));
                        if (this.wakenUp.get()) {
                            this.selector.wakeup();
                        }
```

先判断任务队列里面是否有任务, 如果没有任务, 则调用 select 阻塞, 等待 IO 事件就绪.

```
default:
                        this.cancelledKeys = 0;
                        this.needsToSelectAgain = false;
                        int ioRatio = this.ioRatio;
                        if (ioRatio == 100) {
                            try {
                                this.processSelectedKeys();
                            } finally {
                                this.runAllTasks();
                            }
                        } else {
                            long ioStartTime = System.nanoTime();
                            boolean var13 = false;
 
                            try {
                                var13 = true;
                                this.processSelectedKeys();
                                var13 = false;
                            } finally {
                                if (var13) {
                                    long ioTime = System.nanoTime() - ioStartTime;
                                    this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                                }
                            }
 
                            long ioTime = System.nanoTime() - ioStartTime;
                            this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                        }
```

这段代码里面出现了一个 ioRation 的变量, 它表示的是此线程分配给 IO 操作所占的时间比 (即运行 processSelectedKeys 耗时在整个循环中所占用的时间), 假如总共是 100,IO 操作占用 70, 那么 task 的操作就只能占用 30, 从上面的代码中也可以看到, 如果这个变量值不是 100, 就会计算 io 操作消耗的时间, 然后计算剩余的时间去执行 task 任务. 如果 ioRation 占用 100, 也就是说占用满了, 就直接执行 processSelectedKeys 方法和 runAllTasks() 方法.

接下来就是 Selector 选择器重要的部分了

```
private void processSelectedKeys() {
        if (this.selectedKeys != null) {
            this.processSelectedKeysOptimized();
        } else {
            this.processSelectedKeysPlain(this.selector.selectedKeys());
        }
 
    }
```

调用 processSelectKeys 方法, 这里判断了一个是否存在 selectedKeys, 正常情况下这个值不等于空的, 并且上下两个方法没有多大的差别的.

```
private void processSelectedKeysOptimized() {
        for(int i = 0; i < this.selectedKeys.size; ++i) {
            SelectionKey k = this.selectedKeys.keys[i];
            this.selectedKeys.keys[i] = null;
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            } else {
                NioTask<SelectableChannel> task = (NioTask)a;
                processSelectedKey(k, task);
            }
 
            if (this.needsToSelectAgain) {
                this.selectedKeys.reset(i + 1);
                this.selectAgain();
                i = -1;
            }
        }
 
    }
```

接着调用上面的方法, 我们可以看到是从 selectKeys 中循环获取到的, 上面 SelectionKey 也说到了, 包含的具体的内容, 这里我们取出来的是 attachment 的附加信息, 那么这个附加信息是什么呢?

在 channel 注册过程中, 我们跟踪一下代码可以看到, 附加的就是 NioChannel 对象, 这里我们暂时不说明.

```
private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
        NioUnsafe unsafe = ch.unsafe();
        if (!k.isValid()) {
            //省略
        } else {
            try {
                int readyOps = k.readyOps();
                if ((readyOps & 8) != 0) {
                    int ops = k.interestOps();
                    ops &= -9;
                    k.interestOps(ops);
                    unsafe.finishConnect();
                }
 
                if ((readyOps & 4) != 0) {
                    ch.unsafe().forceFlush();
                }
 
                if ((readyOps & 17) != 0 || readyOps == 0) {
                    unsafe.read();
                }
            } catch (CancelledKeyException var7) {
                unsafe.close(unsafe.voidPromise());
            }
 
        }
    }
```

![](https://img-blog.csdnimg.cn/20190528113143493.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

这里就是真正开始执行业务逻辑的地方了, SelectionKey 中也定义了 4 中事件, 如上图所示.

在 processSelectedKey 方法中, 首先从 selectionKey 中获取 ready set, 根据具体数值判断就绪的是什么事件,=16 就是 accept 事件,=1 就是 read,=4 就是 write,=8 就是 connect.

```
ChannelConfig config = AbstractNioMessageChannel.this.config();
            ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            Handle allocHandle = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
            allocHandle.reset(config);
            boolean closed = false;
            Throwable exception = null;
 
            try {
 
                        allocHandle.incMessagesRead(localRead);
                    } while(allocHandle.continueReading());
                } catch (Throwable var11) {
                    exception = var11;
                }
 
                localRead = this.readBuf.size();
 
                for(int i = 0; i < localRead; ++i) {
                    AbstractNioMessageChannel.this.readPending = false;
                    pipeline.fireChannelRead(this.readBuf.get(i));
                }
 
                this.readBuf.clear();
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
```

分配 ByteBuf, 从 SocketChannel 中读取数据, 调用 pipeline.fireChannelRead 发送一个 inbound 事件.

接下来我们分析一下当 websocket 链接进来以后的流程操作

```
protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(this.javaChannel());
 
        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable var6) {
            //省略
        }
 
        return 0;
    }
```

这里就是接受 accept 的地方, 并且生成一个 socketchannel, 接下来就是初始化 unsafe 和 pipeline, 然后把 channel 注册到 selector 中.

看下这个链接的操作是如何绑定到工作线程组的

```
public void channelRead(ChannelHandlerContext ctx, Object msg) {
            final Channel child = (Channel)msg;
            child.pipeline().addLast(new ChannelHandler[]{this.childHandler});
            AbstractBootstrap.setChannelOptions(child, this.childOptions, ServerBootstrap.logger);
            Entry[] var4 = this.childAttrs;
            int var5 = var4.length;
 
            for(int var6 = 0; var6 < var5; ++var6) {
                Entry<AttributeKey<?>, Object> e = var4[var6];
                child.attr((AttributeKey)e.getKey()).set(e.getValue());
            }
 
            try {
                this.childGroup.register(child).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ServerBootstrap.ServerBootstrapAcceptor.forceClose(child, future.cause());
                        }
 
                    }
                });
            } catch (Throwable var8) {
                forceClose(child, var8);
            }
 
        }
```

这个是 ServerBootStrap 中的 channel read 方法, 首先把相关的 handler 设置进去, 接下里的 this.childGroup 就是在启动的时候初始化进去的 workerGroup, 这里就把工作线程组和 IO 操作关联起来了, 接下来的操作就是注册到 selector 中, 上面已经描述过了.

![](https://img-blog.csdnimg.cn/20190528140745884.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

 这是客户端链接建立以后注册到 selector 时, set 的附加信息就是 NioSocketChannel, 正好对应 NioEventLoop 的 run 的执行方法选择.

![](https://img-blog.csdnimg.cn/20190528141020124.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

从上图我们可以看到当我们建立链接以后, 就会把这个 channel 关联的 io 操作放到 task 任务里面.

看完 IO 操作相关的以后, 我们再看下 EventLoop 中的 runAllTasks 方法, 这个就是执行任务队列里面的待执行的任务列表

```
protected boolean runAllTasks() {
        boolean ranAtLeastOne = false;
        boolean fetchedAll;
        do {
            fetchedAll = this.fetchFromScheduledTaskQueue();
            if (this.runAllTasksFrom(this.taskQueue)) {
                ranAtLeastOne = true;
            }
        } while(!fetchedAll);
 
        this.afterRunningAllTasks();
        return ranAtLeastOne;
    }
```

`fetchFromScheduledTaskQueue这个方法呢就是取出所有到了特定执行时间的Schedule的task任务,放到task队列里面,等待被取出执行.`

3.4 ChannelHandler

![](https://img-blog.csdnimg.cn/20190528141732585.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

如果我们要实现自己的业务处理逻辑, 就需要实现这个接口, 当然了, 我们不能直接实现它, 而是实 ChannelInboundHandlerAdapter 这个适配器类, 在 ChannelHandler 上层还有一个继承了它的接口 ChannelInboundHandler, 事件方法如下图:![](https://img-blog.csdnimg.cn/20190528142008249.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

对应的解释:

![](https://img-blog.csdnimg.cn/2019052814205713.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

在 ChannelHandler 的上层继承接口中有这么 2 个接口, 如下图

![](https://img-blog.csdnimg.cn/20190528142245357.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

InboundHandler 和 OutboundHandler, 下图是展示在 pipeline 中的事件流动方向:

![](https://img-blog.csdnimg.cn/20190528142405846.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

![](https://img-blog.csdnimg.cn/20190528142428491.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

Inbound 是按照放到 pipeline 的从上往下的方向流动, outBound 则是相反, inbound 就像是数据的读取如 read readComplete, 而 outbound 就像是写出操作, 如 write flush.

那么什么是 pipeline 呢?

ChannelPipeline 实际上应该叫做 ChannelHandlerPipeline，可以把 ChannelPipeline 看成是一个 ChandlerHandler 的链表，当需要对 Channel 进行某种处理的时候，Pipeline 负责依次调用每一个 Handler 进行处理。每个 Channel 都有一个属于自己的 Pipeline，调用 Channel#pipeline() 方法可以获得 Channel 的 Pipeline，调用 Pipeline#channel() 方法可以获得 Pipeline 的 Channel。

Pipeline 是什么时候初始化的呢?

```
protected AbstractChannel(Channel parent) {
        this.parent = parent;
        this.id = this.newId();
        this.unsafe = this.newUnsafe();
        this.pipeline = this.newChannelPipeline();
    }
```

在 AbstractChannel 的构造函数中初始化 pipeline 的, 沿着 new 方法继续往下查询,

```
protected DefaultChannelPipeline(Channel channel) {
        this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
        this.succeededFuture = new SucceededChannelFuture(channel, (EventExecutor)null);
        this.voidPromise = new VoidChannelPromise(channel, true);
        this.tail = new DefaultChannelPipeline.TailContext(this);
        this.head = new DefaultChannelPipeline.HeadContext(this);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }
```

就到了 DefaultChannelPipeline 的构造方法, pipeline 维护着 add 进去的所有 handler, 从上面我们可以看到有 head 和 tail 这 2 个变量, 这两个就是 pipeline 链表的头和尾, 默认初始化的, 结构如下图所示:

![](https://img-blog.csdnimg.cn/20190528143036980.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

在引导启动的时候我们看到了一个方法

```
.childHandler(new WebsocketChatServerInitializer())

```

这里就是设置我们自己业务逻辑的地方, 实现了 ChannelInitializer 这个抽象类, 当链接进来以后, 注册完毕, 就会执行 initChannel 方法, 初始化我们自己设置的 channelHandler.

接下来就来看看我们自己的实现逻辑:

```
public void initChannel(SocketChannel ch) throws Exception {//2
		ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64*1024));
		pipeline.addLast(httpRequestHandler);
		pipeline.addLast(new WebSocketServerProtocolHandler("/sockjs", true));
		pipeline.addLast(textWebSocketFrameHandler);
	}
```

addLast 的前 2 个是处理 http 操作, 我们这里暂时不讲, 等下面再详细分析.

`WebSocketServerProtocolHandler,它负责websocket握手以及处理控制框架（Close，Ping（心跳检检测request），Pong（心跳检测响应））,文本和二进制数据帧被传递到管道中的下一个处理程序进行处理.并且执行完这个handler以后,会移除合和替换一些handler.`

```
public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            ctx.pipeline().addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.websocketPath, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch, this.checkStartsWith));
        }
    }
```

在其中的 Added 方法中, 会 new 一个 WebSocketServerProtocolHandshakeHandler, 在这个 handler 里面最终会调用

```
ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);

```

```
 
    public final ChannelFuture handshake(Channel channel, FullHttpRequest req, HttpHeaders responseHeaders, final ChannelPromise promise) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} WebSocket version {} server handshake", channel, this.version());
        }
 
        FullHttpResponse response = this.newHandshakeResponse(req, responseHeaders);
        ChannelPipeline p = channel.pipeline();
        if (p.get(HttpObjectAggregator.class) != null) {
            p.remove(HttpObjectAggregator.class);
        }
 
        if (p.get(HttpContentCompressor.class) != null) {
            p.remove(HttpContentCompressor.class);
        }
 
        ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
        final String encoderName;
        if (ctx == null) {
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
                promise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
                return promise;
            }
 
            p.addBefore(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
            p.addBefore(ctx.name(), "wsencoder", this.newWebSocketEncoder());
            encoderName = ctx.name();
        } else {
            p.replace(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
            encoderName = p.context(HttpResponseEncoder.class).name();
            p.addBefore(encoderName, "wsencoder", this.newWebSocketEncoder());
        }
```

在这个方法里面, 会移除 http 相关的 handler, 并且把 http 的编解码 handler 升级为 websocket 的编解码 handler.

textWebSocketFrameHandler 就是我们真正具体的业务逻辑处理的 handler, 实现了

![](https://img-blog.csdnimg.cn/20190528145342769.png)

我们可以看到泛型中的 TextWebSocketFrame, 那么这个具体是什么数据类型呢?

WebSocket 规范中定义了 6 种类型的桢，netty 为其提供了具体的对应的 POJO 实现。  
WebSocketFrame：所有桢的父类，所谓桢就是 WebSocket 服务在建立的时候，在通道中处理的数据类型。本列子中客户端和服务器之间处理的是文本信息。所以范型参数是 TextWebSocketFrame.

![](https://img-blog.csdnimg.cn/20190528145446371.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

到这里我们的 websocket 业务逻辑也就写完了, 接下来我们在分析一下上述未讲解的 http 的 handler.

我们的项目不仅有 wss 协议的, 也有 http 协议的请求, 如果要处理 http 的请求, 就需要 HttpServerCodec 和 HttpObjectAggregator 这 2 个处理器.

HttpServerCodec 是 netty 针对 http 编解码的处理类.

但是这些只能处理像 http get 的请求, 也就是数据带在 url 后面的 http 请求, 如果是像 post 的请求呢, message 是在 body 里面的.

下面贴一下 http get 和 post 的请求格式:

![](https://img-blog.csdnimg.cn/20190528150044725.png)

![](https://img-blog.csdnimg.cn/20190528150052142.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

那么 HttpObjectAggregator 这个 netty 的处理器就是为了解决这个问题而来的. 它把 HttpMessage 和 HttpContent 聚合成为一个 FullHttpRquest 或者 FullHttpRsponse, 大致结构如下图所示:

![](https://img-blog.csdnimg.cn/20190528150504735.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3prMXoyMzQ1Njc4OQ==,size_16,color_FFFFFF,t_70)

```
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { 
    //省略部分代码,只显示大致结构
 
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
```

上面这个就是我们自己实现的处理 http 的 hanlder, 数据类型就是 FullHttpRequest.

上面的整个流程就是基于 netty 实现的, 简要描述了 netty 处理 http 和 websocket 的大致流程, 如有错误地方, 希望大家提出意见, 谢谢!!!

**备注: 关于 ByteBuf 导致内存泄露的问题**

**从 netty 4.0 开始, ByteBuf 的生命周期, 不再有垃圾收集器管理了, 而是有引用计数器管理.**

**对于 netty Inbound message，当 event loop 读入了数据并创建了 ByteBuf，并用这个 ByteBuf 触发了一个 channelRead() 事件时，那么管道（pipeline）中相应的 ChannelHandler 就负责释放这个 buffer 。因此，处理接数据的 handler 应该在它的 channelRead() 中调用 buffer 的 release().**

**对于 netty Outbound message，你的程序所创建的消息对象都由 netty 负责释放，释放的时机是在这些消息被发送到网络之后。但是，在发送消息的过程中，如果有 handler 截获（intercept）了你的发送请求并创建了一些中间对象，则这些 handler 要确保正确释放这些中间对象.**

**__而有时候，ByteBuf 会被一个 buffer holder 持有，它们都扩展了一个公共接口 ByteBufHolder。正因如此， ByteBuf 并不是 netty 中唯一一种引用计数对象。由 decoder 生成的消息对象很可能也是引用计数对象，比如 HTTP 协议栈中的 HttpContent，因为它也扩展了 ByteBufHolder。__**

**__WebSocketFrame 就实现了 ByteBufHolder, 持有 ByteBuf 的数据, 所以 handler 的实现这里我们使用的是__ SimpleChannelInboundHandler**
