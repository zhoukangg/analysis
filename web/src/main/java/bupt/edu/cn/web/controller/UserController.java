package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import com.peaceful.auth.data.domain.JSONRole;
import com.peaceful.auth.sdk.Impl.AuthServiceImpl;
import com.peaceful.auth.sdk.api.AuthService;
import com.peaceful.auth.sdk.other.AuthContextImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @RequestMapping("/login")
    public ReturnModel login(String username, String password, HttpServletResponse response, HttpServletRequest request){
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("-----------进入方法 /login----------");
        System.out.println("-----------参数1：username = " + username);
        System.out.println("-----------参数2：password = " + password);
        AuthService authService = AuthServiceImpl.getAuthService();
//        System.out.println(authService.getSystem());
//        System.out.println(authService.getRolesOfSystem());
//        System.out.println(authService.getUser(username).getRoles());
//        System.out.println(authService.getUsersOfSystem());
        authService.login(username,password);
        ReturnModel result = new ReturnModel();
//        result.setDatum(authService.getUser(username).getEmail());
//        result.setDatum(authService.getUser(username).getRoles());
//        result.setDatum(authService.getUser(username).getMenus());
        result.setResult(true);
        List<String> roes = new ArrayList<>();
        List<JSONRole> listJSONRole = authService.getUser(username).getRoles();
        for (int i = 0; i<listJSONRole.size();i++){
            roes.add(listJSONRole.get(i).name);
        }
        result.setDatum(roes);
        return result;
        }
}
