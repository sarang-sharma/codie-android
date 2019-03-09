package org.codiecon.reportit.models.response;

import java.util.List;

public class Wrapper<T> {

    public T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

}
