package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.Story;
import bupt.edu.cn.web.pojo.StoryItem;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.repository.StoryItemRepository;
import bupt.edu.cn.web.repository.StoryRepository;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peaceful.auth.sdk.spring.AUTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import bupt.edu.cn.web.pojo.Story;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
public class StoryController {
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    StoryItemRepository storyItemRepository;
    @Autowired
    DiagramRepository diagramRepository;

    @AUTH.RequireLogin
    @AUTH.Role({"数据分析师","超级管理员"})
    @RequestMapping("/findAllStory")
    public ReturnModel findAllStory(String userId, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------findAllStory-----------");;
        System.out.println("userId = " + userId);

        ReturnModel result = new ReturnModel();
        JSONArray storys = new JSONArray(); //存储story
        List<Story> stroyList = storyRepository.findByUserId(userId);
        for (int i = 0; i<stroyList.size();i++){
            JSONArray story = new JSONArray(); //存储storyItem
            Long stroyId  = stroyList.get(i).getId();
            List<StoryItem> storyItemList = storyItemRepository.findByStoryId(stroyId.toString());
            for (int j = 0; j<storyItemList.size();j++){
                JSONObject storyItem = new JSONObject(); //存储option 和 description
//                if (!((storyItemList.get(i).getDiagramId().equals("")) || (storyItemList.get(i).getDiagramId().equals(" ")) || (storyItemList.get(i).getDiagramId() == null))){
//
//                }
                Optional<Diagram> diagramOptional = diagramRepository.findById(Long.valueOf(storyItemList.get(j).getDiagramId()));
                Diagram diagram = new Diagram();
                if (diagramOptional.isPresent()){
                    diagram = diagramOptional.get();
                    storyItem.put("diagramId",diagram.getId());
                    storyItem.put("option",JSON.parseObject(diagram.getChart()));
                    storyItem.put("description",storyItemList.get(j).getDescription());
                }else {
//                    result.setResult(false);
//                    result.setReason("数据库存储的diagram缺失");
//                    return result;
                    storyItem.put("diagramId","");
                    storyItem.put("option","");
                    storyItem.put("description",storyItemList.get(j).getDescription());
                }

                story.add(storyItem);
            }
            JSONObject ss = new JSONObject();
            ss.put("storyName",stroyList.get(i).getStoryName());
            ss.put("storyId",stroyList.get(i).getId());
            ss.put("content",story);
            storys.add(ss);
        }
        result.setResult(true);
        result.setDatum(storys);
        return result;
    }

    @AUTH.RequireLogin
    @AUTH.Role({"数据分析师","超级管理员"})
    @ResponseBody
    @RequestMapping(value = "/createStory", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ReturnModel createStory(@RequestParam(value="content") String content,@RequestParam(value="storyName") String storyName,@RequestParam(value="userId") String userId,  HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------createStory-------------");
        System.out.println("content = " + content);
        System.out.println("storyName = " + storyName);
        System.out.println("userId = " + userId);

        JSONArray conArr = JSON.parseArray(content);
//        for (int i=0;i<conArr.size();i++){
//            System.out.println("conArr = " + conArr.getString(i));
//        }
//        System.out.println("conArr = " + conArr.size());

        ReturnModel result = new ReturnModel();

        //数据库创建一个story
        Story story = new Story();
        story.setUserId(userId);
        story.setContent("init");
        story.setStoryName(storyName);
        Story ss = storyRepository.saveAndFlush(story);
        String storyId = ss.getId().toString();
        System.out.println("create storyId = " + storyId);

        //根据content，数据库创建storyItem
        for (int i = 0; i<conArr.size();i++){
            JSONObject jsonStoryItem = conArr.getJSONObject(i);
            Long diagramId = jsonStoryItem.getLong("diagramId");
            String description = jsonStoryItem.getString("description");
            StoryItem storyItem = new StoryItem();
            storyItem.setDescription(description);
            storyItem.setDiagramId(diagramId.toString());
            storyItem.setStoryId(storyId);
            StoryItem SI = storyItemRepository.saveAndFlush(storyItem);

        }

        result.setResult(true);
        return result;
    }

    //
    @AUTH.RequireLogin
    @AUTH.Role({"数据分析师","超级管理员"})
    @RequestMapping("/updateStory")
    public ReturnModel updateStory(@RequestParam(value="storyId") String storyId,@RequestParam(value="content") String content,
                                   @RequestParam(value="storyName") String storyName,@RequestParam(value="userId") String userId,
                                   HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------updateDiagram-----------");
        System.out.println("storyId = " + storyId);
        System.out.println("content = " + content);
        System.out.println("storyName = " + storyName);
        System.out.println("userId = " + userId);

        ReturnModel result = new ReturnModel();
        JSONArray conArr = JSON.parseArray(content);

        //根据ID查找一个story
        Optional<Story> oStory = storyRepository.findById(Long.valueOf(storyId));
        if (oStory.isPresent()){
            //数据库更新story
            Story story = oStory.get();
            story.setStoryName(storyName);
            storyRepository.saveAndFlush(story);
        }else {
            result.setResult(false);
            result.setReason("不存在该story");
            return result;
        }
        //删除对应的storyItem，以便重新建立
        storyItemRepository.deleteByStoryId(storyId);
        //根据content，数据库创建storyItem
        for (int i = 0; i<conArr.size();i++){
            JSONObject jsonStoryItem = conArr.getJSONObject(i);
            Long diagramId = jsonStoryItem.getLong("diagramId");
            String description = jsonStoryItem.getString("description");
            StoryItem storyItem = new StoryItem();
            storyItem.setDescription(description);
            storyItem.setDiagramId(diagramId.toString());
            storyItem.setStoryId(storyId);
            storyItemRepository.saveAndFlush(storyItem);

        }

        result.setResult(true);
        return result;
    }

    //
    @AUTH.RequireLogin
    @AUTH.Role({"数据分析师","超级管理员"})
    @RequestMapping("/delectStory")
    public ReturnModel delectStory(String storyId, String userId, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------delectStory-----------");
        System.out.println("storyId = " + storyId);
        System.out.println("userId = " + userId);

        ReturnModel result = new ReturnModel();
        storyItemRepository.deleteByStoryId(storyId);
        storyRepository.deleteById(Long.valueOf(storyId));

        result.setResult(true);
        return result;
    }
}
