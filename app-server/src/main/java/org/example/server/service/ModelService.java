package org.example.server.service;

import org.example.common.domain.PageModel;
import org.example.server.dao.PageModelDao;
import org.example.server.dto.ModelReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModelService {

    @Autowired
    private PageModelDao appDao;

    public PageModel save(ModelReq req) {
        PageModel app = new PageModel();
        return appDao.save(app);
    }

    public void deleteById(Long id) {
        appDao.deleteById(id);
    }

    public PageModel findById(Long id) {
        Optional<PageModel> app = appDao.findById(id);
        return app.orElse(null);
    }

    public String summary(Long id) {
        return null;
    }

    public List<PageModel> query() {
        return appDao.findAll();
    }
}