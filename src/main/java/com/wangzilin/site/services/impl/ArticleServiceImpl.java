package com.wangzilin.site.services.impl;

import com.wangzilin.site.dao.ArticleDAO;
import com.wangzilin.site.dao.CategoryDAO;
import com.wangzilin.site.dao.TagDAO;
import com.wangzilin.site.model.DTO.QueryPage;
import com.wangzilin.site.model.DTO.Response;
import com.wangzilin.site.model.blog.Article;
import com.wangzilin.site.model.blog.Category;
import com.wangzilin.site.model.blog.Tag;
import com.wangzilin.site.services.ArticleService;
import com.wangzilin.site.services.CommentService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 10:56 PM 5/6/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    final private ArticleDAO articleDAO;
    final private CategoryDAO categoryDAO;
    final private TagDAO tagDAO;
    final private CommentService commentService;

    final private static org.slf4j.Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    public ArticleServiceImpl(ArticleDAO articleDAO, CategoryDAO categoryDAO, TagDAO tagDAO,
                              CommentService commentService) {
        this.articleDAO = articleDAO;
        this.categoryDAO = categoryDAO;
        this.tagDAO = tagDAO;
        this.commentService = commentService;
    }


    /**
     * @param article
     * @return void
     * @Author wangzilin
     * @Description 添加
     * @Date 4:47 PM 5/7/2020
     * @Param [article]
     */
    @Override
    public void addArticle(Article article) {
        //article collection更新
        String id = articleDAO.add(article).getId();
        log.info("new article id = " + id);
        //category collection更新
        categoryDAO.addArticle(article.getCategoryName(), id);
        //tag collection update
        article.getTagNames().forEach(tagName -> {
            tagDAO.addArticle(tagName, id);
        });
    }

    @Override
    public void deleteArticle(String id) {
        Article article = articleDAO.deleteById(id);
        //删除分类表
        categoryDAO.deleteArticle(article.getCategoryName(), id);
        //删除标签表
        article.getTagNames().forEach(tagName -> {
            tagDAO.deleteArticle(tagName, id);
        });
        //删除评论
        commentService.deleteByArticleId(id);
    }

    /**
     * @param article
     * @return void
     * @Author wangzilin
     * @Description 删改
     * @Date 4:36 PM 5/7/2020
     * @Param [article]
     */
    @Override
    public void updateArticle(Article article) {
        @NotNull
        String id = article.getId();
        Article originArticle = articleDAO.findById(id);
        //更新category:
        if (!originArticle.getCategoryName().equals(article.getCategoryName())) {
            categoryDAO.deleteArticle(originArticle.getCategoryName(), id);
            categoryDAO.addArticle(article.getCategoryName(), id);
        }
        //更新tag:
        //求新旧两个article tag的交集:
        List<String> intersection = new ArrayList<>(originArticle.getTagNames());
        intersection.retainAll(article.getTagNames());
        //相较于原有文章删除的tag:
        ArrayList<String> toBeDeletedTags = new ArrayList<>(originArticle.getTagNames());
        toBeDeletedTags.removeAll(intersection);
        toBeDeletedTags.forEach(deletedTagName -> {
            tagDAO.deleteArticle(deletedTagName, id);
        });
        //相较于原有文章增加的tag:
        ArrayList<String> toBeAddedTags = new ArrayList<>(article.getTagNames());
        toBeAddedTags.removeAll(intersection);
        toBeAddedTags.forEach(addedTagName -> {
            tagDAO.addArticle(addedTagName, id);
        });
        //更新article collection
        articleDAO.update(article);

    }

    @Override
    public Article findArticle(String id) {
        return articleDAO.findById(id);
    }

    /**
     * @param queryPage
     * @return java.util.List<com.wangzilin.site.model.blog.Article>
     * @Author wangzilin
     * @Description 分页列出文章
     * @Date 11:25 AM 5/11/2020
     * @Param [queryPage]
     */
    @Override
    public Response.Page<Article> listArticle(QueryPage queryPage) {
        List<Article> articleList = articleDAO.findAll(queryPage);
        long totalNumber = articleDAO.total();
        return new Response.Page<>(articleList, queryPage, totalNumber);
    }

    /**
     * @return java.util.List<com.wangzilin.site.model.blog.Article>
     * @Author wangzilin
     * @Description 查询title
     * @Date 1:19 PM 5/11/2020
     * @Param [title, queryPage]
     **/
    @Override
    public Response.Page<Article> listArticleByTitle(String title, QueryPage queryPage) {
        List<Article> articleList = articleDAO.findByTitle(title, queryPage);
        return new Response.Page<>(articleList, queryPage, articleList.size());
    }

    /**
     * @param categoryName
     * @param queryPage
     * @return java.util.List<com.wangzilin.site.model.blog.Article>
     * @Author wangzilin
     * @Description 根据分类查询文章
     * @Date 1:07 PM 5/11/2020
     * @Param [category, queryPage]
     */
    @Override
    public Response.Page<Article> listArticleByCategory(String categoryName, QueryPage queryPage) {
        Category category = categoryDAO.findByName(categoryName);
        return getArticlePage(queryPage, category.getArticleId());
    }

    /**
     * @param tagName
     * @param queryPage
     * @return java.util.List<com.wangzilin.site.model.blog.Article>
     * @Author wangzilin
     * @Description 根据tag查询文章
     * @Date 3:20 PM 5/11/2020
     * @Param [tag, queryPage]
     */
    @Override
    public Response.Page<Article> listArticleByTag(String tagName, QueryPage queryPage) {

        Tag tag = tagDAO.findByName(tagName);
        if (tag == null) {
            return null;
        }
        log.info(tag.toString());
        return getArticlePage(queryPage, tag.getArticleId());
    }

    private Response.Page<Article> getArticlePage(QueryPage queryPage, List<String> article_id) {
        ArrayList<Article> articles = new ArrayList<>();
        for (int i = 0; i < queryPage.getLimit(); i++) {

            int index = (queryPage.getPage() - 1) * queryPage.getLimit() + i;
            if (index >= article_id.size())
                break;
            String id = article_id.get(index);
            articles.add(articleDAO.findById(id));
        }
        return new Response.Page<>(articles, queryPage, article_id.size());
    }

    @Service
    static class CategoryServiceImpl implements ArticleService.CategoryService {

        private final CategoryDAO categoryDAO;

        private final ArticleDAO articleDAO;

        private Long totalCategories;

        @Autowired
        public CategoryServiceImpl(CategoryDAO categoryDAO, ArticleDAO articleDAO) {
            this.categoryDAO = categoryDAO;
            this.articleDAO = articleDAO;
            this.totalCategories = categoryDAO.total();
        }

        @Override
        public void add(Category category) {
            categoryDAO.add(category);
            totalCategories++;
        }

        @Override
        public void delete(String name) {
            Category category = categoryDAO.deleteByName(name);
            category.getArticleId().forEach(id -> {
                        articleDAO.updateCategory(id, null);
                    }
            );
            totalCategories--;
        }

        @Override
        public void update(String from, String to) {
            Category category = categoryDAO.deleteByName(from);
            category.getArticleId().forEach(id -> {
                        articleDAO.updateCategory(id, null);
                    }
            );
            categoryDAO.updateName(from, to);
        }

        @Override
        public Response.Page<Category> list(QueryPage queryPage) {
            List<Category> categoryList = categoryDAO.findAll(queryPage);
            return new Response.Page<>(categoryList, queryPage, totalCategories);
        }

        @Override
        public List<Category> list() {
            return categoryDAO.findAll(null);
        }

        @Override
        public Category find(String name) {
            return categoryDAO.findByName(name);
        }
    }

    @Service
    static class TagServiceImpl implements ArticleService.TagService {

        private final TagDAO tagDAO;
        private final ArticleDAO articleDAO;
        private Long totalTags;

        @Autowired
        public TagServiceImpl(TagDAO tagDAO, ArticleDAO articleDAO) {
            this.tagDAO = tagDAO;
            this.articleDAO = articleDAO;
            this.totalTags = tagDAO.total();
        }

        @Override
        public void add(Tag tag) {
            tagDAO.add(tag);
            totalTags++;
        }

        @Override
        public void delete(String name) {
            Tag deletedTag = tagDAO.deleteByName(name);
            deletedTag.getArticleId().forEach(id -> {
                articleDAO.updateTag(id, null, new ArrayList<>(Collections.singleton(name)));
            });
            totalTags--;
        }

        @Override
        public void update(String from, String to) {
            Tag tag = tagDAO.findByName(from);
            tag.setName(to);
            tag.getArticleId().forEach(id -> {
                articleDAO.updateTag(id, new ArrayList<>(Collections.singleton(to)),
                        new ArrayList<>(Collections.singleton(from)));
            });
        }

        @Override
        public Response.Page<Tag> list(QueryPage queryPage) {
            return new Response.Page<>(tagDAO.findAll(queryPage), queryPage, totalTags);
        }

        @Override
        public List<Tag> list() {
            return tagDAO.findAll(null);
        }

        @Override
        public Tag find(String name) {
            return tagDAO.findByName(name);
        }
    }

}
