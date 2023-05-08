package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class BoardService {

//    이렇게 Bean에 등록해서 Spring IOC컨테이너에 관리받게하는 방식을 @Autowired통해 간편하게 할수있다.
//    private BoardRepository boardRepository;
//    public BoardService(BoardRepository boardRepository) {
//        this.boardRepository = boardRepository;
//    }

    //BoardRepository를 @Autowired어노테이션을 통해 Bean에주입한다
    @Autowired
    private BoardRepository boardRepository;

    //======================================================================
    //글 작성 , 파일업로드 추가 => MultipartFile file
    public void write(Board board, MultipartFile file) throws Exception {

        //======================파일 업로드==========================
        //저장할 경로를 지정한다.  System.getProperty("user.dir") <=루트 경로
        String projectPath = System.getProperty("user.dir") + "\\board\\board\\src\\main\\resources\\static\\files";
        //파일이름에 붙일 랜덤 UUID 식별자를 생성한다.
        UUID uuid = UUID.randomUUID();
        //fileName에 파라미터로 넘어온 file의 이름을 붙여 저장한다.
        String fileName = uuid + "_" + file.getOriginalFilename();
        //파일객체를 생성해서 파일경로랑 파일의 이름을 지정해준다.
        File saveFile = new File(projectPath, fileName);
        //transferTo메서드를 예외처리가 필요해서 throws Exception을 해준다.
        file.transferTo(saveFile);
        //파일의 이름을 set으로 저장하고 path는 파일의경로와 이름을 저장해준다.
        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);
        //=========================================================
        boardRepository.save(board);
    }
    //======================================================================

    //게시글 전체 조회
    //======================페이징 처리==========================
    public Page<Board> showAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
        //=====================================================
    }

    //==================================================================
    //검색 조회
    public Page<Board> searchKeyword(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    //======================================================================
    //특정 게시글 조회
    public Board showById(Integer id) {
        //findById의 리턴타입이 Optioncal이다 이럴때 .get을 통해 가져오면 된다.
        return boardRepository.findById(id).get();
    }

    //======================================================================
    //게시글 삭제
    public void deleteById(Integer id) {
        boardRepository.deleteById(id);
    }
    //======================================================================

}
