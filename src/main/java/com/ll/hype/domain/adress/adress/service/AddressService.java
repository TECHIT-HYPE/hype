package com.ll.hype.domain.adress.adress.service;

import com.ll.hype.domain.adress.adress.dto.AddressRequest;
import com.ll.hype.domain.adress.adress.dto.AddressResponse;
import com.ll.hype.domain.adress.adress.entity.Address;
import com.ll.hype.domain.adress.adress.repository.AddressRepository;
import com.ll.hype.global.security.authentication.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {
    private final AddressRepository addressRepository;

    public List<AddressResponse> getMyAddressList(Long memberId) {
        List<AddressResponse> addressList = new ArrayList<>();
        for (Address address : addressRepository.findByMemberIdOrderByIsPrimaryDescCreateDateDesc(memberId)) {
            addressList.add(AddressResponse.of(address));
        }
        return addressList;
    }

    @Transactional
    public void createAddress(UserPrincipal userPrincipal, AddressRequest addressRequest) {
        Address address = AddressRequest.toEntity(addressRequest, userPrincipal.getMember());

        // 대표주소 추가 시 기존 대표주소 false 처리
        if (addressRequest.isPrimary()) {
            addressRepository.updateIsPrimary(userPrincipal.getMember().getId());
        }

        addressRepository.save(address);
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    @Transactional
    public void modifyAddress(Long id, AddressRequest addressRequest, Long memberId) {

        // 대표주소 추가 시 기존 대표주소 false 처리
        if (addressRequest.isPrimary()) {
            addressRepository.updateIsPrimary(memberId);
        }

        Address address = addressRepository.findById(id).get();

        address.change(addressRequest.getAddressName(),
                addressRequest.getPostcode(),
                addressRequest.getAddress(),
                addressRequest.getDetailAddress(),
                addressRequest.getExtraAddress(),
                addressRequest.isPrimary());
    }
}
