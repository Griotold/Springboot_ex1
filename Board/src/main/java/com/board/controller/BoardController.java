package com.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.domain.BoardDTO;
import com.board.service.BoardService;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;

	//글쓰기 화면을 요청하는건가요? 글쓰기 로직을 실행하는 건가요?
	//글쓰기 화면 요청!
	//get요청이기 때문에 그럴 확률이 매우 높다.
	//글쓰기 화면으로 이동
	@GetMapping(value = "/board/write.do")
	public String openBoardWrite(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		
		//요청과 똑같은 걸로 하면 리턴타입이 void
		//여기서 요청은 "/board/write.do"
		if (idx == null) { //idx가 없으면 껍데기 dto생성
			model.addAttribute("board", new BoardDTO());
		} else {	//idx가 있으면 해당idx로 dto가져오기
			BoardDTO board = boardService.getBoardDetail(idx);
			if (board == null) {   //idx로 조회했는데 가져온dto가 null이면 글
				return "redirect:/board/list.do"; //글쓰기 화면이 아닌 목록화면으로 이동
			}
			model.addAttribute("board", board);
		}
		return "board/write";
		//앞에 프리픽스 붙고 뒤에 포스트픽스 붙고 
		// 
	}
	@PostMapping(value = "/board/register.do")
	public String registerBoard(final BoardDTO params) {
		try {
			boolean isRegistered = boardService.registerBoard(params);
			if (isRegistered == false) {
				// TODO => 게시글 등록에 실패하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달

		} catch (Exception e) {
			// TODO => 시스템에 문제가 발생하였다는 메시지를 전달
		}

		return "redirect:/board/list.do";
	}

}
