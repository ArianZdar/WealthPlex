package com.wealthPlex.WealthPlex;

import com.wealthPlex.WealthPlex.core.models.Stock;
import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.models.WatchedStock;
import com.wealthPlex.WealthPlex.core.repositories.UserRepository;
import com.wealthPlex.WealthPlex.core.services.StockPriceService;
import com.wealthPlex.WealthPlex.core.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.util.List;

@SpringBootTest
class manualTests {

	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	StockPriceService stockPriceService;

	@Test
	void manualTests() {
		Stock newStock = new Stock();
		newStock.setPrice(20.0D);
		newStock.setSymbol("NVDA");
		newStock.setAmount(2000);
		WatchedStock watchedStock = new WatchedStock();
		watchedStock.setSymbol("NVDA");
		List<WatchedStock> watchlist = List.of(watchedStock);
		User user = new User();
		user.setUsername("admin");
		user.setPassword("admin");
		user.setId("admin");
		user.setStocks(List.of(newStock));
		user.setProfit(0.0D);
		user.setWatchlist(watchlist);
		userRepository.saveDocumentWithId(user.getId(),user);
		Stock newStock2 = new Stock();
		newStock2.setPrice(25.0D);
		newStock.setSymbol("NVDA");
		newStock.setAmount(2000);

		try {
			userService.buyStock("admin","NVDA",25.0D,2000);
			userService.sellStock("admin","NVDA",25.0D,2000);
		} catch (FileNotFoundException f) {
			System.out.println(f.getMessage());
		}

		User FetchedUser = (User) userRepository.getDocumentById("admin");
		System.out.println(userRepository.getAsMap(FetchedUser));
		stockPriceService.getStockPrice("NVDA");

	}

}
