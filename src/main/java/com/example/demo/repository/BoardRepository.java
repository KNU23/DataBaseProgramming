package com.example.demo.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor

public class BoardRepository {
	
	private final SqlSessionTemplate sql;
	
	/** 도서 목록 불러오기 **/
	public List<BoardDTO> getList() {
		return sql.selectList("Board.getList");
	}
	
	/** 도서정보 저장하기 **/
	public void save(BoardDTO boardDTO) {
		sql.insert("Board.save", boardDTO);
	}
	
	/** 도서정보 상세보기 **/
	public BoardDTO detail(Integer id) {
		return sql.selectOne("Board.detail", id);
	}
	
	/** 도서정보 삭제하기 **/
	public void goDelete(Integer id) {
		sql.delete("Board.goDelete", id);
	}
	
	/** 도서정보 수정하기**/
	public void goUpdate(BoardDTO boardDTO) {
		sql.update("Board.goUpdate",boardDTO);
		
	}

}
