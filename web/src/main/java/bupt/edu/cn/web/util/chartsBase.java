package bupt.edu.cn.web.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import bupt.edu.cn.web.service.NewOptionService;
//用于存储所有类型的表格的初始化格式和所有的Update逻辑
public class chartsBase {
    /*
    * 10笛卡尔积热力图,11:词云图,9:关系图
    * 还有15.交叉表 16.表格
    */
    //7折线图、2面积图
    private String lineBasic = "{'tooltip':{'trigger':'axis'},'title':{'text':'title','left':'center'},'legend':{'data':[],'left':'left',orient:'vertical'},'xAxis':{'type':'category','data':[]},'yAxis':{'type':'value'},'color':[],'series':[{'type':'line','name':'','smooth':true,'itemStyle':{'normal':{}}}]}";
    private String areaBasic = "{'tooltip':{'trigger':'axis'},'title':{'text':'title','left':'center'},'legend':{'data':[]},'xAxis':{'type':'category','boundaryGap':false,'data':[]},'yAxis':{'type':'value'},'color':[],'series':[{'name':'','type':'line','smooth':true,'itemStyle':{'normal':{'areaStyle':{'type':'default'}}},'data':[]}]}";

    //0柱状图、6条形图、1堆积图
    private String barBasic = "{'tooltip':{'trigger':'axis'},'title':{'text':'title','left':'center'},'legend':{'data':[]},'xAxis':{'type':'category','data':[]},'yAxis':{'type':'value'},'color':[],'series':[{'name':'','type':'bar','data':[]}]}";
    private String linebarBasic = "{'tooltip':{'trigger':'axis'},'title':{'text':'title','left':'center'},'legend':{'data':[]},'yAxis':{'type':'category','data':[]},'xAxis':{'type':'value'},'color':[],'series':[{'name':'','stack':'stack','type':'bar','data':[]}]}";
    private String stackbarBasic = "{'tooltip':{'trigger':'axis'},'title':{'text':'title','left':'center'},'legend':{'data':[]},'xAxis':{'type':'category','data':[]},'yAxis':{'type':'value'},'color':[],'series':[{'name':'','stack':'stack','type':'bar','data':[]}]}";

    //3饼图
    private String pieBasic = "{title:{text:'title',left:'center'},legend:{data:[],left:'left',orient:'vertical'},tooltip:{trigger:'item',formatter:'{b}:{c}({d}%)'},color:[],series:[{name:'','radius':'55%','center': ['50%','60%'],type:'pie',data:[]}]}";

    //4雷达图      title,legend.data,radar.indicator,series.data NEED TO BE INITIALIZED
    private String radarBasic  = "{'title':{'text':'标题','left':'center'},'tooltip':{},'legend':{'data':[],'left':'left',orient:'vertical'},'color':[],'radar':{'name':{'textStyle':{'color':'#fff','backgroundColor':'#999','borderRadius':3,'padding':[3,5]}},'indicator':[]},'series':[{'name':'','type':'radar','data':[]}]}";

    //12散点图
    private String scatterBasic = "{'tooltip':{'trigger':'axis'},   'title':{'text':'title','left':'center'},'legend':{'data':[]},'xAxis':{'type':'category','boundaryGap':false,'data':[]},'yAxis':{'type':'value'},'color':[],'series':[{'name':'','stack':'stack','type':'scatter','smooth':true,'data':[]}]}";

    //13漏斗图
    private String funnelBasic = "{title:{text:'title',left:'center'},tooltip:{trigger:'item',formatter:'{a}<br/>{b}:{c}%'},color:[],series:[{name:'',type:'funnel',gap:2,label:{normal:{show:true,position:'inside'},emphasis:{textStyle:{fontSize:20}}},data:[]}]}";

    //8计量图
    private String gaugeBasic = "{'series':[{'data':[{'name':'Transaction_max','value':999}],'max':1176.7953861141796,'axisLine':{'lineStyle':{'color':[[0.3,'#c23531'],[0.7,'#63869e'],[1,'#91c7ae']]}},'name':'Transaction_max','detail':{'formatter':'{value}'},'type':'gauge'}],'tooltip':{'formatter':'{b}:{c}'}}";

    public String transDiagram(int typebefore, int typeafter, String str_option){
        JSONObject newoption = new JSONObject(str_option);

        if (typebefore == 16){              //图表先默认转为面积图或者雷达图
            
        }

        //实现了7,2,12,0,6,3,13,1向其他图的转换(自适应多维度)
        if (typebefore == 7 || typebefore == 2 || typebefore == 12 || typebefore == 4){               //折线图/面积图转：
            if (typebefore == 4){       //雷达图自动先转折线图
                newoption = RadartoLine(newoption);
            }
            if (typebefore == 2)        //面积图自动先转折线图
                for(int i = 0; i < newoption.getJSONArray("series").length();i++){
                    newoption.getJSONArray("series").getJSONObject(i).remove("itemStyle");
                    if (newoption.getJSONObject("xAxis").has("boundaryGap"))
                        newoption.getJSONObject("xAxis").remove("boundaryGap");
                }
            if (typebefore == 12)        //散点图自动先转折线图
                for(int i = 0; i < newoption.getJSONArray("series").length();i++)
                    newoption.getJSONArray("series").getJSONObject(i).put("type","line");
            if (typeafter == 2){            //面积图
                JSONObject itemStyle = new JSONObject("{'normal':{'areaStyle':{'type':'default'}}}");
                for(int i = 0; i < newoption.getJSONArray("series").length();i++){
                    if (!(newoption.getJSONObject("xAxis").has("boundaryGap"))){
                        newoption.getJSONObject("xAxis").put("boundaryGap",false);
                    }
                    newoption.getJSONArray("series").getJSONObject(i).put("itemStyle",itemStyle);
                }

            }else if (typeafter == 0 || typeafter == 1 || typeafter == 6){      //柱状图或者堆积图（取决于数据维度）或条形图
                if (newoption.getJSONObject("xAxis").has("boundaryGap")){
                    newoption.getJSONObject("xAxis").remove("boundaryGap");
                }
                if (newoption.getJSONObject("yAxis").has("boundaryGap")){
                    newoption.getJSONObject("yAxis").remove("boundaryGap");
                }
                if (newoption.getJSONArray("series").length() ==1)
                    newoption.getJSONArray("series").getJSONObject(0).put("type","bar");
                else{
                    for(int i = 0; i < newoption.getJSONArray("series").length();i++) {
                        newoption.getJSONArray("series").getJSONObject(i).put("type","bar");
                        if (typeafter == 1)
                            newoption.getJSONArray("series").getJSONObject(i).put("stack","stack");
                    }
                }
                if (typeafter == 6){
                    JSONObject tempjo = newoption.getJSONObject("yAxis");
                    newoption.put("yAxis",newoption.getJSONObject("xAxis"));
                    newoption.put("xAxis",tempjo);
                }
            }else if (typeafter == 3 || typeafter == 13)  {     //饼图或漏斗图（仅支持一维度）
                newoption = LinetoPie(newoption);
                if (typeafter == 13)
                    newoption.getJSONArray("series").getJSONObject(0).put("type","funnel");
            }else if (typeafter == 4) {     //雷达图
                if (newoption.getJSONArray("series").length() > 1){
                    newoption = LinetoRadar(newoption);
                }else{
                    return "ERROR:一维数据转换为雷达图无意义";
                }
            }else if (typeafter == 12) {    //散点图
                for(int i = 0; i < newoption.getJSONArray("series").length();i++)
                    newoption.getJSONArray("series").getJSONObject(i).put("type","scatter");
            }
        }else if(typebefore == 0 || typebefore == 6 || typebefore == 1){          //柱状图/堆积图/条形图转：
            if (typebefore == 6){       //先把条形图转回柱状图或者堆积图
                JSONObject temp = newoption.getJSONObject("yAxis");
                newoption.put("yAxis",newoption.getJSONObject("xAxis"));
                newoption.put("xAxis",temp);
            }
            if (typebefore == 1){           //去掉堆积图的stack属性
                for (int i = 0;i < newoption.getJSONArray("series").length();i++){
                    if (newoption.getJSONArray("series").getJSONObject(i).has("stack"))
                        newoption.getJSONArray("series").getJSONObject(i).remove("stack");
                }
            }
            if (typeafter == 7 || typeafter ==2){       //折线图或面积图
                for(int i = 0; i < newoption.getJSONArray("series").length();i++) {
                    JSONObject itemStyle = new JSONObject("{'normal':{'areaStyle':{'type':'default'}}}");
                    newoption.getJSONArray("series").getJSONObject(i).put("type", "line");
                    if (typeafter == 2) {
                        if (!(newoption.getJSONObject("xAxis").has("boundaryGap"))){
                            newoption.getJSONObject("xAxis").put("boundaryGap",false);
                        }
                        newoption.getJSONArray("series").getJSONObject(0).put("itemStyle", itemStyle);
                    }
                }
            }else if (typeafter == 6){      //条形图
                JSONObject tempjo = newoption.getJSONObject("yAxis");
                newoption.put("yAxis",newoption.getJSONObject("xAxis"));
                newoption.put("xAxis",tempjo);
            }else if (typeafter == 1){      //堆积图
                for (int i = 0;i < newoption.getJSONArray("series").length();i++){
                    newoption.getJSONArray("series").getJSONObject(i).put("stack","stack");
                }
            }else if (typeafter == 3 || typeafter == 13){       //饼图或漏斗图（仅支持一维度）
                newoption.getJSONArray("series").getJSONObject(0).put("type","line");
                newoption = LinetoPie(newoption);
                if (typeafter == 13)
                    newoption.getJSONArray("series").getJSONObject(0).put("type","funnel");
            }else if (typeafter == 4){      //雷达图
                for(int i = 0; i < newoption.getJSONArray("series").length();i++) {     //先转成折线图
                    newoption.getJSONArray("series").getJSONObject(i).put("type", "line");
                }
                if (newoption.getJSONArray("series").length() > 1){
                    newoption = LinetoRadar(newoption);
                }else{
                    return "ERROR:一维数据转换为雷达图无意义";
                }
            }else if (typeafter == 12) {    //散点图
                for(int i = 0; i < newoption.getJSONArray("series").length();i++) {
                    newoption.getJSONArray("series").getJSONObject(i).put("type","scatter");
                }
            }
        }else if (typebefore == 3 || typebefore == 13){         //饼图/漏斗图转：（仅支持一维度的转换）
            if (typebefore == 13)       //先将漏斗图自动转为饼图
                newoption.getJSONArray("series").getJSONObject(0).put("type","pie");
            if (typeafter == 13){       //漏斗图
                newoption.getJSONArray("series").getJSONObject(0).put("type","funnel");
            }else if(typeafter != 3){
                newoption = PietoLine(newoption);
                if (typeafter == 7 || typeafter ==2){   //折线图或面积图
                    if (typeafter == 2){
                        if (!(newoption.getJSONObject("xAxis").has("boundaryGap"))){
                            newoption.getJSONObject("xAxis").put("boundaryGap",false);
                        }
                        JSONObject itemStyle = new JSONObject("{'normal':{'areaStyle':{'type':'default'}}}");
                        newoption.getJSONArray("series").getJSONObject(0).put("itemStyle",itemStyle);
                    }
                }else if(typeafter == 0){
                    newoption.getJSONArray("series").getJSONObject(0).put("type","bar");
                }else if (typeafter == 6){
                    newoption.getJSONArray("series").getJSONObject(0).put("type","bar");
                    JSONObject tempjo = newoption.getJSONObject("yAxis");
                    newoption.put("yAxis",newoption.getJSONObject("xAxis"));
                    newoption.put("xAxis",tempjo);
                }else if (typeafter == 1){
                    return "ERROR: 一维数据无法转换为堆积图";
                }else if (typeafter == 4){
                    return "ERROR:一维数据转换为雷达图无意义";
                }else if (typeafter == 12){
                    newoption.getJSONArray("series").getJSONObject(0).put("type","scatter");
                }
            }
        }else if (typebefore == 8 || typebefore == 14) {
            if (typeafter == 14){           //仪表盘转指标卡
                Double value = newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").getJSONObject(0).getDouble("value");
                value = new BigDecimal(value).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                String name = newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").getJSONObject(0).getString("name");
                newoption = new JSONObject();
                newoption.put("value",value);
                newoption.put("name",name);
            }else{                          //指标卡转仪表盘
                String name = newoption.getString("name");
                double value = new BigDecimal(newoption.getDouble("value")).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                newoption = new JSONObject(gaugeBasic);
                int numLength = String.valueOf(value).length()-3;
                double maxint = Math.pow(10,numLength);
                newoption.getJSONArray("series").getJSONObject(0).put("name",name);
                newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").getJSONObject(0).put("value",value);
                newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").getJSONObject(0).put("name",name);
                newoption.getJSONArray("series").getJSONObject(0).put("max",maxint);
            }
        }
        return newoption.toString();
    }

    private JSONObject PietoLine(JSONObject newoption){     //Pie转折线
        String title = newoption.getJSONObject("title").getString("text");
        JSONArray dataInfo = newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data");
        JSONArray data = new JSONArray();
        JSONArray name = new JSONArray();
        for (int i =0;i < dataInfo.length();i++){
            data.put(dataInfo.getJSONObject(i).getString("value"));
            name.put(dataInfo.getJSONObject(i).getString("name"));
        }
        newoption = new JSONObject(lineBasic);
        newoption.getJSONObject("title").put("text",title);
        newoption.getJSONObject("xAxis").put("data",name);
        newoption.getJSONArray("series").getJSONObject(0).put("data",data);
        return newoption;
    }

    private JSONObject LinetoPie(JSONObject newoption){     //折线转Pie
        String title = newoption.getJSONObject("title").getString("text");
        JSONArray name = newoption.getJSONObject("xAxis").getJSONArray("data");
        JSONArray data = newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data");
        JSONArray piedata = new JSONArray();
        for (int i = 0; i < name.length(); i++){
            JSONObject tempjo = new JSONObject();
            tempjo.put("value",data.getString(i));
            tempjo.put("name",name.getString(i));
            piedata.put(tempjo);
        }
        newoption = new JSONObject(pieBasic);
        newoption.getJSONObject("title").put("text",title);
        newoption.getJSONObject("legend").put("data",name);
        newoption.getJSONArray("series").getJSONObject(0).put("data",piedata);
        newoption.getJSONArray("series").getJSONObject(0).put("name",title);
        return newoption;
    }

    private JSONObject LinetoRadar(JSONObject newoption){           //折线转雷达
        JSONObject radarOption = new JSONObject(radarBasic);
        radarOption.getJSONObject("title").put("text",newoption.getJSONObject("title").getString("text"));
        JSONArray legendData = newoption.getJSONObject("legend").getJSONArray("data");
        radarOption.getJSONObject("legend").put("data",legendData);     //初始化Legend
        JSONArray xAxisData = newoption.getJSONObject("xAxis").getJSONArray("data");
        System.out.println("___________________");
        System.out.println(legendData);
        System.out.println("___________________");
        System.out.println(xAxisData);
        JSONArray dataInfo = newoption.getJSONArray("series");
        String max;
        System.out.println(newoption.getJSONArray("series"));
        for(int i = 0; i < newoption.getJSONArray("series").length();i++){
            JSONObject legendTemp = new JSONObject();           //填充Radar的Data
            legendTemp.put("value",dataInfo.getJSONObject(i).getJSONArray("data"));
            legendTemp.put("name",legendData.getString(i));
            radarOption.getJSONArray("series").getJSONObject(0).getJSONArray("data").put(legendTemp);
        }
        for (int i = 0;i < newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").length();i++){    //indicator长度取决于xAxis个数
            max = getJSONArrayMAX(newoption.getJSONArray("series"),i);
            JSONObject indicatorTemp = new JSONObject();        //填充Radar的indicator的最大值字段
            indicatorTemp.put("max", Double.valueOf(max)*1.2);
            indicatorTemp.put("name",xAxisData.getString(i));
            radarOption.getJSONObject("radar").getJSONArray("indicator").put(indicatorTemp);
        }
        return radarOption;
    }

    private JSONObject RadartoLine(JSONObject newoption){           //雷达转折线
        JSONObject lineOption = new JSONObject(lineBasic);
        lineOption.getJSONArray("series").remove(0);
        lineOption.getJSONObject("title").put("text",newoption.getJSONObject("title").getString("text"));
        lineOption.put("legend",newoption.getJSONObject("legend"));
        JSONArray xAxisData = new JSONArray();
        for (int i = 0; i < newoption.getJSONObject("radar").getJSONArray("indicator").length();i++){
            String xiName = newoption.getJSONObject("radar").getJSONArray("indicator").getJSONObject(i).getString("name");
            xAxisData.put(xiName);
        }
        for (int i = 0;i < newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").length();i++){
            JSONObject seriesOneData = new JSONObject("{'type':'line','smooth':true}");
            seriesOneData.put("data",newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").getJSONObject(i).getJSONArray("value"));
            seriesOneData.put("name",newoption.getJSONArray("series").getJSONObject(0).getJSONArray("data").getJSONObject(i).getString("name"));
            lineOption.getJSONArray("series").put(seriesOneData);
        }
        lineOption.getJSONObject("xAxis").put("data",xAxisData);
        return lineOption;
    }

    public int getOptionType (JSONObject chOption){
        int int_typeBefore = 0;
        String typeBefore;
        if (chOption.has("value"))
            typeBefore = "indexcard";
        else if (chOption.has("data") && chOption.has("dims") && chOption.has("meas"))
            typeBefore = "excelChart";
        else
            typeBefore = chOption.getJSONArray("series").getJSONObject(0).getString("type");
        switch (typeBefore){
            case "bar":{        //判断是条形还是堆积还是柱状图
                String xType = chOption.getJSONObject("xAxis").getString("type");
                int seriesLength = chOption.getJSONArray("series").length();
                if (chOption.getJSONObject("yAxis").has("data")){
                    int_typeBefore = 6;     //条形图
                } else if(seriesLength > 1) {
                    int_typeBefore = 1;     //堆积图
                } else {
                    int_typeBefore = 0;     //柱状图
                }
                break;
            }
            case "line":{
                //判断是面积图还是折线图
                JSONObject itemstyle = chOption.getJSONArray("series").getJSONObject(0);
                if (itemstyle.has("itemStyle")){
                    int_typeBefore = 2;
                }else{
                    int_typeBefore = 7;
                }
                break;
            }
            case "pie":
                int_typeBefore = 3;
                break;
            case "radar":
                int_typeBefore = 4;
                break;
            case "heatmap":
                int_typeBefore = 10;
                break;
            case "funnel":
                int_typeBefore = 13;
                break;
            case "wordcloud":
                int_typeBefore = 11;
                break;
            case "scatter":
                int_typeBefore = 12;
                break;
            case "gauge":
                int_typeBefore = 8;
                break;
            case "indexcard":
                int_typeBefore = 14;
                break;
            case "excelChart":
                int_typeBefore = 16;
                break;
            default:
        }
        return int_typeBefore;
    }

    private String getJSONArrayMAX(JSONArray data,int index){
        String max = data.getJSONObject(0).getJSONArray("data").getString(index);
        for (int i = 1; i < data.length();i++){
            if (Double.valueOf(max) < Double.valueOf(data.getJSONObject(i).getJSONArray("data").getString(index)))
                max = data.getJSONObject(i).getJSONArray("data").getString(index);
        }
        return max;
    }
}
