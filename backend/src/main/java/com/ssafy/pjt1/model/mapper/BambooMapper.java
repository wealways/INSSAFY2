package com.ssafy.pjt1.model.mapper;

import java.util.List;
import java.util.Map;

import com.ssafy.pjt1.model.dto.bamboo.BambooDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BambooMapper {

	public void createBamboo(BambooDto bambooDto);

	
}
