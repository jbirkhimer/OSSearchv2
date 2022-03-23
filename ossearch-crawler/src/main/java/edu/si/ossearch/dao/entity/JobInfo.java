package edu.si.ossearch.dao.entity;

import java.io.Serializable;
import java.util.Map;

public class JobInfo implements Serializable {
  private static final long serialVersionUID = 7615870960989331667L;

  public enum JobType {
    INJECT, GENERATE, FETCH, PARSE, UPDATEDB, INDEX, READDB, CLASS
  }

  public enum State {
    IDLE, RUNNING, FINISHED, FAILED, KILLED, STOPPING, KILLING, ANY
  }

  private String id;
  private JobType type;
  private String confId;
  private Map<String, Object> args;
  private Map<String, Object> result;
  private State state;
  private String msg;
  private String crawlId;

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public Map<String, Object> getResult() {
    return result;
  }

  public void setResult(Map<String, Object> result) {
    this.result = result;
  }

  public Map<String, Object> getArgs() {
    return args;
  }

  public void setArgs(Map<String, Object> args) {
    this.args = args;
  }

  public String getConfId() {
    return confId;
  }

  public void setConfId(String confId) {
    this.confId = confId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCrawlId() {
    return crawlId;
  }

  public void setCrawlId(String crawlId) {
    this.crawlId = crawlId;
  }

  public JobType getType() {
    return type;
  }

  public void setType(JobType type) {
    this.type = type;
  }

}
