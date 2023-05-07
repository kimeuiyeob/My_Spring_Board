package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//디펜딘시로 다운받은 라이브러리 => JpaRepository를 상속받아 제네릭은 Board엔티티와 해당 ID의 타입을 줘야한다.
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
}
