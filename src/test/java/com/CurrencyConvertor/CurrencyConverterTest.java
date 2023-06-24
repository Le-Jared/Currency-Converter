package com.CurrencyConvertor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterTest {

    private CurrencyConverter converter;

    @BeforeEach
    void setUp() {
    	converter = Mockito.spy(new CurrencyConverter());
    }
    
    @Test
    void testLoadRates() {
        Map<String, Rate> mockRates = new HashMap<>();
        Rate mockRate = new Rate();
        mockRate.setAlphaCode("EUR");
        mockRate.setCode("EUR");
        mockRate.setName("Euro");
        mockRate.setRate(1.18);
        mockRate.setNumericCode("978");
        mockRates.put("eur", mockRate);

        Mockito.doReturn(mockRates).when(converter).loadJsonFile(Mockito.anyString(), Mockito.any());

        Map<String, Rate> rateMap = converter.loadRates();

        assertTrue(rateMap.containsKey("eur"));
        assertEquals("EUR", rateMap.get("eur").getCode());
        assertEquals("EUR", rateMap.get("eur").getAlphaCode());
        assertEquals("978", rateMap.get("eur").getNumericCode());
        assertEquals("Euro", rateMap.get("eur").getName());
        assertEquals(1.18, rateMap.get("eur").getRate());
    }
    
    @Test
    void testProcessTransactions() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        user.getWallet().put("usd", 1000.0);
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();
        Rate rate = new Rate();
        rate.setAlphaCode("EUR");
        rate.setCode("EUR");
        rate.setName("Euro");
        rate.setRate(0.85); 
        rate.setNumericCode("978");
        rateMap.put("eur", rate);

        Mockito.doReturn("Alice USD EUR 500\n").when(converter).readFile(Mockito.any());

        converter.processTransactions(userMap, rateMap);

        assertEquals(500.0, user.getWallet().get("usd")); 
        assertEquals(500 * 0.85, user.getWallet().get("eur")); 
    }

    @Test
    void testUpdateJsonFile() {
        List<User> users = Arrays.asList(new User("Alice", new HashMap<>()), new User("Bob", new HashMap<>()));

        try {
            converter.updateJsonFile("src/main/java/com/fdm/CurrencyConvertor/users.json", users);
            Mockito.verify(converter, Mockito.times(1)).writeFile(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void testProcessTransactionsInvalidLine() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();

        Mockito.doReturn("Alice\n").when(converter).readFile(Mockito.any());

        converter.processTransactions(userMap, rateMap);

        assertEquals(0, user.getWallet().size()); 
    }

    @Test
    void testProcessTransactionsInsufficientFunds() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        user.getWallet().put("usd", 100.0);
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();

        Mockito.doReturn("Alice USD EUR 200\n").when(converter).readFile(Mockito.any());

        converter.processTransactions(userMap, rateMap);

        assertEquals(100.0, user.getWallet().get("usd"));
    }

    @Test
    void testProcessTransactionsSameCurrency() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        user.getWallet().put("usd", 100.0);
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();

        Mockito.doReturn("Alice USD USD 50\n").when(converter).readFile(Mockito.any());

        converter.processTransactions(userMap, rateMap);

        assertEquals(100.0, user.getWallet().get("usd")); 
    }


    @Test
    void testGenerateUserMapWithEmptyList() {
        List<User> users = Arrays.asList();

        Map<String, User> userMap = converter.generateUserMap(users);

        assertTrue(userMap.isEmpty());
    }


    @Test
    void testProcessTransactionsUnknownUser() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();

        Mockito.doReturn("Bob USD EUR 100\n").when(converter).readFile(Mockito.any());

        converter.processTransactions(userMap, rateMap);

        assertEquals(0, user.getWallet().size()); 
    }

    @Test
    void testUpdateJsonFileException() {
        List<User> users = Arrays.asList(new User("Alice", new HashMap<>()), new User("Bob", new HashMap<>()));

        Mockito.doThrow(new RuntimeException()).when(converter).updateJsonFile(Mockito.anyString(), Mockito.any());

        assertThrows(RuntimeException.class, () -> converter.updateJsonFile("invalid_path.json", users));
    }
    
    @Test
    void testProcessTransactionLineInvalidAmount() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        user.getWallet().put("usd", 100.0);
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();

        converter.processTransactionLine("Alice USD EUR hundred", userMap, rateMap);

        assertEquals(100.0, user.getWallet().get("usd")); 
    }
    
    @Test
    void testProcessTransactionsInvalidCurrency() {
        Map<String, User> userMap = new HashMap<>();
        User user = new User("Alice", new HashMap<>());
        user.getWallet().put("usd", 100.0);
        userMap.put("Alice", user);

        Map<String, Rate> rateMap = new HashMap<>();
        Rate rate = new Rate();
        rate.setAlphaCode("EUR");
        rate.setCode("EUR");
        rate.setName("Euro");
        rate.setRate(0.85);
        rate.setNumericCode("978");
        rateMap.put("eur", rate);

       
        Mockito.doReturn("Alice USD XXX 50\n").when(converter).readFile(Mockito.any());

        converter.processTransactions(userMap, rateMap);

        assertEquals(100.0, user.getWallet().get("usd")); 
    } 
}
