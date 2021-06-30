package review;

import java.util.ArrayList;

import mybatis.MyBoardDTO;

public interface MybatisDAOImpl2 {

	public int getTotalCount();
	
	public ArrayList<MyBoardDTO> listPage(int s, int e);
	
	
	
}
