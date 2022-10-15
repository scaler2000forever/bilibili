package com.atlinlin.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ author : LiLin
 * @ create : 2022-10-14 23:36
 */
@RestController
public class RestfulApi {
    private final Map<Integer,Map<String,Object>> dataMap;

    public RestfulApi() {
        dataMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("id",i);
            data.put("name","name" + i);
            dataMap.put(i,data);
        }
    }

    @GetMapping("/objects/{id}")
    public Map<String,Object> getData(@PathVariable Integer id){
        return dataMap.get(id);
    }

    @DeleteMapping("/objects/{id}")
    public String deleteData(@PathVariable Integer id){
        dataMap.remove(id);
        return "delete success";
    }

    @PostMapping("/objects")
    public String postData(@RequestBody Map<String,Object> data){
        Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length - 1] + 1;
        dataMap.put(nextId,data);
        return "post success";
    }

    //包含幂等性，既有新增也有更新
    @PutMapping("/objects")
    public String putData(@RequestBody Map<String,Object> data){
        //取出Object类型的id
        Integer id = Integer.valueOf(String.valueOf(data.get("id")));
        Map<String, Object> containedData = dataMap.get(id);
        if(containedData == null){
            Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length - 1] + 1;
            dataMap.put(nextId,data);
        }else {
            //更新操作替换原来data数据
            dataMap.put(id,data);
        }
        return "put success";
    }
}
