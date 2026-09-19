package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.UserRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.UserResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.UserUpdateRequestDto;
import com.vibolSEN.inventory_mgt_system.model.enums.UserStatus;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto requestDto);

    List<UserResponseDto> getAllUsers();

    PagedResponse<UserResponseDto> getUsersPaginated(int page, int size, String sortBy, String sortDir);

    UserResponseDto getUserById(Long id);

    UserResponseDto getUserByUsername(String username);

    UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto);

    UserResponseDto updateUserStatus(Long id, UserStatus status);

    void deleteUser(Long id);

    List<UserResponseDto> searchUsers(String keyword);
}
