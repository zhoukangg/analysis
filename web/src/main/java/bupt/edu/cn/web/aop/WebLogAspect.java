//package bupt.edu.cn.web.aop;
//
//import javassist.ClassClassPath;
//import javassist.ClassPool;
//import javassist.CtClass;
//import javassist.CtMethod;
//import javassist.bytecode.CodeAttribute;
//import javassist.bytecode.LocalVariableAttribute;
//import javassist.bytecode.MethodInfo;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Modifier;
//import java.util.*;
//
///***
// * 打印请求方法和参数 切面
// * @Author: kang
// */
//
//@Aspect
//@Component
//public class WebLogAspect {
//
//    private final ObjectMapper mapper;
//
//    @Autowired
//    public WebLogAspect(ObjectMapper mapper) {
//        this.mapper = mapper;
//    }
//
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
//    public void webLog() {
//    }
//
//    @Before("webLog()")
//    public void doBefore(JoinPoint joinPoint) {
//        try {
//            System.out.println("----------进入 " + joinPoint.getSignature().getName() + " 接口----------");
//            Map<String, Object> map = getFieldsNameValueMap(joinPoint);
//            Iterator<String> keys = map.keySet().iterator();
//            while (keys.hasNext()) {
//                String name = keys.next();
//                System.out.println(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName()
//                        + " : " + name + " : " + mapper.writeValueAsString(map.get(name)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @AfterReturning(returning = "response", pointcut = "webLog()")
//    public void doAfterReturning(Object response) throws Throwable {
//        if (response != null) {
//            System.out.println("response parameter : " + mapper.writeValueAsString(response));
//        }
//    }
//
//    /**
//     * 获取方法的参数和对应的值
//     *
//     * @param joinPoint
//     * @return
//     * @throws Exception
//     */
//    private Map<String, Object> getFieldsNameValueMap(JoinPoint joinPoint) throws Exception {
//        Object[] args = joinPoint.getArgs();
//        String classType = joinPoint.getTarget().getClass().getName();
//        Class<?> clazz = Class.forName(classType);
//        String clazzName = clazz.getName(); // 类名
//        String methodName = joinPoint.getSignature().getName(); //获取方法名称
//        Map<String, Object> map = new HashMap<>();
//        ClassPool pool = ClassPool.getDefault();
//        ClassClassPath classPath = new ClassClassPath(this.getClass());
//        pool.insertClassPath(classPath);
//        CtClass cc = pool.get(clazzName);
//        CtMethod cm = cc.getDeclaredMethod(methodName);
//        MethodInfo methodInfo = cm.getMethodInfo();
//        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
//        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
//        if (attr == null) {
//            throw new RuntimeException();
//        }
//        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
//        for (int i = 0; i < cm.getParameterTypes().length; i++) {
//            map.put(attr.variableName(i + pos), args[i]);//paramNames即参数名
//        }
//        return map;
//    }
//
//}