package com.seongekim.tcc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Greeting {
    @JsonProperty("id")
    public long id;
    @JsonProperty("content")
    public String content;
}
