package com.simple.autocomplete.Document.Entity;

import com.simple.autocomplete.model.BaseEntity;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.core.style.ToStringCreator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DOCUMENT_TABLE")
public class DocumentVo extends BaseEntity {

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "VIEW_COUNT")
    @ColumnDefault("0")
    private int viewCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DATE", nullable = false)
    private Date regDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EDIT_DATE", nullable = false)
    private Date editDate;



    private String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    private Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    private Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId())
                .append("title", this.getTitle())
                .append("content", this.getContent())
                .append("viewCount", this.getViewCount())
                .append("regDate", this.getRegDate()).append("editDate", this.getEditDate()).toString();
    }
}
