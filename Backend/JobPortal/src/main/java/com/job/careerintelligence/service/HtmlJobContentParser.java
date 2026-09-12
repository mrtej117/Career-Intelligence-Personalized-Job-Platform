package com.job.careerintelligence.service;

import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HtmlJobContentParser {

    @Data
    public static class ParsedSection {
        private String sourceSectionName;
        private List<String> extractedContent = new ArrayList<>();
    }

    public List<ParsedSection> parse(String rawHtml) {
        List<ParsedSection> sections = new ArrayList<>();
        if (rawHtml == null || rawHtml.trim().isEmpty()) {
            return sections;
        }

        Document doc = Jsoup.parseBodyFragment(rawHtml);
        
        ParsedSection currentSection = new ParsedSection();
        currentSection.setSourceSectionName("UNCATEGORIZED_INTRO");
        
        // Traverse the top-level elements (often div, p, h3, ul)
        for (Element child : doc.body().children()) {
            String tagName = child.tagName().toLowerCase();
            
            if (isHeading(child)) {
                // Always add the previous section if we changed the name, even if empty, to detect 'heading without content' edge cases.
                if (!currentSection.getSourceSectionName().equals("UNCATEGORIZED_INTRO") || !currentSection.getExtractedContent().isEmpty()) {
                    sections.add(currentSection);
                }
                currentSection = new ParsedSection();
                // JSoup might have nested lists inside unclosed headers, so we should separate the immediate text from children if malformed, but for now we just use the child's own text without children
                currentSection.setSourceSectionName(cleanText(child.ownText().isEmpty() ? child.text() : child.ownText()));
                
                // If it contains a list inside the header due to malformed HTML, extract it!
                for (Element nested : child.children()) {
                    if (!nested.tagName().equals("strong") && !nested.tagName().equals("b")) {
                        extractNodeContent(nested, currentSection.getExtractedContent());
                    }
                }
            } else {
                extractNodeContent(child, currentSection.getExtractedContent());
            }
        }
        
        if (!currentSection.getSourceSectionName().equals("UNCATEGORIZED_INTRO") || !currentSection.getExtractedContent().isEmpty()) {
            sections.add(currentSection);
        }
        
        return sections;
    }
    
    private boolean isHeading(Element element) {
        String tag = element.tagName().toLowerCase();
        if (tag.matches("h[1-6]")) return true;
        
        // Sometimes companies use <p><strong>Heading</strong></p>
        if (tag.equals("p") || tag.equals("div")) {
            if (element.childrenSize() == 1) {
                String childTag = element.child(0).tagName().toLowerCase();
                if (childTag.equals("strong") || childTag.equals("b")) {
                    // It's just a bold string. Let's assume it's a header if it's relatively short
                    if (element.text().length() < 60) {
                        return true;
                    }
                }
            }
        }
        
        if (tag.equals("strong") || tag.equals("b")) {
            if (element.text().length() < 60) {
                return true;
            }
        }
        
        return false;
    }

    private void extractNodeContent(Node node, List<String> contentAcc) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text();
            if (!text.trim().isEmpty()) {
                contentAcc.add(cleanText(text));
            }
        } else if (node instanceof Element) {
            Element element = (Element) node;
            String tag = element.tagName().toLowerCase();
            
            if (tag.equals("ul") || tag.equals("ol")) {
                for (Element li : element.children()) {
                    if (li.tagName().equalsIgnoreCase("li")) {
                        String liText = cleanText(li.text());
                        if (!liText.isEmpty()) {
                            contentAcc.add(liText);
                        }
                    }
                }
            } else if (tag.equals("p") || tag.equals("div") || tag.equals("span")) {
                // Just extract text of the paragraph block
                String pText = cleanText(element.text());
                if (!pText.isEmpty()) {
                    // Prevent duplicate if we already drilled down? Actually element.text() gets all nested text.
                    // So we shouldn't drill down manually if we just take element.text()
                    contentAcc.add(pText);
                }
            } else if (tag.equals("br")) {
                // ignore
            } else {
                String text = cleanText(element.text());
                if (!text.isEmpty()) {
                    contentAcc.add(text);
                }
            }
        }
    }
    
    private String cleanText(String text) {
        // Normalize whitespace without destroying meaning
        return text.replaceAll("\\s+", " ").trim();
    }
}
