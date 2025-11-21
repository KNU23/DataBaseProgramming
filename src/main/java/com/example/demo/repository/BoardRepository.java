package com.example.demo.repository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
	
	private final SqlSessionTemplate sql;
	
	/** 검색 조건에 맞는 도서 총 개수 **/
	public int countList(String keyword) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("keyword", keyword);
	    return sql.selectOne("Board.countList", params);
	}
	
	/** 도서 목록 불러오기 (페이징 + 정렬 처리) **/
	public List<BoardDTO> getList(String keyword, int offset, int limit, String sort, String order) {
		Map<String, Object> params = new HashMap<>();
		params.put("keyword", keyword);
		params.put("offset", offset);
		params.put("limit", limit);
		params.put("sort", sort);
		params.put("order", order);
		
		return sql.selectList("Board.getList", params);
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
	
	/** 재고 1 감소 **/
    public int decreaseStock(Integer bookid) {
        return sql.update("Board.decreaseStock", bookid);
    }
	
	/** 도서정보 수정하기 **/
	public void goUpdate(BoardDTO boardDTO) {
		sql.update("Board.goUpdate",boardDTO);	
	}
	
	/** 도서목록 전부삭제 **/
	public void deleteAll() {
        sql.delete("Board.deleteAll");
    }
	
    /** 도서 ID 번호표를 1번으로 초기화 **/
    public void resetAutoIncrement() {
        sql.update("Board.resetAutoIncrement");
    }
	
	/** 도서 제목으로 찾기 (중복 확인) **/
    public BoardDTO findByBookname(String bookname) {
        return sql.selectOne("Board.findByBookname", bookname);
    }
}

