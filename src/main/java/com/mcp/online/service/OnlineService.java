package com.mcp.online.service;


import com.mcp.online.mapper.OnlineMapper;
import com.mcp.online.model.Online;
import com.mcp.online.util.DateUtil;
import com.mcp.online.util.HttpClientWrapper;
import com.mcp.online.util.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

@Service
public class OnlineService {

    @Autowired
    private OnlineMapper onlineMapper;

    public void getOnlineData() {
        Date time = new Date();
        String url = "https://mma.qq.com/cgi-bin/im/online";
        Map<String, String> header = new HashMap();
        header.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36");
        HttpResult httpResult = HttpClientWrapper.sendGet(url, header, null);
        Long num = this.getCurNumber(httpResult.getResult());
        if (num == null) {
            return;
        }
        Online preOnline = onlineMapper.getFinalOnline();
        //没有上一期
        if (preOnline == null) {
            Online online = new Online();
            online.setNumber(num);
            online.setChangeNum(0L);
            online.setTime(time);
            onlineMapper.insertSelective(online);
            updateTerm();
        } else {
            Date preTime = preOnline.getTime();
            //不在同一分钟内
            if (!DateUtil.isAlikeMinite(preTime, time) && preOnline.getNumber().compareTo(num) != 0) {
                Online online = new Online();
                online.setNumber(num);
                online.setTime(time);
                online.setChangeNum(num - preOnline.getNumber());
                onlineMapper.insertSelective(online);
                updateTerm();
            }
        }
    }

    @Async
    public void updateTerm() {
        List<Online> list = onlineMapper.getNoneTermOnline();
        for (int i = 0; i < list.size(); i++) {
            Online temp = list.get(i);
            Online update = new Online();
            update.setId(temp.getId());
            update.setPrize(this.getPrize(String.valueOf(temp.getNumber())));
            int term = DateUtil.getHour(temp.getTime()) * 60 + DateUtil.getMinute(temp.getTime());
            if (term == 0) {
                term = 1440;
                update.setTerm(DateUtil.DateToString(DateUtil.addDay(temp.getTime(), -1), "yyyyMMdd") + new DecimalFormat("0000").format(term));
            } else {
                update.setTerm(DateUtil.DateToString(temp.getTime(), "yyyyMMdd") + new DecimalFormat("0000").format(term));
            }
            onlineMapper.updateByPrimaryKeySelective(update);
        }
    }

    private String getPrize(String onlineNum) {
        String[] bb = onlineNum.split("");
        long result = 0;
        for (int i = 0; i < bb.length; i++) {
            result += Long.parseLong(bb[i]);
        }
        String temp = String.valueOf(result);
        String winNum = temp.substring(temp.length() - 1) + onlineNum.substring(onlineNum.length() - 4);
        winNum = StringUtils.join(winNum.split(""), ",");
        return winNum;
    }


    private Long getCurNumber(String data) {
        Pattern p_script = Pattern.compile("(?<=\"c\":)(.*?)(?=,)");
        Matcher m_script = p_script.matcher(data);
        while (m_script.find()) {
            String temp = m_script.group();
            return Long.valueOf(temp);
        }
        return null;
    }


}
