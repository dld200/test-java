package org.example.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.checkerframework.checker.units.qual.A;
import org.example.common.domain.PageModel;
import org.example.common.model.UIElement;
import org.example.mobile.device.Automation;
import org.example.mobile.device.impl.IosSimulatorAutomation;
import org.example.mobile.xml.UIElementHtmlRenderer;
import org.example.mobile.xml.UIElementXmlSerializer;
import org.example.mobile.xml.WdaCleanTreeBuilder;
import org.example.server.dao.PageModelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ModelService {

    @Autowired
    private PageModelDao pageModelDao;

    @Autowired
    private FileService fileService;

    public PageModel save(PageModel req) {
        return pageModelDao.save(req);
    }

    public void deleteById(Long id) {
        pageModelDao.deleteById(id);
    }

    public PageModel findById(Long id) {
        Optional<PageModel> pageModel = pageModelDao.findById(id);
        return pageModel.orElse(null);
    }

    public List<PageModel> query() {
        return pageModelDao.findAll();
    }

    public String summary(Long id) {
        PageModel pageModel = findById(id);
        return pageModel != null ? pageModel.getSummary() : null;
    }

    public PageModel capture() throws IOException {
        Automation automation = new IosSimulatorAutomation();
        automation.setup("F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD", "ca.snappay.snaplii.test");
        automation.screenshot("xxxx.png");
        URL url = ResourceReader.class.getClassLoader().getResource("xxxx.png");
        fileService.saveFile(new File(url.getFile()));
        String source = automation.source();
        UIElement cleanTree = WdaCleanTreeBuilder.parseAndClean(source);
        String json = JSON.toJSONString(cleanTree, SerializerFeature.SortField, SerializerFeature.PrettyFormat);
        String xml = UIElementXmlSerializer.toXml(cleanTree);
        String html = UIElementHtmlRenderer.toHtml(cleanTree, 1.0f);

        PageModel pageModel = new PageModel();
        pageModel.setName("New Model");
        pageModel.setScreenshot("");
        pageModel.setHtml(html);
        pageModel.setXml(xml);
        pageModel.setJson(json);
        return save(pageModel);
    }

}