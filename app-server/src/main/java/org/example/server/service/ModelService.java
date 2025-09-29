package org.example.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.common.domain.PageModel;
import org.example.mobile.automation.source.UIElementIosParser;
import org.example.mobile.automation.source.UiElement;
import org.example.mobile.automation.Automation;
import org.example.mobile.automation.IosAutomation;
import org.example.mobile.automation.source.UIElementHtmlSerializer;
import org.example.mobile.automation.source.UIElementXmlSerializer;
import org.example.server.dao.PageModelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
        Automation automation = new IosAutomation();
        automation.launch("F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD", "ca.snappay.snaplii.test");
        automation.screenshot("/Users/snap/workspace/app-agent/uploads/xxxx.png");
//        URL url = ResourceReader.class.getClassLoader().getResource("xxxx.png");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String file = fileService.saveFile(new File("/Users/snap/workspace/app-agent/uploads/xxxx.png"));
        String source = automation.source();
        UiElement cleanTree = UIElementIosParser.parseAndClean(source);
        String json = JSON.toJSONString(cleanTree, SerializerFeature.SortField, SerializerFeature.PrettyFormat);
        String xml = UIElementXmlSerializer.toXml(cleanTree);
        String html = UIElementHtmlSerializer.toHtml(cleanTree, 1.0f);

        PageModel pageModel = new PageModel();
        pageModel.setName("New Model");
        pageModel.setScreenshot("http://127.0.0.1:8080/api/files/" + file);
        pageModel.setHtml(html);
        pageModel.setXml(xml);
        pageModel.setJson(json);
        return save(pageModel);
    }

}