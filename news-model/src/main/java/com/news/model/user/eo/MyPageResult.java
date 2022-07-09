package com.news.model.user.eo;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 保存es检索数据：总记录数 + 限定分页的数据
 * @create 2022-07-03-19:15
 */
public class MyPageResult {

    private Long total;
    private List<ArticleEo> articleEos;
    private List<FansEo> fansEos;

    public MyPageResult() {
    }

    public MyPageResult(Long total, List<ArticleEo> articleEos) {
        this.total = total;
        this.articleEos = articleEos;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<ArticleEo> getArticleEos() {
        return articleEos;
    }

    public void setArticleEos(List<ArticleEo> articleEos) {
        this.articleEos = articleEos;
    }

    public List<FansEo> getFansEos() {
        return fansEos;
    }

    public void setFansEos(List<FansEo> fansEos) {
        this.fansEos = fansEos;
    }
}
