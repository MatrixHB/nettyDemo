package client;

import java.io.Serializable;

public class HeartBeat implements Serializable {

    private static final long serialVersionUID = -1650778975499993058L;

    private Long id;
    private String content;

    public HeartBeat(Long id, String content){
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
