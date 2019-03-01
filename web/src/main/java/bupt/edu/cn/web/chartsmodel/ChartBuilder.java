package bupt.edu.cn.web.chartsmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 2017/5/2.
 */
public class ChartBuilder {
    public static void main(String... args){
        int[] arr = new int[]{1,1,1,1};
        subarraySum(arr,2);
    }



    public static int subarraySum(int[] nums, int k) {
        int sum = 0, result = 0;
        Map<Integer, Integer> preSum = new HashMap<>();
        preSum.put(0, 1);

        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (preSum.containsKey(sum - k)) {
                result += preSum.get(sum - k);
            }
            preSum.put(sum, preSum.getOrDefault(sum, 0) + 1);
        }

        return result;
    }
}
