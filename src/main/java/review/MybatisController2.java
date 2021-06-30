package review;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import mybatis.MyBoardDTO;
import review.*;
import util.PagingUtil;

//@Controller
public class MybatisController2 {

	
	private SqlSession sqlSession;
	 
	
	// 방명록 리스트 1차버전
	//@RequestMapping("/mybatis/list.do")
	public String list(Model model, HttpServletRequest req) {
		
		int totalRecordCount =
			sqlSession.getMapper(MybatisDAOImpl2.class).getTotalCount();
		
		int pageSize = 4;   
		int blockPage = 2;
		  
		int totalPage =
			(int)Math.ceil((double)totalRecordCount/pageSize);
		
		int nowPage = req.getParameter("nowPage")==null ? 1:
			Integer.parseInt(req.getParameter("nowPage"));
		
		int start = (nowPage-1)*pageSize+1;
		int end = nowPage*pageSize;
		
		ArrayList<MyBoardDTO> lists
			= sqlSession.getMapper(MybatisDAOImpl2.class).listPage(start, end);
		
		String pagingImg = PagingUtil.pagingImg(
				totalRecordCount, pageSize, blockPage, nowPage, req.getContextPath()+"/mybatis/list.do");
		
		model.addAttribute("pagingImg",pagingImg);
		
		for(MyBoardDTO dto: lists) {
			String temp = 
					dto.getContents().replace("\r\n", "<br/>");
			dto.setContents(temp);
		}
		
		model.addAttribute("lists",lists);
		
		return "07Mybatis/list";
		
		
	}
	
	
	
}
