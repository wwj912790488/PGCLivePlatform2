package com.arcvideo.pgcliveplatformserver;

import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.user.TenantDto;
import com.arcvideo.pgcliveplatformserver.model.user.UserDto;
import com.arcvideo.pgcliveplatformserver.service.user.ExternalUserService;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PgcLivePlatformServerApplicationTests {
	Logger logger = LoggerFactory.getLogger(PgcLivePlatformServerApplicationTests.class);
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	ExternalUserService externalUserService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void externalUsers() throws JSONException {
		List<UserDto> userDtos = externalUserService.findExternalUsers(null);
		logger.info(userDtos.toString());
	}

	@Test
	public void externalCompany() throws JSONException {
		List<TenantDto> companyDtoList = externalUserService.findExternalTenants();
		logger.info(companyDtoList.toString());
	}

	@Test
	public void syncUserTest() {
		externalUserService.syncUser();
	}

	@Test
	public void aaa() {
		List<UserDto> aa = externalUserService.findExternalUsers("la");
		logger.info(aa.toString());
	}

	@Test
	public void testHttp() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.setConnection("close");
		HttpEntity entity = new HttpEntity<>(null, headers);
		for (int i = 0; i < 10; i++) {
			if (i%2 == 0) {
				ResponseEntity<String> response = restTemplate.postForEntity("http://172.17.228.103:7102/ipswitch/heart", entity, String.class);
				System.out.println(response);
			} else {
				ResponseEntity<String> response = restTemplate.postForEntity("http://172.17.228.103:7102/ipswitch/status", entity, String.class);
				System.out.println(response);
			}
		}
	}

	@Test
	public void testRegEx() {
		boolean udpFlag = "udp://2.1.1.2:1234".matches(CommonConstants.RegEx_UDP);
		System.out.println(udpFlag);
		boolean rtmpFlag = "rtmp://ww.ds.ds.fs".matches(CommonConstants.RegEx_RTMP);
		System.out.println(rtmpFlag);
		boolean hlsFlag = "http://wwgddsdwadwd.m3u8".matches(CommonConstants.RegEx_HLS);
		System.out.println(hlsFlag);
	}

}
