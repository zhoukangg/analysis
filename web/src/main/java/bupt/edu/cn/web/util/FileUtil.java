package bupt.edu.cn.web.util;

import bupt.edu.cn.web.pojo.DataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @program analysis
 * @description: ${TODO}
 * @author: kang
 * @create: 2019/06/25 23:53
 */
public class FileUtil {
    public static List<String> getFiles(String path) {
        List<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile() && tempList[i].getName().endsWith(".csv")) {
                System.out.println("文     件：" + tempList[i].getName());
                files.add(tempList[i].toString());
            }
            if (tempList[i].isDirectory()) {
//              System.out.println("文件夹：" + tempList[i]);
            }
        }
        return files;
    }

    public static ArrayList<DataSource> getCSVDataSourceByPath(String path) {
        ArrayList<DataSource> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile() && tempList[i].getName().endsWith(".csv")) {
                String fileName = tempList[i].getName().substring(0, tempList[i].getName().length() - 4);
                DataSource ds = new DataSource();
                ds.setId(-1);
                ds.setFileName(fileName);
                ds.setFileType("CSV");
                ds.setFileUrl(tempList[i].getAbsolutePath());
                files.add(ds);
                System.out.println(ds.getFileUrl());
            }
        }
        return files;
    }

    public static ArrayList<DataSource> getCSVDataSourceContainInPath(String path, String content) {
        ArrayList<DataSource> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile() && tempList[i].getName().endsWith(".csv")) {
                String fileName = tempList[i].getName().substring(0, tempList[i].getName().length() - 4);
                if (!fileName.contains(content))
                    break;
                DataSource ds = new DataSource();
                ds.setId(-1);
                ds.setFileName(fileName);
                ds.setFileType("CSV");
                ds.setFileUrl(tempList[i].getAbsolutePath());
                files.add(ds);
                System.out.println(ds.getFileUrl());
            }
        }
        return files;
    }

    public static void main(String[] args) {
//      举例：
        String fName = "/Users/kang/Desktop";

//      方法一：

        File tempFile = new File(fName.trim());

        String fileName = tempFile.getName();

        System.out.println("fileName = " + fileName);

        getCSVDataSourceByPath(fName);
    }
}
