package com.job.careerintelligence;

import com.job.careerintelligence.service.HtmlJobContentParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HtmlJobContentParserTest {

    private final HtmlJobContentParser parser = new HtmlJobContentParser();

    @Test
    public void testH2PlusParagraph() {
        String html = "<h2>About the Role</h2><p>This is a great role.</p>";
        List<HtmlJobContentParser.ParsedSection> sections = parser.parse(html);
        
        assertEquals(1, sections.size());
        assertEquals("About the Role", sections.get(0).getSourceSectionName());
        assertEquals(1, sections.get(0).getExtractedContent().size());
        assertEquals("This is a great role.", sections.get(0).getExtractedContent().get(0));
    }

    @Test
    public void testH3PlusUnorderedList() {
        String html = "<h3>Qualifications</h3><ul><li>Java</li><li>Spring</li></ul>";
        List<HtmlJobContentParser.ParsedSection> sections = parser.parse(html);
        
        assertEquals(1, sections.size());
        assertEquals("Qualifications", sections.get(0).getSourceSectionName());
        assertEquals(2, sections.get(0).getExtractedContent().size());
        assertEquals("Java", sections.get(0).getExtractedContent().get(0));
        assertEquals("Spring", sections.get(0).getExtractedContent().get(1));
    }

    @Test
    public void testStrongPlusParagraph() {
        String html = "<p><strong>What You'll Do</strong></p><p>Write code.</p>";
        List<HtmlJobContentParser.ParsedSection> sections = parser.parse(html);
        
        assertEquals(1, sections.size());
        assertEquals("What You'll Do", sections.get(0).getSourceSectionName());
        assertEquals(1, sections.get(0).getExtractedContent().size());
        assertEquals("Write code.", sections.get(0).getExtractedContent().get(0));
    }

    @Test
    public void testNestedHtml() {
        String html = "<h3>Requirements</h3><div><ul><li><span>Python</span></li></ul></div>";
        List<HtmlJobContentParser.ParsedSection> sections = parser.parse(html);
        
        assertEquals(1, sections.size());
        assertEquals("Requirements", sections.get(0).getSourceSectionName());
        assertEquals(1, sections.get(0).getExtractedContent().size());
        assertEquals("Python", sections.get(0).getExtractedContent().get(0));
    }
    
    @Test
    public void testMultipleConsecutiveSections() {
        String html = "<h3>Section 1</h3><p>Text 1</p><h3>Section 2</h3><p>Text 2</p>";
        List<HtmlJobContentParser.ParsedSection> sections = parser.parse(html);
        
        assertEquals(2, sections.size());
        assertEquals("Section 1", sections.get(0).getSourceSectionName());
        assertEquals("Text 1", sections.get(0).getExtractedContent().get(0));
        assertEquals("Section 2", sections.get(1).getSourceSectionName());
        assertEquals("Text 2", sections.get(1).getExtractedContent().get(0));
    }

    @Test
    public void testMalformedStructure() {
        // Missing closing tags
        String html = "<h3>Qualifications<ul><li>Oops";
        List<HtmlJobContentParser.ParsedSection> sections = parser.parse(html);
        
        assertEquals(1, sections.size());
        assertEquals("Qualifications", sections.get(0).getSourceSectionName());
        assertEquals("Oops", sections.get(0).getExtractedContent().get(0));
    }
}
