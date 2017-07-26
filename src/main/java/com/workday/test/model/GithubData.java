package com.workday.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created by raovinay on 25-07-2017.
 */
@Data
@AllArgsConstructor
@Getter
public class GithubData {
    private Long id;
    private String project;
    private String projectFullName;
    private String summary;
}
