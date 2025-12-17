package com.example.demo.service;

import java.util.Collections;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate; 
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final SqlSessionTemplate sql;

    /** OAuth2 사용자 로드 및 회원가입 처리 **/
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        String email = "kakao_" + attributes.get("id"); 
        String nickname = (String) profile.get("nickname");

        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);

        if (customer == null) {
            customer = new CustomerDTO();
            customer.setName(nickname);
            customer.setEmail(email);
            customer.setPhone(""); 
            customer.setAddress(""); 
            sql.insert("Customer.insert", customer); 
        }
        
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id"
        );
    }
}