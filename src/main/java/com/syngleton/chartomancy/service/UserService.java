package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository)  {
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User create(String email, String password) {

        User user = getByEmail(email);

        if (user == null)    {
            user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user = userRepository.save(user);
        } else if (!user.isEnabled()) {
            user.setEnabled(true);
        } else {
            user = null;
        }
        return user;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User update(String email, User newUser) {

        User user = getByEmailAndEnabled(email);

        if (user != null)    {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user = userRepository.save(user);
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional(readOnly = true)
    public User getByEmailAndEnabled(String email) {
        return userRepository.findByEmailAndEnabled(email, true);
    }
}
