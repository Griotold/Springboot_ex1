package com.board.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.board.domain.BoardDTO;
import com.board.mapper.BoardMapper;

@Service //해당 클래스가 비즈니스 로직을 담당하는 서비스 클래스임을 나타냄
public class BoardServiceImpl implements BoardService {

	@Autowired //BoardMapper 인터페이스 빈을 주입
	private BoardMapper boardMapper;

	//insert와 update는 모두 데이터를 등록하기 때문에
	//두 개의 메서드로 나누어 처리하지 않고
	//idx(게시글 번호)의 유무를 기준으로 인서트 또는 업데이트
	@Override
	public boolean registerBoard(BoardDTO params) {
		int queryResult = 0;

		//컨트롤러에서 넘어온 idx가 없다면 글쓰기 
		if (params.getIdx() == null) { //없다면 글쓰기
			queryResult = boardMapper.insertBoard(params);
		} else { //있다면 글 수정
			queryResult = boardMapper.updateBoard(params);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public BoardDTO getBoardDetail(Long idx) {
		return boardMapper.selectBoardDetail(idx);
	}

	@Override
	public boolean deleteBoard(Long idx) {
		int queryResult = 0;

		BoardDTO board = boardMapper.selectBoardDetail(idx);

		if (board != null && "N".equals(board.getDeleteYn())) {
			queryResult = boardMapper.deleteBoard(idx);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public List<BoardDTO> getBoardList() {
		List<BoardDTO> boardList = Collections.emptyList();

		int boardTotalCount = boardMapper.selectBoardTotalCount();

		if (boardTotalCount > 0) {
			boardList = boardMapper.selectBoardList();
		}

		return boardList;
	}

}
