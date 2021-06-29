package com.kosmo.k11mybatis;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import mybatis.MyBoardDTO;
import mybatis.MybatisDAOImpl;
import util.PagingUtil;

@Controller
public class MybatisController {

	
	/*
	Mybatis를 사용하기 위해 빈을 자동주입 받음
	servlet-context.xml에서 생성함
	 */
	@Autowired
	private SqlSession sqlSession;
//	public void setSqlSession(SqlSession sqlSession) {
//		this.sqlSession = sqlSession;
//		System.out.println("Mybatis 사용준비 끝");
//	}
	
	// 방명록 리스트
	@RequestMapping("/mybatis/list.do")
	public String list(Model model, HttpServletRequest req) {
		
		// 방명록 테이블의 게시물 개수 카운트
		/*
		컨트롤러에서 서비스 역할을 하는 인터페이스에 정의된
		추상메소드를 호출한다. 그러면 mapper에 정의된 쿼리문이
		실행되는 형식으로 동작한다.
		 */
		int totalRecordCount = 
				sqlSession.getMapper(MybatisDAOImpl.class).getTotalCount();
		
			
		// 페이지 처리를 위한 설정값
		int pageSize = 4;
		int blockPage = 2;

		// 전체 페이지 수 계산
		int totalPage =
				(int)Math.ceil((double)totalRecordCount/pageSize);
		
		// 현재 페이지 번호 설정
		int nowPage = req.getParameter("nowPage")==null ? 1:
			Integer.parseInt(req.getParameter("nowPage"));
		
		// select 할 게시물의 구간을 계산
		int start = (nowPage-1)*pageSize +1;
		int end = nowPage*pageSize;
		
		// 목록에 출력할 게시물을 얻어오기 위한 쿼리실행
		ArrayList<MyBoardDTO> lists = sqlSession.getMapper(MybatisDAOImpl.class).listPage(start,end);
		
		// 페이지 번호 출력
		String pagingImg = PagingUtil.pagingImg(totalRecordCount,
				pageSize, blockPage, nowPage, req.getContextPath()+
				"/mybatis/list.do?");
		model.addAttribute("pagingImg",pagingImg);
		
		// 내용에 대한 줄바꿈 처리
		for (MyBoardDTO dto : lists) {
			String temp=
					dto.getContents().replace("\r\n", "<br/>");
			dto.setContents(temp);
		}
		
		model.addAttribute("lists" , lists);
		
		
		
		return "07Mybatis/list";
		   
		
		
		
	}
	
	
}
