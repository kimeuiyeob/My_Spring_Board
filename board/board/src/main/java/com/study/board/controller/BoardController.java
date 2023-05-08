package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    //==============================================================================
    @GetMapping
    public String board() {
        return "board";
    }

    //==============================================================================
    //localhost:8090/board/write
    //글 작성
    @GetMapping("/write")
    public String boardWriteForm() {
        return "boardwrite"; //HTML파일 이름
    }

    //==============================================================================
    @PostMapping("/writepro")
    //폼태그안에 input태그와 textare태그의 name을 title과 content로 줘서 그 키값을 파라미터로 받은것이다.
    //꼭 name에다 준 키값이랑 파라미터의 이름을 동일하게 해야 읽어올수있다.
//    public  String boardWritePro(String title, String content) {

    //이렇게 되면 받아올 값들이 많아지면 파라미터가 길어질수도 있기때문에 board라는 클래스만들어 파라미터로 받고
    //board라는 객체를 database에 저장하면 간편해진다.

    //======================파일 업로드==========================
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        //ID는 AUTO_INCREMENT로 순서대로 번호가 생성되서 들어간다.
        boardService.write(board, file);
        //=====================================================
        return "redirect:/board/list";
    }

    //==============================================================================
    @GetMapping("/list")
    //데이터를 가지고 페이지로 이동해야하기 때문에 이때 쓰는게 Model이라는 객체이다.

    //======================페이징 처리==========================
    // @PageableDefault으로 간편하게 페이징 처리한다. page = 0은 1페이지,  size = 게시글 갯수, direction desc로 주어서 마지막에 쓴글이 가장 먼저나오게한다.
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {
        //addAttribute메서드를 통해 키값을 list로 주고 value값을 boardService.showAll()로 주면
        //화면단에서 키값 list를 가지고 해당 데이터값들을 가져올수있다.

        Page<Board> list = null;
        //만약 검색할 단어가 파라미터로 넘어오면 else문으로 단어명과 페이지를 보내고 아니면 페이지만 보낸다.
        //===================== 검색 기능 =================================
        if (searchKeyword == null) {
            list = boardService.showAll(pageable);
        } else {
            list = boardService.searchKeyword(searchKeyword, pageable);
        }
        //================================================================
//      model.addAttribute("list", boardService.showAll(pageable));

        int nowPage = list.getPageable().getPageNumber() + 1; //페이지가 0부터시작해서 +1
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        //=====================================================
        return "boardlist";
    }

    //==============================================================================
    //해당 게시글 상세
    @GetMapping("/view") //localhost:8090/board/view?id=1
    //th:href="@{/board/view(id=${board.id})}"쿼리스트링으로 id가 넘어온걸 파라미터 id로 받게된거다.
    public String boardView(Model model, Integer id) {
        model.addAttribute("board", boardService.showById(id));
        return "boardview";
    }

    //==============================================================================
    //게시글 삭제하기
    @GetMapping("/delete")
    public String boardDelete(Integer id) {
        boardService.deleteById(id);
        //이때는 게시글을 삭제하고 바로 HTML파일로 가는게 아니라 다시 list컨트롤러를 타게해줘야하니까
        //redirect:/를 사용하여 컨트롤러로 보낸다.
        return "redirect:/board/list";

        //즉 => "boardview"바로 HTML로 보내는거고
        //"redirect:/board/list" 컨트롤러를 한번 더타게하는거다.
    }

    //==============================================================================
    //게시글 수정페이지
    @GetMapping("/modify/{id}")
    public String boardModify(@PathVariable Integer id, Model model) {
        model.addAttribute("board", boardService.showById(id));
        return "boardmodify";
    }

    //==============================================================================
    //게시글 수정하기
    @PostMapping("/update/{id}")
    public String boardUpdate(@PathVariable Integer id, Board board, MultipartFile file) throws Exception {
        //원래 있던 board객체를 id로 찾아오고
        Board boardTemp = boardService.showById(id);
        //수정한 파라미터로 board객체를 받아 set으로 수정을 한다.
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        //수정한 boardTemp객체를 다시 원래있던 객체에 덮어씌운 것이다.
        //======================파일 업로드==========================
        boardService.write(boardTemp, file);
        //=========================================================
        return "redirect:/board/list";
    }
}
