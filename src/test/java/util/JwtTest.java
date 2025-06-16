package util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈玉皓
 * @date 2025/6/15 19:30
 * @description: TODO
 */
public class JwtTest {

    @Test
    public void testGen() {
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", 1);
        claims.put("name", "xixix");

        //生成jwt的代码
        String token = JWT.create()
                .withClaim("user", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12)) //添加过期时间
                .sign(Algorithm.HMAC256("xiajibaxiede"));//制定算法配置秘钥
        System.out.println(token);

    }

    @Test
    public void testParse() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" + ".eyJ1c2VyIjp7Im5hbWUiOiJ4aXhpeCIsImlkIjoxfSwiZXhwIjoxNzUwMDMwNjU3fQ" + ".0wGSCuVJiB-5u0ZaooeZqnnidwxqOVnOhDQeYeBng6c";
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("xiajibaxiede")).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        Map<String, Claim> claims = decodedJWT.getClaims();
        System.out.println(claims.get("user"));

    }
}
