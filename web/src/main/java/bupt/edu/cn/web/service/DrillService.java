package bupt.edu.cn.web.service;

import bupt.edu.cn.web.pojo.DrillDim;
import bupt.edu.cn.web.repository.DrillDimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrillService {

    @Autowired
    bupt.edu.cn.web.repository.DrillDimRepository drillDimRepository;

    public DrillDim getByDimsAndTablename(String dims, String tablename){
        List<DrillDim> drillDims= drillDimRepository.findByDimsEqualsAndTablenameEquals(dims,tablename);
//        确保拿到的是数据库中最新的生成的缓存数据的信息
        DrillDim drillDim = new DrillDim();
        if (drillDims.size() != 0)
            drillDim = drillDims.get(drillDims.size() - 1);
        else
            drillDim.setId(Long.parseLong("-1"));   //ID为-1代表当前数据库中无dims和tablename对应的缓存数据
        return drillDim;
    }

    public DrillDim getDrillDimByID(Long id){
        return drillDimRepository.findByIdEquals(id);
    }

    public DrillDim createDrillDim(String tabname, String dims){
        DrillDim newDrillDim = new DrillDim();
        newDrillDim.setTablename(tabname);
        newDrillDim.setDims(dims);
        drillDimRepository.saveAndFlush(newDrillDim);
        return newDrillDim;
    }
}
