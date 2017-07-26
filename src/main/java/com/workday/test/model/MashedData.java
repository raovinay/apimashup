package com.workday.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Created by raovinay on 25-07-2017.
 */
@Data
@AllArgsConstructor
@Getter
public class MashedData {
    private GithubData githubData;
    private List<TwitterData> tweets;
}
