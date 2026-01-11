package ma.saifdine.hd.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.userservice.dtos.UserRequestDTO;
import ma.saifdine.hd.userservice.dtos.UserResponseDTO;
import ma.saifdine.hd.userservice.entity.User;
import ma.saifdine.hd.userservice.enums.UserRole;
import ma.saifdine.hd.userservice.enums.UserStatus;
import ma.saifdine.hd.userservice.exception.UserAlreadyExistsException;
import ma.saifdine.hd.userservice.exception.UserNotFoundException;
import ma.saifdine.hd.userservice.mapper.UserMapper;
import ma.saifdine.hd.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating new user with email: {}", requestDTO.getEmail());

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with email " + requestDTO.getEmail() + " already exists"
            );
        }

        User user = userMapper.toEntity(requestDTO);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(
                requestDTO.getRole() != null ? requestDTO.getRole() : UserRole.USER
        );

        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String keyword, Pageable pageable) {
        log.info("Searching users with keyword: {}", keyword);

        return userRepository.searchUsers(keyword, pageable)
                .map(userMapper::toResponseDTO);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (!user.getEmail().equals(requestDTO.getEmail()) &&
                userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email " + requestDTO.getEmail() + " is already in use"
            );
        }

        userMapper.updateEntityFromDTO(requestDTO, user);
        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with ID: {}", id);
        return userMapper.toResponseDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        userRepository.deleteById(id);

        log.info("User deleted with ID: {}", id);
    }

    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("User deactivated with ID: {}", id);
    }
}