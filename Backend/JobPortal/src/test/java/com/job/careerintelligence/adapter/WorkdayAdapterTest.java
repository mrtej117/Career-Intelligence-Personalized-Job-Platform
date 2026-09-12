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

public class WorkdayAdapterTest {

    private WorkdayAdapter adapter;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        adapter = new WorkdayAdapter();
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(adapter, "restTemplate");
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testDiscoverJobs() {
        String jsonResponse = "{ \"jobPostings\": [ { \"externalPath\": \"/job/123\", \"title\": \"Java Developer\", \"locationsText\": \"Pune, India\", \"timeType\": \"Full time\" } ] }";
        mockServer.expect(requestTo("https://tenant.myworkdayjobs.com/wday/cxs/tenant/site/jobs"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        List<RawJobDTO> jobs = adapter.discoverJobs("tenant/site");
        mockServer.verify();

        assertEquals(1, jobs.size());
        RawJobDTO job = jobs.get(0);
        assertEquals("/job/123", job.getExternalJobId());
        assertEquals("Java Developer", job.getRawTitle());
        assertEquals("Pune, India", job.getRawLocation());
        assertEquals("https://tenant.myworkdayjobs.com/en-US/site/job/123", job.getSourceJobUrl());
        assertEquals("Workday", job.getSourcePlatform());
        assertEquals("tenant/site", job.getCompanyName());
    }

    @Test
    public void testFetchJobDetails() {
        String jsonResponse = "{ \"jobPostingInfo\": { \"title\": \"Java Developer\", \"location\": \"Pune, India\", \"jobDescription\": \"<p>Desc</p>\", \"jobFamilyGroup\": \"Engineering\" } }";
        mockServer.expect(requestTo("https://tenant.myworkdayjobs.com/wday/cxs/tenant/site/job/123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        RawJobDTO job = adapter.fetchJobDetails("tenant/site", "/job/123");
        mockServer.verify();

        assertEquals("/job/123", job.getExternalJobId());
        assertEquals("Java Developer", job.getRawTitle());
        assertEquals("Pune, India", job.getRawLocation());
        assertEquals("Engineering", job.getRawDepartment());
        assertEquals("<p>Desc</p>", job.getRawHtml());
    }

    @Test
    public void testDiscoverJobsMalformedBoardToken() {
        assertThrows(IllegalArgumentException.class, () -> adapter.discoverJobs("invalidtoken"));
    }
}
