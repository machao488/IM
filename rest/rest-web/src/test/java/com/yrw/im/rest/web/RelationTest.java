package com.yrw.im.rest.web;

import com.yrw.im.common.domain.ResultWrapper;
import com.yrw.im.rest.web.vo.RelationReq;
import com.yrw.im.rest.web.vo.UserReq;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Date: 2019-06-23
 * Time: 20:21
 *
 * @author yrw
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class RelationTest {
    @Autowired
    private WebTestClient webClient;

    private static String token;

    @BeforeClass
    public static void setup() {
        System.setProperty("config", "/Users/yrw/Desktop/github/IM/rest/rest-web/src/test/resources/rest-test.properties");
    }

    @AfterClass
    public static void close(){

    }

    @Before
    public void setupMethod() {
        UserReq req = new UserReq();
        req.setUsername("yuanrw");
        req.setPwd(DigestUtils.sha256Hex("123abc".getBytes(CharsetUtil.UTF_8)));

        ResultWrapper res = webClient.post().uri("/user/login")
            .body(BodyInserters.fromPublisher(Mono.just(req), UserReq.class))
            .exchange()
            .returnResult(ResultWrapper.class).getResponseBody().blockFirst();

        token = ((HashMap<String, String>) res.getData()).get("token");
    }

    @Test
    public void testListFriends() {
        webClient.get().uri("/relation/1119861162352148481")
            .header("token", token)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.msg").isEqualTo("SUCCESS")
            .jsonPath("$.data[0].id").isNumber()
            .jsonPath("$.data[0].userId1").isNumber()
            .jsonPath("$.data[0].userId2").isNumber()
            .jsonPath("$.data[0].encryptKey").exists();
    }

    @Test
    public void testGetRelation() {
        webClient.get().uri("/relation?userId1=1142773797275836418&userId2=1142784917944406018")
            .header("token", token)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.msg").isEqualTo("SUCCESS")
            .jsonPath("$.data.id").exists();
    }

//    @Test
    public void testAddNewRelation() {
        RelationReq req = new RelationReq();
        req.setUserId1(1119861162352148481L);
        req.setUserId2(1142784917944406018L);

        webClient.post().uri("/relation")
            .header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(req))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.msg").isEqualTo("SUCCESS")
            .jsonPath("$.data.id").exists();
    }

//    @Test
    public void testAddExistRelation() {
        RelationReq req = new RelationReq();
        req.setUserId1(1119861162352148481L);
        req.setUserId2(1142784917944406018L);

        webClient.post().uri("/relation")
            .header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(req))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.msg").isEqualTo("SUCCESS")
            .jsonPath("$.data.id").exists();
    }

//    @Test
    public void testAddRelationUserNotExist() {
        RelationReq req = new RelationReq();
        req.setUserId1(123L);
        req.setUserId2(1142784917944406018L);

        webClient.post().uri("/relation")
            .header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(req))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(500)
            .jsonPath("$.msg").isEqualTo("user not exist");
    }

//    @Test
    public void testDeleteRelation() {
        webClient.delete().uri("/relation/1142784914681237505")
            .header("token", token)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.msg").isEqualTo("SUCCESS");
    }
}
