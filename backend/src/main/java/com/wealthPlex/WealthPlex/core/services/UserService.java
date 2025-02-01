package com.wealthPlex.WealthPlex.core.services;


import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Map<String, Object> getUserByUsername(String username) throws FileNotFoundException {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (doesNotExist) {
            throw new FileNotFoundException("User " + username +" does not exist!");
        }
        User user = (User) userRepository.getDocumentById(username);
        return userRepository.getAsMap(user);
    }


    public List<Map<String, Object>> getUserStocks(String username) throws FileNotFoundException {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (doesNotExist) {
            throw new FileNotFoundException("User " + username +" does not exist!");
        }
        User user = (User) userRepository.getDocumentById(username);
        return user.getStocks().stream().map(stock -> userRepository.getStockAsMap(stock)).toList();
    }

    public Map<String, Object> signUp(String username, String password) {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (!doesNotExist) {
            throw new IllegalArgumentException("username : " + username +" not available!");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(username);

        userRepository.saveDocumentWithId(username,user);
        return userRepository.getAsMap(user);
    }

    public Map<String, Object> login(String username, String password) {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (doesNotExist) {
            throw new IllegalArgumentException("User " + username +" does not exist!");
        }

        User user = (User) userRepository.getDocumentById(username);
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password!");
        } else {
            return userRepository.getAsMap(user);
        }
    }




}
