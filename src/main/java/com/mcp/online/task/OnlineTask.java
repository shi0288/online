package com.mcp.online.task;


import com.mcp.online.service.OnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OnlineTask {

    @Autowired
    private OnlineService onlineService;


    /**
     * 获取数据
     */
    @Scheduled(fixedDelay = 5000)
    public void getOnlineData() {
        onlineService.getOnlineData();
    }




}
