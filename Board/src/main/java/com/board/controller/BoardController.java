package com.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.constant.Method;
import com.board.domain.BoardDTO;
import com.board.service.BoardService;
import com.board.util.UiUtils;
//UiUtils를 상속함으로 공통의 것을 상속받는다.
@Controller
public class BoardController extends UiUtils{

	@Autowired
	private BoardService boardService;

	//글쓰기 화면을 요청하는건가요? 글쓰기 로직을 실행하는 건가요?
	//글쓰기 화면 요청!
	//get요청이기 때문에 그럴 확률이 매우 높다.
	//글쓰기 화면으로 이동
	@GetMapping(value = "/board/write.do")
	public String openBoardWrite(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		//지금은 리턴타입이 String이지만
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
		//앞에 프리픽스 붙고 뒤에 suffix(접미사) 붙고 
		// 
	}
	//파라미터 model추가, 주석에 return추가
	@PostMapping(value = "/board/register.do")
	public String registerBoard(final BoardDTO params, Model model) {
		try {
			boolean isRegistered = boardService.registerBoard(params);
			if (isRegistered == false) { //UiUtils를 상송받아서 리턴문 가능
				return showMessageWithRedirect("게시글 등록에 실패하였습니다.", "/board/list.do", Method.GET, null, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);

		} catch (Exception e) {
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		}

		return showMessageWithRedirect("게시글 등록이 완료되었습니다.", "/board/list.do", Method.GET, null, model);
	}
	
	@GetMapping(value = "/board/list.do")
	public String openBoardList(Model model) {
		List<BoardDTO> boardList = boardService.getBoardList();
		model.addAttribute("boardList", boardList);

		return "board/list";
	}

	@GetMapping(value = "/board/view.do")
	public String openBoardDetail(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			// TODO => 올바르지 않은 접근이라는 메시지를 전달하고, 게시글 리스트로 리다이렉트
			return "redirect:/board/list.do";
		}

		BoardDTO board = boardService.getBoardDetail(idx);
		//사실은 아래는 필요가 없을 수 있지만, 그럼에도 조건문을 걸어주는 게 좋다.
		if (board == null || "Y".equals(board.getDeleteYn())) {
			// TODO => 없는 게시글이거나, 이미 삭제된 게시글이라는 메시지를 전달하고, 게시글 리스트로 리다이렉트
			return "redirect:/board/list.do";
		}
		//if문에 return이 있으므로 아래는 사실상 else와 같다고 볼 수 있다.
		model.addAttribute("board", board);

		return "board/view";
	}
	
	//삭제는 get요청이 아니라 post
	//삭제는 크리티컬 하기 때문에 url로 요청을 때린다고 들어주면 곤란하다.
	//post로 보안을 높여준다.
	//파라미터 추가, 주석에 return 추가
	@PostMapping(value = "/board/delete.do")
	public String deleteBoard(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
		}

		try {
			boolean isDeleted = boardService.deleteBoard(idx);
			if (isDeleted == false) {
				return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/board/list.do", Method.GET, null, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);

		} catch (Exception e) {
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		}

		return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/board/list.do", Method.GET, null, model);
	}
	
}
