package mybatis;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

/*
해당 인터페이스는 컨트롤러와 DAO사이에서 매개역할을 하는
서비스 객체로 사용된다.
 */
@Service
public interface MybatisDAOImpl {

	// 게시물 수 카운트 
	public int getTotalCount();
	
	// 목록에 출력할 게시물 가져오기
	public ArrayList<MyBoardDTO> listPage(int s, int e);
	
	
	
}
