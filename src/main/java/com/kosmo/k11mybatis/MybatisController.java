package com.kosmo.k11mybatis;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import mybatis.MemberVO;
import mybatis.MyBoardDTO;
import mybatis.MybatisDAOImpl;
import mybatis.MybatisMemberImpl;
import mybatis.ParameterDTO;
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
		
		ParameterDTO parameterDTO = new ParameterDTO();
		parameterDTO.setSearchField(req.getParameter("searchField"));
		//parameterDTO.setSearchTxt(req.getParameter("searchTxt"));
		//System.out.println("검색어 : "+parameterDTO.getSearchTxt());
		
		
		// 검색어를 스페이스로 구분하는 경우
		ArrayList<String> searchLists = null;
		if(req.getParameter("searchTxt")!=null) {
			
			searchLists = new ArrayList<String>();
			String[] sTxtArray = req.getParameter("searchTxt").split(" ");
			for(String str: sTxtArray) {
				searchLists.add(str);
			}
			
			 
		}
		
		parameterDTO.setSearchTxt(searchLists);
		System.out.println("검색어 : " + searchLists);
		
		
		// 방명록 테이블의 게시물 개수 카운트
		/*
		컨트롤러에서 서비스 역할을 하는 인터페이스에 정의된
		추상메소드를 호출한다. 그러면 mapper에 정의된 쿼리문이
		실행되는 형식으로 동작한다.
		 */
		int totalRecordCount = 
				sqlSession.getMapper(MybatisDAOImpl.class).getTotalCount(parameterDTO);
		
			
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
		
		parameterDTO.setStart(start);
		parameterDTO.setEnd(end);
		
		
		
		// 목록에 출력할 게시물을 얻어오기 위한 쿼리실행
		ArrayList<MyBoardDTO> lists 
			= sqlSession.getMapper(MybatisDAOImpl.class).listPage(parameterDTO);
		
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
	
	// 글쓰기 페이지 매핑
	@RequestMapping("/mybatis/write.do")
	public String write(Model model, HttpSession session, 
			HttpServletRequest req) {
		/*
			매핑된 메소드 내에서 session 내장객체를 사용하기 위해
			매개변수로 선언해준다.
		 */
		
		// 세션영역에 회원인증 관련 속성이 있는지 확인
		if(session.getAttribute("siteUserInfo")==null) {
			
			/*
			글쓰기 페이지 진입시 세션없이 로그인 페이지로 이동할때
			로그인 후에 다시 글쓰기 페이지로 돌아가기 위해
			backUrl을 지정해준다.
			 */
			model.addAttribute("backUrl","07Mybatis/write");
			return "redirect:login.do";
		}

		// 로그인 된 상태라면 쓰기 페이지로 즉시 진입한다.
		return "07Mybatis/write";
		
	}
	
	// 로그인 페이지 매핑
	@RequestMapping("/mybatis/login.do")
	public String login(Model model) {
		return "07Mybatis/login";
	}
	
	
	// 로그인 처리
	@RequestMapping("/mybatis/loginAction.do")
	public ModelAndView loginAction(HttpServletRequest req, HttpSession session) {

		// 파라미터로 전달된 id, pass를 받아 login() 메소드 호출
		MemberVO vo =
			sqlSession.getMapper(MybatisMemberImpl.class).login(
					req.getParameter("id"), 
					req.getParameter("pass")
					);
		
		ModelAndView mv = new ModelAndView();
		if(vo==null) {
			// 로그인에 실패한 경우 실패 메세지를 model에 저장한 후
			mv.addObject("LoginNG","아이디/패스워드가 틀렸습니다.");
			
			// 다시 로그인 페이지를 호출한다.
			mv.setViewName("07Mybatis/login");
			
			return mv;
		}else {
			// 로그인에 성공한 경우 세션영역에 속성을 저장한다
			session.setAttribute("siteUserInfo", vo);
		}
		
		// 로그인 후 돌아갈 페이지가 없는 경우에는 로그인 페이지로 이동한다.
		String backUrl = req.getParameter("backUrl");
		if(backUrl==null || backUrl.equals("")) {
			mv.setViewName("07Mybatis/login");
		}else {
			mv.setViewName(backUrl);
		}
		return mv;
		
		
		
	}
	
		
	@RequestMapping("/mybatis/logout.do")
	public String logout(Model model, HttpSession session) {
		
		session.removeAttribute("siteUserInfo");
		
		
		return "redirect:list.do";
	}
		
	
	// 글쓰기 처리
	@RequestMapping(value="/mybatis/writeAction.do",
			method=RequestMethod.POST)
	public String writeAction(Model model, HttpServletRequest req,
			HttpSession session) {
		// 글쓰기 처리시 로그인 체크 후 진행해야함
		if(session.getAttribute("siteUserInfo")==null) {
			
			return "redirect:login.do";
		
		}
		
		// 글쓰기 처리를 위한 메소드 호출
		//int result = 
		sqlSession.getMapper(MybatisDAOImpl.class).write(
				req.getParameter("name"),
				req.getParameter("contents"),
				((MemberVO)session.getAttribute("siteUserInfo")).getId()
				); // 세션영역에 저장된 VO객체로 부터 아이디를 얻어와 파라미터로 사용
		//System.out.println("입력결과"+result);
		
		// 쓰기 처리를 완료한 후 리스트로 이동
		return "redirect:list.do";
		
	}
	
	// 수정페이지
	@RequestMapping("/mybatis/modify.do")
	public String modify(Model model, HttpServletRequest req,
			HttpSession session) {
		
		// 수정 페이지 진입 전 로그인 확인
		if(session.getAttribute("siteUserInfo")==null) {
			return "redirect:login.do";
		}
		
		// Mapper쪽으로 전달할 파라미터를 저장할 용도의 DTO객체 생성
		ParameterDTO parameterDTO = new ParameterDTO();
		parameterDTO.setBoard_idx(req.getParameter("idx")); // 일련번호
		parameterDTO.setUser_id(
				((MemberVO)session.getAttribute("siteUserInfo")).getId()); // 사용자아이디

		// Mapper 호출시 DTO객체를 파라미터로 전달
		MyBoardDTO dto =
			sqlSession.getMapper(MybatisDAOImpl.class).view(parameterDTO);
		
		model.addAttribute("dto",dto);
		
		return "07Mybatis/modify";
			
		
		
	}     
	
	// 수정 처리
	@RequestMapping("/mybatis/modifyAction.do")
	public String modifyAction(HttpSession session, MyBoardDTO myBoardDTO) {
		
		// 수정페이지에서 전송된 폼값은 커맨드객체를 통해 한꺼번에 받음
		
		// 수정 처리 전 로그인 체크
		if(session.getAttribute("siteUserInfo")==null) {
			
			return "redirect:login.do";
			
			
		}
		
		// 수정 처리를 위해 modify 메소드 호출
		sqlSession.getMapper(MybatisDAOImpl.class).modify(myBoardDTO);
		
		return "redirect:list.do";
		
		   
		
	}
	
	
	@RequestMapping("/mybatis/delete.do")
	public String delete(HttpServletRequest req, HttpSession session) {
		
		if(session.getAttribute("siteUserInfo")==null) {
			return "redirect:login.do";
			
		}
		
		sqlSession.getMapper(MybatisDAOImpl.class).delete(
				req.getParameter("idx"),
				((MemberVO)session.getAttribute("siteUserInfo")).getId()
				);
		
		return "redirect:list.do";
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
