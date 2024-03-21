package co.syngleton.chartomancer.user_management;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class DefaultUserService implements UserService {
    private final UserRepository userRepository;


}
