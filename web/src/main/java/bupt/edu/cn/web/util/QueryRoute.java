package bupt.edu.cn.web.util;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.web.pojo.FaltTable;
import bupt.edu.cn.web.repository.FaltTableRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryRoute {

    @Autowired
    FaltTableRepository faltTableRepository;
    @Autowired
    KylinQueryService kylinQueryService;

    public String route(List<String> dims, List<String> funArr, List<String> meas, String fileName, String fileUrl) {
        System.out.println("-------start route-------");
        if (fileUrl.startsWith("select ")) {
            String project = "";
            String model = "";
            List<FaltTable> faltTables = faltTableRepository.findByNameAndTableSql(fileName, fileUrl);
            System.out.println("-------faltTables-------");
            if (faltTables == null || faltTables.size() == 0) {
                return "error,查找不到宽表";
            } else if (faltTables.size() == 1) {
                project = faltTables.get(0).getProject();
                model = faltTables.get(0).getModel();
            } else {
                return "error,查找到多个宽表";
            }

            ////得到project中所有的cube的维度和度量，并判断kylin中是否构建该查询
            kylinQueryService.login("ADMIN", "KYLIN");
            //得到project中所有的cube(没有维度信息)
            String cubes = kylinQueryService.listCubes(0, 50000, "", project);
            System.out.println("-------cubes-------");
            JSONArray cubesArr = new JSONArray(cubes);
            for (int i = 0; i < cubesArr.length(); i++) {
                JSONObject cube = cubesArr.getJSONObject(i);
                if (cube.getString("status").equals("READY")) {
                    //查询该cube的详细信息（包括维度信息）
                    String newCubeStr = kylinQueryService.getCubeDes(cube.getString("name"));
                    System.out.println(newCubeStr);
                    JSONArray newCubeArr = new JSONArray(newCubeStr);
                    JSONObject dimsCube = newCubeArr.getJSONObject(0);
                    //得到该cube的维度和度量
                    JSONArray cubeDims = dimsCube.getJSONArray("dimensions");
                    JSONArray cubeMeas = dimsCube.getJSONArray("measures");
                    //拼成字符串
                    String cubeDimsStr = "";
//                    if (cubeDims.length()>0){
//                        cubeDimsStr = cubeDims.getJSONObject(0).getString("table") +"."+cubeDims.getJSONObject(0).getString("name");
//                        for (int z = 1; z<cubeDims.length();z++){
//                            cubeDimsStr = cubeDimsStr + "," + cubeDims.getJSONObject(z).getString("table") +"."+cubeDims.getJSONObject(z).getString("name");
//                        }
//                    }
//                    System.out.println(cubeDimsStr);
                    String cubeMeasStr = "";
//                    if (cubeMeas.length()>0){
//                        cubeMeasStr = cubeMeas.getJSONObject(0).getJSONObject("function").getJSONObject("parameter").getString("value");
//                        for (int z = 1; z<cubeMeas.length();z++){
//                            cubeMeasStr = cubeMeasStr + "," + cubeMeas.getJSONObject(z).getJSONObject("function").getJSONObject("parameter").getString("value");
//                        }
//                    }

                    //判断查询是否包含在cube的维度和度量中
                    boolean flagD = true;
                    boolean flagM = true;
                    if (cubeDims.length() > 0) {
                        for (int j = 0; j < dims.size(); j++) {
                            for (int z = 0; z < cubeDims.length(); z++) {
                                cubeDimsStr = cubeDims.getJSONObject(z).getString("table") + "." + cubeDims.getJSONObject(z).getString("name");
                                if ((cubeDimsStr.toLowerCase()).equals(dims.get(j).toLowerCase())) {
                                    break;
                                }
                                if (z == cubeDims.length() - 1) {
                                    flagD = false;
                                }
                            }
                        }
                    }
                    if (cubeMeas.length() > 0) {
                        for (int j = 0; j < meas.size(); j++) {
                            for (int z = 0; z < cubeMeas.length(); z++) {
                                cubeMeasStr = cubeMeas.getJSONObject(z).getJSONObject("function").getJSONObject("parameter").getString("value");
                                String expression = cubeMeas.getJSONObject(z).getJSONObject("function").getString("expression");

                                if ((cubeMeasStr.toLowerCase()).equals(meas.get(j).toLowerCase()) && (expression.toLowerCase()).equals(funArr.get(j).toLowerCase())) {
                                    break;
                                }
                                if (z == cubeMeas.length() - 1) {
                                    flagM = false;
                                }
                            }
                        }
                    }
                    if (flagD && flagM) {
                        return "kylin/" + project;
                    }
                }
            }
            return "hive";
        } else if (fileUrl.endsWith(".csv")) { //以后可以改进为根据data_source type 判断
            return "spark";
        } else if ((fileUrl.split("/").length == 2) && fileName.equals(fileUrl.split("/")[1])) {
            //以后可以改进为根据data_source type 判断  判断条件可能会出现数组越界报错
            return "hive";
        }
        return "hive";
    }

}
