package com.project.roadmapgenerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ContextConfiguration(classes = {RoadmapGeneratorApplication.class})
@EnableConfigurationProperties
public class RoadmapGeneratorApplicationTest {

	@Test
	public void contextLoads() {
	}

}
