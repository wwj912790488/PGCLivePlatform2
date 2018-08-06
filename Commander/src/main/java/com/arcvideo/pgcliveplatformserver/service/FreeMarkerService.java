package com.arcvideo.pgcliveplatformserver.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;

@Service
public class FreeMarkerService {
    @Autowired
    private Configuration config;

    public String renderFromTemplateFile(String template, Object model) throws IOException, TemplateException {
        Template tpl = config.getTemplate(template);
        StringWriter writer = new StringWriter();
        tpl.process(model, writer);
        return writer.toString();
    }
}
