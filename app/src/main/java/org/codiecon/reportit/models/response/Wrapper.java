package org.codiecon.reportit.models.response;

public class Wrapper<T> {

    public T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

}
