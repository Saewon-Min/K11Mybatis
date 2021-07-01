package lombok.test;


import lombok.Data;


/*
롬복 라이브러리를 추가하면 아래와 같이 @Data어노테이션 만으로
getter/setter/constructor/toString 등을 자동으로 생성해준다
 */
@Data
public class UserDTO {

	public String name;
	public int age;
	private String hobbys;
	
	
	
}
