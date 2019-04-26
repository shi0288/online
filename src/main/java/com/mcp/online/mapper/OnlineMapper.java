package com.mcp.online.mapper;


import com.mcp.online.model.Online;
import com.mcp.online.util.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OnlineMapper extends BaseMapper<Online> {

    Online getFinalOnline();

    List<Online> getNoneTermOnline();

}
