package bupt.edu.cn.web.service;

import bupt.edu.cn.web.pojo.DrillDim;
import bupt.edu.cn.web.repository.DrillDimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrillService {

    @Autowired
    bupt.edu.cn.web.repository.DrillDimRepository drillDimRepository;

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
