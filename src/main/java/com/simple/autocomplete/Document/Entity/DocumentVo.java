package com.simple.autocomplete.Document.Entity;

import com.simple.autocomplete.model.BaseEntity;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "DOCUMENT_TABLE")
public class DocumentVo extends BaseEntity {

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "REG_DATE", nullable = false)
    private Date regDate;

    @Column(name = "EDIT_DATE", nullable = false)
    private Date editDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }
}
