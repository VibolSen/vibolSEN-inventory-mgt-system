package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.UserRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.UserResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.UserUpdateRequestDto;
import com.vibolSEN.inventory_mgt_system.exception.DuplicateResourceException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceInUseException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.User;
import com.vibolSEN.inventory_mgt_system.model.enums.UserStatus;
import com.vibolSEN.inventory_mgt_system.repository.UserRepository;
import com.vibolSEN.inventory_mgt_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        String trimmedUsername = requestDto.getUsername().trim();
        if (userRepository.existsByUsernameIgnoreCase(trimmedUsername)) {
            throw new DuplicateResourceException("User", "username", trimmedUsername);
        }

        String trimmedEmail = requestDto.getEmail().trim();
        if (userRepository.existsByEmailIgnoreCase(trimmedEmail)) {
            throw new DuplicateResourceException("User", "email", trimmedEmail);
        }

        User user = User.builder()
                .username(trimmedUsername)
                .password(requestDto.getPassword())
                .fullName(requestDto.getFullName().trim())
                .email(trimmedEmail)
                .phone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null)
                .role(requestDto.getRole())
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponseDto> getUsersPaginated(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponseDto> content = userPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<UserResponseDto>builder()
                .content(content)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return mapToResponseDto(findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto) {
        User user = findUserById(id);
        String trimmedEmail = requestDto.getEmail().trim();

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(trimmedEmail, id)) {
            throw new DuplicateResourceException("User", "email", trimmedEmail);
        }

        user.setFullName(requestDto.getFullName().trim());
        user.setEmail(trimmedEmail);
        user.setPhone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null);

        if (requestDto.getRole() != null) {
            user.setRole(requestDto.getRole());
        }
        if (requestDto.getStatus() != null) {
            user.setStatus(requestDto.getStatus());
        }
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            user.setPassword(requestDto.getPassword());
        }

        User updated = userRepository.save(user);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserStatus(Long id, UserStatus status) {
        User user = findUserById(id);
        user.setStatus(status);
        User updated = userRepository.save(user);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        if (!user.getSales().isEmpty() || !user.getShipments().isEmpty() || !user.getStockTransactions().isEmpty()) {
            throw new ResourceInUseException("User", "id", id, 
                    "it has associated sales, shipments, or stock transaction records. Consider changing status to INACTIVE instead.");
        }
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }
        return userRepository.searchUsers(keyword.trim()).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserResponseDto mapToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
