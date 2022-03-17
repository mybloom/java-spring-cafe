package com.kakao.cafe.domain.users;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryUsersRepository implements UsersRepository {

	private List<Users> usersList = new ArrayList<>();

	public List<Users> findAll() {
		return usersList;
	}

	public void save(Users user) {
		usersList.add(user);
	}

	public Users findByUserId(String userId) {
		Users resultUser = new Users();
		resultUser.setUserId(userId);
		for (Users user : usersList) {
			if(user.equals(resultUser)){
				resultUser = user;
			}
		}
		return resultUser;
	}
}