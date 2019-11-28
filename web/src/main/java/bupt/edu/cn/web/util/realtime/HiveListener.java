package bupt.edu.cn.web.util.realtime;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.service.HiveService;
import bupt.edu.cn.web.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program analysis
 * @description: ${TODO}
 * @author: kang
 * @create: 2019/07/07 17:16
 */

@Service
public class HiveListener {

    @Autowired
    DiagramRepository diagramRepository;

    @Autowired
    QueryService queryService;

    @Autowired
    HiveService hiveService;

    @Autowired
    RtAction ra;
    public static Map<String, Long> FileChangeDate = new ConcurrentHashMap<>();

    //
    public void exeTimeInterval(String hiveChangeUrl){
        if (!FileChangeDate.containsKey(hiveChangeUrl)){
            FileChangeDate.put(hiveChangeUrl, System.currentTimeMillis());
            hiveCam(hiveChangeUrl);
            SocketServer.sendAll("");
        }
    }

    // 20分钟执行一次
    @Bean
    public void removeTimeInterval() {
        System.out.println("-----开始清除任务-----");
        RunnableDemo R1 = new RunnableDemo( "Thread-清除任务");
        R1.start();
    }

    public void hiveCam(String hiveChangeUrl) {
        if(SocketServer.FilesCount.containsKey(hiveChangeUrl) && SocketServer.FilesCount.get(hiveChangeUrl) > 0){
            CopyOnWriteArraySet<CockpitListener> socketServers = SocketServer.getSocketServers();
            socketServers.forEach(cockpitListener ->{
                for (int i = 0; i < cockpitListener.diagrams.size(); i++) {
                    System.out.println("开始文件改变任务" + i);
                    String fileUrl = cockpitListener.dataSources.get(i).getFileUrl();
                    if (hiveChangeUrl.equals(fileUrl)){
                        String sql = cockpitListener.diagramSqls.get(i).getSqlinfo();
                        fileUrl = fileUrl.substring(0, fileUrl.length()-4);
                        List<Map> listJson = queryData(fileUrl, sql);
                        Diagram oldDiagram = cockpitListener.diagrams.get(i);
                        ra.generateOption(listJson, oldDiagram, cockpitListener.diagramSqls.get(i));
                        System.out.println("-----------");
                        System.out.println(cockpitListener.diagrams.get(i).getChart());
                        diagramRepository.saveAndFlush(cockpitListener.diagrams.get(i));
                    }
                }
                ra.updateCockpit(cockpitListener.cockpitId);
            });
        }
    }

    public List<Map> queryData(String fileUrl, String sql) {
        List<Map> listJson = new ArrayList<>();
        try{
            listJson = hiveService.selectData(fileUrl.split("/")[0],sql);
        }catch (Exception e){
            System.out.println("出错了："+e.toString());
        }
        return listJson;
    }
}
