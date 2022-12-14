package org.liubility.commons.zFeign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.liubility.commons.http.HttpException;
import org.liubility.commons.http.HttpUtils;
import org.liubility.commons.json.JsonUtils;
import org.liubility.commons.util.AnnotationUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * jdk8动态代理
 */
@Slf4j
public class DynaProxyHttp implements InvocationHandler {

    private Class<?> object;

    private final String baseUrl;

    //深入解构
    private final String[] depth;


    public DynaProxyHttp(String baseUrl, String[] depth) {
        this.baseUrl = baseUrl;
        this.depth = depth;
    }

    @SuppressWarnings("unchecked")
    public <T> T bindInterface(Class<T> proxyInterface) {
        object = proxyInterface;
        return (T) Proxy.newProxyInstance(
                proxyInterface.getClassLoader(),
                new Class[]{proxyInterface},
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String url = "";
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl);

            RequestMapping requestMapping = (RequestMapping) AnnotationUtils.getAllContainedAnnotationType(method, RequestMapping.class);

            if (requestMapping == null) {
                log.warn("该feign方法没有映射请求路径[{},{}]", object.getName(), method.getName());
                return null;
            }
            String[] values = (String[]) AnnotationUtils.getIncludeAnnotationValue(method, RequestMapping.class, "value");
            urlBuilder.append(values[0]);

            RequestMethod[] request = requestMapping.method();

            HashMap<String, String> params = new HashMap<>();

            Parameter[] parameters = method.getParameters();
            Object body = new Object();

            url = urlBuilder.toString();

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                String value = String.valueOf(args[i]);
                if (param.isAnnotationPresent(RequestParam.class)) {
                    params.put(param.getAnnotation(RequestParam.class).value(), value);
                } else if (param.isAnnotationPresent(RequestBody.class)) {
                    body = args[i];
                } else if (param.isAnnotationPresent(PathVariable.class)) {
                    String pathVar = param.getAnnotation(PathVariable.class).value();
                    url = url.replaceAll("\\{" + pathVar + "}", value);
                }
            }
            return robotHandle(url, request[0], body, params, method.getGenericReturnType());
        } catch (HttpException e) {
            log.error("远程服务调用异常[{}]", url);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object robotHandle(String url, RequestMethod request, Object body, HashMap<String, String> params, Type type) {
        String str = "";
        try {
            HttpUtils httpUtils = HttpUtils.initJson();
            Map<String, String> map;
            if (body == null) return null;

            switch (request) {
                case GET:
                    httpUtils.setParamMap(params);
                    map = httpUtils.get(url);
                    break;
                case POST:
                    String s = JsonUtils.object2Str(body);
                    map = JSON.parseObject(s, new TypeReference<Map<String, String>>() {
                    });
                    httpUtils.setParamMap(params);
                    httpUtils.setBody(map);
                    map = httpUtils.post(url);
                    break;
                default:
                    log.error("暂不支持该类请求[{}]", request.toString());
                    return null;
            }

            str = checkResult(map);
            JSONObject resultJson = JSON.parseObject(str);
            for (String s : depth) {
                if (JsonUtils.isJsonObj(str)) {
                    str = resultJson.getString(s);
                } else {
                    break;
                }
            }
            return JSON.parseObject(str, type);
        } catch (JSONException e) {
            Class<?> typeCls = (Class<?>) type;
            return typeCls.cast(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String checkResult(Map<String, String> result) {
        String statusCode = result.get("statusCode");
        String resultStr = result.get("result");
        if ("200".equals(statusCode)) {
            return resultStr;
        } else {
            throw new HttpException(resultStr);
        }
    }
}
