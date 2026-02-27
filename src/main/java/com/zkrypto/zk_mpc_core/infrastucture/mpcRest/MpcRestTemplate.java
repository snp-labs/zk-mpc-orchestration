package com.zkrypto.zk_mpc_core.infrastucture.mpcRest;

import com.zkrypto.zk_mpc_core.application.mpcRest.MpcRestPort;
import com.zkrypto.zk_mpc_core.infrastucture.mpcRest.dto.response.ApiResponse;
import com.zkrypto.zk_mpc_core.infrastucture.mpcRest.dto.response.PublicKeyResponse;
import com.zkrypto.zk_mpc_core.infrastucture.mpcRest.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;  // 추가
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpcRestTemplate implements MpcRestPort {
    private final RestTemplate restTemplate;

    // 수정: final 제거, @Value 추가
    @Value("${mpc.api.url:http://localhost:8080/api/v1}")
    private String url;

    @Override
    public void setAddress(String sid, String publicKey, String address) {
        log.info("주소, 퍼블릭키 업데이트 요청 : {}", address );

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("groupId", sid);
        requestBody.put("address", address);
        requestBody.put("publicKey", publicKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                url + "/group",
                HttpMethod.PATCH,
                entity,
                Void.class
        );
    }

    @Override
    public String getPublicKey(String sid) {
        log.info("퍼블릭키 조회 요청");

        ResponseEntity<ApiResponse<PublicKeyResponse>> response = restTemplate.exchange(
                url + "/group/" + sid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<PublicKeyResponse>>() {}
        );

        return response.getBody().getData().publicKey();
    }

    @Override
    public TransactionResponse getLastTransaction(String sid) {
        log.info("트랜잭션 조회 요청");

        ResponseEntity<ApiResponse<TransactionResponse>> response = restTemplate.exchange(
                url + "/transaction/group/" + sid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {}
        );

        return response.getBody().getData();
    }

    @Override
    public void updateTransaction(String transactionId, String transactionHash) {
        log.info("트랜잭션 상태 변경 요청");

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("transactionId", transactionId);
        requestBody.put("txId", transactionHash);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                url + "/transaction",
                HttpMethod.PATCH,
                entity,
                Void.class
        );
    }
}