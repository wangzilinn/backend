package com.***REMOVED***.site.model.blog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ***REMOVED***n@gmail.com
 * @Description: 这是储存所有分类标签的一个表
 * @Date: Created in 4:53 PM 5/7/2020
 * @Modified By:***REMOVED***n@gmail.com
 */
@Data
@NoArgsConstructor
public class Category implements Serializable {
    @Id
    private Long id;

    @NotNull
    private String name;

    private List<Long> article_id;

    public Category(String name) {
        this.name = name;
        this.article_id = new ArrayList<>();
    }

}