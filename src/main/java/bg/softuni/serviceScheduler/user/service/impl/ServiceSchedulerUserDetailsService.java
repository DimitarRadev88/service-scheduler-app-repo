package bg.softuni.serviceScheduler.user.service.impl;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServiceSchedulerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public ServiceSchedulerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(o -> new ServiceSchedulerUserDetails(
                        o.getId(),
                        o.getUsername(),
                        o.getPassword(),
                        o.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name())).toList()
                )).orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

}
