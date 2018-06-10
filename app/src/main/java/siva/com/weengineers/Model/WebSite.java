package siva.com.weengineers.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MANIKANDAN on 16-11-2017.
 */
public class WebSite {
     private String status;
     private List<Source> sources=new ArrayList<>();

    public WebSite(String status, List<Source> sources) {
        this.status = status;
        this.sources = sources;
    }

    public WebSite() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
}
