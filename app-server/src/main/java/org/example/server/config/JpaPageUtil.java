package org.example.server.config;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import java.util.Map;

public class JpaPageUtil {

    /**
     * 通用分页查询方法
     *
     * @param repository    JpaRepository 或 JpaSpecificationExecutor
     * @param page          页码，从 0 开始
     * @param size          每页条数
     * @param sortMap       排序字段及方向，key=字段名, value=ASC/DESC
     * @param specification 可选动态条件
     * @param <T>           实体类型
     * @return 分页结果
     */
    public static <T> Page<T> queryPage(
            JpaSpecificationExecutor<T> repository,
            int page,
            int size,
            @Nullable Map<String, Sort.Direction> sortMap,
            @Nullable Specification<T> specification) {

        Sort sort = Sort.unsorted();
        if (sortMap != null && !sortMap.isEmpty()) {
            for (Map.Entry<String, Sort.Direction> entry : sortMap.entrySet()) {
                sort = sort.and(Sort.by(entry.getValue(), entry.getKey()));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        if (specification != null) {
            return repository.findAll(specification, pageable);
        } else {
            // 强制类型转换，需要传入 JpaRepository & JpaSpecificationExecutor
            return repository.findAll((Specification<T>) null, pageable);
        }
    }
}