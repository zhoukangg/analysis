package bupt.edu.cn.spark.utils;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.File;
import java.nio.charset.Charset;

public class FileOperate {

    public static void combineCSV(String newName, String filepath){
        File file = new File(filepath + newName);
        File[] files = file.listFiles();
        String savecsvFilePath = filepath + newName + ".csv";
        boolean headerflag = true;
        try{
            CsvWriter writer = new CsvWriter(savecsvFilePath, ',', Charset.forName("UTF-8"));
            CsvReader reader;
            String[] head = {};
            for (File oneFile : files){
                if (oneFile.getName().length()>10){
                    if (oneFile.getName().substring(0,5).equals("part-")){      //获取到part-开头的所有csv文件
                        reader = new CsvReader(filepath + newName + "/" + oneFile.getName(),',',Charset.forName("UTF-8"));
                        reader.readHeaders();
                        if (headerflag && reader.getRawRecord()!= "") {         //保证只读取一次header即可
                            if (reader.getHeaders().length != 0) {
                                head = reader.getHeaders();
                                headerflag = false;
                                System.out.println(head.length);
                                writer.writeRecord(head);
                            }
                        }
                        while (reader.readRecord()){
                            for (int i = 0;i < head.length;i++){
                                writer.write(reader.get(head[i]));
                                if (i == head.length - 1)
                                    writer.endRecord();
                                System.out.println(head[i] + ":" +reader.get(head[i]));
                            }
                        }
                        reader.close();
                    }
                }
            }
            writer.close();
            System.out.println("文件输出成功");
            deleteDirectory(filepath + newName);    //删除之前的文件夹
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean initDrillJudge(String path, String fileName){
        boolean flag = false;
        fileName += ".csv";
        try {
            File pathfile = new File(path);
            File[] fs = pathfile.listFiles();
            for (File f : fs)
                if (f.getName().equals(fileName))
                    flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }
}
