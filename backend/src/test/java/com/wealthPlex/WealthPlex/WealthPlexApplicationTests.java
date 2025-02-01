package com.wealthPlex.WealthPlex;

import com.wealthPlex.WealthPlex.core.models.Stock;
import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.repositories.UserRepository;
import com.wealthPlex.WealthPlex.firestore.model.FirestoreImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WealthPlexApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void manualTests() {
		Stock newStock = new Stock();
		newStock.setPrice(20.3D);
		newStock.setSymbol("NVDA");
		newStock.setAmount(2000);
		User user = new User();
		user.setUsername("admin");
		user.setPassword("admin");
		user.setId("admin");
		user.setStocks(List.of(newStock));
		userRepository.saveDocumentWithId(user.getId(),user);

		User FetchedUser = (User) userRepository.getDocumentById("admin");
		System.out.println(userRepository.getAsMap(FetchedUser));


	}

}
