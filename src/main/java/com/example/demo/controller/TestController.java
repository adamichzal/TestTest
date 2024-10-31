package com.example.demo.controller;

import com.example.demo.config.InvalidPageRequestException;
import com.example.demo.domain.TestRes;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class TestController {

    @Autowired
    RestTemplate restTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(path = "/data")
    public ResponseEntity<Page<TestRes>> getData(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<List<TestRes>> entity = new HttpEntity<List<TestRes>>(headers);

        List<Map<String, Object>> testRes = restTemplate
                .exchange("https://jsonplaceholder.typicode.com/posts", HttpMethod.GET, entity, List.class)
                .getBody();

        List<TestRes> filteredData = testRes.stream()
                .map(data -> new TestRes(
                        Integer.valueOf(data.get("id").toString()),
                        data.get("title").toString()))
                .collect(Collectors.toList());


        int totalSize = filteredData .size();
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalSize);
        List<TestRes> paginatedData = filteredData.subList(startIndex, endIndex);

        Page<TestRes> resDtos = new PageImpl<>(paginatedData, PageRequest.of(page, size), totalSize);
        return ResponseEntity.ok().body(resDtos);
    }

    // Validate page and size
    private void validatePageAndSize(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidPageRequestException("Page and size parameters must be positive");
        }
    }
}
