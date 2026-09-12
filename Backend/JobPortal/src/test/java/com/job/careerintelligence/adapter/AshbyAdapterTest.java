package com.job.careerintelligence.adapter;

import com.job.careerintelligence.dto.RawJobDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class AshbyAdapterTest {

    private AshbyAdapter adapter;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        adapter = new AshbyAdapter();
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(adapter, "restTemplate");
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testDiscoverJobs() {
        String jsonResponse = "{ \"jobs\": [ { \"id\": \"123\", \"title\": \"Software Engineer\", \"location\": \"Remote India\", \"jobUrl\": \"https://ashbyhq.com/job/123\", \"descriptionHtml\": \"<p>Role</p>\", \"department\": \"Engineering\" } ] }";
        mockServer.expect(requestTo("https://api.ashbyhq.com/posting-api/job-board/testcompany"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        List<RawJobDTO> jobs = adapter.discoverJobs("testcompany");
        mockServer.verify();

        assertEquals(1, jobs.size());
        RawJobDTO job = jobs.get(0);
        assertEquals("123", job.getExternalJobId());
        assertEquals("Software Engineer", job.getRawTitle());
        assertEquals("Remote India", job.getRawLocation());
        assertEquals("https://ashbyhq.com/job/123", job.getSourceJobUrl());
        assertEquals("Ashby", job.getSourcePlatform());
        assertEquals("testcompany", job.getCompanyName());
    }

    @Test
    public void testFetchJobDetails() {
        String jsonResponse = "{ \"jobs\": [ { \"id\": \"123\", \"title\": \"Software Engineer\", \"location\": \"Remote India\", \"jobUrl\": \"https://ashbyhq.com/job/123\", \"descriptionHtml\": \"<p>Role</p>\", \"department\": \"Engineering\" } ] }";
        mockServer.expect(requestTo("https://api.ashbyhq.com/posting-api/job-board/testcompany"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        RawJobDTO job = adapter.fetchJobDetails("testcompany", "123");
        mockServer.verify();

        assertEquals("123", job.getExternalJobId());
        assertEquals("Software Engineer", job.getRawTitle());
        assertEquals("Engineering", job.getRawDepartment());
        assertEquals("<p>Role</p>", job.getRawHtml());
    }
}
