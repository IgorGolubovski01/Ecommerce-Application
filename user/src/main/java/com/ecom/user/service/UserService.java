package com.ecom.user.service;

import com.ecom.user.dto.AddressDto;
import com.ecom.user.dto.UserRequest;
import com.ecom.user.dto.UserResponse;
import com.ecom.user.model.Address;
import com.ecom.user.model.User;
import com.ecom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserRequest request) {
        User user = new User();
        updateUserFromRequest(user, request);
        userRepository.save(user);
    }

    public ResponseEntity<UserResponse> getUserById(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ?
                mapToUserResponse(user) :
                null;
    }

    private void updateUserFromRequest(User user, UserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if(request.getAddress() != null) {
            Address address = new Address();
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setState(request.getAddress().getState());
            address.setCountry(request.getAddress().getCountry());
            address.setZipcode(request.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    private ResponseEntity<UserResponse> mapToUserResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if (user.getAddress() != null) {
            AddressDto address = new AddressDto();
            address.setId(user.getAddress().getId());
            address.setStreet(user.getAddress().getStreet());
            address.setCity(user.getAddress().getCity());
            address.setState(user.getAddress().getState());
            address.setCountry(user.getAddress().getCountry());
            address.setZipcode(user.getAddress().getZipcode());
            response.setAddressDto(address);
        }

        return ResponseEntity.ok(response);
    }

}
