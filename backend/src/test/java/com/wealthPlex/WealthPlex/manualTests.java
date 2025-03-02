//package com.wealthPlex.WealthPlex;
//
//
//import com.wealthPlex.WealthPlex.core.services.StockApiService;
//import org.json.JSONObject;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//class manualTests {
//
//
//
//	@Test
//	void manualTests()  {
//
//        StockApiService service =  new StockApiService();
//
//    }
//
//    @Test
//    void manualTests2()  {
//
//
//        String url = "https://finnhub.io/api/v1/quote/?symbol=NVDA&token=cuqn8o9r01qsd02fanbgcuqn8o9r01qsd02fanc0";
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)")
//                .GET()
//                .build();
//
//        String body = "";
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            body = response.body();
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("Error fetching stock history: " + e.getMessage());
//        }
//
//        JSONObject data = new JSONObject(body);
//        System.out.println("API Response: " + data.toString());
//
//    }
//
//    @Test
//    void manualTests3()  {
//        String url = "https://query2.finance.yahoo.com/v1/test/getcrumb";
//        String headers = "";
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .GET()
//                .build();
//
//        String body = "";
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            body = response.body();
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("Error fetching stock history: " + e.getMessage());
//        }
//
//
//    }
//
//}
