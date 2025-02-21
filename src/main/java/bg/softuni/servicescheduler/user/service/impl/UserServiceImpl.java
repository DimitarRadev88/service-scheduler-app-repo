package bg.softuni.servicescheduler.user.service.impl;

import bg.softuni.servicescheduler.user.dao.UserRepository;
import bg.softuni.servicescheduler.user.service.UserService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
