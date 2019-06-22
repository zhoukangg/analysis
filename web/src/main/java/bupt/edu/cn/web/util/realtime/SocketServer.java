package bupt.edu.cn.web.util.realtime;

import bupt.edu.cn.web.WebApplication;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static bupt.edu.cn.web.WebApplication.getApplicationContext;

@ServerEndpoint(value = "/socketServer/{cockpitId}")
@Component
public class SocketServer{

//	private static ApplicationContext applicationContext;
	rtAction rtaction;
//
//	public static void setApplicationContext(ApplicationContext applicationContext){
//		SocketServer.applicationContext = applicationContext;
//	}

	private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

	public static CopyOnWriteArraySet<CockpitListener> getSocketServers() {
		return socketServers;
	}

	/**
	 *
	 * 用线程安全的CopyOnWriteArraySet来存放客户端连接的信息
	 */
	private static CopyOnWriteArraySet<CockpitListener> socketServers = new CopyOnWriteArraySet<>();
	/**
	 *
	 * websocket封装的session,信息推送，就是通过它来信息推送
	 */
	private Session session;


	public static Map<String, FileAlterationMonitor> FileAlterationMonitors = new ConcurrentHashMap<>();
	public static Map<String, Integer> FilesCount = new ConcurrentHashMap<>();

	/**
	 *
	 * 服务端的cockpitId,因为用的是set，每个客户端的cockpitId必须不一样，否则会被覆盖。
	 * 要想完成ui界面聊天的功能，服务端也需要作为客户端来接收后台推送用户发送的信息
	 */
	private final static int SYS_USERNAME = -1;


	//创建文件变化监听器
	private static FileAlterationMonitor monitor;


	/**
	 *
	 * 用户连接时触发，我们将其添加到
	 * 保存客户端连接信息的socketServers中
	 *
	 * @param session
	 * @param cockpitId
	 */
	@OnOpen
	public void open(Session session, @PathParam(value="cockpitId")int cockpitId){
		rtaction = getApplicationContext().getBean(rtAction.class);
		logger.info("监听CockpitIdL：" + cockpitId + "!!!!!");
		this.session = session;
		CockpitListener cpl = new CockpitListener(new Client(cockpitId, session));
//		new rtAction().getAllPath(cpl);
		rtaction.getAllPath(cpl);
		socketServers.add(cpl);
		observerFile(cpl);
		logger.info("客户端:【{}】连接成功",cockpitId);
	}

	public void observerFile(CockpitListener cpl){
		try {
			// 创建过滤器
			for (int i = 0; i < cpl.diagrams.size(); i++) {
				boolean flag = true;
				String keyName = cpl.dataSources.get(i).getFileUrl();
				if (FilesCount.containsKey(keyName)){
					continue;
				}
				String[] cut = cpl.dataSources.get(i).getFileUrl().split("\\/");
				String filename = cut[cut.length-1];
				// 监控目录
				String rootDir = cpl.dataSources.get(i).getFileUrl().substring(0,cpl.dataSources.get(i).getFileUrl().length()-filename.length()-1);
				// 轮询间隔 5 秒
				long interval = TimeUnit.SECONDS.toMillis(1);
				IOFileFilter directories = FileFilterUtils.and(
						FileFilterUtils.directoryFileFilter(),
						HiddenFileFilter.VISIBLE);
				IOFileFilter files = FileFilterUtils.and(
					FileFilterUtils.fileFileFilter(),
					FileFilterUtils.suffixFileFilter(filename));
				IOFileFilter filter = FileFilterUtils.or(directories, files);
				FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir), filter);
				//不使用过滤器
				//FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir));
				observer.addListener(new FileListener());
				//创建文件变化监听器
				logger.info("开始监听文件");
				monitor = new FileAlterationMonitor(interval, observer);
				// 开始监控
				FilesCount.put(keyName,1);
				FileAlterationMonitors.put(keyName,monitor);
				monitor.start();
			}
		}catch (Exception e){
			logger.info(cpl+"","监听文件出错");
		}
	}

	/**
	 *
	 * 收到客户端发送信息时触发
	 * 我们将其推送给客户端(niezhiliang9595)
	 * 其实也就是服务端本身，为了达到前端聊天效果才这么做的
	 *
	 * @param message
	 */
	@OnMessage
	public void onMessage(String message){

		CockpitListener cockpitListener = socketServers.stream().filter( cpListener -> cpListener.client.getSession() == session)
				.collect(Collectors.toList()).get(0);
		sendMessage(cockpitListener.client.getCockpitId()+"<--"+message,SYS_USERNAME);

		logger.info("客户端:【{}】发送信息:{}",cockpitListener.client.getCockpitId(),message);
	}

	/**
	 *
	 * 连接关闭触发，通过sessionId来移除
	 * socketServers中客户端连接信息
	 */
	@OnClose
	public void onClose(){
		socketServers.forEach(cockpitListener ->{
			if (cockpitListener.client.getSession().getId().equals(session.getId())) {
				logger.info("客户端:【{}】断开连接",cockpitListener.client.getCockpitId());
				socketServers.remove(cockpitListener);

				cockpitListener.dataSources.forEach(ds->{
					if(FilesCount.get(ds.getFileUrl())<=1){
						FilesCount.remove(ds.getFileUrl());
						try{
							FileAlterationMonitors.get(ds.getFileUrl()).stop();
							FileAlterationMonitors.remove(ds.getFileUrl());
						}catch (Exception e){
							e.printStackTrace();
						}

					}else {
						FilesCount.put(ds.getFileUrl(),FilesCount.get(ds.getFileUrl())-1);
					}

				});
//				// 关闭监控
//				try {
//					for (int i = 0; i < cockpitListener.FileAlterationMonitors.size(); i++) {
//						monitor.stop();
//						logger.info("关闭监听器" + i);
//					}
//				}catch (Exception e){
//					logger.info("关闭监听器出错");
//				}
			}
		});
	}

	/**
	 *
	 * 发生错误时触发
	 * @param error
	 */
    @OnError
    public void onError(Throwable error) {
		socketServers.forEach(cockpitListener ->{
			if (cockpitListener.client.getSession().getId().equals(session.getId())) {
				socketServers.remove(cockpitListener);
				logger.error("客户端:【{}】发生异常",cockpitListener.client.getCockpitId());
				error.printStackTrace();
			}
		});
    }

	/**
	 *
	 * 信息发送的方法，通过客户端的userName
	 * 拿到其对应的session，调用信息推送的方法
	 * @param message
	 * @param cockpitId
	 */
	public synchronized static void sendMessage(String message,int cockpitId) {

		socketServers.forEach(cockpitListener ->{
			if (cockpitId == cockpitListener.client.getCockpitId()) {
				try {
					cockpitListener.client.getSession().getBasicRemote().sendText(message);

					logger.info("服务端推送给客户端 :【{}】",cockpitListener.client.getCockpitId(),message);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 *
	 * 获取服务端当前客户端的连接数量，
	 * 因为服务端本身也作为客户端接受信息，
	 * 所以连接总数还要减去服务端
	 * 本身的一个连接数
	 *
	 * 这里运用三元运算符是因为客户端第一次在加载的时候
	 * 客户端本身也没有进行连接，-1 就会出现总数为-1的情况，
	 * 这里主要就是为了避免出现连接数为-1的情况
	 *
	 * @return
	 */
	public synchronized static int getOnlineNum(){
		return socketServers.stream().filter(cockpitListener -> !(cockpitListener.client.getCockpitId() == SYS_USERNAME))
				.collect(Collectors.toList()).size();
	}

	/**
	 *
	 * 获取在线用户名，前端界面需要用到
	 * @return
	 */
//	public synchronized static List<String> getOnlineUsers(){
//
//		List<String> onlineUsers = socketServers.stream()
//				.filter(client -> !client.getCockpitId().equals(SYS_USERNAME))
//				.map(client -> client.getCockpitId())
//				.collect(Collectors.toList());
//
//	    return onlineUsers;
//	}

	/**
	 *
	 * 信息群发，我们要排除服务端自己不接收到推送信息
	 * 所以我们在发送的时候将服务端排除掉
	 * @param message
	 */
	public synchronized static void sendAll(String message) {
		//群发，不能发送给服务端自己
		socketServers.stream().filter(cockpitListener -> cockpitListener.client.getCockpitId() != SYS_USERNAME)
				.forEach(cockpitListener -> {
			try {
				cockpitListener.client.getSession().getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		logger.info("服务端推送给所有客户端 :【{}】",message);
	}

	/**
	 *
	 * 多个人发送给指定的几个用户
	 * @param message
	 * @param ids
	 */
	public synchronized static void SendMany(String message,int [] ids) {
		for (int cockpitId : ids) {
			sendMessage(message,cockpitId);
		}
	}


	public synchronized static void sendResult(String message,String userName) {
		socketServers.forEach(cockpitListener ->{
			if (userName.equals(cockpitListener.client.getCockpitId())) {
				try {
					cockpitListener.client.getSession().getBasicRemote().sendText(message);

					logger.info("服务端推送给客户端 :【{}】",cockpitListener.client.getCockpitId(),message);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}


}
